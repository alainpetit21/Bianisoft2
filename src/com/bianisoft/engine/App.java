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


//Special static LWJGL library imports
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

//Standard Java imports
import java.util.ArrayList;
import java.awt.Canvas;
import java.awt.Font;

//Standard LWJGL Imports
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

//Bianisoft library import
import com.bianisoft.engine.helper.datatypes.Int;
import com.bianisoft.engine.helper.Random;
import com.bianisoft.engine.helper.Sleep;
import com.bianisoft.engine.resmng.Texture;
import com.bianisoft.engine.resmng.TrueTypeFont;
import com.bianisoft.engine.manager.MngContextSwitcher;
import com.bianisoft.engine.manager.MngContextSwitcher_Popup;
import com.bianisoft.engine.manager.MngInput;
import com.bianisoft.engine.manager.MngAudio;
import com.bianisoft.engine.manager.physic.MngPhysic;


// Modifier order
/*
 public		static	abstract	synchronized	transient	final	native
 private										volatile
 protected
*/
public abstract class App{
    public static final boolean	PRINT_DEBUG= false;

    public static App	g_theApp;
	public static int	g_nWidth;
	public static int	g_nHeight;

	public DisplayMode	m_curDisplayMode;

	public TrueTypeFont			m_fontSystem;
	public MngContextSwitcher	m_objContextSwitcher= new MngContextSwitcher();
	private final MngInput			m_mngInput= new MngInput();
	private final MngPhysic			m_mngPhysic= new MngPhysic();
	private final MngAudio			m_mngAudio= new MngAudio();

	public ArrayList<Context>	m_arObj			= new ArrayList<Context>();
	public ArrayList<Int>		m_stkIdxContext	= new ArrayList<Int>();

	private Camera		m_cam2D;
	private Camera		m_cam3D;
	public Context		m_ctxCur;
	public String		m_stGameName;
	public long			m_lastFrameTick;
	public long			m_thisFrameTick;
	public int			m_nCptLoop;
	public boolean		m_isRunning= true;
	public boolean		m_isRunningApplet= false;
	public boolean		m_isFullScreen;


	//STATIC FUNCTIONS
	// App.libMain
	//		libMain is the main entry point, the only thing to be call in client main()*/
    public static void libMain(String[] args){
		try{
			g_theApp.create();
			g_theApp.run();
		}catch(Exception ex){
			System.out.print("***ERROR***\nSomething went wrong\n" + ex);
			ex.printStackTrace();
		}finally{
			if(g_theApp != null)
				g_theApp.destroy();
		}
	}

	// Basic Functions
	//		Getter and an exit function
	public static App get()		{return App.g_theApp;}
	public static void exit(){
		if(!App.g_theApp.m_isRunningApplet)
			App.g_theApp.m_isRunning= false;
	}

	// Constructor
	//		Basic Construction
	public App(String p_stName, int p_nWidth, int p_nHeight, boolean p_isFullscreen){
		g_theApp		= this;
		m_stGameName	= p_stName;
		g_nWidth	= p_nWidth;
		g_nHeight	= p_nHeight;
		m_isFullScreen	= p_isFullscreen && Display.getDesktopDisplayMode().isFullscreenCapable();
	}
	
	//PRIVATE API
	// App.create
	//		Handles the reminder of construction phase. It is matched by the Destroyer
	void create() throws LWJGLException{
		if(m_isFullScreen){
			m_curDisplayMode= Display.getDesktopDisplayMode();
			Display.setFullscreen(true);
		}else{
			m_curDisplayMode= new DisplayMode(g_nWidth, g_nHeight);
           	Display.setDisplayMode(m_curDisplayMode);
		}
	    Display.setTitle(m_stGameName);
	    Display.create();
	    initGL();

	    m_mngAudio.create();
	    m_mngInput.create();

	    //Internal initiation
	    Random.setSeed((int)System.nanoTime());
	    m_fontSystem= new TrueTypeFont(new Font("serif", Font.PLAIN, 24), true);

	    m_cam2D= Camera.createCamera(Camera.TYPE_2D);
	    m_cam3D= Camera.createCamera(Camera.TYPE_3D);

	    load();
	}


