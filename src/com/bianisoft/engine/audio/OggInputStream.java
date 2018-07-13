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
package com.bianisoft.engine.audio;

//Standard Java imports
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//LWJGL library imports
import org.lwjgl.BufferUtils;

//Vorbis + Ogg library imports
import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;


public class OggInputStream extends InputStream {
	private int nBufferSize= 4096 * 4;

	private byte[]		m_bufConversion = new byte[nBufferSize];
	private InputStream m_objISInput;
	private Info		m_objOggInfo= new Info();
	private boolean		m_isEndOfStream;

	private SyncState	m_objSyncState		= new SyncState();	// sync and verify incoming physical bitstream
	private StreamState m_objStreamState	= new StreamState();// take physical pages, weld into a logical stream of packets
	private Page		m_objPage			= new Page();		// one Ogg bitstream page.  Vorbis packets are inside
	private Packet		m_objPacket			= new Packet();		// one raw packet of data for decode
	private Comment		m_objComment		= new Comment();	// struct that stores all the bitstream user comments
	private DspState	m_objDspState		= new DspState();	// central working state for the packet->PCM decoder
	private Block		m_objVorbisBlock	= new Block(m_objDspState);	// local working space for packet->PCM decode
	
	byte[]	m_bufTemp;
	int		m_nNbBytesRead= 0;
	boolean m_isBigEndian = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
	boolean m_isEndOfBitStream = true;
	boolean m_isInited = false;

	private ByteBuffer	m_bufPCM= BufferUtils.createByteBuffer(4096 * 500);
	private int			m_nIdxReadArray;
	private int			m_nTotalBytes;


	public OggInputStream(InputStream p_objISInput){
		m_objISInput= p_objISInput;

		try{
			m_nTotalBytes= p_objISInput.available();
		}catch(IOException e){
			e.printStackTrace();
			System.out.print("\n****ERROR***\nTrouble iwth InputStrea.availabe(" +p_objISInput+ "\n");
		}
		
		init();
	}

	public int getLength()		{return m_nTotalBytes;}
	public int getChannels()	{return m_objOggInfo.channels;}
	public int getRate()		{return m_objOggInfo.rate;}
	public boolean isAtEnd()	{return m_isEndOfStream && (m_nIdxReadArray >= m_bufPCM.position());}
	public int available()		{return (m_isEndOfStream)? 0 : 1;}

	private void init(){
		m_objSyncState.init();
		readPCM();
	}
		
