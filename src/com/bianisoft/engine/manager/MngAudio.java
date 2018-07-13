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
package com.bianisoft.engine.manager;


//Special static LWJGL library imports
import static org.lwjgl.openal.AL10.*;

//Standard Java imports
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

//LWJGL library imports
import com.bianisoft.engine.App;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.OpenALException;



//Bianisoft imports
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.audio.Music;
import com.bianisoft.engine.audio.Sound;


public final class MngAudio{
	private static MngAudio m_objMe;

	private Camera			m_camListener;
	private FloatBuffer		m_vPosListener;
	private FloatBuffer		m_vVelListener;
	private FloatBuffer		m_vOriListener;		// at.x, at.y, at.z, up.x, up.y, up.z
	private int[]			m_arChannels;
	private ArrayList<Music>	m_vecMusPlaying= new ArrayList<Music>();

	public MngAudio()				{m_objMe= this;}
	public static MngAudio get()	{return m_objMe;}


	public void setCameraListener(Camera p_cam){
		m_camListener= p_cam;
	}

	public void create(){
		try{
			AL.create();
			createChannels(64);

			m_vOriListener= BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
			m_vVelListener= BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
			m_vPosListener= BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});

			m_vPosListener.flip();
			m_vVelListener.flip();
			m_vOriListener.flip();

			alListener(AL_POSITION, m_vPosListener);
			alListener(AL_VELOCITY, m_vVelListener);
			alListener(AL_ORIENTATION, m_vOriListener);
		}catch(LWJGLException ex){
			ex.printStackTrace();
			System.out.print("***ERROR***\nError creating OpenAL\n");
		}
	}

	// App.createChannels
	//		Called by Context.activate to enable X amount of sound in one context.
	public void createChannels(int p_nNbSources){
		m_arChannels= new int[p_nNbSources];

		for(int i= 0; i < p_nNbSources; ++i){
			IntBuffer temp= BufferUtils.createIntBuffer(1);

			try{
				alGenSources(temp);
				if(alGetError() == AL_NO_ERROR)
					m_arChannels[i]= temp.get(0);
				
			}catch(OpenALException e){			// expected at the end
				break;
			}
		}
	}

	public int findFreeChannel(){
		for (int i= 0 ; i < m_arChannels.length; ++i){
			int nState= alGetSourcei(m_arChannels[i], AL_SOURCE_STATE);

			if((nState != AL_PLAYING) && (nState != AL_PAUSED))
				return i;
		}

		return -1;
	}

	public void manageListerner(){
		m_vPosListener.clear();							m_vVelListener.clear();							m_vOriListener.clear();
		m_vPosListener.put(m_camListener.getPosArray());m_vVelListener.put(m_camListener.getVelArray());m_vOriListener.put(m_camListener.getOrientation());
		m_vPosListener.flip();							m_vVelListener.flip();							m_vOriListener.flip();
		alListener(AL_POSITION, m_vPosListener);
	}

	public int playSound(Sound p_snd){
		int idChannel= findFreeChannel();
		if(idChannel == -1)
			return -1;

		alSourceStop(m_arChannels[idChannel]);
		alSourcei(m_arChannels[idChannel], AL_BUFFER, p_snd.getIDOpenAL());

		manageListerner();

		alSourcef(m_arChannels[idChannel], AL_REFERENCE_DISTANCE, App.g_nWidth/2);
		alSource(m_arChannels[idChannel], AL_POSITION, p_snd.getPosEmitter());

		alSourcePlay(m_arChannels[idChannel]);
		return idChannel;
	}

	public int playMusicOGG(Music p_music, boolean p_isLooping){
		int idChannel= findFreeChannel();
		if(idChannel == -1)
			return -1;

		alSourceStop(m_arChannels[idChannel]);
		m_vecMusPlaying.add(p_music);

		return m_arChannels[idChannel];
	}

	public void stopSound(Sound p_snd){
		int nPlayingChannel= p_snd.getPlayingChannel();

		if(nPlayingChannel != -1)
			alSourceStop(nPlayingChannel);
	}

	public void stopMusic(Music p_music){
		int nPlayingChannel= p_music.getPlayingChannel();

		if(nPlayingChannel != -1)
			alSourceStop(nPlayingChannel);

		m_vecMusPlaying.remove(p_music);
	}

	public void manage(float p_nScaleTime){
		for(Music mus : m_vecMusPlaying)
			if(mus != null)
				mus.update();
	}
}
