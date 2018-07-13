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
package com.bianisoft.engine._3d;

//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;

//Standard Java library imports
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//Bianisoft library imports
import com.bianisoft.engine.resmng.ImageCache;
import com.bianisoft.engine.resmng.Texture;
import com.bianisoft.engine.Drawable;


public class Object3D extends Drawable{
	protected String	m_stResTexture;
	protected Texture	m_texImage;

	//Native Vertex buffer
	protected FloatBuffer m_bufVertices;
	protected ShortBuffer m_bufIndices;
	protected FloatBuffer m_bufUV;
	
	protected int 	m_nNbVertices;
	protected int 	m_nNbIndices;
	

	public Object3D()	{super(IDCLASS_Object3D);}
	public Object3D(String p_stResIDTexture){
		super(IDCLASS_Object3D);
		
		m_stResTexture= p_stResIDTexture;
	}

	public Object3D(Object3D p_obj3D){
		this();

		m_stResTexture= p_obj3D.m_stResTexture;
		m_texImage		= p_obj3D.m_texImage;

		//Read the ref into a java array
		float[] arVertices= new float[p_obj3D.m_bufVertices.capacity()];
		p_obj3D.m_bufVertices.get(arVertices);
		
		//Create the copy native array
		ByteBuffer vbb= ByteBuffer.allocateDirect(p_obj3D.m_bufVertices.capacity() * 4); 
		vbb.order(ByteOrder.nativeOrder());
		
		//Copy into the native array
		m_bufVertices= vbb.asFloatBuffer();
		m_bufVertices.put(arVertices);
		m_bufVertices.position(0);
	}

	public void load(){
		if(m_stResTexture == null)
			return;
		
		m_texImage= ImageCache.loadImage(m_stResTexture);
	}

	public boolean	isLoaded()		{return m_texImage != null;}


	public void draw(){
		if(!isShown() || m_texImage == null)
			return;

		if((m_bufVertices == null) || (m_bufIndices == null) || (m_bufUV == null))
			return;

		m_texImage.bind();
		
		glPushMatrix();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTranslatef(-getPosX(), getPosY(), -getPosZ());
		glScalef(m_nZoom, m_nZoom, m_nZoom);
		glRotatef(getAngleX(), 1.0f, 0.0f, 0.0f);
		glRotatef(getAngleY(), 0.0f, 1.0f, 0.0f);
		glRotatef(getAngleZ(), 0.0f, 0.0f, 1.0f);
		glColor4f(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);
			
		glFrontFace(GL_CCW);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		//Point to our buffers
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glVertexPointer(3, 0, m_bufVertices);
		glTexCoordPointer(2, 0, m_bufUV);

		glDrawElements(GL_TRIANGLES, m_bufIndices);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisable(GL_CULL_FACE);		
		glPopMatrix();
	}

	public String toString(){
		return "Object3D @ " + (int)getPosX() + ";"+ (int)getPosY() + ";"+ (int)getPosZ() + ";";
	}
}
