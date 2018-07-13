//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick;


public class SlickException extends Exception{
	public SlickException(String message){
		super(message);
	}

	public SlickException(String message, Throwable e){
		super(message, e);
	}
}
