package org.caleydo.core.view.opengl.camera;

import javax.media.opengl.GL2;

/**
 * Defines viewing volume of a OpenGL2 view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ViewFrustum {
	private ECameraProjectionMode projectionMode;

	private float left = 0;
	private float right = 0;
	private float top = 0;
	private float bottom = 0;
	private float near = 0;
	private float far = 0;

	private boolean bConsiderAspectRatio = false;

	/**
	 * Constructor setting a default frustum with {@link ECameraProjectionMode#ORTHOGRAPHIC} and all other
	 * values to 0
	 */
	public ViewFrustum() {
		projectionMode = ECameraProjectionMode.ORTHOGRAPHIC;
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
	public ViewFrustum(ECameraProjectionMode eProjectionMode, float left, float right, float bottom,
		float top, float near, float far) {
		this.projectionMode = eProjectionMode;

		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
		this.near = near;
		this.far = far;
	}

	public ECameraProjectionMode getProjectionMode() {
		return projectionMode;
	}

	public void setProjectionMode(final ECameraProjectionMode eProjectionMode) {
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

		if (getProjectionMode().equals(ECameraProjectionMode.ORTHOGRAPHIC)) {
			gl.glOrtho(left, right, bottom, top, getNear(), getFar());
		}
		else {
			gl.glFrustum(left, right, bottom, top, getNear(), getFar());
		}

		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	@Override
	public String toString() {
		return "[" + left + ", " + bottom + ", " + right + ", " + top + "]";
	}
}
