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
package com.bianisoft.engine;


//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

//LWJGL library imports
import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;


public abstract class Camera extends PhysObj{
	public static final int TYPE_2D= 0;
	public static final int TYPE_3D= 1;

	public static Camera	m_nCur2D= null;
	public static Camera	m_nCur3D= null;

	public float	m_nZoom;
	public float	m_nZoomTo;
	public float	m_nNbMSToZoomTo;

	public Rectangle	m_rectLimit;
	public Rectangle	m_rectViewPort;


	public static Camera createCamera(int p_nType){
		if(p_nType == TYPE_2D)		return new Camera2D();
		else if(p_nType == TYPE_3D)	return new Camera3D();
		else						return null;
	}

	public static Camera getCur(int p_nType){
		if(p_nType == TYPE_2D)		return m_nCur2D;
		else if(p_nType == TYPE_3D)	return m_nCur3D;
		else						return null;
	}

	public Camera(){
		super(IDCLASS_Camera);
		m_nZoom= m_nZoomTo= 1;

		m_rectViewPort= new Rectangle(-App.g_nWidth/2, -App.g_nHeight/2, App.g_nWidth, App.g_nHeight);
	}


	public void setZoom(float p_nZoom)	{m_nZoom= m_nZoomTo= p_nZoom;}
	public float getZoom()				{return m_nZoom;}

	public abstract void setCur();
	public abstract void lockOnObject(PhysObj p_obj);
	public abstract void lookAt(float p_nX, float p_nY, float p_nZ);
	public abstract void setFree();
	public abstract void doProjection();
	public abstract void doWorldProjection();
	public abstract void doScreenProjection();
	public abstract float doUnprojectionX(float p_fValue);
	public abstract float doUnprojectionY(float p_fValue);
	public abstract boolean isOnScreen(float p_nX1, float p_nY1);

	public boolean isMoving(){
		boolean ret= super.isMoving();

		return ret || (m_nZoomTo != m_nZoom);
	}

	public boolean isOnScreen(float p_nX1, float p_nY1, float p_nX2, float p_nY2){
		return (isOnScreen(p_nX2, p_nY2) || isOnScreen(p_nX1, p_nY1));
	}

	public void setLimits(int p_nX, int p_nY, int p_nWidth, int p_nHeight){
		if(m_rectLimit == null)
			m_rectLimit= new Rectangle();
		
		m_rectLimit.setBounds(p_nX, p_nY, p_nWidth, p_nHeight);
	}

	public void moveBy(float p_nDeltaX, float p_nDeltaY, float p_nDeltaZ){
		setPosX(getPosX() + (p_nDeltaX/m_nZoom));
		setPosY(getPosY() + (p_nDeltaY/m_nZoom));

		if(m_rectLimit != null){
			//Check Left Border
			float deltaLeft= (getPosX() + m_rectViewPort.getX()) - m_rectLimit.getX();
			if(deltaLeft < 0)
				setPosX(getPosX() + Math.abs(deltaLeft));

			//Check Up Border
			float deltaUp= (getPosY() + m_rectViewPort.getY()) - m_rectLimit.getY();
			if(deltaUp < 0)
				setPosY(getPosY() + Math.abs(deltaUp));

			//Check Right Border
			float deltaRight= (getPosX() + (m_rectViewPort.getX()+m_rectViewPort.getWidth())) - (m_rectLimit.getX() + m_rectLimit.getWidth());
			if(deltaRight > 0)
				setPosX(getPosX() - deltaRight);

			//Check Bottom Border
			float deltaBottom= (getPosY() + (m_rectViewPort.getY()+m_rectViewPort.getHeight())) - (m_rectLimit.getY() + m_rectLimit.getHeight());
			if(deltaBottom > 0)
				setPosY(getPosY() - deltaBottom);
		}
	}

	public void zoomTo(float p_nZoomTo, float p_nNbMSToZoomTo){
		m_nZoomTo= p_nZoomTo;
		m_nNbMSToZoomTo= p_nNbMSToZoomTo;
	}

