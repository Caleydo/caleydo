package org.caleydo.core.view.opengl.canvas.hierarchy;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class PDDrawingStrategySelected
	extends PDDrawingStrategy {

	@Override
	public void drawFullCircle(GL gl, GLU glu, float fRadius) {
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor4f(0, 0, 0, 1);
		GLPrimitives.renderCircle(gl, glu, fRadius, iNumSlicesPerFullDisc);
		gl.glPopAttrib();

	}

	@Override
	public void drawPartialDisk(GL gl, GLU glu, float fWidth, float fInnerRadius, float fStartAngle,
		float fAngle) {

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		float val = 255.0f / 120.0f;
		float fMidAngle = fStartAngle + (fAngle / 2.0f);
		float rgb[] = { 0, 0, 0 };

		if (fMidAngle <= 120.0f) {
			rgb[0] = 255.0f - (fMidAngle * val);
			rgb[1] = fMidAngle * val;
			rgb[2] = 0;
		}
		if (fMidAngle > 120.0f && fMidAngle <= 240.0f) {
			rgb[0] = 0;
			rgb[1] = 255.0f - ((fMidAngle - 120.0f) * val);
			rgb[2] = (fMidAngle - 120.0f) * val;
		}
		if (fMidAngle > 240.0f && fMidAngle <= 360.0f) {
			rgb[0] = (fMidAngle - 240.0f) * val;
			rgb[1] = 0;
			rgb[2] = 255.0f - ((fMidAngle - 240.0f) * val);
		}

		gl.glColor4f(1 - (rgb[0] / 255), 1 - (rgb[1] / 255), 1 - (rgb[2] / 255), 1);

		GLPrimitives.renderPartialDisc(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle, fAngle,
			iNumSlicesPerFullDisc);
		gl.glPopAttrib();

	}

}
