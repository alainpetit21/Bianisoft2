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
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

//LWJGL library imports
import org.lwjgl.BufferUtils;


public class Texture{
	private static Texture m_texLastBind;


	private int m_nIDtexture;
	private int m_nHeight;
	private int m_nWidth;
	private int m_nTexWidth;
	private int m_nTexHeight;
	private float m_nWidthRatio;
	private float m_nHeightRatio;
	private boolean m_hasAlpha;


	public static Texture	getLastBind()	{return m_texLastBind;}
	public static void		unbind()		{m_texLastBind= null;}

	public static void bindNone(){
		m_texLastBind= null;
		glDisable(GL_TEXTURE_2D);
	}


	public Texture()	{	}
	public Texture(int p_nIDTexture){
		m_nIDtexture= p_nIDTexture;
	}
    
	public void setAlpha(boolean p_hasAlpha)		{m_hasAlpha= p_hasAlpha;}

	public void setHeight(int p_nHeight)			{m_nHeight= p_nHeight; setHeight();}
	public void setWidth(int p_nWidth)				{m_nWidth= p_nWidth; setWidth();}

	public void setTextureHeight(int p_nTexHeight)	{m_nTexHeight= p_nTexHeight; setHeight();}
	public void setTextureWidth(int p_nTexWidth)	{m_nTexWidth= p_nTexWidth; setWidth();}
	public void setTextureID(int textureID)			{m_nIDtexture = textureID;}

	private void setHeight(){
		if(m_nTexHeight != 0)
			m_nHeightRatio= ((float) m_nHeight)/m_nTexHeight;
	}

	private void setWidth(){
		if(m_nTexWidth != 0)
			m_nWidthRatio= ((float) m_nWidth)/m_nTexWidth;
	}

	public boolean hasAlpha()		{return m_hasAlpha;}
	public int getImageHeight()		{return m_nHeight;}
	public int getImageWidth()		{return m_nWidth;}
	public float getHeight()		{return m_nHeightRatio;}
	public float getWidth()			{return m_nWidthRatio;}
	public int getTextureHeight()	{return m_nTexHeight;}
	public int getTextureWidth()	{return m_nTexWidth;}
	public int getTextureID()		{return m_nIDtexture;}

	public void bind(){
		if(m_texLastBind != this){
			m_texLastBind= this;
			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, m_nIDtexture);
		}
	}

	public void release(){
		if(m_texLastBind == this)
			bindNone();

		IntBuffer texBuf= BufferUtils.createIntBuffer(1);
		texBuf.put(m_nIDtexture);
		texBuf.flip();

		glDeleteTextures(texBuf);
	}
    
	
	public byte[] getTextureData(){
		ByteBuffer buffer= BufferUtils.createByteBuffer((hasAlpha() ? 4 : 3) * m_nTexWidth * m_nTexHeight);
		bind();

		glGetTexImage(GL_TEXTURE_2D, 0, hasAlpha()? GL_RGBA:GL_RGB, GL_UNSIGNED_BYTE, buffer);

		byte[] data= new byte[buffer.limit()];
		buffer.get(data);
		buffer.clear();

		return data;
	}
}
