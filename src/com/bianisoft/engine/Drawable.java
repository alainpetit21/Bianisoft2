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


public class Drawable extends PhysObj{
	public boolean	m_isShown		= true;
	public boolean	m_isCameraBound	= false;
	public boolean	m_isInInfinity	= false;

	public float	m_colorFilterRed	=1;
	public float	m_colorFilterGreen	=1;
	public float	m_colorFilterBlue	=1;
	public float	m_colorFilterAlpha	=1;

	public float	m_colorFilterRedTo	=1;
	public float	m_colorFilterGreenTo=1;
	public float	m_colorFilterBlueTo	=1;
	public float	m_colorFilterAlphaTo=1;
	public float	m_nNbMSToFilterTo;

	public float	m_nZoom				=1;
	public float	m_nZoomTo			=1;
	public float	m_nNbMSToZoomTo;

	public float	m_nScaleX			=1;
	public float	m_nScaleY			=1;
	public float	m_nScaleZ			=1;
	public float	m_nScaleXTo			=1;
	public float	m_nScaleYTo			=1;
	public float	m_nScaleZTo			=1;
	public float	m_nNbMSToScaleTo;


	public Drawable()				{super(IDCLASS_PhysObj);}
	public Drawable(int p_nType)	{super(p_nType);}

	public Drawable(Drawable p_refDrawableObj){
		super(p_refDrawableObj);

		m_colorFilterAlpha= p_refDrawableObj.m_colorFilterAlpha;
		m_colorFilterGreen= p_refDrawableObj.m_colorFilterGreen;
		m_colorFilterBlue= p_refDrawableObj.m_colorFilterBlue;
		m_colorFilterRed= p_refDrawableObj.m_colorFilterRed;
		m_isShown= p_refDrawableObj.m_isShown;
		m_nZoom= p_refDrawableObj.m_nZoom;
		m_nZoomTo= p_refDrawableObj.m_nZoomTo;

		m_nScaleX= p_refDrawableObj.m_nScaleX;
		m_nScaleY= p_refDrawableObj.m_nScaleY;
		m_nScaleZ= p_refDrawableObj.m_nScaleZ;
		m_nScaleXTo= p_refDrawableObj.m_nScaleXTo;
		m_nScaleYTo= p_refDrawableObj.m_nScaleYTo;
		m_nScaleZTo= p_refDrawableObj.m_nScaleZTo;
	}

	public void show()					{m_isShown= true;}
	public void hide()					{m_isShown= false;}
	public void show(boolean p_toShow)	{if(p_toShow) show(); else hide();}
	public boolean isShown()			{return m_isShown;}

	public float getFilterRed()		{return m_colorFilterRed;}
	public float getFilterGreen()	{return m_colorFilterGreen;}
	public float getFilterBlue()	{return m_colorFilterBlue;}
	public float getFilterAlpha()	{return m_colorFilterAlpha;}

	public void setFilterRed(float p_nRedComponent)		{m_colorFilterRed	= m_colorFilterRedTo	= p_nRedComponent;}
	public void setFilterGreen(float p_nGreenComponent)	{m_colorFilterGreen	= m_colorFilterGreenTo	= p_nGreenComponent;}
	public void setFilterBlue(float p_nBlueComponent)	{m_colorFilterBlue	= m_colorFilterBlueTo	= p_nBlueComponent;}
	public void setFilterAlpha(float p_nAlphaComponent)	{m_colorFilterAlpha	= m_colorFilterAlphaTo	= p_nAlphaComponent;}

	public void setFilterColor(float p_nRedComponent, float p_nGreenComponent, float p_nBlueComponent){
		setFilterColor(p_nRedComponent, p_nGreenComponent, p_nBlueComponent, getFilterAlpha());
	}

	public void setFilterColor(float p_nRedComponent, float p_nGreenComponent, float p_nBlueComponent, float p_nAlphaComponent){
		m_colorFilterRed	= m_colorFilterRedTo	= p_nRedComponent;
		m_colorFilterGreen	= m_colorFilterGreenTo	= p_nGreenComponent;
		m_colorFilterBlue	= m_colorFilterBlueTo	= p_nBlueComponent;
		m_colorFilterAlpha	= m_colorFilterAlphaTo	= p_nAlphaComponent;
	}

	public void filterTo(float p_nRedTo, float p_nGreenTo, float p_nBlueTo, float p_nAlphaTo, float p_nNbMSToZoomTo){
		m_colorFilterRedTo= p_nRedTo;
		m_colorFilterGreenTo= p_nGreenTo;
		m_colorFilterBlueTo= p_nBlueTo;
		m_colorFilterAlphaTo= p_nAlphaTo;
		m_nNbMSToFilterTo= p_nNbMSToZoomTo;
	}

	public float getZoom()				{return m_nZoom;}
	public void setZoom(float p_nZoom)	{m_nZoom= m_nZoomTo= p_nZoom;}

	public void zoomTo(float p_nZoomTo, float p_nNbMSToZoomTo){
		m_nZoomTo= p_nZoomTo;
		m_nNbMSToZoomTo= p_nNbMSToZoomTo;
	}

