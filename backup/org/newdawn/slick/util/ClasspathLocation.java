//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.util;


import java.io.InputStream;
import java.net.URL;


public class ClasspathLocation implements I_ResourceLocation{
	public URL getResource(String ref){
		String cpRef= ref.replace('\\', '/');
		return ResourceLoader.class.getClassLoader().getResource(cpRef);
	}

	public InputStream getResourceAsStream(String ref){
		String cpRef= ref.replace('\\', '/');
		return ResourceLoader.class.getClassLoader().getResourceAsStream(cpRef);	
	}
}
