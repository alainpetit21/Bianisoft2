//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl;


//Standard Java imports
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

//LWJGL imports
import org.lwjgl.BufferUtils;

//Slick-utils imports
import org.newdawn.slick.opengl.renderer.I_SGL;
import org.newdawn.slick.opengl.renderer.Renderer;


public class TextureImpl implements I_Texture{
	protected static I_SGL GL= Renderer.get();

	static I_Texture m_texLastBind;


	public static I_Texture	getLastBind()	{return m_texLastBind;}
	public static void		unbind()		{m_texLastBind= null;}

	public static void bindNone(){
		m_texLastBind= null;
		GL.glDisable(I_SGL.GL_TEXTURE_2D);
	}


    private int m_nTarget;
    private int m_nIDtexture;
    private int m_nHeight;
    private int m_nWidth;
    private int m_nTexWidth;
    private int m_nTexHeight;
    private float m_nWidthRatio;
    private float m_nHeightRatio;
    private boolean m_hasAlpha;
    private String m_stRef;
    private String m_stCacheName;


    protected TextureImpl()	{	}
    public TextureImpl(String p_stRef, int p_nTarget, int p_nIDTexture){
        m_nIDtexture= p_nIDTexture;
        m_nTarget= p_nTarget;
        m_stRef= p_stRef;
        
		m_texLastBind= this;	//FIXME - not true, this texture will not be bound
    }
    
	public void setCacheName(String p_stCacheName)	{m_stCacheName= p_stCacheName;}
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
    public String getTextureRef()	{return m_stRef;}
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
    		GL.glEnable(I_SGL.GL_TEXTURE_2D);
    	    GL.glBindTexture(m_nTarget, m_nIDtexture);
    	}
    }

	
    public void release(){
		IntBuffer texBuf= createIntBuffer(1);
		texBuf.put(m_nIDtexture);
		texBuf.flip();

		GL.glDeleteTextures(texBuf);

		if(m_texLastBind == this)
			bindNone();

		if(m_stCacheName != null)
			InternalTextureLoader.get().clear(m_stCacheName);
		else
			InternalTextureLoader.get().clear(m_stRef);
    }
    
	
	public byte[] getTextureData(){
		ByteBuffer buffer= BufferUtils.createByteBuffer((hasAlpha() ? 4 : 3) * m_nTexWidth * m_nTexHeight);
		bind();

		GL.glGetTexImage(I_SGL.GL_TEXTURE_2D, 0, hasAlpha()? I_SGL.GL_RGBA:I_SGL.GL_RGB, I_SGL.GL_UNSIGNED_BYTE, buffer);

		byte[] data= new byte[buffer.limit()];
		buffer.get(data);
		buffer.clear();

		return data;
	}

	protected IntBuffer createIntBuffer(int size){
		ByteBuffer temp= ByteBuffer.allocateDirect(4 * size);
		temp.order(ByteOrder.nativeOrder());

		return temp.asIntBuffer();
	}
}
