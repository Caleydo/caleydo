package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class SpacerRenderer extends LayoutRenderer {
	
	private boolean isVertical;
	
	public SpacerRenderer(boolean isVertical) {
		this.isVertical= isVertical;	}

	
	@Override
	public void render(GL2 gl) {

//		gl.glColor3f(0.3f, 0.3f, 0.3f);
//		gl.glLineWidth(1);
//		gl.glBegin(GL2.GL_LINES);
//		
//		if(isVertical) {
//			gl.glVertex3f(x/2.0f, 0, 0);
//			gl.glVertex3f(x/2.0f, y, 0);
//			
//		} else {
//			gl.glVertex3f(0, y/2.0f, 0);
//			gl.glVertex3f(x, y/2.0f, 0);
//		}
//		
//		gl.glEnd();

	}
}
