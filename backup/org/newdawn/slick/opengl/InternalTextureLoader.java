//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl;


//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;

//Standard Java import
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

//LWJGL import
import org.lwjgl.BufferUtils;

//Slick-util library import
import org.newdawn.slick.util.ResourceLoader;


public class InternalTextureLoader{
	private static final InternalTextureLoader m_objLoader= new InternalTextureLoader();

	public static InternalTextureLoader get()	{return m_objLoader;}


	private HashMap m_mapTexLinear= new HashMap();
	private HashMap m_mapTexNearest= new HashMap();
	private int m_nDstPixelFormat= GL_RGBA8;
	private boolean m_isDeferred;

	private InternalTextureLoader()	{	}

	public void setDeferredLoading(boolean p_isDeferred){
		m_isDeferred= p_isDeferred;
	}

	public boolean isDeferredLoading() {
		return m_isDeferred;
	}

	public void clear(String name) {
		m_mapTexLinear.remove(name);
		m_mapTexNearest.remove(name);
	}

	public void clear() {
		m_mapTexLinear.clear();
		m_mapTexNearest.clear();
	}

	public void set16BitMode() {
		m_nDstPixelFormat = GL_RGBA16;
	}

	public static int createTextureID(){
		IntBuffer bufTmp= createIntBuffer(1);
		glGenTextures(bufTmp);

		return bufTmp.get(0);
	}

	public I_Texture getTexture(File p_fileSource, boolean p_isFlipped, int p_nFilter) throws IOException{
		String stResourceName= p_fileSource.getAbsolutePath();
		InputStream is= new FileInputStream(p_fileSource);

		return getTexture(is, stResourceName, p_isFlipped, p_nFilter, null);
	}

	public I_Texture getTexture(File p_fileSource, boolean p_isFlipped, int p_nFilter, int[] p_bufColorTransparent) throws IOException{
		String stResourceName= p_fileSource.getAbsolutePath();
		InputStream is= new FileInputStream(p_fileSource);

		return getTexture(is, stResourceName, p_isFlipped, p_nFilter, p_bufColorTransparent);
	}

	public I_Texture getTexture(String p_stFileName, boolean p_isFlipped, int p_nFilter) throws IOException{
		InputStream is= ResourceLoader.getResourceAsStream(p_stFileName);

		return getTexture(is, p_stFileName, p_isFlipped, p_nFilter, null);
	}

	public I_Texture getTexture(String p_stFileName, boolean p_isFlipped, int p_nFilter, int[] p_bufColorTransparent) throws IOException{
		InputStream in= ResourceLoader.getResourceAsStream(p_stFileName);

		return getTexture(in, p_stFileName, p_isFlipped, p_nFilter, p_bufColorTransparent);
	}

	public I_Texture getTexture(InputStream p_is, String p_stResName, boolean p_isFlipped, int p_nFilter) throws IOException {
		return getTexture(p_is, p_stResName, p_isFlipped, p_nFilter, null);
	}

	public TextureImpl getTexture(InputStream p_is, String p_stResName, boolean p_isFlipped, int p_nFilter, int[] p_bufColorTransparent) throws IOException {
		if(m_isDeferred)
			return new DeferredTexture(p_is, p_stResName, p_isFlipped, p_nFilter, p_bufColorTransparent);

		HashMap hash= m_mapTexLinear;
		if(p_nFilter == GL_NEAREST)
			hash= m_mapTexNearest;

		String stResName= p_stResName;
		if(p_bufColorTransparent != null)
			stResName+= ":" + p_bufColorTransparent[0] +":"+ p_bufColorTransparent[1] +":"+ p_bufColorTransparent[2];

		stResName+= ":" + p_isFlipped;

		SoftReference ref= (SoftReference)hash.get(stResName);
		if(ref != null){
			TextureImpl tex= (TextureImpl) ref.get();

			if(tex != null)
				return tex;
			else
				hash.remove(stResName);
		}

		//horrible test until I can find something more suitable	//apetit - not so bad
		try{
			glGetError();
		}catch (NullPointerException e){
			throw new RuntimeException("Image based resources must be loaded as part of init() or the game loop. They cannot be loaded before initialisation.");
		}

		TextureImpl tex= getTexture(p_is, p_stResName, GL_TEXTURE_2D, p_nFilter, p_nFilter, p_isFlipped, p_bufColorTransparent);

		tex.setCacheName(stResName);
		hash.put(stResName, new SoftReference(tex));

		return tex;
	}

