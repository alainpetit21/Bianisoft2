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


//Special static LWJGL library imports
import java.io.IOException;
import static org.lwjgl.openal.AL10.*;

//Standard Java imports
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

//LWJGL library imports
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.OpenALException;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.PhysObj;
import com.bianisoft.engine.helper.FixResFilename;
import com.bianisoft.engine.manager.MngAudio;


public class Music extends PhysObj{
	private static final int NB_BUFFER= 8;
	private static final int SECTION_SIZE= 4096 * 20;

	private String		m_stResSong;
	private int			m_nIDOpenAL;
	private int			m_nIDPlayingChannel	= -1;
	private boolean		m_isLooping;
	private float		m_nPositionOffset;
	private boolean		m_isDone= true;
	private boolean		m_isPlaying= false;

	private ByteBuffer	m_bufOpenALData	= BufferUtils.createByteBuffer(SECTION_SIZE);
	private IntBuffer	m_bufUnqueuedNames= BufferUtils.createIntBuffer(1);	//Names that been fully played
	private IntBuffer	m_bufOpenALNames;
	private byte[]		m_bufRead= new byte[SECTION_SIZE];
	private int			m_nRemainingBuffer;

	private OggInputStream	m_objOggStream;
	private URL				m_objURL;


	public Music(String p_stResModule){
		super(IDCLASS_Music);
		m_stResSong= p_stResModule;
	}

	public int getIDOpenAL()			{return m_nIDOpenAL;}
	public int getPlayingChannel()		{return m_nIDPlayingChannel;}

	public boolean isPlaying()			{return m_isPlaying;}

	public boolean isDone(){
		return (alGetSourcei(m_nIDPlayingChannel, AL_SOURCE_STATE) != AL_PLAYING) && m_isDone;
	}

	private void initStreams(){
		try{
			if(m_objOggStream != null)
				m_objOggStream.close();

			m_objOggStream= new OggInputStream(m_objURL.openStream());
			m_nPositionOffset= 0;
		}catch(IOException ex){
			System.out.printf("***ERROR***\nError while loading Ogg Stream: " +m_stResSong);
			ex.printStackTrace();
			App.exit();
		}
	}

	private void removeBuffers() {
		IntBuffer buffer= BufferUtils.createIntBuffer(1);
		int nQueued= alGetSourcei(m_nIDPlayingChannel, AL_BUFFERS_QUEUED);

		while(nQueued > 0){
			alSourceUnqueueBuffers(m_nIDPlayingChannel, buffer);
			nQueued--;
		}
	}

	public void play()	{play(m_isLooping);}
	public void play(boolean p_isLooping){
		m_isLooping= p_isLooping;

		m_nIDPlayingChannel= MngAudio.get().playMusicOGG(this, p_isLooping);

		initStreams();

		m_isPlaying= true;
		m_isDone= false;

		alSourceStop(m_nIDPlayingChannel);
		removeBuffers();

		startPlayback();
	}


	public void update(){
		if(m_isDone)
			return;

		float nSampleRate= m_objOggStream.getRate();
		float nSampleSize= m_objOggStream.getChannels() * 2;

		int nProcessed= alGetSourcei(m_nIDPlayingChannel, AL_BUFFERS_PROCESSED);

		while(nProcessed > 0){
			m_bufUnqueuedNames.clear();
			alSourceUnqueueBuffers(m_nIDPlayingChannel, m_bufUnqueuedNames);

			int nIdxBuffer= m_bufUnqueuedNames.get(0);

			float nBufferLength= (alGetBufferi(nIdxBuffer, AL_SIZE) / nSampleSize) / nSampleRate;
			m_nPositionOffset+= nBufferLength;

	        if(stream(nIdxBuffer)){
	        	alSourceQueueBuffers(m_nIDPlayingChannel, m_bufUnqueuedNames);
			}else{
	        	m_nRemainingBuffer--;
	        	if(m_nRemainingBuffer == 0)
	        		m_isDone= true;
	        }

	        nProcessed--;
		}


		int nState= alGetSourcei(m_nIDPlayingChannel, AL_SOURCE_STATE);

		//Update the Gain, appropriatly to music position
		float nGain= ((App.g_nWidth/2)*(App.g_nWidth/2))-((getPosX()*getPosX())+(getPosY()*getPosY()));
		nGain= nGain / ((App.g_nWidth/2)*(App.g_nWidth/2));
		nGain= Math.min(1, nGain);
		nGain= Math.max(0, nGain);
		alSourcef(m_nIDPlayingChannel, AL_GAIN, nGain);
		
	    if(nState != AL_PLAYING)
			alSourcePlay(m_nIDPlayingChannel);
	}

