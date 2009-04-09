package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import java.lang.Math;

public class GLPrimitives {

	public static void renderPartialDisc(GL gl, GLU glu, float fInnerRadius, float fOuterRadius,
		float fStartAngle, float fAngle, int iNumSlicesPerFullDisc) {

		GLUquadric quadric = glu.gluNewQuadric();
		int iMinNumSlices = fAngle < 180 ? 2 : 3;
		int iNumSlices = Math.max(Math.round(fAngle / 360.0f * (float) iNumSlicesPerFullDisc), iMinNumSlices);

		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluPartialDisk(quadric, fInnerRadius, fOuterRadius, iNumSlices, 1, fStartAngle, fAngle);

		glu.gluDeleteQuadric(quadric);
	}

	public static void renderPartialDiscBorder(GL gl, GLU glu, float fInnerRadius, float fOuterRadius,
		float fStartAngle, float fAngle, int iNumSlicesPerFullDisc, float fBorderWidth) {

		GLUquadric quadric = glu.gluNewQuadric();
		int iMinNumSlices = fAngle < 180 ? 2 : 3;
		int iNumSlices = Math.max(Math.round(fAngle / 360.0f * (float) iNumSlicesPerFullDisc), iMinNumSlices);

		gl.glLineWidth(fBorderWidth);
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_SILHOUETTE);
		glu.gluPartialDisk(quadric, fInnerRadius, fOuterRadius, iNumSlices, 1, fStartAngle, fAngle);

		glu.gluDeleteQuadric(quadric);
	}

	public static void renderCircle(GL gl, GLU glu, float fRadius, int iNumSlicesPerFullDisc) {
		GLUquadric quadric = glu.gluNewQuadric();

		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluDisk(quadric, 0, fRadius, iNumSlicesPerFullDisc, 1);

		glu.gluDeleteQuadric(quadric);
	}

	public static void renderCircleBorder(GL gl, GLU glu, float fRadius, int iNumSlicesPerFullDisc,
		float fBorderWidth) {
		GLUquadric quadric = glu.gluNewQuadric();

		gl.glLineWidth(fBorderWidth);
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_SILHOUETTE);
		glu.gluDisk(quadric, 0, fRadius, iNumSlicesPerFullDisc, 1);

		glu.gluDeleteQuadric(quadric);
	}
}
