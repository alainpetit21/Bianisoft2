//---
//Copyright (c) 2007, Kevin Glass
//All rights reserved.
//
// Licenced under the term of BSD - 3 Clauses Licences
//---
package org.newdawn.slick.openal;

public interface I_Audio {
	public void stop();
	public int getBufferID();
	public boolean isPlaying();
	public int playAsSoundEffect(float pitch, float gain, boolean loop);
	public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z);
	public int playAsMusic(float pitch, float gain, boolean loop);
	public boolean setPosition(float position);
	public float getPosition();
}
