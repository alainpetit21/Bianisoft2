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

import com.bianisoft.engine.Countainer;


public final class SpriteOneTimer extends Sprite{
	public SpriteOneTimer(String p_stResImage, int p_nNbFrame, float p_nSpeed){
		super(p_stResImage);
		addState(new State(p_stResImage, p_nNbFrame, p_nSpeed));
	}
	
	public void manage(float p_fTimeScaleFactor){
		super.manage(p_fTimeScaleFactor);

		State stateCur= m_vecStates.get(m_nCurState);
		stateCur.m_nCurFrame+= (stateCur.m_nSpeed*p_fTimeScaleFactor);

		if((stateCur.m_nCurFrame>>5) >= stateCur.m_nMaxFrames){
			stateCur.m_nCurFrame= 0;

			((Countainer)m_objParent).removeChild(this);
		}
	}
}
