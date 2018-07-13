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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//Bianisoft imports
import com.bianisoft.engine.resmng.DataInputStreamLittleEndian;


public final class ObjMD2 extends Object3D{
	private static final int TYPE_MD2= 0x01;

	private String	m_stResMesh;
	private int		m_nNbFrames;
	private int		m_nVertexFrameOffset;
	private int		m_nVertexFrameSize;
	private int		m_nNbTriangles;

	private float	m_fScalingFactor= 1.0f;


	public ObjMD2(String p_stModelName, String p_stTextureName){
		super(p_stTextureName);

		setSubClassID(TYPE_MD2);
		setMeshFileName(p_stModelName);
	}

	public ObjMD2(ObjMD2 p_objMD2){
		super((Object3D)p_objMD2);
		m_stResMesh= p_objMD2.m_stResMesh;
	}

	public void setMeshFileName(String p_stResMesh){
		m_stResMesh= p_stResMesh;
	}

	public void setScalingFactor(float p_fScalingFactor){
		m_fScalingFactor= p_fScalingFactor;
	}

	public void load(){
		super.load();
		DataInputStreamLittleEndian disLE= new DataInputStreamLittleEndian(m_stResMesh);

		int	nMagic				= disLE.readInt();
		int	nVersion			= disLE.readInt();
		int	nSkinWidth			= disLE.readInt();
		int	nSkinHeight			= disLE.readInt();
		int	nFrameSize			= disLE.readInt();
		int	nNumSkins			= disLE.readInt();
		int	nNumVertices		= disLE.readInt();
		int	nNumTexCoords		= disLE.readInt();
		int	nNumTriangles		= disLE.readInt();
		int	nNumGlCommands		= disLE.readInt();
		int	nNumFrames			= disLE.readInt();
		int	nOffsetSkins		= disLE.readInt();
		int	nOffsetTexCoords	= disLE.readInt();
		int	nOffsetTriangles	= disLE.readInt();
		int	nOffsetFrames		= disLE.readInt();
		int	nOffsetGlCommands	= disLE.readInt();
		int	nOffsetEnd			= disLE.readInt();

		m_nNbFrames= nNumFrames;
		m_nVertexFrameSize= nNumVertices;
		m_nNbTriangles= nNumTriangles;

		//Read UV
		int nPos= 0;
		float uv[]= new float[nNumTexCoords * 2];
		
		disLE.seek(nOffsetTexCoords);
		for(int i= 0; i < nNumTexCoords; ++i){
			uv[nPos++]= ((float)disLE.readShort() / nSkinWidth) * m_texImage.getWidth();
			uv[nPos++]= ((float)disLE.readShort() / nSkinHeight) * m_texImage.getHeight();
		}

		//Read Coordonnes
		nPos= 0;
		float vertices[]= new float[nNumFrames*nNumVertices * 3];

		disLE.seek(nOffsetFrames);
		for(int i= 0, cptVertices= 0; i < nNumFrames; ++i){
			float[] scale= new float[3];
			float[] transl= new float[3];

			scale[0]= disLE.readFloat()*m_fScalingFactor;
			scale[1]= disLE.readFloat()*m_fScalingFactor;
			scale[2]= disLE.readFloat()*m_fScalingFactor;
			transl[0]= disLE.readFloat()*m_fScalingFactor;
			transl[1]= disLE.readFloat()*m_fScalingFactor;
			transl[2]= disLE.readFloat()*m_fScalingFactor;
			disLE.skip(16);

			for(int j= 0; j < nNumVertices; ++j){
				vertices[nPos++]= (((float)disLE.readUnsignedChar()) * scale[0]) + transl[0];
				vertices[nPos++]= (((float)disLE.readUnsignedChar()) * scale[1]) + transl[1];
				vertices[nPos++]= (((float)disLE.readUnsignedChar()) * scale[2]) + transl[2];
				disLE.skip(1);
				++cptVertices;
			}
		}

		//Read triangles
		nPos= 0;
		ByteBuffer bufUV= ByteBuffer.allocateDirect(nNumTriangles * 3 * 2 * 4); 
		bufUV.order(ByteOrder.nativeOrder());
		m_bufUV= bufUV.asFloatBuffer();
		
		ByteBuffer bufVertices= ByteBuffer.allocateDirect(nNumTriangles * 3 * 3 * 4); 
		bufVertices.order(ByteOrder.nativeOrder());
		m_bufVertices= bufVertices.asFloatBuffer();

		disLE.seek(nOffsetTriangles);
		for(int i= 0; i < nNumTriangles; ++i){

			short tri1= disLE.readShort();
			short tri2= disLE.readShort();
			short tri3= disLE.readShort();

			short uv1= disLE.readShort();
			short uv2= disLE.readShort();
			short uv3= disLE.readShort();

			m_bufVertices.put(vertices[(tri1*3)+0]);	m_bufVertices.put(vertices[(tri1*3)+1]);	m_bufVertices.put(vertices[(tri1*3)+2]);	
			m_bufVertices.put(vertices[(tri2*3)+0]);	m_bufVertices.put(vertices[(tri2*3)+1]);	m_bufVertices.put(vertices[(tri2*3)+2]);	
			m_bufVertices.put(vertices[(tri3*3)+0]);	m_bufVertices.put(vertices[(tri3*3)+1]);	m_bufVertices.put(vertices[(tri3*3)+2]);	

			m_bufUV.put(uv[(uv1*2)+0]);	m_bufUV.put(uv[(uv1*2)+1]);
			m_bufUV.put(uv[(uv2*2)+0]);	m_bufUV.put(uv[(uv2*2)+1]);
			m_bufUV.put(uv[(uv3*2)+0]);	m_bufUV.put(uv[(uv3*2)+1]);
		}
		int totot= m_bufVertices.position();
		int totot2= m_bufUV.position();
		
		m_bufUV.position(0);
		m_bufVertices.position(0);

		disLE.close();
	}

	public void draw(){
		if(!isShown() || m_texImage == null)
			return;

		if((m_bufVertices == null) || (m_bufUV == null))
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
			
		//Point to our buffers
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glVertexPointer(3, 0, m_bufVertices);
		glTexCoordPointer(2, 0, m_bufUV);

		glDrawArrays(GL_TRIANGLES, 0, m_nNbTriangles*3);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisable(GL_CULL_FACE);		
		glPopMatrix();
	}

	public String toString(){
		return "ObjMD2 @ " + (int)getPosX() + ";"+ (int)getPosY() + ";"+ (int)getPosZ() + ";";
	}
}
