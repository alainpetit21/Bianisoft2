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


public class Timer{
	public interface I_Callback{
		public void TimerEvent(Timer p_nTimer, Object p_objHint);
	};


	public boolean		m_isEnable;
	public double		m_nDelayMax;
	public double		m_nDelay;
	Timer.I_Callback	m_objCallback;
	Object				m_objHint;


	public Timer(int p_nDelay, Timer.I_Callback p_nCallback, Object p_objHint){
		m_nDelayMax		= m_nDelay	= p_nDelay;
		m_objCallback	= p_nCallback;
		m_objHint= p_objHint;
		m_isEnable		= true;
	}

	public void setDelay(int p_nDelay){
		m_nDelayMax= m_nDelay= p_nDelay;
	}

	public void manage(double p_nMS){
		if(!m_isEnable)
			return;

		m_nDelay-= p_nMS;

		if(m_nDelay <= 0){
			m_isEnable= false;
			m_objCallback.TimerEvent(this, m_objHint);
			m_nDelay= m_nDelayMax;
		}
	}
}
