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
package com.bianisoft.engine.labels;


//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;

//LWJGL Library imports
import org.lwjgl.util.Rectangle;

//Bianisoft imports
import com.bianisoft.engine.manager.MngInput;
import com.bianisoft.engine.resmng.Texture;


public class LabelTextField extends Label{// implements KeyListener{
	public interface I_Callback{
		public boolean callbackLostFocus(LabelTextField p_obj);
	};


	public LabelTextField.I_Callback m_objCallback;


	public LabelTextField(String p_stFontName, int p_nFontSize, String p_stText, int p_nMode, boolean p_isMultiline, Rectangle p_rect){
		super(p_stFontName, p_nFontSize, p_stText, p_nMode, p_isMultiline, p_rect);
		setSubClassID(TYPE_TEXTFIELD);
		setFilterColor(0, 0, 0);
	}
	
	public boolean click(){
		return m_hasKeyFocus= true;
	}

	public void manage(float p_fTimeScaleFactor){
		super.manage(p_fTimeScaleFactor);

		if(!m_hasKeyFocus)
			return;

		final LabelTextField objThis= this;
		MngInput mngInput= MngInput.get();
		mngInput.searchBufferForASCII(new MngInput.I_Callback(){
			public void callbackTextASCII(char p_c){
				if(p_c == '\b'){
					if(m_stText.length() > 0){
						m_stText= m_stText.substring(0, m_stText.length()-1);
						m_isDirty= true;
					}
				}else if(p_c == (char)0x1b){		//ESCAPE
					m_hasKeyFocus= false;
				}else if((p_c == '\n') || (p_c == '\r')){
					if(m_isMultiline){
						m_stText+= '\n';
						m_isDirty= true;
					}else{
						if(m_objCallback != null)
							if(m_objCallback.callbackLostFocus(objThis))
								m_hasKeyFocus= false;
					}
				}else{
					m_stText+= p_c;
					m_isDirty= true;
				}
			}
		});
	}

	public void draw(){
		if(!m_isShown)
			return;
/*test*/
/*		glPushMatrix();

		Texture.bindNone();

		float nDestLeft		= m_recLimit.getX();
		float nDestTop		= m_recLimit.getY();
		float nDestRight	= nDestLeft + m_recLimit.getWidth();
		float nDestBottom	= nDestTop + m_recLimit.getHeight();

		glTranslated(getPosX(), getPosY(), 0);
		glScalef(m_nZoom, m_nZoom, 1);

		if(m_hasKeyFocus)
			glColor3d(1.0, 1.0, 1.0);
		else
			glColor3d(0.75, 0.75, 1.0);

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glBegin(GL_QUADS);
			glVertex2d((nDestLeft+1), (nDestTop+1));
			glVertex2d((nDestRight-1),(nDestTop+1));
			glVertex2d((nDestRight-1), (nDestBottom-1));
			glVertex2d((nDestLeft+1), (nDestBottom-1));
		glEnd();
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glColor3d(0.0, 0.0, 0.0);

		glBegin(GL_LINE);
			glVertex2d((nDestLeft+1), (nDestTop+1));
			glVertex2d((nDestRight-1), (nDestTop+1));
			glVertex2d((nDestRight-1), (nDestBottom-1));
			glVertex2d((nDestLeft+1), (nDestBottom-1));
		glEnd();

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();

		super.draw();*/
	}

	public String toString(){
		return "LabelTextField @ " + (int)getPosX() + ";"+ (int)getPosY() + ";"+ (int)getPosZ() + ";";
	}
}