	private boolean getPageAndPacket(){
		// submit a 4k block to libvorbis' Ogg layer
		int nIndex= m_objSyncState.buffer(4096);
		
		m_bufTemp= m_objSyncState.data;
		if(m_bufTemp == null){
			m_isEndOfStream= true;
			return false;
		}
		
		try{
			m_nNbBytesRead= m_objISInput.read(m_bufTemp, nIndex, 4096);

		}catch(Exception e){
			System.out.print("\n***ERROR***\nFailure reading in vorbis\n");
			m_isEndOfStream= true;
			return false;
		}

		m_objSyncState.wrote(m_nNbBytesRead);

		//Get the first page.
		if(m_objSyncState.pageout(m_objPage) != 1){
			// have we simply run out of data?  If so, we're done.
			if(m_nNbBytesRead < 4096)
				return false;

			// error case.  Must not be Vorbis data
			System.out.print("\n***ERROR***\nInput does not appear to be an Ogg bitstream.\n");
			m_isEndOfStream= true;
			return false;
		}

		// Get the serial number and set up the rest of decode. serialno first; use it to set up a logical stream
		m_objStreamState.init(m_objPage.serialno());

		// extract the initial header from the first page and verify that the Ogg bitstream is in fact Vorbis data

		// I handle the initial header first instead of just having the code
		// read all three Vorbis headers at once because reading the initial
		// header is an easy way to identify a Vorbis bitstream and it's
		// useful to see that functionality seperated out.

		m_objOggInfo.init();
		m_objComment.init();

		if(m_objStreamState.pagein(m_objPage) < 0){
			System.out.print("\n***ERROR***\nError reading first page of Ogg bitstream data.\n");
			m_isEndOfStream= true;
			return false;
		}

		if(m_objStreamState.packetout(m_objPacket) != 1){
			// no page? must not be vorbis
			System.out.print("\n***ERROR***\nError reading initial header packet.\n");
			m_isEndOfStream= true;
			return false;
		}

		if(m_objOggInfo.synthesis_headerin(m_objComment, m_objPacket) < 0){
			System.out.print("\n***ERROR***\nThis Ogg bitstream does not contain Vorbis audio data.\n");
			m_isEndOfStream= true;
			return false;
		}

		// At this point, we're sure we're Vorbis.  We've set up the logical
		// (Ogg) bitstream decoder.  Get the comment and codebook headers and
		// set up the Vorbis decoder

		// The next two packets in order are the comment and codebook headers.
		// They're likely large and may span multiple pages.  Thus we reead
		// and submit data until we get our two pacakets, watching that no
		// pages are missing.  If a page is missing, error out; losing a
		// header page is the only place where missing data is fatal. */

		int i= 0;
		while(i < 2){
			while (i < 2){

				int nResult= m_objSyncState.pageout(m_objPage);
				if(nResult == 0)
					break; // Need more data
				// Don't complain about missing or corrupt data yet.  We'll
				// catch it at the packet output phase

				if(nResult == 1){
					m_objStreamState.pagein(m_objPage); // we can ignore any errors here as they'll also become apparent
														// at packetout
					while(i < 2){
						nResult= m_objStreamState.packetout(m_objPacket);
						
						if(nResult == 0)
							break;

						if(nResult == -1){
							// Uh oh; data at some point was corrupted or missing! We can't tolerate that in a header.  Die.
							System.out.print("\n***ERROR***\nCorrupt secondary header.  Exiting.\n");
							m_isEndOfStream= true;
							return false;
						}

						m_objOggInfo.synthesis_headerin(m_objComment, m_objPacket);
						i++;
					}
				}
			}

			// no harm in not checking before adding more
			nIndex= m_objSyncState.buffer(4096);
			m_bufTemp= m_objSyncState.data;

			try{
				m_nNbBytesRead= m_objISInput.read(m_bufTemp, nIndex, 4096);

			}catch(Exception e){
				System.out.print("\n***ERROR***\nFailed to read Vorbis\n");
				m_isEndOfStream= true;
				return false;
			}

			if((m_nNbBytesRead == 0) && (i < 2)){
				System.out.print("\n***ERROR***\nEnd of file before finding all Vorbis headers!\n");
				m_isEndOfStream= true;
				return false;
			}

			m_objSyncState.wrote(m_nNbBytesRead);
		}

		nBufferSize= 4096 / m_objOggInfo.channels;

		// OK, got and parsed all three headers. Initialize the Vorbis packet->PCM decoder.
		m_objDspState.synthesis_init(m_objOggInfo);	// central decode state
		m_objVorbisBlock.init(m_objDspState);		// local state for most of the decode so multiple block decodes can
													// proceed in parallel. We could init multiple vorbis_block structures
													// for vd here.
		
		return true;
	}
	
