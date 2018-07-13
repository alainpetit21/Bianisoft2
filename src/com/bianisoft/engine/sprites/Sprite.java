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
package com.bianisoft.engine.sprites;


//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;

//Standard Java imports
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

//LWJGL library imports
import org.lwjgl.util.Rectangle;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Drawable;
import com.bianisoft.engine.resmng.Texture;
import com.bianisoft.engine.resmng.ImageCache;


public class Sprite extends Drawable{
	public static final int TYPE_STANDARD		= 0;
	public static final int TYPE_BUTTON			= 1;
	public static final int TYPE_LOOP_ANIMATION	= 2;
	public static final int TYPE_FIXED			= 3;
	public static final int TYPE_ONE_TIMER		= 4;
	public static final int TYPE_3D				= 5;
	public static final int TYPE_CUSTOM			= 6;


	public class Frame{
		public Rectangle	m_rectSource;
		public int[]		m_vHotSpot= {0, 0};


		public Frame(int p_nX, int p_nY, int p_nWidth, int p_nHeight){
			m_rectSource= new Rectangle(p_nX, p_nY, p_nWidth, p_nHeight);
			m_vHotSpot[0]= p_nWidth / 2;
			m_vHotSpot[1]= p_nHeight / 2;
		}
	}


	public class State{
		public ArrayList<Frame>	m_vecFrames= new ArrayList<Frame>();

		public String	m_stName;
		public int		m_nIndex;
		public int		m_nMaxFrames;
		public int		m_nSpeed;
		public int		m_nCurFrame;


		public State()	{	}
		public State(State p_refState){
			m_nMaxFrames= p_refState.m_nMaxFrames;
			m_nCurFrame= p_refState.m_nCurFrame;
			m_vecFrames= p_refState.m_vecFrames;
			m_stName= p_refState.m_stName;
			m_nIndex= p_refState.m_nIndex;
			m_nSpeed= p_refState.m_nSpeed;
		}

		public State(String p_stID, int p_nNbFrame, float p_fSpeed){
			m_nMaxFrames	= p_nNbFrame;
			m_nSpeed		= (int)(p_fSpeed*32);
			m_stName		= p_stID;
		}

		public void setMaxFrames(int p_nNbFrame)		{m_nMaxFrames= p_nNbFrame;}
		public void setAnimationSpeed(float p_nSpeed)	{m_nSpeed= (int)(p_nSpeed*32);}
		public float getAnimationSpeed()				{return m_nSpeed / 32.0f;}
	}


	public ArrayList<State>		m_vecStates		= new ArrayList<State>();
	public ArrayList<Method>	m_vecFctManage	= new ArrayList<Method>();
	public int					m_nCurState;
	public int 					m_nHighestFrameCount;

	public int					m_nChangeToState= -1;
	public int					m_nChangeToFrame= -1;
	
	public Texture	m_image;
	public String	m_stResImage;
	public float	m_nWidthFrame;
	public float	m_nHeightFrame;
	public float	m_nWidthImage;
	public float	m_nHeightImage;

	//Native Vertex buffer
	protected FloatBuffer m_bufVertices;
	protected ShortBuffer m_bufIndices;
	protected FloatBuffer m_bufUV;
	
	protected int 	m_nNbVertices;
	protected int 	m_nNbIndices;
	protected int 	m_nNbUV;

	public Sprite()	{this((String)null);}
	public Sprite(String p_stResImage){
		super(IDCLASS_Sprite);
		m_stResImage= p_stResImage;
	}

	public Sprite(Sprite p_refSprite){
		super(p_refSprite);

		m_nHighestFrameCount= p_refSprite.m_nHighestFrameCount;
		m_nHeightImage= p_refSprite.m_nHeightImage;
		m_nWidthImage= p_refSprite.m_nWidthImage;
		m_nHeightFrame= p_refSprite.m_nHeightFrame;
		m_nWidthFrame= p_refSprite.m_nWidthFrame;
		m_stResImage= p_refSprite.m_stResImage;
		m_nCurState= p_refSprite.m_nCurState;
		m_image= p_refSprite.m_image;
		m_bufVertices= p_refSprite.m_bufVertices;
		m_bufIndices= p_refSprite.m_bufIndices;
		m_bufUV= p_refSprite.m_bufUV;
		m_nNbVertices= p_refSprite.m_nNbVertices;
		m_nNbIndices= p_refSprite.m_nNbIndices;
		m_nNbUV= p_refSprite.m_nNbUV;
	
		for(State refState : p_refSprite.m_vecStates)
			m_vecStates.add(new State(refState));
	}

	public void addState(String p_stID, int p_nNbFrame, float p_fSpeed)	{addState(new Sprite.State(p_stID, p_nNbFrame, p_fSpeed));}
	public void addState(Sprite.State p_sprState){
		p_sprState.m_nIndex= m_vecStates.size();
		m_vecStates.add(p_sprState);
	}
	
