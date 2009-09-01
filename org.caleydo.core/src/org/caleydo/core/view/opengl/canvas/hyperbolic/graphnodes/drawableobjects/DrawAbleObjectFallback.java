package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

/**
 * Draw able object: Fallback This draw able object defines the standard object - Circle black
 * 
 * @author Georg Neubauer
 */
public class DrawAbleObjectFallback
	extends ADrawAbleObject {

	@Override
	public ArrayList<Vec3f> getConnectionPoints() {
		ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
		float radius = Math.min(fHeight / 2f, fWidth / 2f);
		for (int i = 0; i < HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS; i++) {
			float angle = (float) (i * 2 * Math.PI / HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS);
			alPoints.add(new Vec3f((float) (fXCoord + Math.cos(angle) * radius), (float) (fYCoord + Math
				.sin(angle)
				* radius), fZCoord));
		}
		return alPoints;
	}

	@Override
	public ArrayList<Vec3f> draw(GL gl, boolean bHighlight) {
		if (bHighlight)
			gl.glColor4fv(HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME_HL, 0);
		else
			gl.glColor4fv(HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME, 0);

		float radius = Math.min(fHeight / 2f, fWidth / 2f);
		gl.glBegin(GL.GL_POLYGON);
		for (int i = 0; i < 180; i++) {
			float angle = (float) (i * 2 * Math.PI / 180f);
			gl.glVertex3f((float) (fXCoord + Math.cos(angle) * radius), (float) (fYCoord + Math.sin(angle)
				* radius), 0.0f);
		}
		gl.glEnd();
		return getConnectionPoints();
	}
}

// @Override
// protected void switchColorMapping(boolean b) {
// if (b) {
// this.fRed = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME_HL[0];
// this.fGreen = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME_HL[1];
// this.fBlue = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME_HL[2];
// this.fAlpha = HyperbolicRenderStyle.DA_OBJ_FALLBACK_ALPHA_HL;
// }
// else {
// this.fRed = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[0];
// this.fGreen = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[1];
// this.fBlue = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[2];
// this.fAlpha = HyperbolicRenderStyle.DA_OBJ_FALLBACK_ALPHA;
// }
// }