	private void readPCM(){
		boolean isWrote= false;
		
		while(true){						// we repeat if the bitstream is chained
			if(m_isEndOfBitStream){
				if(!getPageAndPacket())
					break;

				m_isEndOfBitStream= false;
			}

			if(!m_isInited){
				m_isInited= true;
				return;
			}
			
			float[][][] _pcm= new float[1][][];
			int[] _index= new int[m_objOggInfo.channels];

			// The rest is just a straight decode loop until end of stream
			while(!m_isEndOfBitStream){
				while(!m_isEndOfBitStream){
					int nResult= m_objSyncState.pageout(m_objPage);
					
					if(nResult == 0)
						break;		// need more data
					
					if(nResult == -1){		// missing or corrupt data at this page position
						System.out.print("\n***WARNING***\nCorrupt or missing data in bitstream; continuing...\n");
					}else{
						m_objStreamState.pagein(m_objPage); // can safely ignore errors at this point

						while(true){
							nResult= m_objStreamState.packetout(m_objPacket);

							if(nResult == 0)
								break;			// need more data
							
							if(nResult != -1){	// we have a packet.  Decode it
								int samples;

								if(m_objVorbisBlock.synthesis(m_objPacket) == 0)		// test for success!
									m_objDspState.synthesis_blockin(m_objVorbisBlock);

								// **pcm is a multichannel float vector.  In stereo, for
								// example, pcm[0] is left, and pcm[1] is right.  samples is
								// the size of each channel.  Convert the float values
								// (-1.<=range<=1.) to whatever PCM format and write it out

								while((samples = m_objDspState.synthesis_pcmout(_pcm, _index)) > 0){
									float[][] pcm= _pcm[0];
									//boolean clipflag = false;
									int bout= (samples < nBufferSize)? samples: nBufferSize;

									// convert floats to 16 bit signed ints (host order) and interleave
									for(int i= 0; i < m_objOggInfo.channels; ++i){
										int ptr= i * 2;
										//int ptr=i;
										int mono = _index[i];

										for(int j= 0; j < bout; j++){
											int nValue= (int) (pcm[i][mono + j] * 32767.);
											// might as well guard against clipping

											if(nValue > 32767)
												nValue= 32767;

											if(nValue < -32768)
												nValue= -32768;

											if(nValue < 0)
												nValue= nValue | 0x8000;
				
											if(m_isBigEndian){
												m_bufConversion[ptr]= (byte) (nValue >>> 8);
												m_bufConversion[ptr + 1]= (byte) (nValue);
											}else{
												m_bufConversion[ptr]= (byte) (nValue);
												m_bufConversion[ptr + 1]= (byte) (nValue >>> 8);
											}

											ptr+= 2 * (m_objOggInfo.channels);
										}
									}

									int bytesToWrite= 2 * m_objOggInfo.channels * bout;
									if(bytesToWrite >= m_bufPCM.remaining())
										System.out.print("\n***WARNING***\nRead block from OGG that was too big to be buffered: " + bytesToWrite +"\n");
									else
										m_bufPCM.put(m_bufConversion, 0, bytesToWrite);
									
									isWrote= true;
									m_objDspState.synthesis_read(bout); // tell libvorbis how many samples we
																		// actually consumed
								}
							}
						}

						if(m_objPage.eos() != 0)
							m_isEndOfBitStream= true;
						
						if((!m_isEndOfBitStream) && (isWrote))
							return;
					}
				}

				if(!m_isEndOfBitStream){
					m_nNbBytesRead= 0;

					int nIndex= m_objSyncState.buffer(4096);
					if(nIndex >= 0){
						m_bufTemp= m_objSyncState.data;

						try{
							m_nNbBytesRead= m_objISInput.read(m_bufTemp, nIndex, 4096);
						}catch(Exception e){
							System.out.print("\n***ERROR***\nFailure during vorbis decoding\n");
							m_isEndOfStream= true;
							return;
						}
					}else{
						m_nNbBytesRead= 0;
					}

					m_objSyncState.wrote(m_nNbBytesRead);

					if(m_nNbBytesRead == 0)
						m_isEndOfBitStream= true;
				}
			}

			// clean up this logical bitstream; before exit we see if we're
			// followed by another [chained]
			m_objStreamState.clear();

			// ogg_page and ogg_packet structs always point to storage in
			// libvorbis.  They're never freed or manipulated directly
			m_objVorbisBlock.clear();
			m_objDspState.clear();
			m_objOggInfo.clear();		// must be called last
		}

		// OK, clean up the framer
		m_objSyncState.clear();
		m_isEndOfStream= true;
	}
	
	public int read(){
		if(m_nIdxReadArray >= m_bufPCM.position()){
			m_bufPCM.clear();
			readPCM();
			m_nIdxReadArray = 0;
		}

		if(m_nIdxReadArray >= m_bufPCM.position())
			return -1;

		int nValue= m_bufPCM.get(m_nIdxReadArray);
		if(nValue < 0)
			nValue = 256 + nValue;

		m_nIdxReadArray++;
		return nValue;
	}

	public int read(byte[] b) throws IOException	{return read(b, 0, b.length);}
	public int read(byte[] b, int off, int len) throws IOException {
		
		for(int i= 0; i < len; ++i){
			int nValue= read();

			if(nValue >= 0) {
				b[i]= (byte)nValue;
			}else{
				if(i == 0)
					return -1;
				else
					return i;
			}
		}

		return len;
	}

	
	public void close() throws IOException {	}
}
