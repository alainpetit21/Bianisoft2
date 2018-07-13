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

//Bianisoft imports
import com.bianisoft.engine.helper.datatypes.Real;


public class PhysObj extends Obj{
	class MoveToCtrl{
		class Sextulpe{
			//posX, posY, posZ, angleX, angleY, angleZ
			public float[] m_vData= new float[6];

			public float getDeltaLenght(Sextulpe p_sextulpeRight){
				float[] delta= {m_vData[0] - p_sextulpeRight.m_vData[0],
								m_vData[1] - p_sextulpeRight.m_vData[1],
								m_vData[2] - p_sextulpeRight.m_vData[2],
								m_vData[3] - p_sextulpeRight.m_vData[3],
								m_vData[4] - p_sextulpeRight.m_vData[4],
								m_vData[5] - p_sextulpeRight.m_vData[5]};
				float valueSqr=((delta[0]*delta[0])+
								(delta[1]*delta[1])+
								(delta[2]*delta[2])+
								(delta[3]*delta[3])+
								(delta[4]*delta[4])+
								(delta[5]*delta[5]));

				return (float)Math.sqrt(valueSqr);
			}
		}

		private PhysObj				m_physObj;
		private Sextulpe			m_ptFrom= new Sextulpe();
		private ArrayList<Sextulpe>	m_pathTo= new ArrayList<Sextulpe>();
		private ArrayList<Real>		m_pathToTimer= new ArrayList<Real>();
		private boolean				m_isOn= false;

		private Sextulpe			m_delta= new Sextulpe();


		public MoveToCtrl(PhysObj p_physObj){
			m_physObj= p_physObj;
		}

		public void readData(){
			m_ptFrom.m_vData[0]= m_physObj.m_vPos[0];
			m_ptFrom.m_vData[1]= m_physObj.m_vPos[1];
			m_ptFrom.m_vData[2]= m_physObj.m_vPos[2];
			m_ptFrom.m_vData[3]= m_physObj.m_vAngle[0];
			m_ptFrom.m_vData[4]= m_physObj.m_vAngle[1];
			m_ptFrom.m_vData[5]= m_physObj.m_vAngle[2];
		}

		public void saveData(){
			m_physObj.m_vPos[0]= m_ptFrom.m_vData[0];
			m_physObj.m_vPos[1]= m_ptFrom.m_vData[1];
			m_physObj.m_vPos[2]= m_ptFrom.m_vData[2];
			m_physObj.m_vAngle[0]= m_ptFrom.m_vData[3];
			m_physObj.m_vAngle[1]= m_ptFrom.m_vData[4];
			m_physObj.m_vAngle[2]= m_ptFrom.m_vData[5];
		}

		public void moveTo(float  p_fPosX, float p_fPosY, float p_fPosZ, float p_fAngleX, float p_fAngleY, float p_fAngleZ, int p_nNbMS){
			Sextulpe newSept= new Sextulpe();
			newSept.m_vData[0]= p_fPosX;
			newSept.m_vData[1]= p_fPosY;
			newSept.m_vData[2]= p_fPosZ;
			newSept.m_vData[3]= p_fAngleX;
			newSept.m_vData[4]= p_fAngleY;
			newSept.m_vData[5]= p_fAngleZ;

			readData();
			if(!(m_ptFrom.getDeltaLenght(newSept) > 0))
				return;

			m_pathTo.add(newSept);
			m_pathToTimer.add(new Real(p_nNbMS));
			m_isOn= true;
		}

		public void manage(float p_fTimeScaleFactor) {
			Sextulpe	dst= m_pathTo.get(0);
			Real		timer= m_pathToTimer.get(0);

			readData();

			//Get percentage Move
			float percentage= (p_fTimeScaleFactor*60.0f) / timer.get();

			//Get Distance
			for(int i= 0; i < 6; ++i)
				m_delta.m_vData[i]= (dst.m_vData[i] - m_ptFrom.m_vData[i])*percentage;

			//Alter cur
			for(int i= 0; i < 6; ++i)
				m_ptFrom.m_vData[i]+= m_delta.m_vData[i];

			timer.set(timer.get() - (p_fTimeScaleFactor * 60));
			if(timer.get() <= 0){
				for(int i= 0; i < 6; ++i)
					m_ptFrom.m_vData[i]= dst.m_vData[i];

				m_pathTo.remove(0);
				m_pathToTimer.remove(0);

				if(m_pathTo.isEmpty())
					m_isOn= false;
			}

			saveData();
		}
	}


