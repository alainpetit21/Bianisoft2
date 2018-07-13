//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.openal;

import java.io.IOException;

interface I_AudioInputStream {
	public int getChannels();
	public int getRate();
	public int read() throws IOException;
	public int read(byte[] data) throws IOException;
	public int read(byte[] data, int ofs, int len) throws IOException;
	public boolean atEnd();
	public void close() throws IOException;
}
