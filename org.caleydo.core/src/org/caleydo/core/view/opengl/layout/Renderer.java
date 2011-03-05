package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

/**
 * Every Renderer renders from (0, 0) to (x, y). An Renderer does not take care of any spacings on the
 * sides.
 * 
 * @author Alexander Lex
 */
public class Renderer {
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
	}

}
