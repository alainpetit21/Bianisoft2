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


//Bianisoft imports
import com.bianisoft.engine.App;
import com.bianisoft.engine.Camera;
import com.bianisoft.engine.Context;
import com.bianisoft.engine.audio.Sound;
import com.bianisoft.engine._3d.ObjMD2;
import com.bianisoft.engine.backgrounds.Background;
import com.bianisoft.engine.labels.Label;
import com.bianisoft.engine.labels.LabelTextField;
import com.bianisoft.engine.manager.MngInput;
import com.bianisoft.engine.audio.Music;
import com.bianisoft.engine.sprites.Button;
import com.bianisoft.engine.sprites.Sprite;


public class CtxTest extends Context{
	private static final float[] ZOOMING_LEVEL= {0.9231f, 1.0f, 1.3333f, 2.1333f};

	private Background		m_back1;
	private Background		m_back2;
	private Sprite			m_sprCursor;
	private Button			m_btnQuit;
	private Label			m_LblCredits;
	private LabelTextField	m_LblTextField;
	private Sprite			m_sprSymbol;
	private Sprite			m_sprSymbol2;
	private ObjMD2			m_objMD2OfficerPearl;
	private Camera			m_cam3D;
	private Camera			m_cam2D;
	private Sound			m_sndTest;
	private Music			m_musTest1;
	private Music			m_musTest2;

	public int m_nZoomLevel= 1;
	public float m_nDelayZooming= 0;

	public boolean m_isQuitting= false;
	
	public void activate(){
		super.activate();

		DesignCtxTest.load(this);

		m_is3DFirst= false;
		
		m_back1= (Background)findByTextID("Back_1");
		m_back2= (Background)findByTextID("Back_2");
		m_btnQuit= (Button)findByTextID("Bt_Quit");
		m_LblCredits= (Label)findByTextID("Lbl_Credits1");
		m_sprCursor= (Sprite)findByTextID("Spr_Cursor");
		m_LblTextField= (LabelTextField)findByTextID("Lbl_TextFieldTest");
		m_sprSymbol= (Sprite)findByTextID("Spr_Symbols");
		m_sprSymbol2= (Sprite)findByTextID("Spr_Symbols2");
		m_objMD2OfficerPearl= (ObjMD2)findByTextID("3D_OfficerPearl");

		setCursor(m_sprCursor);

		m_cam2D= Camera.getCur(Camera.TYPE_2D);
		m_cam2D.setPosX(0);
		m_cam2D.setPosY(0);
		m_cam2D.setZoom(1);
		m_cam2D.setLimits(-512, -384, 1024, 768);

		m_cam3D= Camera.getCur(Camera.TYPE_3D);
		m_cam3D.setCur();
		m_cam3D.setPosY(4);
		m_cam3D.setPosZ(-15);
//		m_obj3DCam.lockOnObject(m_objMD2OfficerPearl);

		m_sndTest= new Sound("/restest/sounds/LaserFire2.wav");
		m_sndTest.load();

		m_musTest1= new Music("/restest/musics/test.ogg");
		m_musTest1.load();
		m_musTest1.setPos(0, 0);
		m_musTest1.play(true);
		addChild(m_musTest1);

		m_musTest2= new Music("/restest/musics/test2.ogg");
		m_musTest2.load();
		m_musTest2.setPos(0, 512);
		m_musTest2.play(true);
		addChild(m_musTest2);

		m_btnQuit.setCallback(new Button.I_Callback(){
			public void callbackStateChanged(int p_nNewState, Button p_obj){
				if(p_nNewState == Button.ST_CLICKED){
					m_isQuitting= true;
					m_musTest1.AddMoveTo(m_musTest1.getPosX() + 512, m_musTest1.getPosY(), m_musTest1.getPosZ(), m_musTest1.getAngleX(), m_musTest1.getAngleY(), m_musTest1.getAngleZ(), 3000);
					m_musTest2.AddMoveTo(m_musTest2.getPosX() + 512, m_musTest2.getPosY(), m_musTest2.getPosZ(), m_musTest2.getAngleX(), m_musTest2.getAngleY(), m_musTest2.getAngleZ(), 3000);
				}
			}
		});

	}
	
	public void deActivate(){
		super.deActivate();
	}