	public void manage(float p_fTimeScaleFactor){
		super.manage(p_fTimeScaleFactor);

		if(m_nZoomTo != m_nZoom){
			float percentage= (p_fTimeScaleFactor*60.0f) / m_nNbMSToZoomTo;
			float deltaZoom= (m_nZoomTo - m_nZoom)*percentage;

			m_nZoom+= deltaZoom;
			m_nNbMSToZoomTo-= p_fTimeScaleFactor*60;

			if(m_nNbMSToZoomTo <= 0)
				m_nZoom= m_nZoomTo;
		}

		m_rectViewPort.setBounds((int)((-App.g_nWidth/2)/m_nZoom), (int)((-App.g_nHeight/2)/m_nZoom), (int)((App.g_nWidth)/m_nZoom), (int)((App.g_nHeight)/m_nZoom));
	}
}


final class Camera2D extends Camera{
	public Camera2D(){
		super();
		setSubClassID(TYPE_2D);
		setCur();
	}
	
	public void setCur(){m_nCur2D= this;}

	public void lockOnObject(PhysObj p_obj){
		//TODO: implement me
	}

	public void lookAt(float p_nX, float p_nY, float p_nZ){
		//TODO: implement me
	}

	public void setFree(){
		//TODO: implement me
	}

	public void doProjection(){
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluOrtho2D(m_rectViewPort.getX(), m_rectViewPort.getX() + m_rectViewPort.getWidth(),
					m_rectViewPort.getY() + m_rectViewPort.getHeight(), m_rectViewPort.getY());

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glDisable(GL_DEPTH_TEST);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

	}

	public void doWorldProjection(){
		doProjection();
		glTranslated(-getPosX(), -getPosY(),  getPosZ());
		glRotatef(getAngleX(), 1.0f, 0.0f, 0.0f);
		glRotatef(getAngleY(), 0.0f, 1.0f, 0.0f);
		glRotatef(getAngleZ(), 0.0f, 0.0f, 1.0f);
	}

	public void doScreenProjection(){
		doProjection();
		glScaled(1/m_nZoom, 1/m_nZoom, 1);
	}

	//TODO - maybe the rotation of the camera
	public float doUnprojectionX(float p_fValue){
		return (p_fValue/m_nZoom + getPosX());
	}
	
	//TODO - maybe the rotation of the camera
	public float doUnprojectionY(float p_fValue){
		return (p_fValue/m_nZoom + getPosY());
	}
	
	public boolean isOnScreen(float p_nX, float p_nY){
		int width= App.g_nWidth;
		int height= App.g_nHeight;
		
		Rectangle rect= new Rectangle((int)getPosX() - (width>>1), (int)getPosY() - (height>>1), width, height);

		return rect.contains(new Point((int)p_nX, (int)p_nY));
	}
}


final class Camera3D extends Camera{
	private float[] m_vLookAt= new float[3];
	private float[] m_vUp= {0, 0, 1};


	public Camera3D(){
		super();
		setSubClassID(TYPE_3D);
		setCur();
	}
	
	public void setCur(){m_nCur3D= this;}

	public void lockOnObject(PhysObj p_obj){
		m_vLookAt= p_obj.getPosArray();
	}

	public void lookAt(float p_nX, float p_nY, float p_nZ){
		m_vLookAt[0]= p_nX;
		m_vLookAt[1]= p_nY;
		m_vLookAt[2]= p_nZ;
	}

	public void setFree(){
		m_vLookAt= null;
	}

	public void doProjection(){
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(45.0f, App.g_nWidth / App.g_nHeight, 0.1f, 1000.0f);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	}
	
	public void doWorldProjection(){
		doProjection();
		if(m_vLookAt != null)
			gluLookAt(getPosX(), getPosY(), -getPosZ(), m_vLookAt[0], m_vLookAt[1], m_vLookAt[2], m_vUp[0], m_vUp[1], m_vUp[2]);
	}

	public void doScreenProjection(){
		doProjection();
	}

	public float doUnprojectionX(float p_fValue){
		return p_fValue - getPosX();
	}

	public float doUnprojectionY(float p_fValue){
		return p_fValue - getPosY();
	}

	public boolean isOnScreen(float p_nX, float p_nY){
		//TODO : Camera3D.isOnScreen(x, y);
		return true;
	}
}
