package org.caleydo.view.treemap.layout;

import java.awt.Color;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;

public class GlPainter implements IGlPainter {

	GL gl;
	IViewFrustum viewFrustum;
	
	public GlPainter(GL gl, IViewFrustum viewFrustum){
		this.gl=gl;
		this.viewFrustum=viewFrustum;
	}
	

	public void paintRectangle(float x, float y, float xmax, float ymax, Color c){
		gl.glBegin(GL.GL_QUADS);
		
		float color[] = c.getRGBColorComponents(null);
		gl.glColor3f(color[0], color[1], color[2]);
		
		x=viewFrustum.getWidth()*x;
		y=viewFrustum.getWidth()*y;		
		xmax=viewFrustum.getWidth()*xmax;
		ymax=viewFrustum.getHeight()*ymax;
		
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, ymax, 0);
		gl.glVertex3f(xmax, ymax, 0);
		gl.glVertex3f(xmax, y, 0);
		
		gl.glEnd();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
}