	public boolean stream(int p_nIdBuffer){
		try{
			int nCount= m_objOggStream.read(m_bufRead);

			if(nCount != -1){
				m_bufOpenALData.clear();
				m_bufOpenALData.put(m_bufRead, 0, nCount);
				m_bufOpenALData.flip();

				int nFormat= (m_objOggStream.getChannels() > 1)? AL_FORMAT_STEREO16:AL_FORMAT_MONO16;
				try{
					alBufferData(p_nIdBuffer, nFormat, m_bufOpenALData, m_objOggStream.getRate());
				}catch(OpenALException e){
					System.out.print("\n****ERROR***\nFailed to loop buffer: "+p_nIdBuffer+" "+nFormat+" "+nCount+" "+m_objOggStream.getRate()+ "\n");
					return false;
				}
			}else{
				if(m_isLooping){
					initStreams();
					stream(p_nIdBuffer);
				}else{
					m_isDone= true;
					return false;
				}
			}

			return true;
		}catch(IOException e){
			System.out.print(e);
			return false;
		}
	}

	public boolean setPosition(float p_nPosition){
		try{
			if(getPosition() > p_nPosition)
				initStreams();

			float nSampleRate= m_objOggStream.getRate();
			float nSampleSize= m_objOggStream.getChannels() * 2;

			while(m_nPositionOffset < p_nPosition){
				int nCount= m_objOggStream.read(m_bufRead);

				if(nCount != -1){
					float nBufferLength= (nCount / nSampleSize) / nSampleRate;
					m_nPositionOffset+= nBufferLength;

				}else{
					if(m_isLooping)
						initStreams();
					else
						m_isDone= true;

					return false;
				}
			}

			startPlayback();

			return true;
		}catch(IOException e){
			System.out.print(e);
			return false;
		}
	}

	private void startPlayback(){
		alSourcei(m_nIDPlayingChannel, AL_LOOPING, AL_FALSE);

		m_nRemainingBuffer= NB_BUFFER;

		for(int i= 0; i < NB_BUFFER; ++i)
			stream(m_bufOpenALNames.get(i));

		//Update the Gain, appropriatly to music position
		float nGain= ((App.g_nWidth/2)*(App.g_nWidth/2))-((getPosX()*getPosX())+(getPosY()*getPosY()));
		nGain= nGain / ((App.g_nWidth/2)*(App.g_nWidth/2));
		nGain= Math.min(1, nGain);
		nGain= Math.max(0, nGain);
		alSourcef(m_nIDPlayingChannel, AL_GAIN, nGain);

		alSourceQueueBuffers(m_nIDPlayingChannel, m_bufOpenALNames);
		alSourcePlay(m_nIDPlayingChannel);
	}

	public float getPosition(){
		return m_nPositionOffset + alGetSourcef(m_nIDPlayingChannel, AL11.AL_SEC_OFFSET);
	}

	public void load(){
		try{
			m_stResSong= FixResFilename.fixResFilename(m_stResSong);
			m_objURL= Thread.currentThread().getContextClassLoader().getResource(m_stResSong);

			m_bufOpenALNames= BufferUtils.createIntBuffer(NB_BUFFER);
			alGenBuffers(m_bufOpenALNames);
		}catch(Exception ex){
			System.out.printf("***ERROR***\nError while loading:%s", m_stResSong);
			ex.printStackTrace();
			App.exit();
		}
	}

	
	public void stop(){
		m_isPlaying= false;
		MngAudio.get().stopMusic(this);
	}
}
