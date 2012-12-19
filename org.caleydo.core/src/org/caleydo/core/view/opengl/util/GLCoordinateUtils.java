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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

/**
 * Helper class for converting coordinates
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLCoordinateUtils {

	/**
	 * Converts window coordinates to world coordinates. You need to specify the x and y values of the
	 * position, plus the z value, scaled to 0 and 1 where 0 is the near and 1 is the far clipping plane
	 *
	 * @param gl
	 * @param iWindowCoordinatePositionX
	 *            the x position of the point you want to convert
	 * @param iWindowCoordinatePositionY
	 *            the y position of the point you want to convert
	 * @param fZValue
	 *            the z value, normalized to 0 and 1, where 0 is the near and 1 is the far clipping plane.
	 * @return a float array of length 3 with the world coordinates
	 */
	public static float[] convertWindowCoordinatesToWorldCoordinates(final GL2 gl,
		final int iWindowCoordinatePositionX, final int iWindowCoordinatePositionY, final float fZValue) {

		// FIXME: There is a problem, that causes some calculations not to be up to date (once a problem when
		// dragging)

		float[] fArWorldCoordinatePosition = new float[3];

		double mvmatrix[] = new double[16];
		double projmatrix[] = new double[16];
		int realy = 0;// GL2 y coord pos
		double[] wcoord = new double[4];// wx, wy, wz;// returned xyz coords
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		//
		// gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projmatrix, 0);

		/* note viewport[3] is height of window in pixels */
		realy = viewport[3] - iWindowCoordinatePositionY - 1;

		GLU glu = new GLU();

		// FIXME: the window z value is the problem why the dragging is
		// inaccurate in the bucket
		// For an explanation look at page 161 in the red book
		// 0.3 at least works for the bucket when the user zooms in
		glu.gluUnProject(iWindowCoordinatePositionX, realy, fZValue, //
			mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);

		// System.out.println("World coords at z=0.0 are ( " //
		// + wcoord[0] + ", " + wcoord[1] + ", " + wcoord[2]);

		fArWorldCoordinatePosition[0] = (float) wcoord[0];
		fArWorldCoordinatePosition[1] = (float) wcoord[1];
		// TODO: z is manually set to 0
		// fArWorldCoordinatePosition[2] = 0;// (float)wcoord[2];
		fArWorldCoordinatePosition[2] = (float) wcoord[2];

		return fArWorldCoordinatePosition;
	}

	/**
	 * Convenience version of {@link #convertWindowCoordinatesToWorldCoordinates(GL, int, int, float)} which
	 * assumes z to be 0.3, which is accurate for the bottom of the bucket
	 *
	 * @param gl
	 * @param iWindowCoordinatePositionX
	 * @param iWindowCoordinatePositionY
	 * @return
	 */
	public static float[] convertWindowCoordinatesToWorldCoordinates(final GL2 gl,
		final int iWindowCoordinatePositionX, final int iWindowCoordinatePositionY) {
		return convertWindowCoordinatesToWorldCoordinates(gl, iWindowCoordinatePositionX,
			iWindowCoordinatePositionY, 0.55f); // 0.055 works for bucket
	}

	public static float[] convertWindowToGLCoordinates(final int iWindowWidth, final int iWindowHeight,
		final int iPositionX, final int iPositionY, float fWidth, float fHeight) {
		float[] glCoordinates = new float[2];

		glCoordinates[0] = fWidth / iWindowWidth * iPositionX;
		glCoordinates[1] = fHeight / iWindowHeight * iPositionY;
		return glCoordinates;
	}
}
