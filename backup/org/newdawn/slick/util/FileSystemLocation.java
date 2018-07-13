//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.util;


//Standard Java imports
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class FileSystemLocation implements I_ResourceLocation {
	private File m_fileRoot;
	
	public FileSystemLocation(File p_fileRoot){
		m_fileRoot= p_fileRoot;
	}
	
	public URL getResource(String p_ref){
		try{
			File file= new File(m_fileRoot, p_ref);

			if(!file.exists())
				file= new File(p_ref);

			if(!file.exists())
				return null;
			
			return file.toURI().toURL();
		}catch(IOException e){
			return null;
		}
	}

	public InputStream getResourceAsStream(String p_ref){
		try{
			File file= new File(m_fileRoot, p_ref);

			if(!file.exists())
				file= new File(p_ref);

			return new FileInputStream(file);
		}catch(IOException e){
			return null;
		}
	}
}
