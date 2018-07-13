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

//LWJGL library imports
import org.lwjgl.util.Rectangle;

//Bianisoft imports
import com.bianisoft.engine.sprites.Sprite;
import com.bianisoft.engine.labels.Label;
import com.bianisoft.engine.backgrounds.Background;


public class Countainer extends Obj{
	protected ArrayList<PhysObj>	m_vecPhysObj			= new ArrayList<PhysObj>();
	protected ArrayList<PhysObj>	m_vecPhysObjToDeleted	= new ArrayList<PhysObj>();
	protected ArrayList<PhysObj>	m_vecPhysObjToAdd		= new ArrayList<PhysObj>();

	protected boolean m_isDefferingModification;


	public Countainer()			{super(IDCLASS_Countainer);}
	public Countainer(int p_nID){super(p_nID);}

	public  ArrayList<PhysObj> getVectorChilds()		{return m_vecPhysObj;}

	protected void removeChildLater(PhysObj p_obj)	{m_vecPhysObjToDeleted.add(p_obj);}
	protected void addChildLater(PhysObj p_obj)		{m_vecPhysObjToAdd.add(p_obj);}
	public void removeAllChilds()					{m_vecPhysObj.clear();}

	public void removeChild(PhysObj p_obj){
		if(m_isDefferingModification)
			removeChildLater(p_obj);
		else
			m_vecPhysObj.remove(p_obj);
	}
	
	public void addChild(PhysObj p_physObj){
		if(m_isDefferingModification)
			addChildLater(p_physObj);
		else{
			p_physObj.m_objParent= this;
			m_vecPhysObj.add(p_physObj);
		}
	}

	public PhysObj findByTextID(String p_stTextID){
		for(PhysObj physObj : m_vecPhysObj){
			if(physObj.getTextID().equals(p_stTextID))
				return physObj;
		}

		return null;
	}

	public PhysObj findByRtti(int p_idClass){
		for(PhysObj physObj : m_vecPhysObj){
			if(physObj.isKindOf(p_idClass)){
				return physObj;
			}
		}
		return null;
	}

	public PhysObj findAtByRtti(int p_nX, int p_nY, int p_nZ, int p_idClass){
		PhysObj		objNearest	= null;
		double	nearestZ	= 1000000.0;

		for(PhysObj physObj : m_vecPhysObj){
			if(!physObj.isKindOf(p_idClass))
				continue;
			if((physObj.getPosZ()) <= p_nZ)
				continue;

			Rectangle	rect= new Rectangle();

			if(physObj.isKindOf(Obj.IDCLASS_Sprite)){
				Sprite spr= (Sprite)physObj;

				if(!spr.isShown())
					continue;
				if(!spr.isLoaded())
					continue;

				rect.setX((int)(spr.getPosX() - spr.getHotSpotX()));
				rect.setY((int)(spr.getPosY() - spr.getHotSpotY()));
				rect.setWidth(spr.getWidh());
				rect.setHeight(spr.getHeight());
			}else if(physObj.isKindOf(Obj.IDCLASS_Background)){
				Background back= (Background)physObj;

				if(!back.isShown())
					continue;
				if(!back.isLoaded())
					continue;

				rect.setX((int)(back.getPosX() - (back.m_nWidth/2)));
				rect.setY((int)(back.getPosY() - (back.m_nHeight/2)));
				rect.setWidth((int)back.m_nWidth);
				rect.setHeight((int)back.m_nHeight);
			}else if(physObj.isKindOf(Obj.IDCLASS_Label)){
				Label lbl= (Label)physObj;

				if(!lbl.isShown())
					continue;

				rect.setX((int)(lbl.getPosX() + lbl.m_recLimit.getX()));
				rect.setY((int)(lbl.getPosY() + lbl.m_recLimit.getY()));
				rect.setWidth(lbl.m_recLimit.getWidth());
				rect.setHeight(lbl.m_recLimit.getHeight());
			}
			
			if(!rect.contains(p_nX, p_nY))
				continue;

			if((physObj.getPosZ()) < nearestZ){
				nearestZ= physObj.getPosZ();
				objNearest= physObj;
			}
		}

		return objNearest;
	}

	public String toString() {
		return m_vecPhysObj.toString();
	}
}
