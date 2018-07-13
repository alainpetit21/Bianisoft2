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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;


public final class BackgroundTiled extends Background{
	public static final int TYPE_TILED	= 2;

	public static ArrayList<Background>	g_arTileBank; 
	public static ArrayList<Integer>	g_arNbTilesInBank;
			
	private int[][]	m_arMap;
	private int		m_nMapWidth;
	private int		m_nMapHeight;
	private int		m_nTextureWidth;
	private int		m_nTextureHeight;
	private int		m_nTileBankWidth;
	private int		m_nOffsetTileBankIdx;

	public int		m_nTileSize;


	public BackgroundTiled(){
		super(null);
		setSubClassID(TYPE_TILED);
	}

	public void loadWithTileBank(Background p_bkTileBank){
		Background backTied= p_bkTileBank;
		m_stResImage= backTied.m_stResImage;
		m_image		= backTied.m_image;
		m_nWidth	= m_image.getImageWidth();
		m_nHeight	= m_image.getImageHeight();
		m_nTextureWidth	= m_image.getTextureWidth();
		m_nTextureHeight= m_image.getTextureHeight();
		m_nTileBankWidth= (int)m_nWidth / m_nTileSize;
		
		//Create the Geometry
		float[] vertices= new float[((m_nMapWidth)*(m_nMapHeight))*6*3];
		float[] uv= new float[((m_nMapWidth)*(m_nMapHeight))*6*2];
		
		int nPosBufVertices	=0;
		int nPosBufUV		=0;
		
		float nY= -(float)m_nMapHeight/2;
		for(int j= 0; j < m_nMapHeight; ++j){

			float nX= -(float)m_nMapWidth/2;
			for(int i= 0; i < m_nMapWidth; ++i){
				int nIdxBank= m_arMap[i][j] - m_nOffsetTileBankIdx;
				
				if(nIdxBank == -1)
					continue;
				
				int nOffBankX= nIdxBank % m_nTileBankWidth;
				int nOffBankY= nIdxBank / m_nTileBankWidth;
				
				float nLeft		= ((float)nOffBankX * m_nTileSize) / m_nTextureWidth;
				float nRight	= (((float)nOffBankX * m_nTileSize) + m_nTileSize) / m_nTextureWidth;
				float nTop		= ((float)nOffBankY * m_nTileSize) / m_nTextureHeight;
				float nBottom	= (((float)nOffBankY * m_nTileSize) + m_nTileSize) / m_nTextureHeight;

				uv[nPosBufUV+0]= nLeft;		uv[nPosBufUV+1]= nTop;
				uv[nPosBufUV+2]= nRight;	uv[nPosBufUV+3]= nTop;
				uv[nPosBufUV+4]= nLeft;		uv[nPosBufUV+5]= nBottom;
				uv[nPosBufUV+6]= nRight;	uv[nPosBufUV+7]= nTop;
				uv[nPosBufUV+8]= nLeft;		uv[nPosBufUV+9]= nBottom;
				uv[nPosBufUV+10]= nRight;	uv[nPosBufUV+11]= nBottom;
				nPosBufUV+= 12;

				vertices[nPosBufVertices+0]= nX;		vertices[nPosBufVertices+1]= nY;		vertices[nPosBufVertices+2]= 0; 	
				vertices[nPosBufVertices+3]= nX+1.0f;	vertices[nPosBufVertices+4]= nY;		vertices[nPosBufVertices+5]= 0;
				vertices[nPosBufVertices+6]= nX;		vertices[nPosBufVertices+7]= nY+1.0f;	vertices[nPosBufVertices+8]= 0;
				vertices[nPosBufVertices+9]= nX+1.0f;	vertices[nPosBufVertices+10]= nY;		vertices[nPosBufVertices+11]= 0;
				vertices[nPosBufVertices+12]= nX;		vertices[nPosBufVertices+13]= nY+1.0f;	vertices[nPosBufVertices+14]= 0;
				vertices[nPosBufVertices+15]= nX+1.0f;	vertices[nPosBufVertices+16]= nY+1.0f;	vertices[nPosBufVertices+17]= 0;
				nPosBufVertices+= 18;
				
				nX+= 1.0f;
			}
			
			nY+= 1.0f;
		}
			
		ByteBuffer vbb= ByteBuffer.allocateDirect((m_nMapWidth*m_nMapHeight)*6*3*4); 
		vbb.order(ByteOrder.nativeOrder());
		m_bufVertices= vbb.asFloatBuffer();
		m_bufVertices.put(vertices);
		m_bufVertices.position(0);

		ByteBuffer uvb= ByteBuffer.allocateDirect((m_nMapWidth*m_nMapHeight)*6*2*4);
		uvb.order(ByteOrder.nativeOrder());
		m_bufUV= uvb.asFloatBuffer();
		m_bufUV.put(uv);
		m_bufUV.position(0);


		setScaleX(m_nTileSize);	setScaleY(m_nTileSize);
	}
	
	public void load(){
		int nTileOffset= m_arMap[0][0];
		int nWhichTileBank=0;
		
		do{
			m_nOffsetTileBankIdx+= g_arNbTilesInBank.get(nWhichTileBank);
			nTileOffset-= g_arNbTilesInBank.get(nWhichTileBank);
			nWhichTileBank++;
		}while(nTileOffset > 0);
		
		//Some fixing, since we've been one loop more than we needed
		nWhichTileBank--;
		nTileOffset+= g_arNbTilesInBank.get(nWhichTileBank);
		m_nOffsetTileBankIdx-= g_arNbTilesInBank.get(nWhichTileBank);
		
		loadWithTileBank(g_arTileBank.get(nWhichTileBank));
	}

	public void setMap(int[][] p_arMap){
		m_arMap= p_arMap;
		m_nMapWidth= m_arMap.length;
		m_nMapHeight= m_arMap[0].length;
	}

	public void draw(){
		if(!isShown() || !isLoaded())
			return;

		if((m_bufVertices == null) || (m_bufUV == null))
			return;
		
		glPushMatrix();

		m_image.bind();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTranslatef(getPosX(), getPosY(), 0);
		glScalef(m_nScaleX*m_nZoom, m_nScaleY*m_nZoom, 1);
		glColor4f(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);
		glRotatef(getAngleZ(), 0, 0, 1);
		
		//Point to our buffers
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glVertexPointer(3, 0, m_bufVertices);
		glTexCoordPointer(2, 0, m_bufUV);
		glDrawArrays(GL_TRIANGLES, 0, ((m_nMapWidth)*(m_nMapHeight))*6);
		
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glPopMatrix();
	}
}
