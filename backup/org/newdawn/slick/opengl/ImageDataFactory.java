//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.opengl;


//Standard Java imports
import java.security.AccessController;
import java.security.PrivilegedAction;

//Slick-util library import
import org.newdawn.slick.util.Log;


public class ImageDataFactory{
	private static final String PNG_LOADER= "org.newdawn.slick.pngloader";

	private static boolean m_bUsePngLoader= true;
	private static boolean m_isPNGLoaderPropertyChecked= false;


	private static void checkProperty(){
		if(!m_isPNGLoaderPropertyChecked){
			m_isPNGLoaderPropertyChecked= true;

			try{
				AccessController.doPrivileged(new PrivilegedAction(){
		            public Object run(){
						String val = System.getProperty(PNG_LOADER);
						if("false".equalsIgnoreCase(val))
							m_bUsePngLoader= false;
						
						Log.info("Use Java PNG Loader = " + m_bUsePngLoader);
						return null;
		            }
				});
			}catch(Throwable e){
				// ignore, security failure - probably an applet
			}
		}
	}
	
	public static LoadableImageData getImageDataFor(String p_stRef){
		checkProperty();
		
		p_stRef= p_stRef.toLowerCase();
		
        if(p_stRef.endsWith(".tga"))
        	return new TGAImageData();

		if(p_stRef.endsWith(".png")){
        	CompositeImageData data = new CompositeImageData();

			if(m_bUsePngLoader)
        		data.add(new PNGImageData());

			data.add(new ImageIOImageData());
        	return data;
        } 
        
        return new ImageIOImageData();
	}
}
