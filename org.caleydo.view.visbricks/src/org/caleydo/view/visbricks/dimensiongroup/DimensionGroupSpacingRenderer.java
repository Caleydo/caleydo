package org.caleydo.view.visbricks.dimensiongroup;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class DimensionGroupSpacingRenderer extends LayoutRenderer {

	private boolean renderSpacer = false;
	
	private boolean isVertical = true;
	
	private float lineLength = 0;
	
	@Override
	public void render(GL2 gl) {
		
		if (renderSpacer) {
			gl.glColor3f(0,0,0);
			gl.glLineWidth(3);
			
			gl.glBegin(GL2.GL_LINES);
			
			if (isVertical) {
				gl.glVertex2f(x/2f, 0);
				gl.glVertex2f(x/2f, y);						
			}
			else {
				gl.glVertex2f(0, y/2f);
				gl.glVertex2f(x, y/2f);										
			}
			
			gl.glEnd();			
		}
	}
	
	public void setRenderSpacer(boolean renderSpacer) {
		this.renderSpacer = renderSpacer;
	}
	
	public void setVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}
	
	public void setLineLength(float lineLength) {
		this.lineLength = lineLength;
	}
}
