//---
//Copyright (c) 2007, Matthias Mann
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl;

//Standard Java imports
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

//Slick-util library imports
import org.lwjgl.BufferUtils;


public class PNGImageData implements LoadableImageData{
	private static final byte[] SIGNATURE= {(byte)137, 80, 78, 71, 13, 10, 26, 10};
	private static final int IDCHUNK_HEADER	= 0x49484452;
	private static final int IDCHUNK_PALETTE= 0x504C5445;
	private static final int IDCHUNK_TRANS	= 0x74524E53;
	private static final int IDCHUNK_DATA	= 0x49444154;
	private static final int IDCHUNK_END	= 0x49454E44;

	private static final byte COLOR_GREYSCALE = 0;
	private static final byte COLOR_TRUECOLOR = 2;
	private static final byte COLOR_INDEXED = 3;
	private static final byte COLOR_GREYALPHA = 4;
	private static final byte COLOR_TRUEALPHA = 6;

	private InputStream m_isInput;
	private final CRC32 m_nCRC;
	private final byte[] m_bufTemp;

	private int m_nChunkLength;
	private int m_nChunkType;
	private int m_nByteRemainingInChunk;

	private int m_nWidth;
	private int m_nHeight;
	private int m_nColorType;
	private int m_nBytesPerPixel;
	private byte[] m_arPalette;
	private byte[] m_arPaletteAlpha;
	private byte[] m_bufTransPixel;

	private int m_nBitDepth;
	private int m_nTexWidth;
	private int m_nTexHeight;

	private ByteBuffer m_bufData;


	public PNGImageData(){
		m_bufTemp= new byte[4096];
		m_nCRC= new CRC32();
	}
    
    private void init(InputStream input) throws IOException{
        this.m_isInput= input;
        
        int nRead= input.read(m_bufTemp, 0, SIGNATURE.length);
        if(nRead != SIGNATURE.length || !checkSignature(m_bufTemp))
            throw new IOException("Not a valid PNG file");
       
        openChunk(IDCHUNK_HEADER);
        readIHDR();
        closeChunk();
        
        searchIDAT: for(;;){
            openChunk();
            switch (m_nChunkType){
            case IDCHUNK_DATA:
                break searchIDAT;
            case IDCHUNK_PALETTE:
                readPLTE();
                break;
            case IDCHUNK_TRANS:
                readtRNS();
                break;
            }

            closeChunk();
        }
    }

    public int getHeight()	{return m_nHeight;}
    public int getWidth()	{return m_nWidth;}
    
    public boolean hasAlpha(){
        return (m_nColorType == COLOR_TRUEALPHA) || (m_arPaletteAlpha != null) || (m_bufTransPixel != null);
    }
    
    public boolean isRGB(){
        return (m_nColorType == COLOR_TRUEALPHA) || (m_nColorType == COLOR_TRUECOLOR) || (m_nColorType == COLOR_INDEXED);
    }
    
    private void decode(ByteBuffer p_buffer, int p_nStride, boolean p_isFlipped) throws IOException{
        final int nOffset= p_buffer.position();
        byte[] curLine= new byte[(m_nWidth*m_nBytesPerPixel)+1];
        byte[] prevLine= new byte[(m_nWidth*m_nBytesPerPixel)+1];
        
        final Inflater inflater= new Inflater();
        try {
            for(int yIndex= 0; yIndex < m_nHeight; yIndex++){
            	int y= yIndex;
            	if(p_isFlipped)
            		y= m_nHeight - 1 - yIndex;
            	
                readChunkUnzip(inflater, curLine, 0, curLine.length);
                unfilter(curLine, prevLine);

                p_buffer.position(nOffset + y*p_nStride);

                switch(m_nColorType){
					case COLOR_TRUECOLOR:
					case COLOR_TRUEALPHA:	copy(p_buffer, curLine);		break;
					case COLOR_INDEXED:		copyExpand(p_buffer, curLine);	break;
					default:
						System.out.print("***ERROR***\nPNGImageData.decode.m_nColorType= " +m_nColorType+"Not yet implemented\n");
                }

                byte[] tmp= curLine;
                curLine= prevLine;
                prevLine= tmp;
            }
        }finally{
            inflater.end();
        }
        
        m_nBitDepth= hasAlpha()? 32:24;
    }
    
