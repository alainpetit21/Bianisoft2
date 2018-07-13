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
package com.bianisoft.engine;


//Standard Java imports
import java.util.ArrayList;

//Standard LWJGL Imports
import org.lwjgl.util.Rectangle;

//Bianisoft imports
import com.bianisoft.engine._3d.Object3D;
import com.bianisoft.engine.sprites.Sprite;
import com.bianisoft.engine.labels.Label;
import com.bianisoft.engine.manager.MngAudio;
import com.bianisoft.engine.manager.MngInput;
import com.bianisoft.engine.manager.physic.MngPhysic;
import com.bianisoft.engine.sprites.Button;


public class Context extends Obj{
	public static final int COUNTAINER_TYPE_INFINITE	= 0;
	public static final int COUNTAINER_TYPE_WORLD_2D	= 1;
	public static final int COUNTAINER_TYPE_WORLD_3D	= 2;
	public static final int COUNTAINER_TYPE_CAMERA_2D	= 3;
	public static final int COUNTAINER_TYPE_CAMERA_3D	= 4;

	private Sprite		m_sprMouse;
	private Obj			m_objWithKeyFocus;

	private Countainer	m_containerInfity		= new Countainer();	//2d Object always drawn at their X,Y,Z without being altered by the Camera movement
	private Countainer	m_containerWorld2D		= new Countainer();	//2D Object in the world
	private Countainer	m_containerWorld3D		= new Countainer();	//3D Objects in the world
	private Countainer	m_containerCamera2D		= new Countainer();	//2D Objects tied to the camera
	private Countainer	m_containerCamera3D		= new Countainer();	//3D Objects tied to the camera
	private Countainer	m_containerSpecial		= new Countainer();	//Special Objects


	public ArrayList<Timer>	m_arTimer= new ArrayList<Timer>();
	public int	m_nIndex;

	public boolean	m_is3DFirst= true;


	public Context(){
		super(IDCLASS_Context);
		m_objWithKeyFocus= this;
	}

	public void setCursor(Sprite p_physObj){
		if(p_physObj != null)
			p_physObj.setHotSpot(0, 0);

		m_sprMouse= p_physObj;
	}

	private void setKeyFocusObj(Obj p_obj){
		if(m_objWithKeyFocus.isKindOf(IDCLASS_PhysObj)){
			PhysObj physObj= (PhysObj)m_objWithKeyFocus;
			physObj.m_hasKeyFocus= false;
		}
		if(p_obj.isKindOf(IDCLASS_PhysObj)){
			PhysObj physObj= (PhysObj)p_obj;
			physObj.m_hasKeyFocus= true;
		}

		m_objWithKeyFocus= p_obj;
	}

	public Countainer getCountainer(int p_nType){
		switch(p_nType){
		case COUNTAINER_TYPE_INFINITE:	return m_containerInfity;
		case COUNTAINER_TYPE_WORLD_2D:	return m_containerWorld2D;
		case COUNTAINER_TYPE_WORLD_3D:	return m_containerWorld3D;
		case COUNTAINER_TYPE_CAMERA_2D:	return m_containerCamera2D;
		case COUNTAINER_TYPE_CAMERA_3D:	return m_containerCamera3D;
		}

		return null;
	}
	
	public Sprite getCursor()			{return m_sprMouse;}
	public void removeAllTimers()		{m_arTimer.clear();}

	public void addTimer(int p_nDelay, Timer.I_Callback p_objTimerCallback, Context p_objHint){
		m_arTimer.add(new Timer(p_nDelay, p_objTimerCallback, p_objHint));
	}

	public void addTimer(Timer p_timer){
		m_arTimer.add(p_timer);
	}

