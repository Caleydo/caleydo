package org.caleydo.core.view.opengl.canvas.hierarchy;

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

		gl.glColor4f(0, 0, 0, 1);
		gl.glLineWidth(3);
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_SILHOUETTE);
		glu.gluPartialDisk(quadric, fInnerRadius, fOuterRadius, iNumSlices, 1, fStartAngle, fAngle);

		glu.gluDeleteQuadric(quadric);
		// fAngle = (fAngle > 360) ? 360 : fAngle;
		// gl.glBegin(GL.GL_QUAD_STRIP);
		// for(float i = fStartAngle; i <= fStartAngle + fAngle; i++)
		// {
		// float fRadiantAngle = (float)((Math.PI/ 180 )* i);
		// float fBaseX = (float)Math.cos(fRadiantAngle);
		// float fBaseY = (float)Math.sin(fRadiantAngle);
		// gl.glVertex3f(fBaseX * fInnerRadius,
		// fBaseY * fInnerRadius, -1);
		// gl.glVertex3f(fBaseX * fOuterRadius,
		// fBaseY * fOuterRadius, -1);
		// }
		// gl.glEnd();
	}

	public static void renderCircle(GL gl, GLU glu, float fRadius, int iNumSlicesPerFullDisc) {
		GLUquadric quadric = glu.gluNewQuadric();

		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluDisk(quadric, 0, fRadius, iNumSlicesPerFullDisc, 1);

		gl.glColor4f(0, 0, 0, 1);
		gl.glLineWidth(3);
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_SILHOUETTE);
		glu.gluDisk(quadric, 0, fRadius, iNumSlicesPerFullDisc, 1);

		glu.gluDeleteQuadric(quadric);
		// gl.glBegin(GL.GL_TRIANGLE_FAN);
		// gl.glVertex3f(0, 0, -1);
		// for(float i = 0; i <= 360; i++)
		// {
		// float fRadiantAngle = (float)((Math.PI/ 180 )* i);
		// float fBaseX = (float)Math.cos(fRadiantAngle);
		// float fBaseY = (float)Math.sin(fRadiantAngle);
		// gl.glVertex3f(fBaseX * fRadius,
		// fBaseY * fRadius, -1);
		// }
		// gl.glEnd();
	}
}
