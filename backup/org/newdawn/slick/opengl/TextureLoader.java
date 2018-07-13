//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl;

//Standard Java import
import java.io.IOException;
import java.io.InputStream;

//LWJGL import
import org.lwjgl.opengl.GL11;


public class TextureLoader {
	public static I_Texture getTexture(String p_stFormat, InputStream p_is) throws IOException{
		return getTexture(p_stFormat, p_is, false, GL11.GL_LINEAR);
	}

	public static I_Texture getTexture(String p_stFormat, InputStream p_is, boolean p_isFlipped)  throws IOException{
		return getTexture(p_stFormat, p_is, p_isFlipped, GL11.GL_LINEAR);
	}

	public static I_Texture getTexture(String p_stFormat, InputStream p_is, int p_nFilter) throws IOException{
		return getTexture(p_stFormat, p_is, false, p_nFilter);
	}

	public static I_Texture getTexture(String p_stFormat, InputStream p_isIn, boolean p_isFlipped, int p_nFilter) throws IOException{
		return InternalTextureLoader.get().getTexture(p_isIn, p_isIn.toString()+"."+p_stFormat, p_isFlipped, p_nFilter);
	}
}