	void create(Canvas p_objParent) throws LWJGLException{
		Display.setParent(p_objParent);
		create();
	}

	// App.destroy
	//		Freeing some stuff need to be freed
	void destroy(){
	    m_mngInput.destroy();
	    Display.destroy();
	}

	// App.initGL
	//		Called when it is timeto initiate OpenGL
	private void initGL(){
		glShadeModel(GL_SMOOTH);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		resizeGL(g_nWidth, g_nHeight);
	}

	// App.initGL
	//		Called when it is timeto initiate OpenGL
	private void resizeGL(int p_nWidth, int p_nHeight){
		glViewport(0, 0, p_nWidth, p_nHeight);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluOrtho2D(0.0f, p_nWidth, p_nHeight, 0.0f);
		glPushMatrix();

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
	}

	// App.run
	//		Called internally to process the main loop
	void run(){
		while(m_isRunning){
			m_thisFrameTick= System.nanoTime();

			float nRationTime= (m_thisFrameTick - m_lastFrameTick)/16666667.0f;	
			m_cam2D.manage(nRationTime);
			m_cam3D.manage(nRationTime);

			if(Display.isVisible()){
				if(m_objContextSwitcher.isActive()){
					m_objContextSwitcher.manage();
					m_lastFrameTick= m_thisFrameTick;
					m_nCptLoop+= 1;
				}else{
					m_mngInput.manage();
					m_mngAudio.manage(m_thisFrameTick - m_lastFrameTick);
					m_ctxCur.keyboardManage(m_mngInput);
					m_ctxCur.mouseManage(m_mngInput);
					m_ctxCur.manage(nRationTime);
					draw(true);
				}
			}else{
				draw(false);
				Sleep.sleep(100);
			}

			Display.update();
			Display.sync(60);

			if(Display.isCloseRequested())
				exit();

			m_lastFrameTick= m_thisFrameTick;
			m_nCptLoop+= 1;
		}
		
		Sleep.sleep(500);
	}

	// App.run
	//		Called internally to handling the draw managment, ultimatly will call context to actual do it.
	private void draw(boolean p_isActive){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Texture.bindNone();

		if(p_isActive){
			m_ctxCur.draw();
			if(PRINT_DEBUG)
				drawDebug();
		}else{
			if(Display.isDirty()){
				m_ctxCur.draw();
				if(PRINT_DEBUG)
					drawDebug();
			}
		}
	}

	// App.run
	//		Called internally to display the framerate in debug mode
	private void drawDebug(){
		//Print FPS
		double fps= 1000000000.0/((m_thisFrameTick - m_lastFrameTick)+1);
		Camera cam= Camera.getCur(Camera.TYPE_2D);

		if(cam == null)
			return;

		cam.doProjection();
		m_fontSystem.drawString(-g_nWidth/2, -g_nHeight/2, Double.toString(fps));
	}


	public abstract void load();

	public abstract String getVersion();

	public static Context getCurContext(){
		return App.g_theApp.m_ctxCur;
	}

	public void setCurContext(Int p_nContext)	{setCurContext(p_nContext.m_nValue);}
	public void setCurContext(int p_nContext){
		m_mngInput.reInit();
		
		if(m_arObj.isEmpty())
			m_objContextSwitcher.set(m_ctxCur, p_nContext);
		else
			m_objContextSwitcher.set(m_ctxCur, (Context)m_arObj.get(p_nContext));
	}
	
	public void addContext(Context p_ctx){
		p_ctx.m_nIndex= m_arObj.size();
		m_arObj.add(p_ctx);
	}

	public void pushContext(int p_nContext){
		m_stkIdxContext.add(new Int(m_ctxCur.m_nIndex));
		m_objContextSwitcher= new MngContextSwitcher_Popup();
		setCurContext(p_nContext);
	}

	public void popContext(){
		if(m_stkIdxContext.isEmpty())
			return;

		setCurContext(m_stkIdxContext.get(m_stkIdxContext.size()-1));
		m_stkIdxContext.remove(m_stkIdxContext.size()-1);
	}
}