    private void copyExpand(ByteBuffer p_buffer, byte[] p_bufCurLine){
    	for(int i= 1; i < p_bufCurLine.length; i++){
    		int v= p_bufCurLine[i] & 255;
    
    		int index= v * 3;

    		for(int j= 0; j < 3; j++)
    			p_buffer.put(m_arPalette[index+j]);
    		
    		if(hasAlpha()){
	    		if(m_arPaletteAlpha != null)
	    			p_buffer.put(m_arPaletteAlpha[v]);
	    		else
	    			p_buffer.put((byte)255);
			}
    	}
    }
    
    private void copy(ByteBuffer p_buffer, byte[] p_bufCurLine){
        p_buffer.put(p_bufCurLine, 1, p_bufCurLine.length-1);
    }

    private void unfilter(byte[] p_bufCurLine, byte[] p_bufPrevLine) throws IOException {
        switch(p_bufCurLine[0]){
            case 0:													break;
            case 1:	unfilterSub(p_bufCurLine);						break;
            case 2:	unfilterUp(p_bufCurLine, p_bufPrevLine);		break;
            case 3: unfilterAverage(p_bufCurLine, p_bufPrevLine);	break;
            case 4: unfilterPaeth(p_bufCurLine, p_bufPrevLine);		break;
            default:
				System.out.print("***ERROR***\nPNGImageData.unfilter.p_bufCurLine[0]= " +p_bufCurLine[0]+ "Invalid filter type in scanline\n");
        }
    }
    
    private void unfilterSub(byte[] p_bufCurLine){
        final int nBPP= m_nBytesPerPixel;
        final int nLineSize= m_nWidth*nBPP;
        
        for(int i= nBPP+1; i <= nLineSize; ++i)
            p_bufCurLine[i]+= p_bufCurLine[i-nBPP];
    }

    private void unfilterUp(byte[] p_bufCurLine, byte[] p_bufPrevLine){
        final int nBPP= m_nBytesPerPixel;
        final int nLineSize= m_nWidth*nBPP;
        
        for(int i= 1; i <= nLineSize; ++i)
            p_bufCurLine[i]+= p_bufPrevLine[i];
    }

    private void unfilterAverage(byte[] p_bufCurLine, byte[] p_bufPrevLine){
        final int nBPP= this.m_nBytesPerPixel;
        final int nLineSize= m_nWidth*nBPP;
        
        int i= 1;
        for(; i <= nBPP; ++i)
            p_bufCurLine[i]+= (byte)((p_bufPrevLine[i] & 0xFF) >>> 1);
        for(; i <= nLineSize; ++i)
            p_bufCurLine[i]+= (byte)(((p_bufPrevLine[i] & 0xFF) + (p_bufCurLine[i - nBPP] & 0xFF)) >>> 1);
    }

    private void unfilterPaeth(byte[] p_bufCurLine, byte[] p_bufPrevLine) {
        final int nBPP= this.m_nBytesPerPixel;
        final int nLineSize= m_nWidth*nBPP;
        
        int i= 1;
        for(; i <= nBPP ; ++i)
            p_bufCurLine[i]+= p_bufPrevLine[i];

		for(; i <= nLineSize ; ++i){
            int a= p_bufCurLine[i - nBPP] & 255;
            int b= p_bufPrevLine[i] & 255;
            int c= p_bufPrevLine[i - nBPP] & 255;

            int p= a + b - c;

			int pa= p - a; if(pa < 0) pa = -pa;
            int pb= p - b; if(pb < 0) pb = -pb;
            int pc= p - c; if(pc < 0) pc = -pc;

            if((pa <= pb) && (pa<=pc))
                c= a;
            else if(pb <= pc)
                c= b;

            p_bufCurLine[i]+= (byte)c;
        }
    }
      
