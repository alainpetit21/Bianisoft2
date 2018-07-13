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
 * 18/12/10			0.1.0 First beta initial Version.
 * 12/09/11			0.1.2 Moved everything to a com.bianisoft
 *
 *-----------------------------------------------------------------------
 */
package com.bianisoft.tests;


//LWJGL library imports
import com.bianisoft.engine.App;
import org.lwjgl.util.Rectangle;

//Bianisoft imports
import com.bianisoft.engine.Context;
import com.bianisoft.engine.labels.LabelGradual;
import com.bianisoft.engine.manager.MngInput;
import com.bianisoft.engine.sprites.Sprite;


public class CtxQuit extends Context{
	private LabelGradual	m_lblQuit;
	private Sprite			m_sprCursor;

	
	public void activate(){
		super.activate();

		m_lblQuit= (LabelGradual)new LabelGradual("/restest/fonts/DidactGothic.ttf", 25, "", 0, false, new Rectangle(-220, -140, 440, 280));
		m_lblQuit.setTextID("Lbl_Quit");
		m_lblQuit.setPos(0, 0, 6);
		m_lblQuit.load();
		addChild(m_lblQuit);
		m_lblQuit.hide();
		m_lblQuit.set("Now exiting the game ...\nFinished\nYou may now exit your browser...");
		m_lblQuit.setSpeed(0.005f);

		/*DATA_SPRITE_TEMPLATE:#|CLASS_ID|NAME|RESSOURCE_NAME|POS_X|POS_Y|POS_Z|DEFAULT_STATE|DEFAULT_FRAME|NB_STATES|STATE_NAME|STATE_NB_FRAMES|STATE_SPEED*/
		/*DATA:1|Sprite|Spr_Cursor|/restest/sprite/Cursor.png|0|0|0|0|0|1|Idle|1|0.0|*/
		m_sprCursor= new Sprite("/restest/sprites/cursorDummy.png");
		m_sprCursor.setTextID("Spr_Cursor");
		m_sprCursor.setPos(0, 0, 0);
		m_sprCursor.addState(m_sprCursor.new State("Idle", 1, 0.0f));
		m_sprCursor.load();
		m_sprCursor.setCurState(0);
		m_sprCursor.setCurFrame(0);
		addChild(m_sprCursor);
		setCursor(m_sprCursor);

		//Applet will ignore this and will be drawing the Label
		AppTest.exit();
	}

	public boolean keyboardManage(MngInput p_mngInput){
		if(!super.keyboardManage(p_mngInput))
			return false;

		if(p_mngInput.isKeyboardClicked(MngInput.K_F5))
			App.g_theApp.setCurContext(AppTest.IDCTX_QUIT);

		return true;
	}

	public void mouseManage(MngInput p_mngInput){
		super.mouseManage(p_mngInput);

		p_mngInput.ungrabMouse();
	}
	
	public void manage(float p_nRatioMove){
		super.manage(p_nRatioMove);
		m_lblQuit.show();
	}

	public void draw(){
		super.draw();
	}
}


