package org.caleydo.core.view.opengl.camera;

import javax.media.opengl.GL;

/**
 * Interface for the viewing volume data of an OpenGL view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IViewFrustum
{
	public EProjectionMode getProjectionMode();

	public void setProjectionMode(final EProjectionMode eProjectionMode);

	public float getLeft();

	public float getRight();

	public float getTop();

	public float getBottom();

	public float getNear();

	public float getFar();

	public float getWidth();

	public float getHeight();

	public void setLeft(final float fLeft);

	public void setRight(final float fRight);

	public void setTop(final float fTop);

	public void setBottom(final float fBottom);

	public void setNear(final float fNear);

	public void setFar(final float fFar);

	/**
	 * Define whether to consider aspect ratios when setting the projection
	 * matrix. This guarantees rectangular appearance of views
	 * 
	 * @param bConsiderAspectRatio
	 * @deprecated Because fAspectRatio should not be used any more.
	 */
	@Deprecated
	public void considerAspectRatio(boolean bConsiderAspectRatio);

	/**
	 * Sets the projection matrix, according to the projection mode defined in
	 * the frustum
	 * 
	 * @param gl the GL context
	 */
	public void setProjectionMatrix(GL gl);

	/**
	 * Sets the projection matrix, according to the projection mode defined in
	 * the frustum
	 * 
	 * @param gl the GL context
	 * @param fAspectRatio the aspect ratio
	 * @deprecated Because fAspectRatio should not be used any more.
	 */
	@Deprecated
	public void setProjectionMatrix(GL gl, float fAspectRatio);
}
