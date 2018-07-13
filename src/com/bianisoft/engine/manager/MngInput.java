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


//Standard LWJGL imports
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.helper.datatypes.Char;


public final class MngInput{
	public interface I_Callback{
		public void callbackTextASCII(char p_text);
	};

	public static final int K_A= Keyboard.KEY_A;	public static final int K_B= Keyboard.KEY_B;
	public static final int K_C= Keyboard.KEY_C;	public static final int K_D= Keyboard.KEY_D;
	public static final int K_E= Keyboard.KEY_E;	public static final int K_F= Keyboard.KEY_F;
	public static final int K_G= Keyboard.KEY_G;	public static final int K_H= Keyboard.KEY_H;
	public static final int K_I= Keyboard.KEY_I;	public static final int K_J= Keyboard.KEY_J;
	public static final int K_K= Keyboard.KEY_K;	public static final int K_L= Keyboard.KEY_L;
	public static final int K_M= Keyboard.KEY_M;	public static final int K_N= Keyboard.KEY_N;
	public static final int K_O= Keyboard.KEY_O;	public static final int K_P= Keyboard.KEY_P;
	public static final int K_Q= Keyboard.KEY_Q;	public static final int K_R= Keyboard.KEY_R;
	public static final int K_S= Keyboard.KEY_S;	public static final int K_T= Keyboard.KEY_T;
	public static final int K_U= Keyboard.KEY_U;	public static final int K_V= Keyboard.KEY_V;
	public static final int K_W= Keyboard.KEY_W;	public static final int K_X= Keyboard.KEY_X;
	public static final int K_Y= Keyboard.KEY_Y;	public static final int K_Z= Keyboard.KEY_Z;

	public static final int K_0= Keyboard.KEY_0;	public static final int K_1= Keyboard.KEY_1;
	public static final int K_2= Keyboard.KEY_2;	public static final int K_3= Keyboard.KEY_3;
	public static final int K_4= Keyboard.KEY_4;	public static final int K_5= Keyboard.KEY_5;
	public static final int K_6= Keyboard.KEY_6;	public static final int K_7= Keyboard.KEY_7;
	public static final int K_8= Keyboard.KEY_8;	public static final int K_9= Keyboard.KEY_9;

	public static final int K_MINUS				= Keyboard.KEY_MINUS;
	public static final int K_EQUALS			= Keyboard.KEY_EQUALS;

	public static final int K_LEFT_ALT		= Keyboard.KEY_LMENU;
	public static final int K_RIGHT_ALT		= Keyboard.KEY_RMENU;
	public static final int K_ESCAPE		= Keyboard.KEY_ESCAPE;
	public static final int K_ENTER			= Keyboard.KEY_RETURN;
	public static final int K_SPACE			= Keyboard.KEY_SPACE;
	public static final int K_ARROW_LEFT	= Keyboard.KEY_LEFT;
	public static final int K_ARROW_UP		= Keyboard.KEY_UP;
	public static final int K_ARROW_RIGHT	= Keyboard.KEY_RIGHT;
	public static final int K_ARROW_DOWN	= Keyboard.KEY_DOWN;
	public static final int K_TAB			= Keyboard.KEY_TAB;
	public static final int K_DELETE		= Keyboard.KEY_DELETE;
	public static final int K_BACKSPACE		= Keyboard.KEY_BACK;
	public static final int K_LAST			= Keyboard.KEYBOARD_SIZE;

	public static final int K_F1	= Keyboard.KEY_F1;	public static final int K_F2	= Keyboard.KEY_F2;
	public static final int K_F3	= Keyboard.KEY_F3;	public static final int K_F4	= Keyboard.KEY_F4;
	public static final int K_F5	= Keyboard.KEY_F5;	public static final int K_F6	= Keyboard.KEY_F6;
	public static final int K_F7	= Keyboard.KEY_F7;	public static final int K_F8	= Keyboard.KEY_F8;
	public static final int K_F9	= Keyboard.KEY_F9;	public static final int K_F10	= Keyboard.KEY_F10;
	public static final int K_F11	= Keyboard.KEY_F11;	public static final int K_F12	= Keyboard.KEY_F12;

	public static final int M_LEFT	= 0x0001;
	public static final int M_MIDDLE= 0x0002;
	public static final int M_RIGHT	= 0x0004;

	private static MngInput m_objMe;


	public boolean[]	m_bKeyboardBack= new boolean[K_LAST];
	public boolean[]	m_bKeyboard= new boolean[K_LAST];

	public int		m_nMouse;
	public int		m_nMouseBack;

	private int		m_nMouseX;
	private int		m_nMouseY;
	private int		m_nMouseZ;
	private int		m_nMouseBackX;
	private int		m_nMouseBackY;
	private int		m_nMouseBackZ;
	private int		m_nMouseDeltaX;
	private int		m_nMouseDeltaY;
	private int		m_nMouseDeltaZ;

