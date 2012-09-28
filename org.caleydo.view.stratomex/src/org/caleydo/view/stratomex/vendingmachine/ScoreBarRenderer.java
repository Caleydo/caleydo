package org.caleydo.view.stratomex.vendingmachine;

import javax.media.opengl.GL2;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class ScoreBarRenderer
	extends LayoutRenderer {
	
	
	
	private float score;
	
	private Color color;
	
	public ScoreBarRenderer(float score, Color color) {
		this.score = score;
		this.color = color;
		
		
	}

	@Override
	public void renderContent(GL2 gl) {

		float padding = 0.015f;
		float barWidth = x * score;

		gl.glColor4fv(color.getRGBA(), 0);
		
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0+padding, 0+padding, 0);
		gl.glVertex3f(barWidth-padding, 0+padding, 0);
		gl.glVertex3f(barWidth-padding, y-padding, 0);
		gl.glVertex3f(0+padding, y-padding, 0);
		gl.glEnd();
		
		gl.glLineWidth(1);
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1);
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
	}

	@Override
	protected boolean permitsDisplayLists() {
		return true;
	}
}
