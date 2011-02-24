package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

/**
 * Every ARenderer renders from (0, 0) to (x, y). An ARenderer does not take care of any spacings on the
 * sides.
 * 
 * @author Alexander Lex
 */
public class ARenderer {
	protected float x;
	protected float y;
	protected boolean debugMode = true;

	protected ElementLayout elementLayout;

	public void setLimits(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** Calculate spacing if required */
	public void updateSpacing(Template template, ElementLayout elementLayout) {
		this.elementLayout = elementLayout;
	}

	public void render(GL2 gl) {

//		if (debugMode && elementLayout != null) {
//			gl.glBegin(GL.GL_LINE_LOOP);
//			gl.glVertex3f(0, 0, 0);
//			gl.glVertex3f(elementLayout.getSizeScaledX(), 0, 0);
//			gl.glVertex3f(elementLayout.getSizeScaledX(), elementLayout.getSizeScaledY(), 0);
//			gl.glVertex3f(0, elementLayout.getSizeScaledY(), 0);
//			gl.glEnd();
//		}
	}

}
