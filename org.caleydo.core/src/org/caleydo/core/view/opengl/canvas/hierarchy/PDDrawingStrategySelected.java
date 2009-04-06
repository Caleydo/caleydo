package org.caleydo.core.view.opengl.canvas.hierarchy;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class PDDrawingStrategySelected
	extends PDDrawingStrategy {

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
		
		if(pdDiscToDraw == null)
			return;
		
		float fRadius = pdDiscToDraw.getCurrentWidth();
		
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor4f(0.6f, 1.0f, 1.0f, 1.0f);
		GLPrimitives.renderCircle(gl, glu, fRadius, iNumSlicesPerFullDisc);
		gl.glPopAttrib();

	}

	@Override
	public void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw) {
		
		if(pdDiscToDraw == null)
			return;
		
		float fStartAngle = pdDiscToDraw.getCurrentStartAngle();
		float fAngle = pdDiscToDraw.getCurrentAngle();
		float fInnerRadius = pdDiscToDraw.getCurrentInnerRadius();
		float fWidth = pdDiscToDraw.getCurrentWidth();

		float fMidAngle = fStartAngle + (fAngle / 2.0f);
		while (fMidAngle > 360) {
			fMidAngle -= 360;
		}
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		gl.glColor4f(0.6f, 1.0f, 1.0f, 1.0f);
		GLPrimitives.renderPartialDisc(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle, fAngle,
			iNumSlicesPerFullDisc);
		gl.glColor4f(0, 0, 0, 1);
		GLPrimitives.renderPartialDiscBorder(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle,
			fAngle, iNumSlicesPerFullDisc, 2);

		gl.glPopAttrib();

	}

}
