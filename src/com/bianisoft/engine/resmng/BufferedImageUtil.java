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
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

//LWJGL library imports
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureMirrorClamp;
import org.lwjgl.opengl.GLContext;


// BufferedImageUtil
//		This is a utility class that allows you to convert a BufferedImage into atexture.
public class BufferedImageUtil{
	private static int createTextureID(){
		IntBuffer bufTmp= BufferUtils.createIntBuffer(1);
		glGenTextures(bufTmp);
		return bufTmp.get(0);
	}

	public static Texture getTexture(BufferedImage p_imgBuffered){
		BufferedImageData data= new BufferedImageData();
		int srcPixelFormat;

		// create the texture ID for this texture
		int textureID= createTextureID();
		Texture texture= new Texture(textureID);

		// Enable & bind the texturing
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, textureID);

		BufferedImage bufferedImage= p_imgBuffered;
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		srcPixelFormat= bufferedImage.getColorModel().hasAlpha()? GL_RGBA:GL_RGB;

		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer= data.imageToByteBuffer(bufferedImage, false, false, null);
		texture.setTextureHeight(data.getTexHeight());
		texture.setTextureWidth(data.getTexWidth());
		texture.setAlpha(data.getDepth() == 32);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		if(GLContext.getCapabilities().GL_EXT_texture_mirror_clamp){
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, EXTTextureMirrorClamp.GL_MIRROR_CLAMP_TO_EDGE_EXT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, EXTTextureMirrorClamp.GL_MIRROR_CLAMP_TO_EDGE_EXT);
		}else{
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		}

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, texture.getTextureWidth(), texture.getTextureHeight(), 0, srcPixelFormat, GL_UNSIGNED_BYTE, textureBuffer);
		return texture;
	}
}
