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

//Standard Java import
import java.awt.BorderLayout;
import java.awt.Canvas;

//LWJGL library imports
import org.lwjgl.LWJGLException; 


public abstract class Applet extends java.applet.Applet{
	private App		m_objApp;
	private Canvas	m_objCanvas;
	private Thread	m_threadGame;


	// Constructor
	//		Basic Construction
	public Applet(String p_stName, int p_nWidth, int p_nHeight){
		super();

		final Applet objApplet= this;
		m_objApp= new App(p_stName, p_nWidth, p_nHeight, false){
			public void load(){
				objApplet.load();
			}
			public String getVersion(){
				return objApplet.getVersion();
			}
		};
	}

	
	//PRIVATE API
	// Applet.startLWJGL
	//		Handles starting of the LWJGL Library, and creation thread for the Applet
	private void startLWJGL(){
		m_threadGame= new Thread(){
			public void run(){
				try{
					m_objApp.create(m_objCanvas);
					m_objApp.m_isRunningApplet= true;
				}catch (LWJGLException e){
					e.printStackTrace();
				}

				m_objApp.run();
			}
		};

		m_threadGame.start();
	}

	// Applet.stopLWJGL
	//		Stop and merge the thread to be able to exit properly
	private void stopLWJGL(){
		m_objApp.m_isRunning= false;
		try{
			m_threadGame.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	//OVERRIDES Function - needed by java.applet.Applet
	public void start()		{	}
	public void stop()		{	}

	// Applet.destroy
	//		Called after the stop to clean memory
	public void destroy(){
		remove(m_objCanvas);
		super.destroy();
	}

	// Applet.destroy
	//		Called before start, and we will branch the library on the focusRequest
	public void init(){
		setLayout(new BorderLayout());

		try{
			m_objCanvas= new Canvas(){
				public final void addNotify(){
					super.addNotify();
					startLWJGL();
				}

				public final void removeNotify(){
					stopLWJGL();
					super.removeNotify();
				}
			};

			m_objCanvas.setSize(getWidth(), getHeight());
			add(m_objCanvas);

			m_objCanvas.setFocusable(true);
			m_objCanvas.requestFocus();
			m_objCanvas.setIgnoreRepaint(true);

			setVisible(true);
		}catch (Exception e){
			e.printStackTrace();
			System.out.print("***ERROR***\nUnable to create display\n");
			App.exit();
		}
	}

	//PUBLIC API
	// Applet.load; Applet.setCurContext and Applet.addContext
	//		Will be relayed to the dummy App Object
	public abstract void load();
	public abstract String getVersion();
	public void setCurContext(int p_nIdCtx)		{m_objApp.setCurContext(p_nIdCtx);}
	public void addContext(Context p_ctxToAdd)	{m_objApp.addContext(p_ctxToAdd);}
}