	private MoveToCtrl	m_objMoveToCtrl= new MoveToCtrl(this);

	public boolean m_isCollidable	= false;
	public boolean m_hasKeyFocus	= false;

	public float	m_fRadius= 0.0f;

	private float[]	m_vOrientaion	= {0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f};		//TODO - FIXME
	private float[]	m_vPos			= {0.0f, 0.0f, 0.0f};
	private float[]	m_vVel			= {0.0f, 0.0f, 0.0f};
	private float[]	m_vAccel		= {0.0f, 0.0f, 0.0f};
	private float[]	m_vAngle		= {0.0f, 0.0f, 0.0f};
	private float[]	m_vAngleVel		= {0.0f, 0.0f, 0.0f};
	private float[]	m_vAngleAccel	= {0.0f, 0.0f, 0.0f};
	private float	m_nVelMax;


	public PhysObj()			{super(IDCLASS_PhysObj);}
	public PhysObj(int p_nType)	{super(p_nType);}

	public PhysObj(PhysObj p_refPhysObj){
		super(p_refPhysObj);

		m_isCollidable= p_refPhysObj.m_isCollidable;
		m_fRadius= p_refPhysObj.m_fRadius;
		m_nVelMax= p_refPhysObj.m_nVelMax;

		m_vAngleAccel= new float[]{p_refPhysObj.m_vAngleAccel[0], p_refPhysObj.m_vAngleAccel[1], p_refPhysObj.m_vAngleAccel[2]};
		m_vAngleVel= new float[]{p_refPhysObj.m_vAngleVel[0], p_refPhysObj.m_vAngleVel[1], p_refPhysObj.m_vAngleVel[2]};
		m_vAccel= new float[]{p_refPhysObj.m_vAccel[0], p_refPhysObj.m_vAccel[1], p_refPhysObj.m_vAccel[2]};
		m_vAngle= new float[]{p_refPhysObj.m_vAngle[0], p_refPhysObj.m_vAngle[1], p_refPhysObj.m_vAngle[2]};
		m_vPos= new float[]{p_refPhysObj.m_vPos[0], p_refPhysObj.m_vPos[1], p_refPhysObj.m_vPos[2]};
		m_vVel= new float[]{p_refPhysObj.m_vVel[0], p_refPhysObj.m_vVel[1], p_refPhysObj.m_vVel[2]};
	}

	public float[] getAngleArray()	{return m_vAngle;}
	public float getAngleX()	{return m_vAngle[0];}
	public float getAngleY()	{return m_vAngle[1];}
	public float getAngleZ()	{return m_vAngle[2];}

	public void setAngleX(float p_nAngleX)	{m_vAngle[0]= p_nAngleX;}
	public void setAngleY(float p_nAngleY)	{m_vAngle[1]= p_nAngleY;}
	public void setAngleZ(float p_nAngleZ)	{m_vAngle[2]= p_nAngleZ;}

	public void setAngle(float p_nAngleX, float p_nAngleY, float p_nAngleZ){
		m_vAngle[0]= p_nAngleX;
		m_vAngle[1]= p_nAngleY;
		m_vAngle[2]= p_nAngleZ;
	}

	public float[] getPosArray()	{return m_vPos;}
	public float getPosX()	{return m_vPos[0];}
	public float getPosY()	{return m_vPos[1];}
	public float getPosZ()	{return m_vPos[2];}

	public void setPosX(float p_nPosX)	{m_vPos[0]= p_nPosX;}
	public void setPosY(float p_nPosY)	{m_vPos[1]= p_nPosY;}
	public void setPosZ(float p_nPosZ)	{m_vPos[2]= p_nPosZ;}

