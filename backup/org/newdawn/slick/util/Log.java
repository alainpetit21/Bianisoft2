//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.util;


//Standard Java imports
import java.security.AccessController;
import java.security.PrivilegedAction;


public final class Log{
	private static boolean verbose= true;
	private static boolean forcedVerbose= false;
	private static final String forceVerboseProperty= "org.newdawn.slick.forceVerboseLog";
	private static final String forceVerbosePropertyOnValue= "true";
	
	private Log()	{	}
	
	public static void setVerbose(boolean v){
		if (forcedVerbose)
			return;

		verbose= v;
	}

	public static void checkVerboseLogSetting(){
		try{
			AccessController.doPrivileged(new PrivilegedAction(){
				
	            public Object run() {
					String val = System.getProperty(Log.forceVerboseProperty);
					if ((val != null) && (val.equalsIgnoreCase(Log.forceVerbosePropertyOnValue))){
						Log.setForcedVerboseOn();
					}
					
					return null;
	            }
			});
		}catch (Throwable e){
			// ignore, security failure - probably an applet
		}
	}
	
	public static void setForcedVerboseOn(){
		forcedVerbose= true;
		verbose= true;
	}
	
	public static void error(String message, Throwable e){
		System.out.print("***ERROR***\n" + message);
	}

	public static void error(Throwable e){
		System.out.print("***ERROR***\n" + e);
	}

	public static void error(String message){
		System.out.print("***ERROR***\n" + message);
	}

	public static void warn(String message){
		System.out.print("***WARNING***\n" + message);
	}
	
	public static void warn(String message, Throwable e){
		System.out.print("***WARNING***\n" + message);
	}

	public static void info(String message){
		if(verbose || forcedVerbose){
			System.out.print("***INFO***\n" + message);
		}
	}

	public static void debug(String message){
		if (verbose || forcedVerbose) {
			System.out.print("***DEBUG***\n" + message);
		}
	}
}
