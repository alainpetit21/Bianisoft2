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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

//LWJGL library imports
import org.lwjgl.util.Rectangle;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Drawable;
import com.bianisoft.engine.resmng.BufferedImageUtil;
import com.bianisoft.engine.resmng.Texture;
import com.bianisoft.engine.resmng.FontCache;


public class Label extends Drawable{
	public static final int TYPE_NORMAL= 0;
	public static final int TYPE_GRADUAL= 1;
	public static final int TYPE_TEXTFIELD= 2;

	public static final int MODE_LEFT= 0;
	public static final int MODE_CENTER= 1;
	public static final int MODE_RIGHT= 2;

	//Only used temporarily to getMetric().getCharWidht


	public Font			m_font;
	public String		m_stText;
	public Rectangle	m_recLimit;
	public int			m_nMode;
	public String		m_stFontName;
	public int			m_nFontSize;
	public boolean		m_isMultiline;

	private int			m_nOffsetChar= 0;
	private float		m_nOffsetY= 0;

	private Texture			m_imgPreRendered;

	public boolean			m_isScrollable= false;
	protected boolean		m_isDirty= true;

	public Label()	{this("", 0, "", MODE_LEFT, false, new Rectangle());}
	public Label(String p_stFontName, int p_nFontSize, String p_stText, int p_nMode, boolean p_isMultiline, Rectangle p_rect){
		super(IDCLASS_Label);
		setSubClassID(TYPE_NORMAL);

		m_stText		= p_stText;
		m_recLimit		= p_rect;
		m_stFontName	= p_stFontName;
		m_nFontSize		= p_nFontSize;
		m_nMode			= p_nMode;
		m_isMultiline	= p_isMultiline;
	}

	public Label(Label p_lblRef){
		super(IDCLASS_Label);
		setSubClassID(TYPE_NORMAL);

		m_stText		= p_lblRef.m_stText;
		m_recLimit		= p_lblRef.m_recLimit;
		m_stFontName	= p_lblRef.m_stFontName;
		m_nFontSize		= p_lblRef.m_nFontSize;
		m_nMode			= p_lblRef.m_nMode;
		m_isMultiline	= p_lblRef.m_isMultiline;
		m_font			= p_lblRef.m_font;
	}

	public void load(){
		m_font= FontCache.getFontFromFile(m_stFontName, m_nFontSize);
	}