	public void activate(){
		MngAudio.get().setCameraListener(Camera2D.getCur(m_nIndex));

//		if(App.PRINT_DEBUG){
			Label lblLbl_Version= new Label("/restest/fonts/DidactGothic.ttf", 10, "", Label.MODE_RIGHT, true, new Rectangle(-300, -7, 300, 15));
			lblLbl_Version.setTextID("Lbl_Version");
			lblLbl_Version.setFilterColor(0, 0, 0);
			lblLbl_Version.setPos(App.g_nWidth/2, App.g_nHeight/2 -10, 1);
			lblLbl_Version.load();
			lblLbl_Version.set("Version: " +App.get().getVersion());
			addChild(lblLbl_Version, true, false);
//		}
	}

	public void deActivate(){
		m_containerInfity.removeAllChilds();
		m_containerWorld2D.removeAllChilds();
		m_containerWorld3D.removeAllChilds();
		m_containerCamera2D.removeAllChilds();
		m_containerCamera3D.removeAllChilds();

		MngPhysic.get().m_arLines.clear();
		removeAllTimers();
	}

	public void removeAllChilds(){
		m_containerInfity.removeAllChilds();
		m_containerWorld2D.removeAllChilds();
		m_containerWorld3D.removeAllChilds();
		m_containerCamera2D.removeAllChilds();
		m_containerCamera3D.removeAllChilds();
	}

	public void removeChild(Object3D p_obj){
		m_containerWorld3D.removeChild(p_obj);
		m_containerCamera3D.removeChild(p_obj);
	}

	public void removeChild(Drawable p_obj){
		m_containerInfity.removeChild(p_obj);
		m_containerWorld2D.removeChild(p_obj);
		m_containerCamera2D.removeChild(p_obj);
	}

	public void addChild(Object3D p_obj3D)	{addChild(p_obj3D, false, false);}
	public void addChild(Object3D p_obj3D, boolean p_isAttachedToCamera)	{addChild(p_obj3D, p_isAttachedToCamera, false);}
	public void addChild(Object3D p_obj3D, boolean p_isAttachedToCamera, boolean p_isInInfinity){
		if(p_isAttachedToCamera){
			p_obj3D.m_isCameraBound= true;
			m_containerCamera3D.addChild(p_obj3D);
		}else{
			m_containerWorld3D.addChild(p_obj3D);
		}
	}

	public void addChild(Drawable p_objDrawable)	{addChild(p_objDrawable, false, false);}
	public void addChild(Drawable p_objDrawable, boolean p_isAttachedToCamera)	{addChild(p_objDrawable, p_isAttachedToCamera, false);}
	public void addChild(Drawable p_objDrawable, boolean p_isAttachedToCamera, boolean p_isInInfinity){
		if(p_isAttachedToCamera){
			p_objDrawable.m_isCameraBound= true;

			if(p_isInInfinity){
				p_objDrawable.m_isInInfinity= true;
				m_containerInfity.addChild(p_objDrawable);
			}else{
				m_containerCamera2D.addChild(p_objDrawable);
			}
		}else{
			m_containerWorld2D.addChild(p_objDrawable);
		}
	}

	public void addChild(PhysObj p_physObj){
		m_containerSpecial.addChild(p_physObj);
	}

	public PhysObj findByTextID(String p_stTextID){
		PhysObj ret;

		if((ret= m_containerInfity.findByTextID(p_stTextID)) != null)
			return ret;
		if((ret= m_containerWorld2D.findByTextID(p_stTextID)) != null)
			return ret;
		if((ret= m_containerWorld3D.findByTextID(p_stTextID)) != null)
			return ret;
		if((ret= m_containerCamera2D.findByTextID(p_stTextID)) != null)
			return ret;
		if((ret= m_containerCamera3D.findByTextID(p_stTextID)) != null)
			return ret;


		return null;
	}

	public PhysObj findByTextID(String p_stTextID, boolean m_is3D){
		if(m_is3D){
			Object3D ret;

			if((ret= (Object3D)m_containerWorld3D.findByTextID(p_stTextID)) != null)
				return ret;
			if((ret= (Object3D)m_containerCamera3D.findByTextID(p_stTextID)) != null)
				return ret;
		}else{
			PhysObj ret;

			if((ret= m_containerInfity.findByTextID(p_stTextID)) != null)
				return ret;
			if((ret= m_containerWorld2D.findByTextID(p_stTextID)) != null)
				return ret;
			if((ret= m_containerCamera2D.findByTextID(p_stTextID)) != null)
				return ret;
		}

		return null;
	}

