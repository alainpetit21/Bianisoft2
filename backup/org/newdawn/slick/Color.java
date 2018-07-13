//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick;

//Standard Java imports
import java.io.Serializable;
import java.nio.FloatBuffer;

//Slick-utils imports
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.I_SGL;


public class Color implements Serializable{
	private static final long serialVersionUID= 1393939L;
	
	protected static I_SGL GL= Renderer.get();
	
	public static final Color WHITE		= new Color(1.0f,1.0f,1.0f,1.0f);
	public static final Color YELLOW	= new Color(1.0f,1.0f,0,1.0f);
	public static final Color RED		= new Color(1.0f,0,0,1.0f);
	public static final Color BLUE		= new Color(0,0,1.0f,1.0f);
	public static final Color GREEN		= new Color(0,1.0f,0,1.0f);
	public static final Color BLACK		= new Color(0,0,0,1.0f);
	public static final Color GREY		= new Color(0.5f,0.5f,0.5f,1.0f);
	public static final Color CYAN		= new Color(0,1.0f,1.0f,1.0f);
	public static final Color DARK_GREY = new Color(0.3f,0.3f,0.3f,1.0f);
	public static final Color LIGHT_GREY= new Color(0.7f,0.7f,0.7f,1.0f);
    public static final Color PINK		= new Color(255, 175, 175, 255);
    public static final Color ORANGE	= new Color(255, 200, 0, 255);
    public static final Color MAGENTA	= new Color(255, 0, 255, 255);
    
	public float m_nRed;
	public float m_nGreen;
	public float m_nBlue;
	public float m_nAlpha= 1.0f;


	public Color(Color p_color){
		m_nRed	= p_color.m_nRed;
		m_nGreen= p_color.m_nGreen;
		m_nBlue	= p_color.m_nBlue;
		m_nAlpha= p_color.m_nAlpha;
	}

	public Color(FloatBuffer buffer){
		m_nRed	= buffer.get();
		m_nGreen= buffer.get();
		m_nBlue	= buffer.get();
		m_nAlpha= buffer.get();
	}
	
	public Color(float p_nRed, float p_nGreen, float p_nBlue){
		m_nRed	= p_nRed;
		m_nGreen= p_nGreen;
		m_nBlue	= p_nBlue;
		m_nAlpha= 1;
	}

	public Color(float p_nRed, float p_nGreen, float p_nBlue, float p_nAlpha) {
		m_nRed	= Math.min(p_nRed, 1);
		m_nGreen= Math.min(p_nGreen, 1);
		m_nBlue	= Math.min(p_nBlue, 1);
		m_nAlpha= Math.min(p_nAlpha, 1);
	}

	public Color(int p_nRed, int p_nGreen, int p_nBlue) {
		m_nRed	= p_nRed / 255.0f;
		m_nGreen= p_nGreen / 255.0f;
		m_nBlue	= p_nBlue / 255.0f;
		m_nAlpha= 1;
	}

	public Color(int p_nRed, int p_nGreen, int p_nBlue, int p_nAlpha) {
		m_nRed	= p_nRed / 255.0f;
		m_nGreen= p_nGreen / 255.0f;
		m_nBlue	= p_nBlue / 255.0f;
		m_nAlpha= p_nAlpha / 255.0f;
	}
	
	public Color(int value) {
		int a= (value & 0xFF000000) >> 24;
		int r= (value & 0x00FF0000) >> 16;
		int g= (value & 0x0000FF00) >> 8;
		int b= (value & 0x000000FF);
				
		if(a < 0)
			a += 256;
		if(a == 0)
			a= 255;
		
		m_nRed	= r / 255.0f;
		m_nGreen= g / 255.0f;
		m_nBlue	= b / 255.0f;
		m_nAlpha= a / 255.0f;
	}

	//FIXME - why are these the same as *_Byte ???
	public int getRed()		{return (int) (m_nRed * 255);}
	public int getGreen()	{return (int) (m_nGreen * 255);}
	public int getBlue()	{return (int) (m_nBlue * 255);}
	public int getAlpha()	{return (int) (m_nAlpha * 255);}

	public int getRedByte()		{return (int) (m_nRed * 255);}
	public int getGreenByte()	{return (int) (m_nGreen * 255);}
	public int getBlueByte()	{return (int) (m_nBlue * 255);}
	public int getAlphaByte()	{return (int) (m_nAlpha * 255);}

	public void bind(){
		GL.glColor4f(m_nRed, m_nGreen, m_nBlue, m_nAlpha);
	}
	
	
	public boolean equals(Object p_objOther){
		if(p_objOther instanceof Color){
			Color objColor= (Color) p_objOther;
			return ((objColor.m_nRed == m_nRed) && (objColor.m_nGreen == m_nGreen) && (objColor.m_nBlue == m_nBlue) && (objColor.m_nAlpha == m_nAlpha));
		}
		
		return false;
	}
	
	public Color darker(){
		return darker(0.5f);
	}
	
	public Color darker(float p_nScale){
        p_nScale= 1 - p_nScale;
		Color temp= new Color(m_nRed * p_nScale, m_nGreen * p_nScale, m_nBlue * p_nScale, m_nAlpha);
		
		return temp;
	}

	public Color brighter(){
		return brighter(0.2f);
	}

	public Color brighter(float p_nScale){
        p_nScale+= 1;
		Color temp= new Color(m_nRed * p_nScale, m_nGreen * p_nScale, m_nBlue * p_nScale, m_nAlpha);

		return temp;
	}

	public Color multiply(Color p_objColor){
		return new Color(m_nRed * p_objColor.m_nRed, m_nGreen * p_objColor.m_nGreen, m_nBlue * p_objColor.m_nBlue, m_nAlpha * p_objColor.m_nAlpha);
	}

	public void add(Color p_objColor){
		m_nRed	+= p_objColor.m_nRed;
		m_nGreen+= p_objColor.m_nGreen;
		m_nBlue	+= p_objColor.m_nBlue;
		m_nAlpha+= p_objColor.m_nAlpha;
	}

	public void scale(float p_nValue){
		m_nRed	*= p_nValue;
		m_nGreen*= p_nValue;
		m_nBlue	*= p_nValue;
		m_nAlpha*= p_nValue;
	}

	//FIXME - what if value go over 1.0 or 255???
	public Color addToCopy(Color p_obColor){
		Color copy= new Color(m_nRed, m_nGreen, m_nBlue, m_nAlpha);

		copy.m_nRed		+= p_obColor.m_nRed;
		copy.m_nGreen	+= p_obColor.m_nGreen;
		copy.m_nBlue	+= p_obColor.m_nBlue;
		copy.m_nAlpha	+= p_obColor.m_nAlpha;

		return copy;
	}

	public Color scaleCopy(float p_nValue){
		Color copy= new Color(m_nRed, m_nGreen, m_nBlue, m_nAlpha);

		copy.m_nRed		*= p_nValue;
		copy.m_nGreen	*= p_nValue;
		copy.m_nBlue	*= p_nValue;
		copy.m_nAlpha	*= p_nValue;

		return copy;
	}

	public static Color decode(String p_stEncodedColor){
		return new Color(Integer.decode(p_stEncodedColor).intValue());
	}

	
	public int hashCode(){
		return ((int) (m_nRed+m_nGreen+m_nBlue+m_nAlpha)*255);
	}

	
	public String toString(){
		return "Color ("+m_nRed+","+m_nGreen+","+m_nBlue+","+m_nAlpha+")";
	}
}