	public boolean	m_isMouseGrabbed;


	public MngInput()				{m_objMe= this;}
	public static MngInput get()	{return m_objMe;}
	
	public int getMouseX()			{return m_nMouseX;}
	public int getMouseY()			{return m_nMouseY;}
	public int getMouseZ()			{return m_nMouseZ;}
	public int getMouseDeltaX()		{return m_nMouseDeltaX;}
	public int getMouseDeltaY()		{return m_nMouseDeltaY;}
	public int getMouseDeltaZ()		{return m_nMouseDeltaZ;}

	public void grabMouse()			{Mouse.setGrabbed(m_isMouseGrabbed= false);}
	public void ungrabMouse()		{Mouse.setGrabbed(m_isMouseGrabbed= false);}
	public boolean isMouseGrabbed()	{return m_isMouseGrabbed;}


	public void create() throws LWJGLException{
		//Keyboard
		Keyboard.create();

		//Mouse
		grabMouse();
		Mouse.create();
	}

	public void destroy(){
		Mouse.destroy();
		Keyboard.destroy();
	}

	public Char convertKeyboardToASCII(int p_nInput){
		if((p_nInput >= K_1) && p_nInput <= K_0)
			return new Char((char)(((int)'1' + p_nInput) % 10));
		if((p_nInput >= K_Q) && p_nInput <= K_0)
			return new Char((char)(((int)'1' + p_nInput) % 10));
		return null;
	}

	public int convertIndexToMouseCode(int p_nInput){
		switch(p_nInput){
		case 0:	return M_LEFT;
		case 2:	return M_MIDDLE;
		case 1:	return M_RIGHT;
		}
		return 0xFFFFFFFF;
	}

	public int convertMouseToIndexCode(int p_nInput){
		switch(p_nInput){
		case M_LEFT:	return 0;
		case M_MIDDLE:	return 2;
		case M_RIGHT:	return 1;
		}
		return 0xFFFFFFFF;
	}

	public void searchBufferForASCII(I_Callback p_objCallback){
		while(Keyboard.next()){
			char c= Keyboard.getEventCharacter();

			if(c != 0x0)
				p_objCallback.callbackTextASCII(c);
		}
	}

	public void reInit(){
		for(int i= 0; i < K_LAST; ++i)
			m_bKeyboardBack[i]= m_bKeyboardBack[i]= false;

		m_nMouse= m_nMouseBack= 0;
	}

	public void manage(){
		m_nMouseDeltaX= -(m_nMouseX - m_nMouseBackX);
		m_nMouseDeltaY= m_nMouseY - m_nMouseBackY;
		m_nMouseDeltaZ= m_nMouseZ - m_nMouseBackZ;

		//Back buffer for mouse position
		m_nMouseBackX= m_nMouseX;
		m_nMouseBackY= m_nMouseY;
		m_nMouseBackZ= m_nMouseZ;
		m_nMouseX= Mouse.getX();
		m_nMouseY= Mouse.getY();
		m_nMouseZ+= (m_nMouseDeltaZ= Mouse.getDWheel());

		//Back buffer for mouse Button
		m_nMouseBack= m_nMouse;
		for(int i= 0; i < 3; ++i){
			if(Mouse.isButtonDown(i))
				m_nMouse|= convertIndexToMouseCode(i);
			else
				m_nMouse&= ~convertIndexToMouseCode(i);
		}

		//Back buffer for keyboard
		for(int i= 0; i < K_LAST; ++i)
			m_bKeyboardBack[i]= m_bKeyboard[i];
		for(int i= 0; i < K_LAST; ++i)
			m_bKeyboard[i]= Keyboard.isKeyDown(i);

		if(isKeyboardClicked(K_LEFT_ALT) || isKeyboardClicked(K_RIGHT_ALT))
			Mouse.setGrabbed(m_isMouseGrabbed= !m_isMouseGrabbed);
		if((isKeyboardDown(K_LEFT_ALT)||isKeyboardDown(K_RIGHT_ALT)) && isKeyboardClicked(K_F4))
			App.exit();
		if(isMouseClicked(M_LEFT))
			grabMouse();
	}



	public boolean isMouseDown(int p_nKeyCode){
		return (m_nMouse&p_nKeyCode)==p_nKeyCode;
	}

	public boolean isMouseClicked(int p_nKeyCode){
		return ((m_nMouse&p_nKeyCode)!=p_nKeyCode) && ((m_nMouseBack&p_nKeyCode)==p_nKeyCode);
	}

	public boolean isKeyboardDown(int p_nKeyCode){
		return m_bKeyboard[p_nKeyCode];
	}

	public boolean isKeyboardClicked(int p_nKeyCode){
		return !m_bKeyboard[p_nKeyCode] && m_bKeyboardBack[p_nKeyCode];
	}
}
