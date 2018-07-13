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
import static org.lwjgl.openal.AL10.*;

//Standard Java imports
import java.nio.FloatBuffer;

//LWJGL library imports
import org.lwjgl.BufferUtils;

//Bianisoft imports
import com.bianisoft.engine.PhysObj;
import com.bianisoft.engine.manager.MngAudio;
import com.bianisoft.engine.resmng.SoundCache;


public class Sound extends PhysObj{
	private int		m_nIDOpenAL;
	private int		m_nIDPlayingChannel= -1;

	private String		m_stResSound;
	private FloatBuffer	m_vPosEmitter;
	private int			m_nNbBytes;
	private int			m_nNbBits;
	private int			m_nNbChannels;
	private int			m_nFreq;
	private int			m_nNbSamples;
	private float		m_nLength;


	public Sound(String p_stResSound){
		super(IDCLASS_Sound);
		m_stResSound= p_stResSound;
	}

	public int getIDOpenAL()			{return m_nIDOpenAL;}
	public int getPlayingChannel()		{return m_nIDPlayingChannel;}
	public FloatBuffer getPosEmitter()	{return m_vPosEmitter;}
	
	public void load(){
		m_nIDOpenAL= SoundCache.getAudioWAV(m_stResSound);

		m_nNbBytes		= alGetBufferi(m_nIDOpenAL, AL_SIZE);
		m_nNbBits		= alGetBufferi(m_nIDOpenAL, AL_BITS);
		m_nNbChannels	= alGetBufferi(m_nIDOpenAL, AL_CHANNELS);
		m_nFreq			= alGetBufferi(m_nIDOpenAL, AL_FREQUENCY);

		m_nNbSamples= m_nNbBytes / (m_nNbBits / 8);
		m_nLength	= (m_nNbSamples / (float) m_nFreq) / m_nNbChannels;

		m_vPosEmitter= BufferUtils.createFloatBuffer(3).put(getPosArray());
	}

	public void play(){
		//Update the Float Buffer Position before playing
		m_vPosEmitter.clear();
		m_vPosEmitter.put(getPosArray());
		m_vPosEmitter.flip();

		//Reminder is managed by AudioManager
		m_nIDPlayingChannel= MngAudio.get().playSound(this);
	}

	public void stop(){
		MngAudio.get().stopSound(this);
	}

	public boolean isPlaying(){
		return m_nIDPlayingChannel != -1;
	}
}
