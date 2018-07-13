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
package com.bianisoft.engine.backgrounds;


//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;

//Bianisoft imports
import com.bianisoft.engine.Drawable;
import com.bianisoft.engine.resmng.Texture;
import com.bianisoft.engine.resmng.ImageCache;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public class Background extends Drawable{
	public static final int TYPE_NORMAL= 1;

	public String	m_stResImage;
	public Texture	m_image;
	public float	m_nWidth;
	public float	m_nHeight;

	//Native Vertex buffer
	protected FloatBuffer m_bufVertices;
	protected ShortBuffer m_bufIndices;
	protected FloatBuffer m_bufUV;
	
	protected int 	m_nNbVertices;
	protected int 	m_nNbIndices;
	

	public Background()	{this("");}
	public Background(String p_stResImage){
		super(IDCLASS_Background);
		setSubClassID(TYPE_NORMAL);

		m_stResImage= p_stResImage;
	}

	public boolean isLoaded()							{return m_image != null;}
	public Texture getImage()							{return m_image;}
	public void	setImage(Texture p_texImage)			{m_image= p_texImage;}
	public String getImageFilename()					{return m_stResImage;}
	public void	setImageFilename(String p_stResImage)	{m_stResImage= p_stResImage;}

	public void load(){
		float vertices[]= {
			-0.5f, -0.5f, 0.0f,	 
			 0.5f, -0.5f, 0.0f,	
			-0.5f,  0.5f, 0.0f,
			 0.5f,  0.5f, 0.0f,   
		};

		float texture[]= {    		
			0.0f, 0.0f,		
			1.0f, 0.0f,		
			0.0f, 1.0f,		
			1.0f, 1.0f, 
		};
		
		short indices[]= {
			0, 1, 3,		 
			0, 3, 2,
		};

		m_image		= ImageCache.loadImage(m_stResImage);
		m_nWidth	= m_image.getImageWidth();
		m_nHeight	= m_image.getImageHeight();

		texture[2]= (float)m_image.getImageWidth() / (float)m_image.getTextureWidth();
		texture[5]= (float)m_image.getImageHeight() / (float)m_image.getTextureHeight();
		texture[6]= (float)m_image.getImageWidth() / (float)m_image.getTextureWidth();
		texture[7]= (float)m_image.getImageHeight() / (float)m_image.getTextureHeight();
		
		ByteBuffer vbb= ByteBuffer.allocateDirect((m_nNbVertices= vertices.length) * 4); 
		vbb.order(ByteOrder.nativeOrder());
		m_bufVertices= vbb.asFloatBuffer();
		m_bufVertices.put(vertices);
		m_bufVertices.position(0);

		ByteBuffer ibb = ByteBuffer.allocateDirect((m_nNbIndices= indices.length) * 2);
		ibb.order(ByteOrder.nativeOrder());
		m_bufIndices = ibb.asShortBuffer();
		m_bufIndices.put(indices);
		m_bufIndices.position(0);		

		ByteBuffer uvb= ByteBuffer.allocateDirect(m_nNbVertices * 4);
		uvb.order(ByteOrder.nativeOrder());
		m_bufUV = uvb.asFloatBuffer();
		m_bufUV.put(texture);
		m_bufUV.position(0);
	}
	
	public void draw(){
		if(!m_isShown || (m_image == null))
			return;

		if((m_bufVertices == null) || (m_bufIndices == null) || (m_bufUV == null))
			return;
			
		glPushMatrix();

		m_image.bind();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTranslatef(getPosX(), getPosY(), 0);
		glScalef(m_nWidth*m_nZoom, m_nHeight*m_nZoom, 1);
		glRotatef(getAngleZ(), 0.0f, 0.0f, 1.0f);
		glColor4f(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);
		
		//Point to our buffers
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glVertexPointer(3, 0, m_bufVertices);
		glTexCoordPointer(2, 0, m_bufUV);

		glDrawElements(GL_TRIANGLES, m_bufIndices);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glPopMatrix();
	}

	public String toString(){
		return "Background @ " + (int)getPosX() + ";"+ (int)getPosY() + ";"+ (int)getPosZ() + ";";
	}
}
