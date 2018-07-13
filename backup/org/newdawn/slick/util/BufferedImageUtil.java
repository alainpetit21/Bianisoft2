//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.util;

//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;

//Standard Java imports
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

//LWJGL library imports
import org.lwjgl.opengl.EXTTextureMirrorClamp;
import org.lwjgl.opengl.GLContext;

//Slick-utils library imports
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.I_Texture;
import org.newdawn.slick.opengl.TextureImpl;


// BufferedImageUtil
//		This is a utility class that allows you to convert a BufferedImage into atexture.
public class BufferedImageUtil{
	public static I_Texture getTexture(String p_stFilename, BufferedImage p_imgBuffered) throws IOException{
		return getTexture(p_stFilename, p_imgBuffered, GL_TEXTURE_2D, GL_RGBA8, GL_LINEAR, GL_LINEAR);
	}

	public static I_Texture getTexture(String p_stFilename, BufferedImage p_imgBuffered, int filter) throws IOException{
		return getTexture(p_stFilename, p_imgBuffered, GL_TEXTURE_2D, GL_RGBA8, filter, filter);
	}
	
	public static I_Texture getTexture(String p_stFilename, BufferedImage p_imgBuffered, int p_nTarget, int p_nDstPixelFormat, int p_nMinFilter, int p_nMagFilter) throws IOException{
		ImageIOImageData data= new ImageIOImageData();
		int srcPixelFormat= 0;

		// create the texture ID for this texture
		int textureID= InternalTextureLoader.createTextureID();
		TextureImpl texture= new TextureImpl(p_stFilename, p_nTarget, textureID);

		// Enable texturing
		glEnable(GL_TEXTURE_2D);

		// bind this texture
		glBindTexture(p_nTarget, textureID);

		BufferedImage bufferedImage= p_imgBuffered;
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		srcPixelFormat= bufferedImage.getColorModel().hasAlpha()? GL_RGBA:GL_RGB;

		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer= data.imageToByteBuffer(bufferedImage, false, false, null);
		texture.setTextureHeight(data.getTexHeight());
		texture.setTextureWidth(data.getTexWidth());
		texture.setAlpha(data.getDepth() == 32);
		
		if(p_nTarget == GL_TEXTURE_2D){
			glTexParameteri(p_nTarget, GL_TEXTURE_MIN_FILTER, p_nMinFilter);
			glTexParameteri(p_nTarget, GL_TEXTURE_MAG_FILTER, p_nMagFilter);
			
	        if (GLContext.getCapabilities().GL_EXT_texture_mirror_clamp) {
	        	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, EXTTextureMirrorClamp.GL_MIRROR_CLAMP_TO_EDGE_EXT);
	            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, EXTTextureMirrorClamp.GL_MIRROR_CLAMP_TO_EDGE_EXT);
	        } else {
	            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	        }
		}

        glTexImage2D(p_nTarget, 0, p_nDstPixelFormat, texture.getTextureWidth(), texture.getTextureHeight(),
                      0, srcPixelFormat, GL_UNSIGNED_BYTE, textureBuffer); 

		return texture;
	}
}
