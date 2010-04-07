package org.caleydo.core.view.opengl.camera;

import javax.media.opengl.GL;

/**
 * Defines viewing volume of a OpenGL view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ViewFrustum
	implements IViewFrustum {
	private EProjectionMode eProjectionMode;

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
	public ViewFrustum(EProjectionMode eProjectionMode, float fLeft, float fRight, float fBottom, float fTop,
		float fNear, float fFar) {
		this.eProjectionMode = eProjectionMode;

		this.left = fLeft;
		this.right = fRight;
		this.bottom = fBottom;
		this.top = fTop;
		this.fNear = fNear;
		this.fFar = fFar;
	}

	@Override
	public EProjectionMode getProjectionMode() {
		return eProjectionMode;
	}

	@Override
	public void setProjectionMode(final EProjectionMode eProjectionMode) {
		this.eProjectionMode = eProjectionMode;
	}

	@Override
	public float getLeft() {
		return left;
	}

	@Override
	public float getRight() {
		return right;
	}

	@Override
	public float getTop() {
		return top;
	}

	@Override
	public float getBottom() {
		return bottom;
	}

	@Override
	public float getNear() {
		return fNear;
	}

	@Override
	public float getFar() {
		return fFar;
	}

	@Override
	public float getWidth() {
		return right - left;
	}

	@Override
	public float getHeight() {
		return top - bottom;
	}

	@Override
	public void setLeft(final float fLeft) {

		this.left = fLeft;
	}

	@Override
	public void setRight(final float fRight) {

		this.right = fRight;
	}

	@Override
	public void setTop(final float fTop) {

		this.top = fTop;
	}

	@Override
	public void setBottom(final float fBottom) {

		this.bottom = fBottom;
	}

	@Override
	public void setNear(final float fNear) {

		this.fNear = fNear;
	}

	@Override
	public void setFar(final float fFar) {

		this.fFar = fFar;
	}

	@Override
	public void considerAspectRatio(boolean bConsiderAspectRatio) {
		this.bConsiderAspectRatio = bConsiderAspectRatio;
	}

	@Override
	public void setProjectionMatrix(GL gl, float fAspectRatio) {

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

		if (getProjectionMode().equals(EProjectionMode.ORTHOGRAPHIC)) {
			gl.glOrtho(left, right, bottom, top, getNear(), getFar());
		}
		else {
			gl.glFrustum(left, right, bottom, top, getNear(), getFar());
		}

		gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	@Override
	public String toString() {
		return "[" + left + ", " + bottom + ", " + right + ", " + top + "]";
	}
}
