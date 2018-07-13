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
package com.bianisoft.engine.manager;


//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Context;


public class MngContextSwitcher_Popup extends MngContextSwitcher{
	private boolean m_isPopup= true;


	public boolean managePopUp(){
		if(!m_isFinishedFadeout){
			m_fAlphaFade+= m_fIncAlpha;
			if(m_fAlphaFade >= 1.0)
				m_isFinishedFadeout= true;

			if(App.get().m_ctxCur != null)
				draw();
		}else if(!m_isFinishedActivate){
			if(m_ctxTo == null){
				if(!App.get().m_arObj.isEmpty())
					m_ctxTo= (Context)App.get().m_arObj.get(m_nCtxTo);

				return false;
			}

//			We don't de-activate the old context
//			if(m_ctxFrom != null)
//				m_ctxFrom.deActivate();

			App.get().m_ctxCur= m_ctxTo;
			m_ctxTo.activate();
			m_ctxTo.manage(1.0f);
			draw();
			m_isFinishedActivate= true;
		}else if(!m_isFinishedFadeIn){
			m_fAlphaFade-= m_fIncAlpha;

			if(m_fAlphaFade <= 0.0){
				m_isFinishedFadeIn= true;
				m_isPopup= false;
			}
			draw();
		}

		return (m_isActive= !m_isFinishedFadeIn);
	}
	public boolean managePopOut(){
		if(!m_isFinishedFadeout){
			m_fAlphaFade+= m_fIncAlpha;
			if(m_fAlphaFade >= 1.0)
				m_isFinishedFadeout= true;

			if(App.get().m_ctxCur != null)
				draw();
		}else if(!m_isFinishedActivate){
			if(m_ctxTo == null){
				if(!App.get().m_arObj.isEmpty())
					m_ctxTo= (Context)App.get().m_arObj.get(m_nCtxTo);

				return false;
			}

			if(m_ctxFrom != null)
				m_ctxFrom.deActivate();

			App.get().m_ctxCur= m_ctxTo;

//			We don't re-activate the old context
//			m_ctxTo.activate();
			m_ctxTo.manage(1.0f);
			draw();
			m_isFinishedActivate= true;
		}else if(!m_isFinishedFadeIn){
			m_fAlphaFade-= m_fIncAlpha;
			if(m_fAlphaFade <= 0.0){
				m_isFinishedFadeIn= true;
				App.get().m_objContextSwitcher= new MngContextSwitcher();
			}

			draw();
		}

		return (m_isActive= !m_isFinishedFadeIn);
	}

	public boolean manage(){
		if(m_isPopup)
			return managePopUp();
		else
			return managePopOut();

	}
}