	public void setPos(float p_nPosX, float p_nPosY)	{setPos(p_nPosX, p_nPosY, m_vPos[2]);}
	public void setPos(float p_nPosX, float p_nPosY, float p_nPosZ){
		m_vPos[0]= p_nPosX;
		m_vPos[1]= p_nPosY;
		m_vPos[2]= p_nPosZ;
	}

	public float[] getVelArray()	{return m_vVel;}
	public float getVelX()	{return m_vVel[0];}
	public float getVelY()	{return m_vVel[1];}
	public float getVelZ()	{return m_vVel[2];}

	public void setVelX(float p_nVelX)	{m_vVel[0]= p_nVelX;}
	public void setVelY(float p_nVelY)	{m_vVel[1]= p_nVelY;}
	public void setVelZ(float p_nVelZ)	{m_vVel[2]= p_nVelZ;}

	public void setVel(float p_nVelX, float p_nVelY)	{setVel(p_nVelX, p_nVelY, m_vVel[2]);}
	public void setVel(float p_nVelX, float p_nVelY, float p_nVelZ){
		m_vVel[0]= p_nVelX;
		m_vVel[1]= p_nVelY;
		m_vVel[2]= p_nVelZ;
	}

	public float[] getAccelArray()	{return m_vAccel;}
	public float getAccelX()	{return m_vAccel[0];}
	public float getAccelY()	{return m_vAccel[1];}
	public float getAccelZ()	{return m_vAccel[2];}

	public void setAccelX(float p_nAccelX)	{m_vAccel[0]= p_nAccelX;}
	public void setAccelY(float p_nAccelY)	{m_vAccel[1]= p_nAccelY;}
	public void setAccelZ(float p_nAccelZ)	{m_vAccel[2]= p_nAccelZ;}

	public void setAccel(float p_nAccelX, float p_nAccelY, float p_nAccelZ){
		m_vAccel[0]= p_nAccelX;
		m_vAccel[1]= p_nAccelY;
		m_vAccel[2]= p_nAccelZ;
	}
	public void setAccel(float p_nAccelX, float p_nAccelY){
		m_vAccel[0]= p_nAccelX;
		m_vAccel[1]= p_nAccelY;
	}

	public float[] getAngleVelArray()	{return m_vAngleVel;}
	public float getAngleVelX()	{return m_vAngleVel[0];}
	public float getAngleVelY()	{return m_vAngleVel[1];}
	public float getAngleVelZ()	{return m_vAngleVel[2];}

	public void setAngleVelX(float p_nAngleVelX)	{m_vAngleVel[0]= p_nAngleVelX;}
	public void setAngleVelY(float p_nAngleVelY)	{m_vAngleVel[1]= p_nAngleVelY;}
	public void setAngleVelZ(float p_nAngleVelZ)	{m_vAngleVel[2]= p_nAngleVelZ;}

	public void setAngleVel(float p_nAngleVelX, float p_nAngleVelY, float p_nAngleVelZ){
		m_vAngleVel[0]= p_nAngleVelX;
		m_vAngleVel[1]= p_nAngleVelY;
		m_vAngleVel[2]= p_nAngleVelZ;
	}
	public void setAngleVel(float p_nAngleVelX, float p_nAngleVelY){
		m_vAngleVel[0]= p_nAngleVelX;
		m_vAngleVel[1]= p_nAngleVelY;
	}

	public float[] getAngleAccelArray()	{return m_vAngleAccel;}
	public float getAngleAccelX()	{return m_vAngleAccel[0];}
	public float getAngleAccelY()	{return m_vAngleAccel[1];}
	public float getAngleAccelZ()	{return m_vAngleAccel[2];}

	public void setAngleAccelX(float p_nAngleAccelX)	{m_vAngleAccel[0]= p_nAngleAccelX;}
	public void setAngleAccelY(float p_nAngleAccelY)	{m_vAngleAccel[1]= p_nAngleAccelY;}
	public void setAngleAccelZ(float p_nAngleAccelZ)	{m_vAngleAccel[2]= p_nAngleAccelZ;}

