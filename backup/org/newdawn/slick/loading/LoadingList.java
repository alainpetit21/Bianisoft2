//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.loading;


//Standard Java import
import java.util.ArrayList;

//Slick-utils library imports
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.util.Log;


public class LoadingList{
	private static LoadingList m_objSingleton= new LoadingList();

	private ArrayList m_arDeferredRes= new ArrayList();
	private int m_nTotal;

	
	public static LoadingList get()		{return m_objSingleton;}
	
	public static void setDeferredLoading(boolean loading){
		m_objSingleton= new LoadingList();
		
		InternalTextureLoader.get().setDeferredLoading(loading);
		SoundStore.get().setDeferredLoading(loading);
	}
	
	public static boolean isDeferredLoading(){
		return InternalTextureLoader.get().isDeferredLoading();
	}


	private LoadingList()	{	}
	
	public int getTotalResources()		{return m_nTotal;}
	public int getRemainingResources()	{return m_arDeferredRes.size();}

	public void add(I_DeferredResource p_objRes){
		m_nTotal++;
		m_arDeferredRes.add(p_objRes);
	}
	
	public void remove(I_DeferredResource p_objRes){
		Log.info("Early loading of deferred resource due to req: " + p_objRes.getDescription());

		m_nTotal--;
		m_arDeferredRes.remove(p_objRes);
	}
	
	public I_DeferredResource getNext(){
		if (m_arDeferredRes.size() == 0)
			return null;
		
		return (I_DeferredResource)m_arDeferredRes.remove(0);
	}
}