	public float getScaleX()				{return m_nScaleX;}
	public float getScaleY()				{return m_nScaleY;}
	public float getScaleZ()				{return m_nScaleZ;}
	public void setScaleX(float p_nScale)	{m_nScaleX= m_nScaleXTo= p_nScale;}
	public void setScaleY(float p_nScale)	{m_nScaleY= m_nScaleYTo= p_nScale;}
	public void setScaleZ(float p_nScale)	{m_nScaleZ= m_nScaleZTo= p_nScale;}

	public void scaleTo(float p_nScaleXTo, float p_nScaleYTo, float p_nScaleZTo, float p_nNbMSToScaleTo){
		m_nScaleXTo= p_nScaleXTo;
		m_nScaleYTo= p_nScaleYTo;
		m_nScaleZTo= p_nScaleZTo;
		m_nNbMSToScaleTo= p_nNbMSToScaleTo;
	}

	public boolean isMoving(){
		boolean ret= super.isMoving();

		return ret ||
				(m_nZoomTo != m_nZoom) ||
				(m_nScaleXTo != m_nScaleX) ||
				(m_nScaleYTo != m_nScaleY) ||
				(m_nScaleZTo != m_nScaleZ) ||
				(m_colorFilterRedTo != m_colorFilterRed) ||
				(m_colorFilterGreenTo != m_colorFilterGreen) ||
				(m_colorFilterBlueTo != m_colorFilterBlue) ||
				(m_colorFilterAlphaTo != m_colorFilterAlpha);
	}

	public void manage(float p_fTimeScaleFactor){
		super.manage(p_fTimeScaleFactor);

		if(m_nZoomTo != m_nZoom){
			float percentage= (p_fTimeScaleFactor*60.0f) / m_nNbMSToZoomTo;
			float delta= (m_nZoomTo - m_nZoom)*percentage;

			m_nZoom+= delta;
			m_nNbMSToZoomTo-= p_fTimeScaleFactor*60;

			if(m_nNbMSToZoomTo <= 0)
				m_nZoom= m_nZoomTo;
		}

		if(m_colorFilterRedTo != m_colorFilterRed){
			float percentage= (p_fTimeScaleFactor*60.0f) / m_nNbMSToFilterTo;
			float delta= (m_colorFilterRedTo - m_colorFilterRed)*percentage;

			m_colorFilterRed+= delta;
			m_nNbMSToFilterTo-= p_fTimeScaleFactor*60;

			if(m_nNbMSToFilterTo <= 0)
				m_colorFilterRed= m_colorFilterRedTo;
		}

		if(m_colorFilterGreenTo != m_colorFilterGreen){
			float percentage= (p_fTimeScaleFactor*60.0f) / m_nNbMSToFilterTo;
			float delta= (m_colorFilterGreenTo - m_colorFilterGreen)*percentage;

			m_colorFilterGreen+= delta;
			m_nNbMSToFilterTo-= p_fTimeScaleFactor*60;

			if(m_nNbMSToFilterTo <= 0)
				m_colorFilterGreen= m_colorFilterGreenTo;
		}

		if(m_colorFilterBlueTo != m_colorFilterBlue){
			float percentage= (p_fTimeScaleFactor*60.0f) / m_nNbMSToFilterTo;
			float delta= (m_colorFilterBlueTo - m_colorFilterBlue)*percentage;

			m_colorFilterBlue+= delta;
			m_nNbMSToFilterTo-= p_fTimeScaleFactor*60;

			if(m_nNbMSToFilterTo <= 0)
				m_colorFilterBlue= m_colorFilterBlueTo;
		}

		if(m_colorFilterAlphaTo != m_colorFilterAlpha){
			float percentage= (p_fTimeScaleFactor*60.0f) / m_nNbMSToFilterTo;
			float delta= (m_colorFilterAlphaTo - m_colorFilterAlpha)*percentage;

			m_colorFilterAlpha+= delta;
			m_nNbMSToFilterTo-= p_fTimeScaleFactor*60;

			if(m_nNbMSToFilterTo <= 0)
				m_colorFilterAlpha= m_colorFilterAlphaTo;
		}

		if(m_nScaleXTo != m_nScaleX){
			float percentage= (p_fTimeScaleFactor*60.0f) / m_nNbMSToScaleTo;
			float delta= (m_nScaleXTo - m_nScaleX)*percentage;

			m_nScaleX+= delta;
			m_nNbMSToScaleTo-= p_fTimeScaleFactor*60;

			if(m_nNbMSToScaleTo <= 0)
				m_nScaleX= m_nScaleXTo;
		}

		if(m_nScaleYTo != m_nScaleY){
			float percentage= (p_fTimeScaleFactor*60.0f) / m_nNbMSToScaleTo;
			float delta= (m_nScaleYTo - m_nScaleY)*percentage;

			m_nScaleY+= delta;
			m_nNbMSToScaleTo-= p_fTimeScaleFactor*60;

			if(m_nNbMSToScaleTo <= 0)
				m_nScaleY= m_nScaleYTo;
		}

		if(m_nScaleZTo != m_nScaleZ){
			float percentage= (p_fTimeScaleFactor*60.0f) / m_nNbMSToScaleTo;
			float delta= (m_nScaleZTo - m_nScaleZ)*percentage;

			m_nScaleZ+= delta;
			m_nNbMSToScaleTo-= p_fTimeScaleFactor*60;

			if(m_nNbMSToScaleTo <= 0)
				m_nScaleZ= m_nScaleZTo;
		}
	}

	public void draw()	{	}
}