    private void readIHDR() throws IOException{
        checkChunkLength(13);
        readChunk(m_bufTemp, 0, 13);

        m_nWidth = readInt(m_bufTemp, 0);
        m_nHeight = readInt(m_bufTemp, 4);
        
        if(m_bufTemp[8] != 8)
            System.out.print("***ERROR***\nPNGImageData.readIHDR: Unsupported bit depth");
        
        m_nColorType= m_bufTemp[9] & 255;
        switch(m_nColorType) {
        case COLOR_GREYSCALE:	m_nBytesPerPixel= 1;	break;
        case COLOR_TRUECOLOR:	m_nBytesPerPixel= 3;	break;
        case COLOR_TRUEALPHA:	m_nBytesPerPixel= 4;	break;
        case COLOR_INDEXED:		m_nBytesPerPixel= 1;	break;
        default:
            System.out.print("***ERROR***\nPNGImageData.readIHDR: unsupported color format");
        }
        
        if(m_bufTemp[10] != 0)
            System.out.print("***ERROR***\nPNGImageData.readIHDR: unsupported compression method");
        if(m_bufTemp[11] != 0)
            System.out.print("***ERROR***\nPNGImageData.readIHDR: unsupported filtering method");
        if(m_bufTemp[12] != 0)
            System.out.print("***ERROR***\nPNGImageData.readIHDR: unsupported interlace method");
    }

    private void readPLTE() throws IOException{
        int nPaletteEntries= m_nChunkLength / 3;

        if((nPaletteEntries < 1) || (nPaletteEntries > 256) || ((m_nChunkLength % 3) != 0))
            System.out.print("***ERROR***\nPNGImageData.readPLTE: PLTE chunk has wrong length\n");
        
        m_arPalette= new byte[nPaletteEntries * 3];
        readChunk(m_arPalette, 0, m_arPalette.length);
    }

    private void readtRNS() throws IOException{
        switch(m_nColorType){
        case COLOR_GREYSCALE:
            checkChunkLength(2);
            m_bufTransPixel= new byte[2];
            readChunk(m_bufTransPixel, 0, 2);
		break;
        case COLOR_TRUECOLOR:
            checkChunkLength(6);
            m_bufTransPixel= new byte[6];
            readChunk(m_bufTransPixel, 0, 6);
		break;
        case COLOR_INDEXED:
            if(m_arPalette == null)
                System.out.print("***ERROR***\nPNGImageData.readtRNS: tRNS chunk without PLTE chunk\n");

			m_arPaletteAlpha= new byte[m_arPalette.length / 3];

            //initialise default palette values
            for(int i= 0; i < m_arPaletteAlpha.length; i++)
            	m_arPaletteAlpha[i]= (byte)255;

			readChunk(m_arPaletteAlpha, 0, m_arPaletteAlpha.length);
		break;
        }
    }
    
    private void closeChunk() throws IOException{
        if(m_nByteRemainingInChunk > 0){
			try{
	            m_isInput.skip(m_nByteRemainingInChunk + 4);
			}catch(IOException e){
                System.out.print("***ERROR***\nPNGImageData.closeChunk: m_isInput.skip Exception\n");
			}
        }else{
            readFully(m_bufTemp, 0, 4);

            int nExpectedCrc= readInt(m_bufTemp, 0);
            int nComputedCrc= (int)m_nCRC.getValue();
            
			if(nComputedCrc != nExpectedCrc)
                System.out.print("***ERROR***\nPNGImageData.closeChunk: Invalid CRC\n");
        }

        m_nByteRemainingInChunk= 0;
        m_nChunkLength= 0;
        m_nChunkType= 0;
    }
    