	public int getMetricHeight(){
		int		y= m_nFontSize;
		
		if(m_isMultiline){
			char[]	temp	= new char[256];
			int		cptSrc	= 0;

			while(cptSrc < m_stText.length()){
				int		nLastCptSrc		= 0;
				int		nLastCptDst		= -1;
				int		nX				= 0;
				int		cptDst			= 0;

				for(int i= 0; i < 256; ++i)
					temp[i]= 0;
				while(cptSrc < m_stText.length()){
					if(((m_stText.charAt(cptSrc) == '\\') && (m_stText.charAt(cptSrc+1) == 'n'))){
						cptSrc+=2;
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
						break;
					}else if(m_stText.charAt(cptSrc) == '\n'){
						cptSrc+=1;
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
						break;
					}

					if(m_stText.charAt(cptSrc) == ' '){
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
					}

					temp[cptDst++]= m_stText.charAt(cptSrc++);

					// put new line in last good spot
					int nLetterWidth=  m_nFontSize;
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

				if(cptSrc == m_stText.length()){
					nLastCptDst= cptDst;
				}

				y+= m_nFontSize;
			}
		}
		
		return y;
	}

	public int getMetricWidth(){
		return m_recLimit.getWidth();
	}

	public void preRenderLine(Graphics2D p_objJava2d, int p_nX, int p_nY, String p_stText){
		FontMetrics objMetric= p_objJava2d.getFontMetrics();
		int	nLeft	= p_nX;
		int	nTop	= p_nY + objMetric.getAscent();
		int	nRight	= p_nX + m_recLimit.getWidth();
		int nTextWidth	= objMetric.stringWidth(p_stText);

		switch(m_nMode){
		case MODE_LEFT:
			p_objJava2d.drawString(p_stText, nLeft, nTop);
		break;
		case MODE_CENTER:
			p_objJava2d.drawString(p_stText, nLeft + ((nRight-nLeft)/2) - (nTextWidth/2), nTop);
		break;
		case MODE_RIGHT:
			p_objJava2d.drawString(p_stText, nRight-nTextWidth, nTop);
		break;
		}
	}

	public void preRender(BufferedImage	p_bufImage){
		Graphics2D objJava2d= (Graphics2D)p_bufImage.getGraphics();
		m_isDirty= false;

		if(m_imgPreRendered != null)
			m_imgPreRendered.release();

		objJava2d.setColor(Color.WHITE);
		objJava2d.setFont(m_font);

		if(!m_isMultiline){
			preRenderLine(objJava2d, 0, 0, m_stText.substring(m_nOffsetChar));
		}else{
			char[]	temp	= new char[256];
			int		cptSrc	= m_nOffsetChar;
			int		y		= 0;

			while(cptSrc < m_stText.length()){
				int		nLastCptSrc	= 0;
				int		nLastCptDst	= -1;
				int		nX			= 0;
				int		cptDst		= 0;

				for(int i= 0; i < 256; ++i)
					temp[i]= 0;
				while(cptSrc < m_stText.length()){
					if(((m_stText.charAt(cptSrc) == '\\') && (m_stText.charAt(cptSrc+1) == 'n'))){
						cptSrc+=2;
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
						break;
					}else if(m_stText.charAt(cptSrc) == '\n'){
						cptSrc+=1;
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
						break;
					}

					if(m_stText.charAt(cptSrc) == ' '){
						nLastCptDst		= cptDst;
						nLastCptSrc		= cptSrc;
					}

					temp[cptDst++]= m_stText.charAt(cptSrc++);

					// put new line in last good spot
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

				if(cptSrc == m_stText.length()){
					nLastCptDst= cptDst;
				}

				String toDraw;
				toDraw= new String(temp, 0, nLastCptDst);
				preRenderLine(objJava2d, 0, y, toDraw);
				y+= m_nFontSize;
			}
		}

		m_imgPreRendered= BufferedImageUtil.getTexture(p_bufImage);
	}

	public boolean click()			{return false;}
	public boolean doubleClick()	{return false;}

	public boolean drag(int p_nX, int p_nY){
		if(m_isScrollable){
			addPixelOffsetY(p_nY);
			return true;
		}
		return false;
	}

	public void set(int p_nValue)	{set(Integer.toString(p_nValue));}
	public void set(String p_stText){
		if(m_stText.equals(p_stText))
			return;

		m_stText= p_stText;
		m_isDirty= true;
	}
	
	public void addPixelOffsetY(float p_nOffsetY)	{setOffsetY(m_nOffsetY + (((float)p_nOffsetY/(float)m_recLimit.getHeight())*m_imgPreRendered.getHeight()));}
	public void setPixelOffsetY(float p_nOffsetY)	{setOffsetY(p_nOffsetY*m_imgPreRendered.getHeight());}
	public void setOffsetY(float p_nOffsetY){
		m_nOffsetY= p_nOffsetY;

		if(m_nOffsetY > (m_imgPreRendered.getHeight() - (m_imgPreRendered.getHeight()*((float)m_recLimit.getHeight()/(float)m_imgPreRendered.getImageHeight()))))
			m_nOffsetY= (m_imgPreRendered.getHeight() - (m_imgPreRendered.getHeight()*((float)m_recLimit.getHeight()/(float)m_imgPreRendered.getImageHeight())));

		if(m_nOffsetY < 0)
			m_nOffsetY= 0;
	}

	public void set(float p_nValue, int p_nNbDigit){
		int		intValue= (int)p_nValue;
		float	decValue= p_nValue - intValue;

		decValue*= Math.pow(10, p_nNbDigit);

		String	intPart= Integer.toString(intValue);
		String	digitPart= Integer.toString((int)(decValue));

		for(int i= 0; i < p_nNbDigit-1; ++i){
			if(decValue < (10 ^ i)){
				digitPart= "0" + digitPart;
			}
		}

		set(intPart + "." + digitPart);
	}

	public void append(String p_stText){
		m_stText+= p_stText;
		m_isDirty= true;
	}
	
	public void manage(float p_fTimeScaleFactor) {
		super.manage(p_fTimeScaleFactor);

		if(m_isDirty){
			//Recreate a backBuffer
			int nWidth= getMetricWidth();
			int nHeight= getMetricHeight();

			preRender(new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_4BYTE_ABGR));
		}
	}
	
	public void draw(){
		if((!isShown()) || (m_stText == null))
			return;

		glPushMatrix();

		m_imgPreRendered.bind();

		float	nSrcUVLeft	= 0;
		float	nSrcUVTop	= m_nOffsetY;
		float	nSrcUVRight	= nSrcUVLeft + m_imgPreRendered.getWidth();
		float	nSrcUVBottom= nSrcUVTop + m_imgPreRendered.getHeight();

		float	nDestLeft	= m_recLimit.getX();
		float	nDestTop	= m_recLimit.getY();
		float	nDestRight	= nDestLeft + m_recLimit.getWidth();
		float	nDestBottom	= nDestTop + m_recLimit.getHeight();

		if(m_imgPreRendered.getImageHeight() < m_recLimit.getHeight()){
			nDestBottom= nDestTop + m_imgPreRendered.getImageHeight();
			nSrcUVBottom=  m_imgPreRendered.getHeight();
		}else if(m_imgPreRendered.getImageHeight() > m_recLimit.getHeight()){
			float nRatioRequested=  (float)m_recLimit.getHeight() / (float)m_imgPreRendered.getImageHeight();

			nSrcUVBottom= nSrcUVTop + (m_imgPreRendered.getHeight() * nRatioRequested);
		}

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTranslated(getPosX(), getPosY(), 0);
		glScalef(m_nZoom, m_nZoom, 1);
		glColor4d(m_colorFilterRed, m_colorFilterGreen, m_colorFilterBlue, m_colorFilterAlpha);

	    glBegin(GL_QUADS);
			glTexCoord2d(nSrcUVLeft, nSrcUVBottom);
			glVertex2d(nDestLeft, nDestBottom);

			glTexCoord2d(nSrcUVRight, nSrcUVBottom);
			glVertex2d(nDestRight, nDestBottom);

			glTexCoord2d(nSrcUVRight, nSrcUVTop);
			glVertex2d(nDestRight, nDestTop);

			glTexCoord2d(nSrcUVLeft, nSrcUVTop);
			glVertex2d(nDestLeft, nDestTop);
		glEnd ();
		glPopMatrix();

		//Print Debug Rect
		if(App.PRINT_DEBUG)
			drawDebug();
	}

	public void drawDebug(){
		glPushMatrix();

		int	nCumulLeft	= m_recLimit.getX();
		int	nCumulTop	= m_recLimit.getY();
		int	nCumulRight	= nCumulLeft + m_recLimit.getWidth();
		int	nCumulBottom= nCumulTop + m_recLimit.getHeight();

		glTranslated(getPosX(), getPosY(), 0);
		glScalef(m_nZoom, m_nZoom, 1);
		glColor3d(1.0, 1.0, 1.0);

		Texture.bindNone();
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glBegin(GL_LINE_LOOP);
			glVertex2d(nCumulLeft, nCumulTop);
			glVertex2d(nCumulRight, nCumulTop);
			glVertex2d(nCumulRight, nCumulBottom);
			glVertex2d(nCumulLeft, nCumulBottom);
		glEnd();

		glColor3d(0.0, 0.0, 1.0);
		glBegin(GL_LINE);
			switch(m_nMode){
			case MODE_LEFT:
				glVertex2d(nCumulLeft, nCumulTop);
				glVertex2d(nCumulLeft, nCumulBottom);
			break;
			case MODE_CENTER:
				glVertex2d((nCumulLeft + ((nCumulRight-nCumulLeft)/2)), nCumulTop);
				glVertex2d((nCumulLeft + ((nCumulRight-nCumulLeft)/2)), nCumulBottom);
			break;
			case MODE_RIGHT:
				glVertex2d(nCumulRight, nCumulTop);
				glVertex2d(nCumulRight, nCumulBottom);
			}
		glEnd();

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();
	}

	public String toString() {
		return "Label @ " + (int)getPosX() + ";"+ (int)getPosY() + ";"+ (int)getPosZ() + ";";
	}
}
