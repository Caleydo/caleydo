package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.swing.text.html.MinimalHTMLWriter;

/**
 * Draw able object: Fallback This draw able object defines the standard object - Circle black
 * 
 * @author Georg Neubauer
 */
public class DrawAbleObjectFallback
	extends ADrawAbleObject {

	public DrawAbleObjectFallback() {
		this.fRed = 0;
		this.fGreen = 0;
		this.fBlue = 0;
		this.fAlpha = 1;
	}

	@Override
	public ArrayList<Vec3f> drawObjectAtPosition(GL gl, float fXCoord, float fYCoord, float fZCoord,
		float fHeight, float fWidth) {
		float angle;
		float radius;
		gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
		radius = Math.min(fHeight, fWidth);
		gl.glBegin(GL.GL_POLYGON);
		for (int i = 0; i < 180; i++) {
			angle = (float) (i*2*Math.PI / 100f);
			gl.glVertex3f((float) (fXCoord + Math.cos(angle) * radius), (float) (fYCoord + Math.sin(angle) * radius), 0.0f);
		}
		gl.glEnd();
		gl.glFlush();
		// TODO: calculate points
		return null;
	}
}