    private void openChunk(){
		try{
			readFully(m_bufTemp, 0, 8);

			m_nChunkLength= readInt(m_bufTemp, 0);
			m_nChunkType= readInt(m_bufTemp, 4);

			m_nByteRemainingInChunk= m_nChunkLength;

			m_nCRC.reset();
			m_nCRC.update(m_bufTemp, 4, 4);   // only chunkType
		}catch(IOException ex){
			System.out.print("***ERROR***\nPNGImageData.openChunk: Exception\n");
		}
    }
    
    private void openChunk(int p_nType){
        openChunk();

		if(m_nChunkType != p_nType)
			System.out.print("***ERROR***\nPNGImageData.openChunk(int p_nType): Expected chunk: " +Integer.toHexString(p_nType)+ "\n");
    }

    private void checkChunkLength(int p_nLenghtExpected){
        if(m_nChunkLength != p_nLenghtExpected)
			System.out.print("***ERROR***\nPNGImageData.checkChunkLength(int p_nLenghtExpected): Chunk has wrong size\n");
    }
    
    private int readChunk(byte[] p_buffer, int p_nOffset, int p_nLength) throws IOException {
        if(p_nLength > m_nByteRemainingInChunk)
            p_nLength= m_nByteRemainingInChunk;

        readFully(p_buffer, p_nOffset, p_nLength);

        m_nCRC.update(p_buffer, p_nOffset, p_nLength);
        m_nByteRemainingInChunk-= p_nLength;
        
		return p_nLength;
    }

    private void refillInflater(Inflater inflater) throws IOException {
        while(m_nByteRemainingInChunk == 0){
            closeChunk();
            openChunk(IDCHUNK_DATA);
        }

        int nRead= readChunk(m_bufTemp, 0, m_bufTemp.length);
        inflater.setInput(m_bufTemp, 0, nRead);
    }
    
    private void readChunkUnzip(Inflater inflater, byte[] p_buffer, int p_nOffset, int p_nLength) throws IOException{
        try{
            do{
                int nRead= inflater.inflate(p_buffer, p_nOffset, p_nLength);
                
				if(nRead <= 0){
                    if(inflater.finished())
                        throw new EOFException();

					if(inflater.needsInput())
                        refillInflater(inflater);
                    else
                        throw new IOException("Can't inflate " + p_nLength + " bytes");

				}else{
                    p_nOffset+= nRead;
                    p_nLength-= nRead;
                }
            }while(p_nLength > 0);

        }catch(DataFormatException ex){
            IOException io= new IOException("inflate error");
            io.initCause(ex);
            
            throw io;
        }
    }

    private void readFully(byte[] p_buffer, int p_nOffset, int p_nLength) throws IOException{
        do{
            int nRead= m_isInput.read(p_buffer, p_nOffset, p_nLength);
            if(nRead < 0)
                throw new EOFException();

			p_nOffset+= nRead;
            p_nLength-= nRead;
        }while(p_nLength > 0);
    }
    
    private int readInt(byte[] p_buffer, int p_nOffset){
        return	((p_buffer[p_nOffset  ]      ) << 24) |
                ((p_buffer[p_nOffset+1] & 255) << 16) |
                ((p_buffer[p_nOffset+2] & 255) <<  8) |
                ((p_buffer[p_nOffset+3] & 255)      );
    }
    
    private boolean checkSignature(byte[] p_buffer){
        for(int i= 0; i < SIGNATURE.length; i++)
            if(p_buffer[i] != SIGNATURE[i])
                return false;

		return true;
    }

	public int getDepth()					{return m_nBitDepth;}
	public ByteBuffer getImageBufferData()	{return m_bufData;}
	public int getTexHeight()				{return m_nTexHeight;}
	public int getTexWidth()				{return m_nTexWidth;}


