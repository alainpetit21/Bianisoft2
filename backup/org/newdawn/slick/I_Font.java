//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick;


public interface I_Font{
	public abstract int getWidth(String str);
	public abstract int getHeight(String str);
	public int getLineHeight();
	
	public abstract void drawString(float x, float y, String text);
	public abstract void drawString(float x, float y, String text, Color col);
	public abstract void drawString(float x, float y, String text, Color col, int startIndex, int endIndex);
}