	public boolean keyboardManage(MngInput p_mngInput){
		if(!super.keyboardManage(p_mngInput))
			return false;

		if(p_mngInput.isKeyboardDown(MngInput.K_ARROW_UP))		m_back1.setPosY(m_back1.getPosY() - 0.1f);
		if(p_mngInput.isKeyboardDown(MngInput.K_ARROW_DOWN))	m_back1.setPosY(m_back1.getPosY() + 0.1f);
		if(p_mngInput.isKeyboardDown(MngInput.K_ARROW_LEFT))	m_back1.setPosX(m_back1.getPosX() - 0.1f);
		if(p_mngInput.isKeyboardDown(MngInput.K_ARROW_RIGHT))	m_back1.setPosX(m_back1.getPosX() + 0.1f);

		if(p_mngInput.isKeyboardDown(MngInput.K_0))		m_sprSymbol.setCurState(0);
		if(p_mngInput.isKeyboardDown(MngInput.K_1))		m_sprSymbol.setCurState(1);
		if(p_mngInput.isKeyboardDown(MngInput.K_2))		m_sprSymbol.setCurState(2);
		if(p_mngInput.isKeyboardDown(MngInput.K_3))		m_sprSymbol.setCurState(3);
		if(p_mngInput.isKeyboardDown(MngInput.K_4))		m_sprSymbol.setCurState(4);
		if(p_mngInput.isKeyboardDown(MngInput.K_5))		m_sprSymbol.setCurState(5);
		if(p_mngInput.isKeyboardDown(MngInput.K_6))		m_sprSymbol.setCurState(6);
		if(p_mngInput.isKeyboardDown(MngInput.K_7))		m_sprSymbol.setCurState(7);
		if(p_mngInput.isKeyboardDown(MngInput.K_8))		m_sprSymbol.setCurState(8);
		if(p_mngInput.isKeyboardDown(MngInput.K_9))		m_sprSymbol.setCurState(9);

		if(p_mngInput.isMouseClicked(MngInput.M_LEFT)){
			m_sndTest.setPos(m_sprCursor.getPosX(), m_sprCursor.getPosY(), m_sprCursor.getPosZ());
			m_sndTest.play();
		}else if(p_mngInput.isMouseClicked(MngInput.M_MIDDLE)){
			m_musTest1.AddMoveTo(m_musTest1.getPosX(), m_musTest1.getPosY() + 512, m_musTest1.getPosZ(), m_musTest1.getAngleX(), m_musTest1.getAngleY(), m_musTest1.getAngleZ(), 3000);
			m_musTest2.AddMoveTo(m_musTest2.getPosX(), m_musTest2.getPosY() + 512, m_musTest2.getPosZ(), m_musTest2.getAngleX(), m_musTest2.getAngleY(), m_musTest2.getAngleZ(), 3000);
		}else if(p_mngInput.isMouseClicked(MngInput.M_RIGHT)){
			m_musTest1.AddMoveTo(m_musTest1.getPosX(), m_musTest1.getPosY() - 512, m_musTest1.getPosZ(), m_musTest1.getAngleX(), m_musTest1.getAngleY(), m_musTest1.getAngleZ(), 3000);
			m_musTest2.AddMoveTo(m_musTest2.getPosX(), m_musTest2.getPosY() - 512, m_musTest2.getPosZ(), m_musTest2.getAngleX(), m_musTest2.getAngleY(), m_musTest2.getAngleZ(), 3000);
		}

		if(p_mngInput.isMouseDown(MngInput.M_LEFT)){
			m_cam2D.moveBy(p_mngInput.getMouseDeltaX(), p_mngInput.getMouseDeltaY(), 0);
		}

		if(p_mngInput.isKeyboardClicked(MngInput.K_ESCAPE)){
			m_musTest1.stop();
			m_musTest2.stop();
		}

		if(!m_cam2D.isMoving()){
			m_nDelayZooming+= MngInput.get().getMouseDeltaZ();
			if(Math.abs(m_nDelayZooming) > 500){
				m_nZoomLevel+= Math.signum(m_nDelayZooming);
				m_nDelayZooming= 0;

				if(m_nZoomLevel < 0)
					m_nZoomLevel= 0;
				else if(m_nZoomLevel > 3)
					m_nZoomLevel= 3;

				m_cam2D.zoomTo(ZOOMING_LEVEL[m_nZoomLevel], 1000);

				if(m_nZoomLevel <= 1)
					m_cam2D.AddMoveTo(0, 0, m_cam2D.getPosZ(), 0, 0, m_cam2D.getAngleZ(), 1000);
			}
		}

		return true;
	}

	public void manage(float p_nRatioMove){
		super.manage(p_nRatioMove);

		if(m_isQuitting){
			if(m_musTest1.isMoving())
				return;
			
			App.g_theApp.setCurContext(AppTest.IDCTX_QUIT);
		}

		m_LblCredits.addPixelOffsetY(0.01f);

//		m_cam2D.setAngleZ(m_cam2D.getAngleZ() - 0.1f);

		m_sprSymbol.setAngleZ(m_sprSymbol.getAngleZ() + 0.5f);
		m_sprSymbol2.setZoom((m_sprSymbol2.getZoom() + 0.005f) % 10.0f);

		m_back2.setAngleZ(m_back2.getAngleZ() + 0.5f);
		m_back2.setZoom((m_back2.getZoom() + 0.005f) % 2.0f);

		m_objMD2OfficerPearl.setAngleX(m_objMD2OfficerPearl.getAngleX() + 0.3f);
		m_objMD2OfficerPearl.setAngleY(m_objMD2OfficerPearl.getAngleY() + 0.1f);
		m_objMD2OfficerPearl.setAngleZ(m_objMD2OfficerPearl.getAngleZ() + 0.5f);
	}

	
	public void draw(){
		super.draw();
	}
}