	public PhysObj findByTextID(String p_stTextID, boolean m_is3D, boolean m_isCameraAttached){
		if(m_is3D){
			Object3D ret;

			if(m_isCameraAttached){
				if((ret= (Object3D)m_containerCamera3D.findByTextID(p_stTextID)) != null)
					return ret;
			}else{
				if((ret= (Object3D)m_containerWorld3D.findByTextID(p_stTextID)) != null)
					return ret;
			}
		}else{
			PhysObj ret;

			if(m_isCameraAttached){
				if((ret= m_containerInfity.findByTextID(p_stTextID)) != null)
					return ret;
				if((ret= m_containerCamera2D.findByTextID(p_stTextID)) != null)
					return ret;
			}else{
				if((ret= m_containerWorld2D.findByTextID(p_stTextID)) != null)
					return ret;
			}
		}

		return null;
	}

	public PhysObj findByRtti(int p_idClass){
		PhysObj ret;

		if((p_idClass & IDCLASS_Object3D) == IDCLASS_Object3D){
			if((ret= m_containerWorld3D.findByRtti(p_idClass)) != null)
				return ret;
			if((ret= m_containerCamera3D.findByRtti(p_idClass)) != null)
				return ret;
		}else{
			if((ret= m_containerInfity.findByRtti(p_idClass)) != null)
				return ret;
			if((ret= m_containerWorld2D.findByRtti(p_idClass)) != null)
				return ret;
			if((ret= m_containerCamera2D.findByRtti(p_idClass)) != null)
				return ret;
		}

		return null;
	}

	public PhysObj findByRtti(int p_idClass, boolean p_isCameraAttached){
		PhysObj ret;

		if((p_idClass & IDCLASS_Object3D) == IDCLASS_Object3D){
			if(p_isCameraAttached){
				if((ret= m_containerCamera3D.findByRtti(p_idClass)) != null)
					return ret;
			}else{
				if((ret= m_containerWorld3D.findByRtti(p_idClass)) != null)
					return ret;
			}
		}else{
			if(p_isCameraAttached){
				if((ret= m_containerInfity.findByRtti(p_idClass)) != null)
					return ret;
				if((ret= m_containerCamera2D.findByRtti(p_idClass)) != null)
					return ret;
			}else{
				if((ret= m_containerWorld2D.findByRtti(p_idClass)) != null)
					return ret;
			}
		}

		return null;
	}

