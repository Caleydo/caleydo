/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Class that provides static methods for rendering circles and partial discs.
 *
 * @author Christian Partl
 */
public class GLPrimitives {

	/**
	 * Renders a partial disc using the specified parameters.
	 *
	 * @param glu
	 *            GLU object that shall be used for drawing the partial disc.
	 * @param fInnerRadius
	 *            Inner radius of the partial disc.
	 * @param fOuterRadius
	 *            Outer radius of the partial disc.
	 * @param fStartAngle
	 *            Angle where to start drawing the partial disc.
	 * @param fAngle
	 *            Angle of the partial disc.
	 * @param iNumSlicesPerFullDisc
	 *            The number of slices which would be drawn to approximate a
	 *            full disc. Higher numbers produce a better approximation but
	 *            the performance is worse.
	 */
	public static void renderPartialDisc(GLU glu, float fInnerRadius, float fOuterRadius,
			float fStartAngle, float fAngle, int iNumSlicesPerFullDisc) {

		GLUquadric quadric = glu.gluNewQuadric();
		int iMinNumSlices = fAngle < 180 ? 2 : 3;
		int iNumSlices = Math.max(Math.round(fAngle / 360.0f * iNumSlicesPerFullDisc),
				iMinNumSlices);

		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluPartialDisk(quadric, fInnerRadius, fOuterRadius, iNumSlices, 1,
				fStartAngle, fAngle);

		glu.gluDeleteQuadric(quadric);
	}

	/**
	 * Renders the border of a partial disc using the specified parameters.
	 *
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing the partial disc.
	 * @param fInnerRadius
	 *            Inner radius of the partial disc.
	 * @param fOuterRadius
	 *            Outer radius of the partial disc.
	 * @param fStartAngle
	 *            Angle where to start drawing the partial disc.
	 * @param fAngle
	 *            Angle of the partial disc.
	 * @param iNumSlicesPerFullDisc
	 *            The number of slices which would be drawn to approximate a
	 *            full disc. Higher numbers produce a better approximation but
	 *            the performance is worse.
	 * @param fBorderWidth
	 *            Width of the drawn border.
	 */
	public static void renderPartialDiscBorder(GL2 gl, GLU glu, float fInnerRadius,
			float fOuterRadius, float fStartAngle, float fAngle,
			int iNumSlicesPerFullDisc, float fBorderWidth) {

		GLUquadric quadric = glu.gluNewQuadric();
		int iMinNumSlices = fAngle < 180 ? 2 : 3;
		int iNumSlices = Math.max(Math.round(fAngle / 360.0f * iNumSlicesPerFullDisc),
				iMinNumSlices);

		gl.glLineWidth(fBorderWidth);
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_SILHOUETTE);
		glu.gluPartialDisk(quadric, fInnerRadius, fOuterRadius, iNumSlices, 1,
				fStartAngle, fAngle);

		glu.gluDeleteQuadric(quadric);
	}

	/**
	 * Renders a filled circle.
	 *
	 * @param glu
	 *            GLU object that shall be used for drawing the circle.
	 * @param fRadius
	 *            Radius of the circle.
	 * @param iNumSlicesPerFullDisc
	 *            The number of slices which would be drawn to approximate a
	 *            full disc. Higher numbers produce a better approximation but
	 *            the performance is worse.
	 */
	public static void renderCircle(GLU glu, float fRadius, int iNumSlicesPerFullDisc) {
		GLUquadric quadric = glu.gluNewQuadric();

		glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
		glu.gluDisk(quadric, 0, fRadius, iNumSlicesPerFullDisc, 1);

		glu.gluDeleteQuadric(quadric);
	}

	/**
	 * Renders the border of a circle.
	 *
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing the circle.
	 * @param fRadius
	 *            Radius of the circle.
	 * @param iNumSlicesPerFullDisc
	 *            The number of slices which would be drawn to approximate a
	 *            full disc. Higher numbers produce a better approximation but
	 *            the performance is worse.
	 * @param fBorderWidth
	 *            Width of the drawn border.
	 */
	public static void renderCircleBorder(GL2 gl, GLU glu, float fRadius,
			int iNumSlicesPerFullDisc, float fBorderWidth) {
		gl.glLineWidth(fBorderWidth);
		renderCircleBorder(glu, fRadius, iNumSlicesPerFullDisc);
	}

	public static void renderCircleBorder(GLU glu, float fRadius, int iNumSlicesPerFullDisc) {
		GLUquadric quadric = glu.gluNewQuadric();

		glu.gluQuadricDrawStyle(quadric, GLU.GLU_SILHOUETTE);
		glu.gluDisk(quadric, 0, fRadius, iNumSlicesPerFullDisc, 1);

		glu.gluDeleteQuadric(quadric);
	}
}
