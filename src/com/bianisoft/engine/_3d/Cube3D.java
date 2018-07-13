package com.bianisoft.engine._3d;


//Standard Java library imports
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class Cube3D extends Object3D{
	private static final int TYPE_OTHER= 0x03;

	
	public Cube3D(String p_stTextureFilename){
		super(p_stTextureFilename);
		setSubClassID(TYPE_OTHER);
	}

	public void load(){
		super.load();
		
		float vertices[]= {
			-1.0f, -1.0f,  1.0f,	 1.0f, -1.0f,  1.0f,	-1.0f,  1.0f,  1.0f,	 1.0f,  1.0f,  1.0f,   
			 1.0f, -1.0f,  1.0f,	 1.0f, -1.0f, -1.0f,	 1.0f,  1.0f,  1.0f,	 1.0f,  1.0f, -1.0f,
			 1.0f, -1.0f, -1.0f,	-1.0f, -1.0f, -1.0f,	 1.0f,  1.0f, -1.0f,	-1.0f,  1.0f, -1.0f,
			-1.0f, -1.0f, -1.0f,	-1.0f, -1.0f,  1.0f,	-1.0f,  1.0f, -1.0f,	-1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f, -1.0f,	 1.0f, -1.0f, -1.0f,	-1.0f, -1.0f,  1.0f,	 1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f,  1.0f,	 1.0f,  1.0f,  1.0f,	-1.0f,  1.0f, -1.0f,	 1.0f,  1.0f, -1.0f,
		};

		float texture[]= {    		
			0.0f, 0.0f,		0.0f, 1.0f,		1.0f, 0.0f,		1.0f, 1.0f, 
			0.0f, 0.0f,		0.0f, 1.0f,		1.0f, 0.0f,		1.0f, 1.0f,
			0.0f, 0.0f,		0.0f, 1.0f,		1.0f, 0.0f,		1.0f, 1.0f,
			0.0f, 0.0f,		0.0f, 1.0f,		1.0f, 0.0f,		1.0f, 1.0f,
			0.0f, 0.0f,		0.0f, 1.0f,		1.0f, 0.0f,		1.0f, 1.0f,
			0.0f, 0.0f,		0.0f, 1.0f,		1.0f, 0.0f,		1.0f, 1.0f,
		};
		
		short indices[]= {
			0,   1,  3,		 0,  3,  2,
			4,   5,  7,		 4,  7,  6,
			8,   9, 11,		 8, 11, 10, 
			12, 13, 15,		12, 15, 14, 	
			16, 17, 19,		16, 19, 18, 	
			20, 21, 23,		20, 23, 22,
		};

		ByteBuffer vbb= ByteBuffer.allocateDirect((m_nNbVertices= vertices.length) * 4); 
		vbb.order(ByteOrder.nativeOrder());
		m_bufVertices= vbb.asFloatBuffer();
		m_bufVertices.put(vertices);
		m_bufVertices.position(0);

		ByteBuffer ibb = ByteBuffer.allocateDirect((m_nNbIndices= indices.length) * 2);
		ibb.order(ByteOrder.nativeOrder());
		m_bufIndices= ibb.asShortBuffer();
		m_bufIndices.put(indices);
		m_bufIndices.position(0);		

		ByteBuffer uvb= ByteBuffer.allocateDirect(m_nNbVertices * 4);
		uvb.order(ByteOrder.nativeOrder());
		m_bufUV= uvb.asFloatBuffer();
		m_bufUV.put(texture);
		m_bufUV.position(0);
	}

	public void manage(float p_nMoveRatio){
		super.manage(p_nMoveRatio);
		
		//Apply Rotational friction
		setAngleVelX(getAngleVelX() * 0.99f);
		setAngleVelY(getAngleVelY() * 0.99f);
		setAngleVelZ(getAngleVelZ() * 0.99f);
	}
}