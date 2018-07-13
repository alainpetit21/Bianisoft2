/* This file is part of the Bianisoft game library.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *----------------------------------------------------------------------
 * Copyright (C) Alain Petit - alainpetit21@hotmail.com
 *
 * 18/12/10			0.1 First beta initial Version.
 * 12/09/11			0.1.2 Moved everything to a com.bianisoft
 *
 *-----------------------------------------------------------------------
 */
package com.bianisoft.engine.resmng;


//Standard Java imports
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import javax.imageio.ImageIO;


public class BufferedImageData{
	private static final ColorModel glAlphaColorModel= new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
																			   new int[] {8,8,8,8}, true, false,
																			   ComponentColorModel.TRANSLUCENT,
																			   DataBuffer.TYPE_BYTE);
	private static final  ColorModel glColorModel=	new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
																			new int[] {8,8,8,0}, false, false,
																			ComponentColorModel.OPAQUE,
																			DataBuffer.TYPE_BYTE);

	private int m_nDepth;
	private int m_nHeight;
	private int m_nWidth;
	private int m_nTexWidth;
	private int m_nTexHeight;
	private boolean m_isEdging= true;


	public int getDepth()		{return m_nDepth;}
	public int getHeight()		{return m_nHeight;}
	public int getTexHeight()	{return m_nTexHeight;}
	public int getTexWidth()	{return m_nTexWidth;}
	public int getWidth()		{return m_nWidth;}

	public ByteBuffer loadImage(InputStream p_is) throws IOException		{return loadImage(p_is, true, null);}
	public ByteBuffer loadImage(InputStream p_is, boolean p_isFlipped, int[] p_arColorTrans) throws IOException		{return loadImage(p_is, p_isFlipped, false, p_arColorTrans);}
	public ByteBuffer loadImage(InputStream p_is, boolean p_isFlipped, boolean p_isForceAlpha, int[] p_arColorTrans) throws IOException {
		if(p_arColorTrans != null)
			p_isForceAlpha= true;
		
		BufferedImage bufferedImage= ImageIO.read(p_is);
		return imageToByteBuffer(bufferedImage, p_isFlipped, p_isForceAlpha, p_arColorTrans);
	}
	
	public ByteBuffer imageToByteBuffer(BufferedImage p_bufImage, boolean p_isFlipped, boolean p_isForceAlpha, int[] p_arColorTrans){
	    ByteBuffer imageBuffer; 
        WritableRaster raster;
        BufferedImage texImage;
        
        int texWidth= 2;
        int texHeight= 2;
        while(texWidth < p_bufImage.getWidth())
            texWidth*= 2;
        while (texHeight < p_bufImage.getHeight())
            texHeight*= 2;
        
        m_nWidth	= p_bufImage.getWidth();
        m_nHeight	= p_bufImage.getHeight();
        m_nTexHeight= texHeight;
        m_nTexWidth	= texWidth;
        
        // create a raster that can be used by OpenGL as a source
        // for a texture
        boolean useAlpha= p_bufImage.getColorModel().hasAlpha() || p_isForceAlpha;
        
        if(useAlpha){
        	m_nDepth= 32;
            raster	= Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
            texImage= new BufferedImage(glAlphaColorModel, raster, false, new Hashtable());
        }else{
        	m_nDepth= 24;
            raster	= Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
            texImage= new BufferedImage(glColorModel,raster, false, new Hashtable());
        }
            
        Graphics2D g= (Graphics2D) texImage.getGraphics();

		//Mac compatibility requires to clear the image
        if(useAlpha){
	        g.setColor(new Color(0f,0f,0f,0f));
	        g.fillRect(0, 0, texWidth, texHeight);
        }
        
        if(p_isFlipped){
        	g.scale(1, -1);
        	g.drawImage(p_bufImage, 0, -m_nHeight, null);
        }else{
        	g.drawImage(p_bufImage, 0, 0, null);
        }
        
        if(m_isEdging){
	        if(m_nHeight < texHeight - 1){
	        	copyArea(texImage, 0, 0, m_nWidth, 1, 0, texHeight-1);
	        	copyArea(texImage, 0, m_nHeight-1, m_nWidth, 1, 0, 1);
	        }
	        if(m_nWidth < texWidth - 1){
	        	copyArea(texImage, 0,0,1,m_nHeight,texWidth-1,0);
	        	copyArea(texImage, m_nWidth-1,0,1,m_nHeight,1,0);
	        }
        }
        
        // build a byte buffer from the temporary image that be used by OpenGL to produce a texture.
        byte[] data= ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData(); 
        
        if(p_arColorTrans != null){
	        for(int i= 0; i < data.length; i+= 4){
	        	boolean isMatch= true;

	        	for(int c= 0; c < 3; c++){
	        		int value= (data[i+c] < 0)? (256 + data[i+c]):data[i+c];
	        		
					if(value != p_arColorTrans[c])
	        			isMatch = false;
	        	}
	  
	        	if(isMatch)
	         		data[i+3]= 0;
	        }
        }
        
        imageBuffer= ByteBuffer.allocateDirect(data.length); 
        imageBuffer.order(ByteOrder.nativeOrder()); 
        imageBuffer.put(data, 0, data.length); 
        imageBuffer.flip();
        g.dispose();
        
        return imageBuffer; 
	}

	private void copyArea(BufferedImage p_bufImage, int p_nX, int p_nY, int p_nWidth, int p_nHeight, int p_nDeltaX, int p_nDeltaY) {
		Graphics2D g= (Graphics2D) p_bufImage.getGraphics();
		
		g.drawImage(p_bufImage.getSubimage(p_nX, p_nY, p_nWidth, p_nHeight), p_nX+p_nDeltaX, p_nY+p_nDeltaY, null);
	}

	public void configureEdging(boolean p_isEdging){
		m_isEdging= p_isEdging;
	}
}
