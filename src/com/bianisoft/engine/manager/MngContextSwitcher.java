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


//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.Context;
import com.bianisoft.engine.resmng.Texture;


public class MngContextSwitcher{
	protected boolean	m_isFinishedFadeout	= false;
	protected boolean	m_isFinishedActivate= false;
	protected boolean	m_isFinishedFadeIn	= false;
	protected float		m_fAlphaFade		= 0.0f;
	protected float		m_fIncAlpha			= 0.05f;

	protected boolean	m_isActive= false;
	protected Context	m_ctxFrom;
	protected Context	m_ctxTo;
	protected int		m_nCtxTo= -1;


	public void set(Context p_ctxCur, Context p_ctxTo){
		m_ctxFrom= p_ctxCur;
		m_ctxTo= p_ctxTo;

		m_isFinishedFadeout	= false;
		m_isFinishedActivate= false;
		m_isFinishedFadeIn	= false;
		m_isActive= true;
	}

	public void set(Context p_ctxCur, int p_nCtxTo){
		m_ctxFrom= p_ctxCur;
		m_nCtxTo= p_nCtxTo;
		m_isActive= true;
	}

	public boolean isActive()	{return m_isActive;}

	public boolean manage(){
		if(!m_isFinishedFadeout){
			m_fAlphaFade+= m_fIncAlpha;
			if(m_fAlphaFade >= 1.0)
				m_isFinishedFadeout= true;

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
			m_ctxTo.activate();
			m_ctxTo.manage(1.0f);
			draw();
			m_isFinishedActivate= true;
		}else if(!m_isFinishedFadeIn){
			m_fAlphaFade-= m_fIncAlpha;
			if(m_fAlphaFade <= 0.0)
				m_isFinishedFadeIn= true;

			draw();
		}

		return (m_isActive= !m_isFinishedFadeIn);
	}

	protected void draw(){
		Camera.getCur(Camera.TYPE_2D).doScreenProjection();

		//Initiate Draw
		glClear(GL_COLOR_BUFFER_BIT);
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();

		//Draw 2D
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		if(App.get().m_ctxCur != null)
			App.get().m_ctxCur.draw();

		glPushMatrix();

		glColor4d(0, 0, 0, m_fAlphaFade);
		Texture.bindNone();
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

	    glBegin(GL_QUADS);
			glVertex2d(-(App.g_nWidth/2),  (App.g_nHeight/2));
			glVertex2d( (App.g_nWidth/2),  (App.g_nHeight/2));
			glVertex2d( (App.g_nWidth/2), -(App.g_nHeight/2));
			glVertex2d(-(App.g_nWidth/2), -(App.g_nHeight/2));
		glEnd();

		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}
}
