package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL2;

import org.caleydo.view.heatmap.heatmap.layout.ATemplate;
import org.caleydo.view.heatmap.heatmap.layout.RenderParameters;

/**
 * Every ARenderer renders from (0, 0) to (x, y). An ARenderer does not take
 * care of any spacings on the sides.
 * 
 * @author Alexander Lex
 * 
 */
public abstract class ARenderer {
	protected float x;
	protected float y;

	public void setLimits(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** Calculate spacing if required */
	public void updateSpacing(ATemplate template, RenderParameters parameters) {

	}

	public abstract void render(GL2 gl);

}