	public void load(){
		float vertices[]= {
			-0.5f, -0.5f, 0.0f,	 
			 0.5f, -0.5f, 0.0f,	
			-0.5f,  0.5f, 0.0f,
			 0.5f,  0.5f, 0.0f,   
		};

		short indices[]= {
			0, 1, 3,		 
			0, 3, 2,
		};
			
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

		m_image= ImageCache.loadImage(m_stResImage);
		m_nWidthImage	= m_image.getImageWidth();
		m_nHeightImage	= m_image.getImageHeight();

		//Do calculation
		//Find Highest Frame Count within all states
		m_nHighestFrameCount= 0;
		for(int i= 0; i < m_vecStates.size(); ++i){
			State stateCur= m_vecStates.get(i);

			if(stateCur.m_nMaxFrames > m_nHighestFrameCount)
				m_nHighestFrameCount= stateCur.m_nMaxFrames;
		}

		//Using Widht of image, and highest mNbFrame find each frame width
		m_nWidthFrame= m_nWidthImage / m_nHighestFrameCount;
		m_nHeightFrame= m_nHeightImage / m_vecStates.size();
		for(int i= 0; i < m_vecStates.size(); ++i){
			State stateCur = m_vecStates.get(i);
			for(int j= 0; j < stateCur.m_nMaxFrames; ++j){
				stateCur.m_vecFrames.add(new Sprite.Frame((int) (j*m_nWidthFrame), (int) (i*m_nHeightFrame), (int) m_nWidthFrame, (int) m_nHeightFrame));
			}
		}

		float 	uv[]= new float[m_nNbUV= (8*m_nHighestFrameCount*m_vecStates.size())];
		int 	nCurPos= 0;
		float 	nRationX= m_nWidthImage / m_image.getTextureWidth();
		float 	nRationY= m_nHeightImage / m_image.getTextureHeight();
		float 	nIncX= m_nWidthFrame / m_nWidthImage;
		float 	nIncY= m_nHeightFrame / m_nHeightImage;

		for(int j= 0; j < m_vecStates.size(); ++j){
			for(int i= 0; i < m_nHighestFrameCount; ++i){
				uv[nCurPos+0]= ((i+0)*nIncX)*nRationX;	uv[nCurPos+1]= ((j+0)*nIncY)*nRationY;
				uv[nCurPos+2]= ((i+1)*nIncX)*nRationX;	uv[nCurPos+3]= ((j+0)*nIncY)*nRationY;
				uv[nCurPos+4]= ((i+0)*nIncX)*nRationX;	uv[nCurPos+5]= ((j+1)*nIncY)*nRationY;
				uv[nCurPos+6]= ((i+1)*nIncX)*nRationX;	uv[nCurPos+7]= ((j+1)*nIncY)*nRationY;
				nCurPos+= 8;
			}
		}
		
		ByteBuffer uvb= ByteBuffer.allocateDirect(m_nNbUV * 4);
		uvb.order(ByteOrder.nativeOrder());
		m_bufUV = uvb.asFloatBuffer();
		m_bufUV.put(uv);
		m_bufUV.position(0);
		
		registerManageFunctions();
	}

	public void registerManageFunctions(){
		//Set All rectangles of all frames of all states
		for(int i= 0; i < m_vecStates.size(); ++i){
			try{
				Class[] parTypes = new Class[1];
				parTypes[0] = Float.TYPE;
				m_vecFctManage.add(getClass().getMethod("onManage", parTypes));
			}catch(NoSuchMethodException ex){
				ex.printStackTrace();
			}catch(SecurityException ex){
				ex.printStackTrace();
			}
		}
	}

	public void registerManageFunction(int p_nNumber, String p_stFunctionName){
		try{
			Class[] parTypes = new Class[1];
			parTypes[0] = Float.TYPE;
			m_vecFctManage.set(p_nNumber, getClass().getMethod(p_stFunctionName, parTypes));
		}catch(NoSuchMethodException ex){
		}catch(SecurityException ex){
		}
	}

	public void onManage(float p_tick)	{	}
	public boolean isLoaded()							{return m_image != null;}
	public Texture getImage()							{return m_image;}
	public void setImage(Texture p_image)				{m_image= p_image;}
	public String getImageFilename()					{return m_stResImage;}
	public void	setImageFilename(String p_stResImage)	{m_stResImage= p_stResImage;}

	public int getCurState()	{return m_nCurState;}

	public int getWidh(){
		State stateCur= m_vecStates.get(m_nCurState);
		Frame frameCur= stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_rectSource.getWidth();
	}

	public int getHeight(){
		State stateCur= m_vecStates.get(m_nCurState);
		Frame frameCur= stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_rectSource.getHeight();
	}

