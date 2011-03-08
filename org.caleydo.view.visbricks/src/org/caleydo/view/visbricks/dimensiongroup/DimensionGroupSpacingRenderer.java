package org.caleydo.view.visbricks.dimensiongroup;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class DimensionGroupSpacingRenderer extends LayoutRenderer {

	boolean renderSpacer = false;
	
	@Override
	public void render(GL2 gl) {
		
		if (renderSpacer) {
			gl.glColor3f(0,0,0);
			
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex2f(0, 0);
			gl.glVertex2f(x, 0);
			gl.glVertex2f(x, y);
			gl.glVertex2f(0, y);		
			gl.glEnd();			
		}
	}
	
	public void setRenderSpacer(boolean renderSpacer) {
		this.renderSpacer = renderSpacer;
	}
}
