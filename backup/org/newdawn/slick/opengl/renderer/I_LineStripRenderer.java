//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl.renderer;


public interface I_LineStripRenderer {
	public abstract boolean applyGLLineFixes();
	public abstract void start();
	public abstract void end();
	public abstract void vertex(float x, float y);
	public abstract void color(float r, float g, float b, float a);
	public abstract void setWidth(float width);
	public abstract void setAntiAlias(boolean antialias);
	public void setLineCaps(boolean caps);
}