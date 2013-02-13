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
package org.caleydo.core.view.opengl.camera;

import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

/**
 * Defines viewing volume of a OpenGL2 view.
 *
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ViewFrustum {
	private CameraProjectionMode projectionMode;

	private float left = 0;
	private float right = 0;
	private float top = 0;
	private float bottom = 0;
	private float near = 0;
	private float far = 0;

	private boolean bConsiderAspectRatio = false;

	/**
	 * Constructor setting a default frustum with {@link CameraProjectionMode#ORTHOGRAPHIC} and all other
	 * values to 0
	 */
	public ViewFrustum() {
		projectionMode = CameraProjectionMode.ORTHOGRAPHIC;
	}

	/**
	 * Constructor
	 *
	 * @param projectionMode
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param near
	 * @param far
	 */
	public ViewFrustum(CameraProjectionMode eProjectionMode, float left, float right, float bottom,
		float top, float near, float far) {
		this.projectionMode = eProjectionMode;

		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
		this.near = near;
		this.far = far;
	}

	public CameraProjectionMode getProjectionMode() {
		return projectionMode;
	}

	public void setProjectionMode(final CameraProjectionMode eProjectionMode) {
		this.projectionMode = eProjectionMode;
	}

	public float getLeft() {
		return left;
	}

	public float getRight() {
		return right;
	}

	public float getTop() {
		return top;
	}

	public float getBottom() {
		return bottom;
	}

	public float getNear() {
		return near;
	}

	public float getFar() {
		return far;
	}

	public float getWidth() {
		return right - left;
	}

	public float getHeight() {
		return top - bottom;
	}

	public void setLeft(final float left) {
		this.left = left;
	}

	public void setRight(final float right) {
		this.right = right;
	}

	public void setTop(final float top) {
		this.top = top;
	}

	public void setBottom(final float bottom) {
		this.bottom = bottom;
	}

	public void setNear(final float fNear) {
		this.near = fNear;
	}

	public void setFar(final float fFar) {
		this.far = fFar;
	}

	/**
	 * Define whether to consider aspect ratios when setting the projection matrix. This guarantees
	 * rectangular appearance of views
	 *
	 * @param bConsiderAspectRatio
	 * @deprecated Because fAspectRatio should not be used any more.
	 */
	@Deprecated
	public void considerAspectRatio(boolean bConsiderAspectRatio) {
		this.bConsiderAspectRatio = bConsiderAspectRatio;
	}

	/**
	 * Sets the projection matrix, according to the projection mode defined in the frustum
	 *
	 * @param gl
	 *            the GL2 context
	 * @param fAspectRatio
	 *            the aspect ratio
	 * @deprecated Because fAspectRatio should not be used any more.
	 */
	@Deprecated
	public void setProjectionMatrix(GL2 gl, float fAspectRatio) {

		// The member values must be copied to local values
		// Only the local values are allowed to be written - otherwise we would mess up the frustum with the
		// aspect ration calculation in every frame!
		float left = getLeft();
		float right = getRight();
		float bottom = getBottom();
		float top = getTop();

		if (bConsiderAspectRatio) {
			if (fAspectRatio < 1.0f) {
				left /= fAspectRatio;
				right /= fAspectRatio;
			}
			else {
				bottom *= fAspectRatio;
				top *= fAspectRatio;
			}

			// System.out.println("Aspect ratio:" +fAspectRatio);
			// System.out.println(fLeft + "," +fRight + "," +fTop + "," +fBottom);
		}

		if (getProjectionMode().equals(CameraProjectionMode.ORTHOGRAPHIC)) {
			gl.glOrtho(left, right, bottom, top, getNear(), getFar());
		}
		else {
			gl.glFrustum(left, right, bottom, top, getNear(), getFar());
		}

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	public void setProjectionMatrix(GL2 gl) {
		float left = getLeft();
		float right = getRight();
		float bottom = getBottom();
		float top = getTop();

		switch (this.projectionMode) {
		case ORTHOGRAPHIC:
			gl.glOrtho(left, right, bottom, top, getNear(), getFar());
			break;
		case PERSPECTIVE:
			gl.glFrustum(left, right, bottom, top, getNear(), getFar());
			break;
		}
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	@Override
	public String toString() {
		return "[" + left + ", " + bottom + ", " + right + ", " + top + "]";
	}
}
