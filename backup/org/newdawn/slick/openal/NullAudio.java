package org.newdawn.slick.openal;

/**
 * A null implementation used to provide an object reference when sound
 * has failed.
 * 
 * @author kevin
 */
public class NullAudio implements I_Audio {
	/**
	 * @see org.newdawn.slick.openal.I_Audio#getBufferID()
	 */
	public int getBufferID() {
		return 0;
	}

	/**
	 * @see org.newdawn.slick.openal.I_Audio#getPosition()
	 */
	public float getPosition() {
		return 0;
	}

	/**
	 * @see org.newdawn.slick.openal.I_Audio#isPlaying()
	 */
	public boolean isPlaying() {
		return false;
	}

	/**
	 * @see org.newdawn.slick.openal.I_Audio#playAsMusic(float, float, boolean)
	 */
	public int playAsMusic(float pitch, float gain, boolean loop) {
		return 0;
	}

	/**
	 * @see org.newdawn.slick.openal.I_Audio#playAsSoundEffect(float, float, boolean)
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop) {
		return 0;
	}

	/**
	 * @see org.newdawn.slick.openal.I_Audio#playAsSoundEffect(float, float, boolean, float, float, float)
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop,
			float x, float y, float z) {
		return 0;
	}

	/**
	 * @see org.newdawn.slick.openal.I_Audio#setPosition(float)
	 */
	public boolean setPosition(float position) {
		return false;
	}

	/**
	 * @see org.newdawn.slick.openal.I_Audio#stop()
	 */
	public void stop() {
	}

}
