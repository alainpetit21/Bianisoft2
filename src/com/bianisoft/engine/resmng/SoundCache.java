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


//Special static LWJGL library imports
import static org.lwjgl.openal.AL10.*;

//Standard Java imports
import java.io.InputStream;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//LWJGL library imports
import org.lwjgl.BufferUtils;
import org.lwjgl.util.WaveData;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.helper.datatypes.Int;
import com.bianisoft.engine.helper.FixResFilename;


public final class SoundCache{
	private static Map<String, Int> m_mapCache= new ConcurrentHashMap<String, Int>();

	public static int getAudioWAV(String p_stRessource){
		Int nIDWav;
		URL objURL= null;
		InputStream objIS= null;

		if((nIDWav= m_mapCache.get(p_stRessource)) != null)
			return nIDWav.get();

		IntBuffer buf= BufferUtils.createIntBuffer(1);
		try{
			p_stRessource= FixResFilename.fixResFilename(p_stRessource);
			objIS= Thread.currentThread().getContextClassLoader().getResourceAsStream(p_stRessource);
			objURL= Thread.currentThread().getContextClassLoader().getResource(p_stRessource);

			WaveData data= WaveData.create(objIS);
			if(data == null){
				System.out.print("\n***WARNING***\nLoading the Wav file with InputStream failed, trying with URL;\n");

				data= WaveData.create(objURL);
				if(data == null){
					System.out.print("\n***ERROR***\nLoading Wavfile failed with both techniques, expect a crash!\n");
				}
			}

			alGenBuffers(buf);
			alBufferData(buf.get(0), data.format, data.data, data.samplerate);

			m_mapCache.put(p_stRessource, new Int(buf.get(0)));
			return buf.get(0);

		}catch(Exception e1){
			System.out.print("***ERROR***\nError while loading: " +p_stRessource+ ", " +objIS+ ", " +objURL+ "\n");
			e1.printStackTrace();
			App.exit();
		}

		return 0;
	}
}
