//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl;

//Standard Java imports
import java.nio.ByteBuffer;


public interface I_ImageData{
	public int getDepth();
	public int getWidth();
	public int getHeight();
	public int getTexWidth();
	public int getTexHeight();
	public ByteBuffer getImageBufferData();
}