	public PhysObj findAtByRtti(int p_nX, int p_nY, int p_nZ, int p_idClass){
		PhysObj ret;

		if((p_idClass & IDCLASS_Object3D) == IDCLASS_Object3D){
			if((ret= m_containerWorld3D.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
				return ret;
			if((ret= m_containerCamera3D.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
				return ret;
		}else{
			if((ret= m_containerInfity.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
				return ret;
			if((ret= m_containerWorld2D.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
				return ret;
			if((ret= m_containerCamera2D.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
				return ret;
		}

		return null;
	}

	public PhysObj findAtByRtti(int p_nX, int p_nY, int p_nZ, int p_idClass, boolean p_isCameraAttached){
		PhysObj ret;

		if((p_idClass & IDCLASS_Object3D) == IDCLASS_Object3D){
			if(p_isCameraAttached){
				if((ret= m_containerCamera3D.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
					return ret;
			}else{
				if((ret= m_containerWorld3D.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
					return ret;
			}
		}else{
			if(p_isCameraAttached){
				if((ret= m_containerInfity.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
					return ret;
				if((ret= m_containerCamera2D.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
					return ret;
			}else{
				if((ret= m_containerWorld2D.findAtByRtti(p_nX, p_nY, p_nZ, p_idClass)) != null)
					return ret;
			}
		}

		return null;
	}

	public void manageSort(){
		manageSort(m_containerInfity);
		manageSort(m_containerWorld2D);
		manageSort(m_containerWorld3D);
		manageSort(m_containerCamera2D);
		manageSort(m_containerCamera3D);
	}

	public void manageSort(Countainer p_objCountainer){
		for(int i= 0; i < p_objCountainer.m_vecPhysObj.size(); ++i){
			PhysObj physObj1= p_objCountainer.m_vecPhysObj.get(i);

			for(int j= i+1; j < p_objCountainer.m_vecPhysObj.size(); ++j){
				PhysObj physObj2= p_objCountainer.m_vecPhysObj.get(j);

				if(physObj2.getPosZ() > physObj1.getPosZ()){
					p_objCountainer.m_vecPhysObj.set(i, physObj2);
					p_objCountainer.m_vecPhysObj.set(j, physObj1);
					i= -1;
					break;
				}
			}
		}
	}

	public boolean keyboardManage(MngInput p_input){
		return m_objWithKeyFocus == this;
	}

	public void mouseManage(MngInput p_input){
	}

	private void afterManageMouseManage(){
		if(m_sprMouse == null)
			return;

		MngInput mngInput= MngInput.get();
		Camera cam= Camera.getCur(Camera.TYPE_2D);
		m_sprMouse.setPosX(mngInput.getMouseX() - App.g_nWidth/2);
		m_sprMouse.setPosY((App.g_nHeight-mngInput.getMouseY()) - App.g_nHeight/2);

		int worldPosX= (int)cam.doUnprojectionX(m_sprMouse.getPosX());
		int worldPosY= (int)cam.doUnprojectionY(m_sprMouse.getPosY());
		int camPosX= (int)m_sprMouse.getPosX();
		int camPosY= (int)m_sprMouse.getPosY();

		Button objButton= (Button)m_containerCamera2D.findAtByRtti(camPosX, camPosY, Integer.MIN_VALUE, Obj.IDCLASS_Button);
		if(objButton == null)
			objButton= (Button)m_containerWorld2D.findAtByRtti(worldPosX, worldPosY, Integer.MIN_VALUE, Obj.IDCLASS_Button);

		if((objButton != null) && (!objButton.m_isDisabled)){
			if(objButton.m_nCurState == Button.ST_IDLE){
				objButton.m_nOldState= objButton.m_nCurState;

				if(mngInput.isMouseClicked(MngInput.M_LEFT)){
					setKeyFocusObj(this);

					if(objButton.m_objCallback != null)
						objButton.m_objCallback.callbackStateChanged(Button.ST_CLICKED, objButton);
				}else if(mngInput.isMouseDown(MngInput.M_LEFT)){
					setKeyFocusObj(this);
					objButton.m_nCurState= Button.ST_DOWN;

					if(objButton.m_objCallback != null)
						objButton.m_objCallback.callbackStateChanged(Button.ST_DOWN, objButton);
				}else{
					objButton.m_nCurState= Button.ST_OVER;

					if(objButton.m_objCallback != null)
						objButton.m_objCallback.callbackStateChanged(Button.ST_OVER, objButton);
				}

				if(m_sprMouse.m_vecStates.size() > 1)
					m_sprMouse.m_nCurState= 1;
			}
		}

		Label objLabel= (Label)m_containerCamera2D.findAtByRtti(camPosX, camPosY, Integer.MIN_VALUE, Obj.IDCLASS_Label);
		if(objLabel == null)
			objLabel= (Label)m_containerWorld2D.findAtByRtti(worldPosX, worldPosY, Integer.MIN_VALUE, Obj.IDCLASS_Label);

		if(objLabel != null){
			if(mngInput.isMouseClicked(MngInput.M_LEFT))
				if(objLabel.click())
					setKeyFocusObj(objLabel);
		}else{
			m_sprMouse.m_nCurState= 0;

			if(mngInput.isMouseClicked(MngInput.M_LEFT))
				setKeyFocusObj(this);
		}
	}

	public void manageOneCountainer(float p_fRatioMovement, Countainer p_objCountainer){
		p_objCountainer.m_isDefferingModification= true;
		for(PhysObj physObj1 : p_objCountainer.m_vecPhysObj){
			boolean isInDeletionList= false;

			for(PhysObj physObj2 : p_objCountainer.m_vecPhysObjToDeleted){
				if(physObj1 == physObj2){
					isInDeletionList= true;
					break;
				}
			}

			if(!isInDeletionList)
				physObj1.manage(p_fRatioMovement);

		}
		p_objCountainer.m_isDefferingModification= false;

		//If any Phys were removed/added during Manage, they were actually deffered to be deleted here
		for(PhysObj physObj : p_objCountainer.m_vecPhysObjToAdd)
			p_objCountainer.m_vecPhysObj.add(physObj);
		for(PhysObj physObj : p_objCountainer.m_vecPhysObjToDeleted)
			p_objCountainer.m_vecPhysObj.remove(physObj);
		p_objCountainer.m_vecPhysObjToDeleted.clear();
		p_objCountainer.m_vecPhysObjToAdd.clear();

		manageSort(p_objCountainer);
	}

	public void manage(float p_fRatioMovement){
		MngPhysic.get().manageCollision(p_fRatioMovement, m_containerWorld2D.m_vecPhysObj);
		MngPhysic.get().manageCollision(p_fRatioMovement, m_containerWorld3D.m_vecPhysObj);

		manageOneCountainer(p_fRatioMovement, m_containerInfity);
		manageOneCountainer(p_fRatioMovement, m_containerWorld2D);
		manageOneCountainer(p_fRatioMovement, m_containerWorld3D);
		manageOneCountainer(p_fRatioMovement, m_containerCamera2D);
		manageOneCountainer(p_fRatioMovement, m_containerCamera3D);
		manageOneCountainer(p_fRatioMovement, m_containerSpecial);

		//Timers Manage
		for(Timer objTimer : m_arTimer)
			objTimer.manage(16.67 * p_fRatioMovement);

		afterManageMouseManage();
	}

	public void draw(){
		Camera.getCur(Camera.TYPE_2D).doScreenProjection();

		for(PhysObj physObject :m_containerInfity.m_vecPhysObj)
			((Drawable)physObject).draw();

		if(m_is3DFirst){
			Camera.getCur(Camera.TYPE_3D).doWorldProjection();

			for(PhysObj physObject :m_containerWorld3D.m_vecPhysObj)
				((Drawable)physObject).draw();

			Camera.getCur(Camera.TYPE_2D).doWorldProjection();

			for(PhysObj physObject :m_containerWorld2D.m_vecPhysObj)
				((Drawable)physObject).draw();
		}else{
			Camera.getCur(Camera.TYPE_2D).doWorldProjection();

			for(PhysObj physObject :m_containerWorld2D.m_vecPhysObj)
				((Drawable)physObject).draw();

			Camera.getCur(Camera.TYPE_3D).doWorldProjection();

			for(PhysObj physObject :m_containerWorld3D.m_vecPhysObj)
				((Drawable)physObject).draw();
		}

		if(m_is3DFirst){
			Camera.getCur(Camera.TYPE_3D).doScreenProjection();
			for(PhysObj physObject :m_containerCamera3D.m_vecPhysObj)
				((Drawable)physObject).draw();

			Camera.getCur(Camera.TYPE_2D).doScreenProjection();

			for(PhysObj physObject :m_containerCamera2D.m_vecPhysObj)
				((Drawable)physObject).draw();
		}else{
			Camera.getCur(Camera.TYPE_2D).doScreenProjection();

			for(PhysObj physObject :m_containerCamera2D.m_vecPhysObj)
				((Drawable)physObject).draw();

			Camera.getCur(Camera.TYPE_3D).doScreenProjection();
			
			for(PhysObj physObject :m_containerCamera3D.m_vecPhysObj)
				((Drawable)physObject).draw();
		}

		if(App.PRINT_DEBUG)
			drawDebug();

	}

	public void drawDebug(){
		Camera.getCur(Camera.TYPE_2D).doWorldProjection();
		MngPhysic.get().drawDebug();
	}
}