	private TextureImpl getTexture(InputStream p_is, String p_nResourceName, int p_nTarget, int p_nMagFilter, int p_nMinFilter,	boolean p_isFlipped, int[] p_bufColorTransparent) throws IOException{
		// create the texture ID for this texture
		int textureID= createTextureID();
		TextureImpl texture= new TextureImpl(p_nResourceName, p_nTarget, textureID);

		// bind this texture
		glBindTexture(p_nTarget, textureID);

		LoadableImageData imageData= ImageDataFactory.getImageDataFor(p_nResourceName);
		ByteBuffer textureBuffer= imageData.loadImage(new BufferedInputStream(p_is), p_isFlipped, p_bufColorTransparent);

		texture.setTextureWidth(imageData.getTexWidth());
		texture.setTextureHeight(imageData.getTexHeight());

		IntBuffer temp= BufferUtils.createIntBuffer(16);
		glGetInteger(GL_MAX_TEXTURE_SIZE, temp);
		int nMaxSize= temp.get(0);

		if((texture.getTextureWidth() > nMaxSize) || (texture.getTextureHeight() > nMaxSize))
			throw new IOException("Attempt to allocate a texture to big for the current hardware");

		boolean hasAlpha= (imageData.getDepth() == 32);
		int nSrcPixelFormat= (hasAlpha)? GL_RGBA:GL_RGB;

		texture.setWidth(imageData.getWidth());
		texture.setHeight(imageData.getHeight());
		texture.setAlpha(hasAlpha);

		glTexParameteri(p_nTarget, GL_TEXTURE_MIN_FILTER, p_nMinFilter);
		glTexParameteri(p_nTarget, GL_TEXTURE_MAG_FILTER, p_nMagFilter);

		// produce a texture from the byte buffer
		glTexImage2D(p_nTarget, 0, m_nDstPixelFormat, imageData.getTexWidth(), imageData.getTexHeight(), 0, nSrcPixelFormat, GL_UNSIGNED_BYTE, textureBuffer);

		return texture;
	}

	public I_Texture createTexture(int p_nWidth, int p_nHeight) throws IOException{
		return createTexture(p_nWidth, p_nHeight, GL_NEAREST);
	}

	public I_Texture createTexture(final int p_nWidth, final int p_nHeight, final int p_nFilter) throws IOException{
		I_ImageData ds= new EmptyImageData(p_nWidth, p_nHeight);

		return getTexture(ds, p_nFilter);
	}

	public I_Texture getTexture(I_ImageData p_dataSource, int p_nFilter) throws IOException{
		int target= GL_TEXTURE_2D;

		// create the texture ID for this texture
		int textureID= createTextureID();
		TextureImpl texture= new TextureImpl("generated:"+p_dataSource, target ,textureID);

		int minFilter= p_nFilter;
		int magFilter= p_nFilter;
		boolean flipped= false;

		// bind this texture
		glBindTexture(target, textureID);

		ByteBuffer textureBuffer= p_dataSource.getImageBufferData();
		int nWidth= p_dataSource.getWidth();
		int nHeight= p_dataSource.getHeight();
		boolean hasAlpha= (p_dataSource.getDepth() == 32);

		texture.setTextureWidth(p_dataSource.getTexWidth());
		texture.setTextureHeight(p_dataSource.getTexHeight());

		int nTexWidth= texture.getTextureWidth();
		int nTexHeight= texture.getTextureHeight();

		int srcPixelFormat= (hasAlpha)? GL_RGBA:GL_RGB;
		int componentCount= (hasAlpha)? 4:3;

		texture.setWidth(nWidth);
		texture.setHeight(nHeight);
		texture.setAlpha(hasAlpha);

		IntBuffer temp= BufferUtils.createIntBuffer(16);
		glGetInteger(GL_MAX_TEXTURE_SIZE, temp);
		int max= temp.get(0);
		if((nTexWidth > max) || (nTexHeight > max))
			throw new IOException("Attempt to allocate a texture to big for the current hardware");

		glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
		glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);

		// produce a texture from the byte buffer
		glTexImage2D(target, 0, m_nDstPixelFormat, get2Fold(nWidth), get2Fold(nHeight), 0, srcPixelFormat, GL_UNSIGNED_BYTE, textureBuffer);

		return texture;
	}

	public static int get2Fold(int fold){
		int ret= 2;
		
		while(ret < fold)
			ret*= 2;

		return ret;
	}

	public static IntBuffer createIntBuffer(int size){
		ByteBuffer temp= ByteBuffer.allocateDirect(4 * size);
		temp.order(ByteOrder.nativeOrder());

		return temp.asIntBuffer();
	}
}
