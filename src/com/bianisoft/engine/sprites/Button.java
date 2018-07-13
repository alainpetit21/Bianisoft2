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


//Bianisoft imports
import com.bianisoft.engine.Countainer;
import com.bianisoft.engine.labels.Label;
import com.bianisoft.engine.sprites.Sprite.State;


public class Button extends Sprite{
	public interface I_Callback{
		public void callbackStateChanged(int p_nNewState, Button p_obj);
	};

	public static final int ST_IDLE		= 0;
	public static final int ST_OVER		= 1;
	public static final int ST_DOWN		= 2;
	public static final int ST_CLICKED	= 4;

	public Label		m_lblAttached;
	public I_Callback	m_objCallback;
	public int			m_nOldState;
	public boolean		m_isSelected;
	public boolean		m_isDisabled;


	public Button()	{this("", 1, 0.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);}
	public Button(String p_stResImage, int p_nFrameIdle, float p_nSpeedIdle,int p_nFrameOver, float p_nSpeedOver,
				  int p_nFrameDown, float p_nSpeedDown,int p_nFrameSelected, float p_nSpeedSelected){
		super(p_stResImage);
		setClassID(IDCLASS_Button);

		m_vecStates.add(new State("Idle", p_nFrameIdle, p_nSpeedIdle));
		m_vecStates.add(new State("Over", p_nFrameOver, p_nSpeedOver));
		m_vecStates.add(new State("Down", p_nFrameDown, p_nSpeedDown));
		m_vecStates.add(new State("Selected", p_nFrameSelected, p_nSpeedSelected));
	}

	public void setSelected(boolean p_isSelected)	{m_isSelected= p_isSelected;}
	public void setDisabled(boolean p_isDisabled){
		m_isDisabled= p_isDisabled;
		setFilterAlpha((m_isDisabled)? 0.5f:1);
	}

	public boolean isSelected()	{return m_isSelected;}
	public boolean isDisabled()	{return m_isDisabled;}

	public void setCallback(I_Callback p_objCallback){
		m_objCallback= p_objCallback;
	}
	
	public void setLabel(Label p_lblToAttach){
		m_lblAttached= p_lblToAttach;
		
		Countainer objCnt= ((Countainer)m_lblAttached.m_objParent);
		objCnt.removeChild(m_lblAttached);
	}
	
	public void setCurState(int p_nIdx){
		super.setCurState(p_nIdx);
		m_nOldState= p_nIdx;
	}

	public void show(){
		m_isShown= true;

		if(m_lblAttached != null)
			m_lblAttached.show();
	}

	public void hide(){
		m_isShown= false;

		if(m_lblAttached != null)
			m_lblAttached.hide();
	}

	public void manage(float p_fTimeScaleFactor){
		super.manage(p_fTimeScaleFactor);
		m_nCurState= m_nOldState;

		if(m_lblAttached != null)
			m_lblAttached.manage(p_fTimeScaleFactor);
	}

	
	public void draw() {
		super.draw();

		if(m_isSelected){
			int nOldState= m_nCurState;
			m_nCurState= 3;
			super.draw();
			m_nCurState= nOldState;
		}
		
		if(m_lblAttached != null)
			m_lblAttached.draw();
	}

	public String toString(){
		return "Button @ " + (int)getPosX() + ";"+ (int)getPosY() + ";"+ (int)getPosZ() + ";";
	}
}
