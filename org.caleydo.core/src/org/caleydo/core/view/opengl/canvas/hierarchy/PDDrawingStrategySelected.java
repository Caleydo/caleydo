package org.caleydo.core.view.opengl.canvas.hierarchy;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

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
	public void drawPartialDisc(GL gl, GLU glu, float fWidth, float fInnerRadius, float fStartAngle,
		float fAngle) {

		float fMidAngle = fStartAngle + (fAngle / 2.0f);
		while (fMidAngle > 360) {
			fMidAngle -= 360;
		}
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
		float fArRGB[] = cmRainbow.getColor(fMidAngle / 360);

		gl.glColor4f(1 - fArRGB[0], 1 - fArRGB[1], 1 - fArRGB[2], 1);
		GLPrimitives.renderPartialDisc(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle, fAngle,
			iNumSlicesPerFullDisc);
		gl.glColor4f(0, 0, 0, 1);
		GLPrimitives.renderPartialDiscBorder(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle,
			fAngle, iNumSlicesPerFullDisc, 3);

		gl.glPopAttrib();

	}

}
