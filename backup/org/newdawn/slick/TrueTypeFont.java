//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick;


//Standard Java imports
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//Slick-utils library imports
import org.newdawn.slick.opengl.GLUtils;
import org.newdawn.slick.opengl.I_Texture;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.I_SGL;
import org.newdawn.slick.util.BufferedImageUtil;


public class TrueTypeFont implements I_Font{
	private static final I_SGL GL= Renderer.get();

	private FontMetrics m_objFontMetrics;
	private Font m_objFont;

	private CharObject[] m_arChar= new CharObject[256];
	private Map m_mapCustomChars = new HashMap();
	private I_Texture m_texFont;

	private boolean m_isAntiAlias;
	private int m_nFontSize		= 0;
	private int m_nFontHeight	= 0;
	private int m_nTexWidth		= 512;
	private int m_nTexHeight	= 512;

	private class CharObject {
		public int m_nWidth;
		public int m_nHeight;
		public int m_nLocX;
		public int m_nLocY;
	}

	public TrueTypeFont(java.awt.Font p_objFont, boolean p_isAntiAlias)	{this(p_objFont, p_isAntiAlias, null);}
	public TrueTypeFont(java.awt.Font p_objFont, boolean p_isAntiAlias, char[] p_stAdditionalChars){
		GLUtils.checkGLContext();
		
		m_objFont= p_objFont;
		m_nFontSize= p_objFont.getSize();
		m_isAntiAlias= p_isAntiAlias;

		createSet(p_stAdditionalChars);
	}

	public java.awt.Font getJavaFont()			{return m_objFont;}
	public int getHeight()						{return m_nFontHeight;}
	public int getHeight(String HeightString)	{return m_nFontHeight;}
	public int getLineHeight()					{return m_nFontHeight;}

	private BufferedImage getFontImage(char p_char){
		BufferedImage tempfontImage= new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g= (Graphics2D)tempfontImage.getGraphics();

		if(m_isAntiAlias == true)
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);

		g.setFont(m_objFont);
		m_objFontMetrics= g.getFontMetrics();

		int charwidth= m_objFontMetrics.charWidth(p_char);
		if(charwidth <= 0)
			charwidth= 1;

		int charheight = m_objFontMetrics.getHeight();
		if(charheight <= 0)
			charheight= m_nFontSize;

		// Create another image holding the character we are creating
		BufferedImage fontImage= new BufferedImage(charwidth, charheight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt= (Graphics2D) fontImage.getGraphics();

		if(m_isAntiAlias == true)
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		gt.setFont(m_objFont);
		gt.setColor(Color.WHITE);

		int charx= 0;
		int chary= 0;
		gt.drawString(String.valueOf(p_char), charx, chary + m_objFontMetrics.getAscent());

		return fontImage;
	}

