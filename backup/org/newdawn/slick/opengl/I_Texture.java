//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl;


public interface I_Texture{
	public void bind();
	public void release();

	public String	getTextureRef();
	public int		getImageHeight();
	public int		getImageWidth();
	public float	getHeight();
	public float	getWidth();
	public int		getTextureHeight();
	public int		getTextureWidth();
	public int		getTextureID();
	public boolean	hasAlpha();

	public byte[]	getTextureData();
}