	public boolean isPointOver(float p_nX, float p_nY){
		p_nX-= getPosX();
		p_nY-= getPosY();

		p_nX+= getHotSpotX();
		p_nY+= getHotSpotY();

		return ((p_nX < getWidh()) && (p_nX > 0) &&
				(p_nY < getHeight()) && (p_nY > 0));
	}

	public Rectangle getRect(){
		State stateCur= m_vecStates.get(m_nCurState);
		Frame frameCur= stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_rectSource;
	}

	public int getHotSpotX(){
		State stateCur= (State)m_vecStates.get(m_nCurState);
		Frame frameCur= (Frame)stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_vHotSpot[0];
	}

	public int getHotSpotY(){
		State stateCur= m_vecStates.get(m_nCurState);
		Frame frameCur= stateCur.m_vecFrames.get(stateCur.m_nCurFrame>>5);
		return frameCur.m_vHotSpot[1];
	}

	public void setCurState(int p_nIdx){
		m_nCurState= p_nIdx;
		m_vecStates.get(m_nCurState).m_nCurFrame= 0;
	}

	public void setCurFrame(int p_nIdx){
		m_vecStates.get(m_nCurState).m_nCurFrame= p_nIdx<<5;
	}

	public int getCurFrame(){
		return m_vecStates.get(m_nCurState).m_nCurFrame>>5;
	}

	public int getMaxFrame(){
		return m_vecStates.get(m_nCurState).m_nMaxFrames;
	}

	public void setHotSpot(int p_nX, int p_nY){
		for(int i= 0; i < m_vecStates.size(); ++i){
			State stateCur= (State)m_vecStates.get(i);

			for(int j= 0; j < stateCur.m_nMaxFrames; ++j){
				Frame frameCur= (Frame)stateCur.m_vecFrames.get(j);

				frameCur.m_vHotSpot[0]= p_nX;
				frameCur.m_vHotSpot[1]= p_nY;
			}
		}
	}

	public void setAnimationSpeed(float p_fSpeed){
		State stateCur		= m_vecStates.get(m_nCurState);
		stateCur.m_nSpeed	= (int)(p_fSpeed*32);
	}

	public void manage(float p_fTimeScaleFactor){
		super.manage(p_fTimeScaleFactor);

		if(m_nChangeToState != -1){
			setCurState(m_nChangeToState);
			m_nChangeToState= -1;
		}
		if(m_nChangeToFrame != -1){
			setCurFrame(m_nChangeToFrame);
			m_nChangeToFrame= -1;	
		}

		
		State stateCur= m_vecStates.get(m_nCurState);
		stateCur.m_nCurFrame+= (stateCur.m_nSpeed*p_fTimeScaleFactor);

		try{
			if(!m_vecFctManage.isEmpty()){
				Method fct= m_vecFctManage.get(m_nCurState);
				fct.invoke(this, p_fTimeScaleFactor);
			}
		}catch(IllegalAccessException ex){
			ex.printStackTrace();
		}catch(IllegalArgumentException ex){
			ex.printStackTrace();
		}catch(InvocationTargetException ex){
			ex.printStackTrace();
		}

		if((stateCur.m_nCurFrame>>5) >= stateCur.m_nMaxFrames){
			stateCur.m_nCurFrame= 0;
		}
	}

	public void draw(){
		if(!isShown() || !isLoaded())
			return;

		if((m_bufVertices == null) || (m_bufIndices == null) || (m_bufUV == null))
			return;

		glPushMatrix();

		State objStateCur= m_vecStates.get(m_nCurState);
		int nCurFrame= objStateCur.m_nCurFrame>>5;

		Frame objFrameCur= objStateCur.m_vecFrames.get(nCurFrame);
		float nHotSpotX= (m_nWidthFrame/2) - objFrameCur.m_vHotSpot[0];
		float nHotSpotY= (m_nHeightFrame/2) - objFrameCur.m_vHotSpot[1];
		
		m_image.bind();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTranslatef(getPosX()+nHotSpotX, getPosY()+nHotSpotY, 0);
		glScalef(m_nWidthFrame*m_nZoom*m_nScaleX, m_nHeightFrame*m_nZoom*m_nScaleY, 1);
		glColor4f(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);
		glRotatef(getAngleZ(), 0, 0, 1);

		//Point to our buffers
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glVertexPointer(3, 0, m_bufVertices);
		
		m_bufUV.position(((m_nCurState*m_nHighestFrameCount)+nCurFrame)*8);
		glTexCoordPointer(2, 0, m_bufUV);

		glDrawElements(GL_TRIANGLES, m_bufIndices);

		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glPopMatrix();

		//Print Debug Collision
		if(App.PRINT_DEBUG && m_isCollidable)
			drawDebug();

	}

	public void drawDebug(){
	}

	public String toString(){
		return "Sprite @ " + (int)getPosX() + ";"+ (int)getPosY() + ";"+ (int)getPosZ() + ";";
	}
}
