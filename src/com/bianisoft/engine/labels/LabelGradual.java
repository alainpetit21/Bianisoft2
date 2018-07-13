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

//Standard Java imports
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

//LWJGL library imports
import org.lwjgl.util.Rectangle;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.helper.datatypes.Real;
import com.bianisoft.engine.resmng.BufferedImageUtil;
import com.bianisoft.engine.resmng.Texture;


public class LabelGradual extends Label{
	private ArrayList<String>	m_vecText= new ArrayList<String>();
	private ArrayList<Real>		m_vecCpt= new ArrayList<Real>();
	private ArrayList<Texture>	m_vecTexture= new ArrayList<Texture>();

	private int		m_nCurLine;
	private float	m_fInc= 0.05f;
	public boolean	m_isPaused;


	public LabelGradual(String p_stFontName, int p_nFontSize, String p_stText, int p_nMode, boolean p_isMultiline, Rectangle p_rect){
		super(p_stFontName, p_nFontSize, p_stText, p_nMode, p_isMultiline, p_rect);
		setSubClassID(TYPE_GRADUAL);
	}
	
	public void set(String p_stText) {
		super.set(p_stText);
		
		//Chop full text into Vector of lines of text
		//Init
		m_vecText= new ArrayList<String>();
		m_vecCpt= new ArrayList<Real>();
		m_vecTexture= new ArrayList<Texture>();
		m_nCurLine= 0;
		m_isPaused= false;

		//Temp object needed to calculate metrics
		BufferedImage	tempImage= new BufferedImage(8, 8, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D		objJava2d= (Graphics2D)tempImage.getGraphics();

		objJava2d.setFont(m_font);

		//Temp variables needed for String calculations
		char[]			temp	= new char[256];
		int				cptSrc	= 0;


		//Loop through Whole Text
		while(cptSrc < m_stText.length()){
			int		nLastCptSrc		= 0;	//Keep track of last space of \n, in whole src buffer
			int		nLastCptDst		= -1;	//Keep track of last space of \n, in dst buffer (on a per-line basis)
			int		nX				= 0;
			int		cptDst			= 0;

			//Within a line loop
			while(cptSrc < m_stText.length()){
				if(((m_stText.charAt(cptSrc) == '\\') && (m_stText.charAt(cptSrc+1) == 'n'))){
					cptSrc+=2;
					temp[cptDst++]	= '\n';
					nLastCptDst		= cptDst;
					nLastCptSrc		= cptSrc;
					break;
				}else if(m_stText.charAt(cptSrc) == '\n'){
					cptSrc+=1;
					temp[cptDst++]	= '\n';
					nLastCptDst		= cptDst;
					nLastCptSrc		= cptSrc;
					break;
				}

				if(m_stText.charAt(cptSrc) == ' '){
					nLastCptDst		= cptDst+1;
					nLastCptSrc		= cptSrc+1;
				}

				temp[cptDst++]= m_stText.charAt(cptSrc++);

				//Put new line in last good spot
				int nLetterWidth=  objJava2d.getFontMetrics().charWidth(temp[cptDst - 1]);
				if((nX + nLetterWidth) > m_recLimit.getWidth()){
					if(nLastCptSrc == 0){
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
					}

					temp[nLastCptDst]	= '\0';
					cptSrc				= nLastCptSrc;
					break;
				}

				nX			+= nLetterWidth;
			}

			if(cptSrc == m_stText.length())
				nLastCptDst= cptDst;

			m_vecText.add(new String(temp, 0, nLastCptDst));
			m_vecCpt.add(new Real(0));
		}

		for(int i= 0; i < m_vecText.size(); ++i){
			tempImage= new BufferedImage(m_recLimit.getWidth(), m_nFontSize*2, BufferedImage.TYPE_4BYTE_ABGR);
			objJava2d= (Graphics2D)tempImage.getGraphics();

			objJava2d.setColor(Color.WHITE);
			objJava2d.setFont(m_font);

			preRenderLine(objJava2d, 0, 0, m_vecText.get(i));
			m_vecTexture.add(BufferedImageUtil.getTexture(tempImage));
		}
	}

	public boolean isDone()				{return ((int)m_nCurLine == (m_vecCpt.size()));}
	public void setSpeed(float p_fInc)	{m_fInc= p_fInc;};
	public void clear(){
		set("");
	}
	
	public void append(String p_stText){
		set(m_stText + p_stText);
	}
	
	public boolean click(){
		m_isPaused= !m_isPaused;
		return false;
	}
	
	public boolean doubleClick(){
		return false;
	}
	
	public void manage(float p_fTimeScaleFactor){
		if(!m_isShown || m_isPaused || isDone())
			return;

		for(int i= 0; i <= m_nCurLine; ++i){
			if(i >= m_vecTexture.size())
				continue;

			Real cpt= m_vecCpt.get(i);

			if((cpt.add(m_fInc)) >= 1.0){
				cpt.set(1.0f);

				if(m_nCurLine == i){
					m_nCurLine++;

					String text= m_vecText.get(i);
					if(text.endsWith("\n")){
						m_isPaused= true;
						break;
					}
				}
			}
		}
	}
	
	public void draw(){
		if((!isShown()) || (m_stText == null))
			return;

		glPushMatrix();

		glTranslated(getPosX(), getPosY(), 0);
		glScalef(m_nZoom, m_nZoom, 1);

		for(int i= 0; i <= m_nCurLine; ++i){
			if(i >= m_vecTexture.size())
				continue;

			Texture texRendered= m_vecTexture.get(i);
			texRendered.bind();

			Real cpt= m_vecCpt.get(i);

			float	nSrcLeft	= 0;
			float	nSrcTop		= 0;
			float	nSrcRight	= texRendered.getWidth() * cpt.get();
			float	nSrcBottom	= texRendered.getHeight();

			float	nDestLeft	= m_recLimit.getX();
			float	nDestTop	= m_recLimit.getY() + (i*m_nFontSize);
			float	nDestRight	= nDestLeft + (m_recLimit.getWidth() * cpt.get());
			float	nDestBottom	= nDestTop + m_nFontSize*2;

			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glColor4d(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);

			glBegin(GL_QUADS);
				glTexCoord2d(nSrcLeft, nSrcBottom);
				glVertex2d(nDestLeft, nDestBottom);

				glTexCoord2d(nSrcRight, nSrcBottom);
				glVertex2d(nDestRight, nDestBottom);

				glTexCoord2d(nSrcRight, nSrcTop);
				glVertex2d(nDestRight, nDestTop);

				glTexCoord2d(nSrcLeft, nSrcTop);
				glVertex2d(nDestLeft, nDestTop);
			glEnd ();
		}
		glPopMatrix();

		//Print Debug Rect
		if(App.PRINT_DEBUG)
			drawDebug();
	}

	public String toString(){
		return "LabelGradual @ " + (int)getPosX() + ";"+ (int)getPosY() + ";"+ (int)getPosZ() + ";";
	}
}
