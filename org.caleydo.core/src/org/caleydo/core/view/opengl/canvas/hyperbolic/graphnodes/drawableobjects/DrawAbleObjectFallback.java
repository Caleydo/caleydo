package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.swing.text.html.MinimalHTMLWriter;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

/**
 * Draw able object: Fallback This draw able object defines the standard object - Circle black
 * 
 * @author Georg Neubauer
 */
public class DrawAbleObjectFallback
	extends ADrawAbleObject {

	public DrawAbleObjectFallback() {
		this.fRed = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSHEME[0];
		this.fGreen = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSHEME[1];
		this.fBlue = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSHEME[2];
		this.fAlpha = HyperbolicRenderStyle.DA_OBJ_FALLBACK_ALPHA;
	}

	@Override
	public ArrayList<Vec3f> drawObjectAtPosition(GL gl, float fXCoord, float fYCoord, float fZCoord,
		float fHeight, float fWidth) {
		float angle;
		float radius;
		gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
		radius = Math.min(fHeight / 2f, fWidth / 2f);
		gl.glBegin(GL.GL_POLYGON);
		for (int i = 0; i < 180; i++) {
			angle = (float) (i*2*Math.PI / 180f);
			gl.glVertex3f((float) (fXCoord + Math.cos(angle) * radius), (float) (fYCoord + Math.sin(angle) * radius), 0.0f);
		}
		gl.glEnd();
		gl.glFlush();
		
		ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
		
		for(int i = 0; i < HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS; i++){
			angle = (float) (i*2*Math.PI / HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS);
			alPoints.add(new Vec3f((float) (fXCoord + Math.cos(angle) * radius), (float) (fYCoord + Math.sin(angle) * radius), fZCoord));
		}
		return alPoints;
	}
}
