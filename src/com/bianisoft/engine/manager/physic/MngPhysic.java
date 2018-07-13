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
package com.bianisoft.engine.manager.physic;


//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;

//Standard Java imports
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;

//Bianisoft imports
import com.bianisoft.engine.PhysObj;
import com.bianisoft.engine.helper.FixResFilename;
import com.bianisoft.engine.resmng.Texture;


public class MngPhysic implements I_ColliderCallback{
	private static MngPhysic m_objMe;

	private I_ColliderCallback m_overloadCB= this;
	public ArrayList<Line>	m_arLines= new ArrayList<Line>();


	public MngPhysic()				{m_objMe= this;}
	public static MngPhysic get()	{return m_objMe;}

	public void setCallback(I_ColliderCallback p_objColliderCallback){
		m_overloadCB= p_objColliderCallback;
	}

	public void addLine(Line p_objLine)	{m_arLines.add(p_objLine);}
	public void addLine(int p_x1, int p_y1, int p_x2, int p_y2, int p_group){
		m_arLines.add(new Line(p_x1, p_y1, p_x2, p_y2, p_group));
	}

	public void loadCollisionFile(String p_stRessource){
		try{
			p_stRessource= FixResFilename.fixResFilename(p_stRessource);
			InputStream objIS= Thread.currentThread().getContextClassLoader().getResourceAsStream(p_stRessource);
			DataInputStream objDIS	= new DataInputStream(objIS);


			//Polygons
			int nbPolygons= objDIS.readInt();
			for(int i= 0; i < nbPolygons; ++i){
				int nbPoints= objDIS.readInt();
				int nGroup= objDIS.readInt();

				int[] m_vBufferX= new int[nbPoints];
				int[] m_vBufferY= new int[nbPoints];

				for(int j= 0; j < nbPoints; ++j){
					m_vBufferX[j]= objDIS.readInt();
					m_vBufferY[j]= objDIS.readInt();
				}

				for(int j= 1; j < nbPoints; ++j)
					addLine(m_vBufferX[j-1], m_vBufferY[j-1], m_vBufferX[j], m_vBufferY[j], nGroup);

				//Close the polygon
				addLine(m_vBufferX[nbPoints-1], m_vBufferY[nbPoints-1], m_vBufferX[0], m_vBufferY[0], nGroup);
			}

			//Polylines
			int nbPolylines= objDIS.readInt();
			for(int i= 0; i < nbPolylines; ++i){
				int nbPoints= objDIS.readInt();
				int nGroup= objDIS.readInt();

				int[] m_vBufferX= new int[nbPoints];
				int[] m_vBufferY= new int[nbPoints];

				for(int j= 0; j < nbPoints; ++j){
					m_vBufferX[j]= objDIS.readInt();
					m_vBufferY[j]= objDIS.readInt();
				}

				for(int j= 1; j < nbPoints; ++j)
					addLine(m_vBufferX[j-1], m_vBufferY[j-1], m_vBufferX[j], m_vBufferY[j], nGroup);
			}

			//Lines
			int nbLines= objDIS.readInt();
			for(int i= 0; i < nbLines; ++i){
				int	sX		= objDIS.readInt();
				int	sY		= objDIS.readInt();
				int	eX		= objDIS.readInt();
				int	eY		= objDIS.readInt();
				int	group	= objDIS.readInt();

				addLine(sX, sY, eX, eY, group);
			}

			objDIS.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void manageCollision(float p_fRatioMovement, ArrayList<PhysObj> p_vecPhysObj){
		for(int i= 0; i < p_vecPhysObj.size(); ++i){
			PhysObj physObj1= p_vecPhysObj.get(i);

			if(!((PhysObj)physObj1).m_isCollidable)
				continue;

			for(int j= i+1; j < p_vecPhysObj.size(); ++j){
				PhysObj physObj2= p_vecPhysObj.get(j);

				if(!((PhysObj)physObj2).m_isCollidable)
					continue;

				HitInfo hitStruct= new HitInfo();

				if(isCollidingObj2Obj(hitStruct, physObj1, physObj2))
					m_overloadCB.onCollideObj2Obj(hitStruct, physObj1, physObj2);
			}

			//Find Nearest Line
			HitInfo hitStruct= new HitInfo();
			boolean hadCollision;
			do{
				hadCollision= false;
				float nearestValue= 1000000000;

				for(Line objLine : m_arLines){
					if((hitStruct.lineHitten != objLine) && isCollidingObj2Line(hitStruct, physObj1, objLine)){
						if(hitStruct.u2 < nearestValue){
							nearestValue= hitStruct.u2;
							hadCollision= true;
						}
					}
				}

				if(hadCollision)
					m_overloadCB.onCollideObj2Line(hitStruct, physObj1);

			}while(hadCollision);
		}
	}

	public boolean isCollidingObj2Obj(HitInfo p_hitStruct, PhysObj p_physObj1, PhysObj p_physObj2){
		float	dpx		= p_physObj1.getPosX() - p_physObj2.getPosX();
		float	dpy		= p_physObj1.getPosY() - p_physObj2.getPosY();
		float	lenPSqr	= (dpx*dpx) + (dpy*dpy);

		float	dvpx	= (p_physObj1.getPosX()+p_physObj1.getVelX()+p_physObj1.getAccelX()) - (p_physObj2.getPosX()+p_physObj2.getVelX()+p_physObj2.getAccelX());
		float	dvpy	= (p_physObj1.getPosY()+p_physObj1.getVelY()+p_physObj1.getAccelY()) - (p_physObj2.getPosY()+p_physObj2.getVelY()+p_physObj2.getAccelY());
		float	lenVPSqr= (dvpx*dvpx) + (dvpy*dvpy);

		//float	dvx		= p_physObj1.getVelX()-p_physObj2.getVelX();
		//float	dvy		= p_physObj1.getVelY()-p_physObj2.getVelY();

		if(lenVPSqr <= ((p_physObj1.m_fRadius + p_physObj2.m_fRadius) * (p_physObj1.m_fRadius + p_physObj2.m_fRadius))){
			float dX	= p_physObj1.getPosX() - p_physObj2.getPosX();
			float dY	= p_physObj1.getPosY() - p_physObj2.getPosY();
			float len	= (float)Math.sqrt(lenPSqr);

			p_hitStruct.ptHit[0]	= p_physObj1.getPosX();
			p_hitStruct.ptHit[1]	= p_physObj1.getPosY();
			p_hitStruct.norm[0]		= dX/len;
			p_hitStruct.norm[1]		= dY/len;
			p_hitStruct.u2			= 0;
			p_hitStruct.idHitten	= 1;
			return true;
		}
		return false;
	}
	
	public void onCollideObj2Obj(HitInfo p_hitStruct, PhysObj p_physObj1, PhysObj p_physObj2){
		float	vel1[]= {p_physObj1.getVelX()+p_physObj1.getAccelX(), p_physObj1.getVelY()+p_physObj1.getAccelY(), 0};
		float	vel2[]= {p_physObj2.getVelX()+p_physObj2.getAccelX(), p_physObj2.getVelY()+p_physObj2.getAccelY(), 0};
		float	nor1[]= {-p_hitStruct.norm[0], -p_hitStruct.norm[1], 0};
		float	para1[]	={-nor1[1], nor1[0], 0};
		float	nor2[]	={-nor1[0], -nor1[1], 0};
		float	para2[]	={-para1[0], -para1[1], 0};
		float	scale;
		float	tempR2[]= new float[3];
		float	tempR1[]= new float[3];

			scale		= (vel1[0] * para1[0]) + (vel1[1] * para1[1]);
			para1[0]	= para1[0] * scale;
			para1[1]	= para1[1] * scale;

			scale		= (vel1[0] * nor1[0]) + (vel1[1] * nor1[1]);
			tempR1[0]	= nor1[0] * scale;
			tempR1[1]	= nor1[1] * scale;

			scale		= (vel2[0] * para2[0]) + (vel2[1] * para2[1]);
			para2[0]	= para2[0] * scale;
			para2[1]	= para2[1] * scale;

			scale		= (vel2[0] * nor2[0]) + (vel2[1] * nor2[1]);
			tempR2[0]	= nor2[0] * scale;
			tempR2[1]	= nor2[1] * scale;

			p_physObj1.setVelX((para1[0] + tempR2[0]) - p_physObj1.getAccelX());
			p_physObj1.setVelY((para1[1] + tempR2[1]) - p_physObj1.getAccelY());
			p_physObj2.setVelX((para2[0] + tempR1[0]) - p_physObj2.getAccelX());
			p_physObj2.setVelY((para2[1] + tempR1[1]) - p_physObj2.getAccelY());
	}

	public boolean isCollidingObj2Line(HitInfo p_hitStruct, PhysObj p_physObj, Line p_pLine){
		float s_x1	=	p_physObj.getPosX();
		float s_x2	=	p_physObj.getPosX() + p_physObj.getVelX() + p_physObj.getAccelX();
		float s_x3	=	p_pLine.m_fStart[0];
		float s_x4	=	p_pLine.m_fEnd[0];
		float s_y1	=	p_physObj.getPosY();
		float s_y2	=	p_physObj.getPosY() + p_physObj.getVelY() + p_physObj.getAccelY();
		float s_y3	=	p_pLine.m_fStart[1];
		float s_y4	=	p_pLine.m_fEnd[1];

		{
			float	y1My3	=	(s_y1-s_y3);
			float	x1Mx3	=	(s_x1-s_x3);
			float	denom	=	((s_y4-s_y3) * (s_x2-s_x1)) - ((s_x4-s_x3) * (s_y2-s_y1));
			float	u1	,u2;
			float	xI1	,yI1;
			float	xI2	,yI2;
			float angle;

			if(denom == 0)
				return false;

			float[] vel	= {(s_x2-s_x1), (s_y2-s_y1)};
			float[] normale= {0, 0};

			u1=	(((s_x4-s_x3) * y1My3) - ((s_y4-s_y3) * x1Mx3)) / denom;
			u2=	(((s_x2-s_x1) * y1My3) - ((s_y2-s_y1) * x1Mx3)) / denom;

			if((u1 > 0) && (u2 >= 0) && (u1 <= 1) && (u2 <= 1)){
				xI1	=	s_x1 + (u1 * (s_x2 - s_x1));
				yI1	=	s_y1 + (u1 * (s_y2 - s_y1));
				xI2	=	s_x3 + (u2 * (s_x4 - s_x3));
				yI2	=	s_y3 + (u2 * (s_y4 - s_y3));

				angle= p_pLine.m_fAngle;
				normale[0]= -(float)Math.sin(angle);
				normale[1]= (float)Math.cos(angle);

				float value= (vel[0] * normale[0]) + (vel[1] * normale[1]);
				if(value <= 0){
					p_hitStruct.angle		= angle;
					p_hitStruct.ptHit[0]	= xI1;
					p_hitStruct.ptHit[1]	= yI1;
					p_hitStruct.u1			= u1;
					p_hitStruct.u2			= u2;
					p_hitStruct.norm[0]		= normale[0];
					p_hitStruct.norm[1]		= normale[1];
					p_hitStruct.lineHitten	= p_pLine;
					p_hitStruct.idHitten	= p_pLine.m_nGroup;
					return true;
				}
			}
		}

		return false;
	}
	
	public void onCollideObj2Line(HitInfo p_hitStruct, PhysObj p_physObj){
		float[] pos			= {p_physObj.getPosX(), p_physObj.getPosY()};
		float[] velNormalized	= {p_physObj.getVelX() + p_physObj.getAccelX(), p_physObj.getVelY() + p_physObj.getAccelY()};
		float[] nor			= {-p_hitStruct.norm[1], p_hitStruct.norm[0]};
		float[] u				= {0, 0};
		float[] v				= {0, 0};
		float len				= (float)Math.sqrt((velNormalized[0] * velNormalized[0]) + (velNormalized[1] * velNormalized[1]));
		float scale;
		float numer;
		float denom;

		velNormalized[0]/= len;
		velNormalized[1]/= len;

		pos[0]= p_hitStruct.ptHit[0];
		pos[1]= p_hitStruct.ptHit[1];

		//Calculate Bounce
		numer	= (velNormalized[0] * nor[0]) + (velNormalized[1] * nor[1]);
		denom	= (nor[0] * nor[0]) + (nor[1] * nor[1]);
		scale	= numer / denom;
		u[0]	= nor[0] * scale;
		u[1]	= nor[1] * scale;
		v[0]	= (u[0] - (velNormalized[0])) + u[0];
		v[1]	= (u[1] - (velNormalized[1])) + u[1];

		p_physObj.setVelX((v[0] * len) - p_physObj.getAccelX());
		p_physObj.setVelY((v[1] * len) - p_physObj.getAccelY());
	}

	public void drawDebug(){
		glPushMatrix();

		Texture.bindNone();
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glScalef(1, -1, 1);

		//Print Debug Collision
		for(Line objLine : m_arLines){
			float middleX= objLine.m_fStart[0] + ((objLine.m_fEnd[0] - objLine.m_fStart[0])/2);
			float middleY= objLine.m_fStart[1] + ((objLine.m_fEnd[1] - objLine.m_fStart[1])/2);
			float middleNormX= middleX - (float)(Math.sin(objLine.m_fAngle) * 10.0f);
			float middleNormY= middleY + (float)(Math.cos(objLine.m_fAngle) * 10.0f);

			glColor3d(1.0, 0.0, 0.0);
			glBegin(GL_LINE);
				glVertex2d(objLine.m_fStart[0], objLine.m_fStart[1]);
				glVertex2d(objLine.m_fEnd[0], objLine.m_fEnd[1]);
			glEnd();

			glColor3d(0.0, 1.0, 0.0);
			glBegin(GL_LINE);
				glVertex2d(middleX, middleY);
				glVertex2d(middleNormX, middleNormY);
			glEnd();
		}

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();
	}
}
