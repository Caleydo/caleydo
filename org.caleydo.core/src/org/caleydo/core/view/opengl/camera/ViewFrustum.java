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
	private CameraProjectionMode eProjectionMode;

	private float left;
	private float right;
	private float top;
	private float bottom;
	private float fNear;
	private float fFar;

	private boolean bConsiderAspectRatio = false;

	/**
	 * Constructor
	 * 
	 * @param eProjectionMode
	 * @param fLeft
	 * @param fRight
	 * @param fBottom
	 * @param fTop
	 * @param fNear
	 * @param fFar
	 */
	public ViewFrustum(CameraProjectionMode eProjectionMode, float fLeft, float fRight, float fBottom, float fTop,
		float fNear, float fFar) {
		this.eProjectionMode = eProjectionMode;

		this.left = fLeft;
		this.right = fRight;
		this.bottom = fBottom;
		this.top = fTop;
		this.fNear = fNear;
		this.fFar = fFar;
	}

	public CameraProjectionMode getProjectionMode() {
		return eProjectionMode;
	}

	public void setProjectionMode(final CameraProjectionMode eProjectionMode) {
		this.eProjectionMode = eProjectionMode;
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
		return fNear;
	}

	public float getFar() {
		return fFar;
	}

	public float getWidth() {
		return right - left;
	}

	public float getHeight() {
		return top - bottom;
	}

	public void setLeft(final float fLeft) {

		this.left = fLeft;
	}

	public void setRight(final float fRight) {

		this.right = fRight;
	}

	public void setTop(final float fTop) {

		this.top = fTop;
	}

	public void setBottom(final float fBottom) {

		this.bottom = fBottom;
	}

	public void setNear(final float fNear) {

		this.fNear = fNear;
	}

	public void setFar(final float fFar) {

		this.fFar = fFar;
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

		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	@Override
	public String toString() {
		return "[" + left + ", " + bottom + ", " + right + ", " + top + "]";
	}
}
