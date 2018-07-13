//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.util;


//Standard Java Input
import java.io.InputStream;
import java.net.URL;


public interface I_ResourceLocation{
	public InputStream getResourceAsStream(String ref);
	public URL getResource(String ref);
}