	public void setAngleAccel(float p_nAngleAccelX, float p_nAngleAccelY, float p_nAngleAccelZ){
		m_vAngleAccel[0]= p_nAngleAccelX;
		m_vAngleAccel[1]= p_nAngleAccelY;
		m_vAngleAccel[2]= p_nAngleAccelZ;
	}
	public void setAngleAccel(float p_nAngleAccelX, float p_nAngleAccelY){
		m_vAngleAccel[0]= p_nAngleAccelX;
		m_vAngleAccel[1]= p_nAngleAccelY;
	}

	public float[] getOrientation(){
		//TODO - FIXME, regeneration the At and Up Vector from AngleX, Y, Z
		return m_vOrientaion;
	}

	public void rotateTo(float p_fAngleZ, int p_nNbMS)	{moveTo(getPosX(), getPosY(), getPosZ(), getAngleX(), getAngleY(), p_fAngleZ, p_nNbMS);}
	public void rotateTo(float p_fAngleX, float p_fAngleY, float p_fAngleZ, int p_nNbMS)	{moveTo(getPosX(), getPosY(), getPosZ(), p_fAngleX, p_fAngleY, p_fAngleZ, p_nNbMS);}
	
	public void moveTo(float  p_fPosX, float p_fPosY, int p_nNbMS)	{moveTo(p_fPosX, p_fPosY, getPosZ(), getAngleX(), getAngleY(), getAngleZ(), p_nNbMS);}
	public void moveTo(float  p_fPosX, float p_fPosY, float p_fPosZ, int p_nNbMS)	{moveTo(p_fPosX, p_fPosY, p_fPosZ, getAngleX(), getAngleY(), getAngleZ(), p_nNbMS);}
	public void	moveTo(float  p_fPosX, float p_fPosY, float p_fPosZ, float p_fAngleX, float p_fAngleY, float p_fAngleZ, int p_nNbMS){
		m_objMoveToCtrl.moveTo(p_fPosX, p_fPosY, p_fPosZ, p_fAngleX, p_fAngleY, p_fAngleZ, p_nNbMS);
	}

	public boolean isMoving(){
		return (m_objMoveToCtrl.m_isOn) || (m_vVel[0] != 0) || (m_vVel[1] != 0) || (m_vVel[2] != 0) ||
			   (m_vAngleVel[0] != 0)|| (m_vAngleVel[1] != 0)|| (m_vAngleVel[2] != 0);
	}

	public void stopMoveTo(){
		m_objMoveToCtrl.m_isOn= false;
		m_vVel[0]= m_vAngleVel[0]= 0;
		m_vVel[1]= m_vAngleVel[1]= 0;
		m_vVel[2]= m_vAngleVel[2]= 0;
	}

	public void finishMoveTo(){
		for(int i= 0; i < 6; ++i)
			m_objMoveToCtrl.m_ptFrom.m_vData[i]= m_objMoveToCtrl.m_pathTo.get(m_objMoveToCtrl.m_pathTo.size()-1).m_vData[i];

		stopMoveTo();
	}

	public void manage(float p_fTimeScaleFactor){
		if(m_objMoveToCtrl.m_isOn){
			m_objMoveToCtrl.manage(p_fTimeScaleFactor);
		}else{
			m_vAngleVel[0]+= m_vAngleAccel[0];
			m_vAngleVel[1]+= m_vAngleAccel[1];
			m_vAngleVel[2]+= m_vAngleAccel[2];

			m_vAngle[0]+= m_vAngleVel[0] * p_fTimeScaleFactor;
			m_vAngle[1]+= m_vAngleVel[1] * p_fTimeScaleFactor;
			m_vAngle[2]+= m_vAngleVel[2] * p_fTimeScaleFactor;

			m_vVel[0]+= m_vAccel[0];
			m_vVel[1]+= m_vAccel[1];
			m_vVel[2]+= m_vAccel[2];

			m_vPos[0]+= m_vVel[0] * p_fTimeScaleFactor;
			m_vPos[1]+= m_vVel[1] * p_fTimeScaleFactor;
			m_vPos[2]+= m_vVel[2] * p_fTimeScaleFactor;
		}
	}

	public String toString() {
		return "PhysObj @ " + (int)m_vPos[0] + ";"+ (int)m_vPos[1] + ";"+ (int)m_vPos[2] + ";";
	}
	
	public void load()	{	}
}