	public ByteBuffer loadImage(InputStream p_is) throws IOException {return loadImage(p_is, false, null);}
	public ByteBuffer loadImage(InputStream p_is, boolean p_isFlipped, int[] p_arColortrans) throws IOException {return loadImage(p_is, p_isFlipped, false, p_arColortrans);}
	public ByteBuffer loadImage(InputStream p_is, boolean p_isFlipped, boolean p_isForceAlpha, int[] p_arColortrans) throws IOException {
		if(p_arColortrans != null)
			p_isForceAlpha= true;
		
		init(p_is);
		
		if(!isRGB())
			System.out.print("***ERROR***\nPNGImageData.loadImage(): Only RGB formatted images are supported by the PNGLoader\n");
		
		m_nTexWidth= get2Fold(m_nWidth);
		m_nTexHeight= get2Fold(m_nHeight);
		
		int perPixel= hasAlpha()? 4:3;
		
		// Get a pointer to the image memory
		m_bufData = BufferUtils.createByteBuffer(m_nTexWidth * m_nTexHeight * perPixel);
		decode(m_bufData, m_nTexWidth * perPixel, p_isFlipped);

		if(m_nHeight < m_nTexHeight-1){
			int nTopOffset= (m_nTexHeight-1) * (m_nTexWidth*perPixel);
			int nBottomOffset= (m_nHeight-1) * (m_nTexWidth*perPixel);

			for(int x= 0; x < m_nTexWidth; x++){
				for(int i= 0;i < perPixel; i++){
					m_bufData.put(nTopOffset+x+i, m_bufData.get(x+i));
					m_bufData.put(nBottomOffset+(m_nTexWidth*perPixel)+x+i, m_bufData.get(nBottomOffset+x+i));
				}
			}
		}

		if(m_nWidth < m_nTexWidth-1){
			for(int y= 0; y < m_nTexHeight; y++){
				for(int i= 0; i < perPixel; i++){
					m_bufData.put(((y+1)*(m_nTexWidth*perPixel))-perPixel+i, m_bufData.get(y*(m_nTexWidth*perPixel)+i));
					m_bufData.put((y*(m_nTexWidth*perPixel))+(m_nWidth*perPixel)+i, m_bufData.get((y*(m_nTexWidth*perPixel))+((m_nWidth-1)*perPixel)+i));
				}
			}
		}
		
		if(!hasAlpha() && p_isForceAlpha){
			ByteBuffer temp= BufferUtils.createByteBuffer(m_nTexWidth * m_nTexHeight * 4);
			
			for(int x= 0; x < m_nTexWidth; x++){
				for(int y= 0; y < m_nTexHeight;y++){
					int srcOffset= (y*3)+(x*m_nTexHeight*3);
					int dstOffset= (y*4)+(x*m_nTexHeight*4);
					
					temp.put(dstOffset, m_bufData.get(srcOffset));
					temp.put(dstOffset+1, m_bufData.get(srcOffset+1));
					temp.put(dstOffset+2, m_bufData.get(srcOffset+2));
					temp.put(dstOffset+3, (byte) 255);
				}
			}

			m_nColorType= COLOR_TRUEALPHA;
			m_nBitDepth= 32;
			m_bufData= temp;
		}
			
		if(p_arColortrans != null){
	        for(int i= 0; i < (m_nTexWidth*m_nTexHeight*4); i+= 4){
	        	boolean isMatch= true;

	        	for(int c= 0; c < 3; c++)
	        		if(toInt(m_bufData.get(i+c)) != p_arColortrans[c])
	        			isMatch= false;
	  
	        	if(isMatch)
	        		m_bufData.put(i+3, (byte) 0);
	        }
	    }
		
		m_bufData.position(0);
		return m_bufData;
	}
	
	private int toInt(byte p_nByte){
		if(p_nByte < 0)
			return 256+p_nByte;

		return p_nByte;
	}
	
    private int get2Fold(int p_nFold){
        int nRet= 2;
        
		while(nRet < p_nFold)
            nRet*= 2;

		return nRet;
    }
    
	public void configureEdging(boolean edging) {	}
}
