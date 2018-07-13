package org.newdawn.slick.opengl.renderer;

/**
 * The default version of the renderer relies of GL calls to do everything. 
 * Unfortunately this is driver dependent and often implemented inconsistantly
 * 
 * @author kevin
 */
public class DefaultLineStripRenderer implements I_LineStripRenderer {
	/** The access to OpenGL */
	private static I_SGL GL = Renderer.get();
	
	/**
	 * @see org.newdawn.slick.opengl.renderer.I_LineStripRenderer#end()
	 */
	public void end() {
		GL.glEnd();
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.I_LineStripRenderer#setAntiAlias(boolean)
	 */
	public void setAntiAlias(boolean antialias) {
		if (antialias) {
			GL.glEnable(I_SGL.GL_LINE_SMOOTH);
		} else {
			GL.glDisable(I_SGL.GL_LINE_SMOOTH);
		}
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.I_LineStripRenderer#setWidth(float)
	 */
	public void setWidth(float width) {
		GL.glLineWidth(width);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.I_LineStripRenderer#start()
	 */
	public void start() {
		GL.glBegin(I_SGL.GL_LINE_STRIP);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.I_LineStripRenderer#vertex(float, float)
	 */
	public void vertex(float x, float y) {
		GL.glVertex2f(x,y);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.I_LineStripRenderer#color(float, float, float, float)
	 */
	public void color(float r, float g, float b, float a) {
		GL.glColor4f(r, g, b, a);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.I_LineStripRenderer#setLineCaps(boolean)
	 */
	public void setLineCaps(boolean caps) {
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.I_LineStripRenderer#applyGLLineFixes()
	 */
	public boolean applyGLLineFixes() {
		return true;
	}

}
