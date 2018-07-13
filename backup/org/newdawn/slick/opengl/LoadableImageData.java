//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl;

//Standard Java imports
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public interface LoadableImageData extends I_ImageData{
	public void configureEdging(boolean edging);
	
	public ByteBuffer loadImage(InputStream fis) throws IOException;
	public ByteBuffer loadImage(InputStream fis, boolean flipped, int[] transparent) throws IOException;
	public ByteBuffer loadImage(InputStream fis, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException;
}