	private void createSet(char[] p_arCustomChars){
		// If there are custom chars then I expand the font texture twice		
		if(p_arCustomChars != null && p_arCustomChars.length > 0){
			m_nTexWidth *= 2;
		}
		
		// In any case this should be done in other way. I_Texture with size 512x512 can maintain only 256 characters
		// with resolution of 32x32. The texture size should be calculated dynamicaly by looking at character sizes.
		try {
			BufferedImage imgTemp= new BufferedImage(m_nTexWidth, m_nTexHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g= (Graphics2D) imgTemp.getGraphics();

			g.setColor(new Color(0, 0, 1, 0));
			g.fillRect(0, 0, m_nTexWidth, m_nTexHeight);
			
			int rowHeight= 0;
			int positionX= 0;
			int positionY= 0;
			
			int customCharsLength= (p_arCustomChars != null)? p_arCustomChars.length:0;

			for(int i= 0; i < 256 + customCharsLength; i++){
				// get 0-255 characters and then custom characters
				char ch= (i < 256)? (char)i:p_arCustomChars[i-256];
				
				BufferedImage fontImage= getFontImage(ch);
				CharObject newCharObject= new CharObject();

				newCharObject.m_nWidth= fontImage.getWidth();
				newCharObject.m_nHeight= fontImage.getHeight();

				if(positionX + newCharObject.m_nWidth >= m_nTexWidth){
					positionX= 0;
					positionY+= rowHeight;
					rowHeight= 0;
				}

				newCharObject.m_nLocX= positionX;
				newCharObject.m_nLocY= positionY;

				if(newCharObject.m_nHeight > m_nFontHeight)
					m_nFontHeight = newCharObject.m_nHeight;

				if(newCharObject.m_nHeight > rowHeight)
					rowHeight = newCharObject.m_nHeight;

				// Draw it here
				g.drawImage(fontImage, positionX, positionY, null);

				positionX+= newCharObject.m_nWidth;

				if(i < 256)						// standard characters
					m_arChar[i]= newCharObject;
				else							// custom characters
					m_mapCustomChars.put(new Character(ch), newCharObject);

				fontImage= null;
			}

			m_texFont= BufferedImageUtil.getTexture(m_objFont.toString(), imgTemp);

		}catch(IOException e){
			System.err.println("Failed to create font.");
			e.printStackTrace();
		}
	}
	
	
	private void drawQuad(float p_nDrawX, float p_nDrawY, float p_nDrawX2, float p_nDrawY2, float p_nSrcX, float p_nSrcY, float p_nSrcX2, float p_nSrcY2){
		float nDrawWidth	= p_nDrawX2 - p_nDrawX;
		float nDrawHeight	= p_nDrawY2 - p_nDrawY;
		float nTextureSrcX	= p_nSrcX / m_nTexWidth;
		float nTextureSrcY	= p_nSrcY / m_nTexHeight;
		float nSrcWidth		= p_nSrcX2 - p_nSrcX;
		float nSrcHeight	= p_nSrcY2 - p_nSrcY;
		float nRenderWidth	= (nSrcWidth / m_nTexWidth);
		float nRenderHeight	= (nSrcHeight / m_nTexHeight);

		GL.glTexCoord2f(nTextureSrcX, nTextureSrcY);
		GL.glVertex2f(p_nDrawX, p_nDrawY);
		GL.glTexCoord2f(nTextureSrcX, nTextureSrcY + nRenderHeight);
		GL.glVertex2f(p_nDrawX, p_nDrawY + nDrawHeight);
		GL.glTexCoord2f(nTextureSrcX + nRenderWidth, nTextureSrcY + nRenderHeight);
		GL.glVertex2f(p_nDrawX + nDrawWidth, p_nDrawY + nDrawHeight);
		GL.glTexCoord2f(nTextureSrcX + nRenderWidth, nTextureSrcY);
		GL.glVertex2f(p_nDrawX + nDrawWidth, p_nDrawY);
	}

	
	public int getWidth(String p_stText){
		CharObject charObject;
		int nTotalwidth= 0;
		int currentChar;

		for(int i= 0; i < p_stText.length(); i++){
			currentChar= p_stText.charAt(i);

			if(currentChar < 256)
				charObject= m_arChar[currentChar];
			else
				charObject= (CharObject)m_mapCustomChars.get(new Character((char)currentChar));
			
			if(charObject != null)
				nTotalwidth+= charObject.m_nWidth;
		}
		return nTotalwidth;
	}

	
	public void drawString(float p_nX, float p_nY, String p_stText, org.newdawn.slick.Color p_objColor){
		drawString(p_nX, p_nY, p_stText, p_objColor, 0, p_stText.length()-1);
	}

	
	public void drawString(float p_nX, float p_nY, String p_stText){
		drawString(p_nX, p_nY, p_stText, org.newdawn.slick.Color.WHITE);
	}

	
	public void drawString(float p_nX, float p_nY, String p_stText, org.newdawn.slick.Color p_objColor, int p_nIdxStart, int p_nIdxEnd){
		p_objColor.bind();
		m_texFont.bind();

		CharObject charObject;
		int charCurrent;

		GL.glEnable(GL.GL_BLEND);
		GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		GL.glBegin(I_SGL.GL_QUADS);

		int nTotalwidth= 0;
		for(int i= 0; i < p_stText.length(); i++){
			charCurrent= p_stText.charAt(i);

			if(charCurrent < 256)
				charObject= m_arChar[charCurrent];
			else
				charObject= (CharObject)m_mapCustomChars.get(new Character((char) charCurrent));
			
			
			if(charObject != null){
				if((i >= p_nIdxStart) || (i <= p_nIdxEnd)){
					drawQuad((p_nX + nTotalwidth), p_nY,
							(p_nX + nTotalwidth + charObject.m_nWidth),
							(p_nY + charObject.m_nHeight), charObject.m_nLocX,
							charObject.m_nLocY, charObject.m_nLocX + charObject.m_nWidth,
							charObject.m_nLocY + charObject.m_nHeight);
				}

				nTotalwidth += charObject.m_nWidth;
			}
		}

		GL.glEnd();
	}
}