/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
		GLUquadric quadric = glu.gluNewQuadric();

		gl.glLineWidth(fBorderWidth);
		glu.gluQuadricDrawStyle(quadric, GLU.GLU_SILHOUETTE);
		glu.gluDisk(quadric, 0, fRadius, iNumSlicesPerFullDisc, 1);

		glu.gluDeleteQuadric(quadric);
	}
}
