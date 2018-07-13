//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.loading;


import java.io.IOException;


public interface I_DeferredResource{
	public void load() throws IOException;
	public String getDescription();
}
