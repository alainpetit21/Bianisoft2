//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl;


//Standard Java imports
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

//Slick-utils library imports
import org.newdawn.slick.util.Log;


public class CompositeImageData implements LoadableImageData{
	private ArrayList m_arSources= new ArrayList();
	private LoadableImageData m_dataPicked;
	
	public void add(LoadableImageData data) {
		m_arSources.add(data);
	}
	
	public ByteBuffer loadImage(InputStream fis) throws IOException {
		return loadImage(fis, false, null);
	}

	public ByteBuffer loadImage(InputStream p_fis, boolean p_isFlipped, int[] m_arColorTransparent) throws IOException{
		return loadImage(p_fis, p_isFlipped, false, m_arColorTransparent);
	}

	public ByteBuffer loadImage(InputStream p_fis, boolean p_isFlipped, boolean p_isForceAlpha, int[] p_arColorTransparent) throws IOException{
		CompositeIOException exception= new CompositeIOException();
		ByteBuffer buffer= null;
		
		BufferedInputStream in= new BufferedInputStream(p_fis, p_fis.available());
		in.mark(p_fis.available());
		
		// cycle through our source until one of them works
		for (int i= 0; i < m_arSources.size(); i++){
			in.reset();
			try{
				LoadableImageData data= (LoadableImageData) m_arSources.get(i);
				
				buffer= data.loadImage(in, p_isFlipped, p_isForceAlpha, p_arColorTransparent);
				m_dataPicked= data;
				break;
			}catch(Exception e){
				Log.warn(m_arSources.get(i).getClass()+" failed to read the data", e);
				exception.addException(e);
			}
		}
		
		if(m_dataPicked == null)
			throw exception;
		
		return buffer;
	}

	/**
	 * Check the state of the image data and throw a
	 * runtime exception if theres a problem
	 */
	private void checkPicked() {
		if (m_dataPicked == null) {
			throw new RuntimeException("Attempt to make use of uninitialised or invalid composite image data");
		}
	}
	
	/**
	 * @see org.newdawn.slick.opengl.ImageData#getDepth()
	 */
	public int getDepth() {
		checkPicked();
		
		return m_dataPicked.getDepth();
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getHeight()
	 */
	public int getHeight() {
		checkPicked();
		
		return m_dataPicked.getHeight();
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getImageBufferData()
	 */
	public ByteBuffer getImageBufferData() {
		checkPicked();
		
		return m_dataPicked.getImageBufferData();
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getTexHeight()
	 */
	public int getTexHeight() {
		checkPicked();
		
		return m_dataPicked.getTexHeight();
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getTexWidth()
	 */
	public int getTexWidth() {
		checkPicked();
		
		return m_dataPicked.getTexWidth();
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getWidth()
	 */
	public int getWidth() {
		checkPicked();
		
		return m_dataPicked.getWidth();
	}

	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#configureEdging(boolean)
	 */
	public void configureEdging(boolean edging) {
		for (int i=0;i<m_arSources.size();i++) {
			((LoadableImageData) m_arSources.get(i)).configureEdging(edging);
		}
	}

}
