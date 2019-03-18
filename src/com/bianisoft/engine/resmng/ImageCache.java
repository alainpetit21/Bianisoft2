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
import static org.lwjgl.opengl.GL11.*;

//Standard Java imports
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//LWJGL library imports
import org.lwjgl.BufferUtils;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.helper.FixResFilename;


public final class ImageCache{
	private static	Map<String, Texture> m_mapCache= new ConcurrentHashMap<String, Texture>();

	private static int createTextureID(){
		IntBuffer bufTmp= BufferUtils.createIntBuffer(1);
		glGenTextures(bufTmp);
		return bufTmp.get(0);
	}

	public static Texture loadImage(String p_stRessource){
		Texture texture;
		
		if(p_stRessource == null){
			System.out.printf("***ERROR***\nError while loading null image\n");
			App.exit();
		}
		
		p_stRessource= FixResFilename.fixResFilename(p_stRessource);
		if((texture= m_mapCache.get(p_stRessource)) != null)
			return texture;


		try{
			//InputStream objIS= Thread.currentThread().getContextClassLoader().getResourceAsStream(p_stRessource);
			FileInputStream objIS= new FileInputStream("res/"+p_stRessource);
			
			texture= new Texture(createTextureID());

			PNGImageData objPNGImageData= new PNGImageData();
			ByteBuffer textureBuffer= objPNGImageData.loadImage(objIS);

			texture.setTextureWidth(objPNGImageData.getTexWidth());
			texture.setTextureHeight(objPNGImageData.getTexHeight());

			IntBuffer temp= BufferUtils.createIntBuffer(16);
			glGetInteger(GL_MAX_TEXTURE_SIZE, temp);
			int nMaxSize= temp.get(0);

			if((texture.getTextureWidth() > nMaxSize) || (texture.getTextureHeight() > nMaxSize))
				System.out.print("***ERROR***\nImageCache.loadImage: Attempt to allocate a texture to big for the current hardware\n");

			boolean hasAlpha= (objPNGImageData.getDepth() == 32);
			int nSrcPixelFormat= (hasAlpha)? GL_RGBA:GL_RGB;

			texture.setWidth(objPNGImageData.getWidth());
			texture.setHeight(objPNGImageData.getHeight());
			texture.setAlpha(hasAlpha);

			texture.bind();
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, objPNGImageData.getTexWidth(), objPNGImageData.getTexHeight(), 0, nSrcPixelFormat, GL_UNSIGNED_BYTE, textureBuffer);

			objIS.close();
		}catch(Exception e1){
			System.out.printf("***ERROR***\nError while loading: %s", p_stRessource);
			e1.printStackTrace();
			App.exit();
		}

		m_mapCache.put(p_stRessource, texture);
		return texture;
	}
}

