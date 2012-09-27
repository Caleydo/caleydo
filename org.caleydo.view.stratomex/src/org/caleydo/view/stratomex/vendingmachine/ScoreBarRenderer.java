package org.caleydo.view.stratomex.vendingmachine;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class ScoreBarRenderer
	extends LayoutRenderer {

	private final static float[] BAR_COLOR = { 1f, 0f, 0f, 1 };

	private float score;

	public ScoreBarRenderer(float score) {
		this.score = score;
	}

	@Override
	public void renderContent(GL2 gl) {

		float barWidth = x * score;

		gl.glColor4fv(BAR_COLOR, 0);
		gl.glBegin(GL2.GL_POLYGON);

		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(barWidth, 0, 0);
		gl.glVertex3f(barWidth, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
	}

	@Override
	protected boolean permitsDisplayLists() {
		return true;
	}
}
