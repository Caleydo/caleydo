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
	implements IViewFrustum
{
	private EProjectionMode eProjectionMode;

	private float fLeft;
	private float fRight;
	private float fTop;
	private float fBottom;
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
	public ViewFrustum(EProjectionMode eProjectionMode, float fLeft, float fRight,
			float fBottom, float fTop, float fNear, float fFar)
	{
		this.eProjectionMode = eProjectionMode;

		this.fLeft = fLeft;
		this.fRight = fRight;
		this.fBottom = fBottom;
		this.fTop = fTop;
		this.fNear = fNear;
		this.fFar = fFar;
	}

	@Override
	public EProjectionMode getProjectionMode()
	{
		return eProjectionMode;
	}

	@Override
	public void setProjectionMode(final EProjectionMode eProjectionMode)
	{
		this.eProjectionMode = eProjectionMode;
	}

	@Override
	public float getLeft()
	{

		return fLeft;
	}

	@Override
	public float getRight()
	{

		return fRight;
	}

	@Override
	public float getTop()
	{

		return fTop;
	}

	@Override
	public float getBottom()
	{
		return fBottom;
	}

	@Override
	public float getNear()
	{

		return fNear;
	}

	@Override
	public float getFar()
	{

		return fFar;
	}

	@Override
	public float getWidth()
	{
		return fRight - fLeft;
	}

	@Override
	public float getHeight()
	{
		return fTop - fBottom;
	}

	@Override
	public void setLeft(final float fLeft)
	{

		this.fLeft = fLeft;
	}

	@Override
	public void setRight(final float fRight)
	{

		this.fRight = fRight;
	}

	@Override
	public void setTop(final float fTop)
	{

		this.fTop = fTop;
	}

	@Override
	public void setBottom(final float fBottom)
	{

		this.fBottom = fBottom;
	}

	@Override
	public void setNear(final float fNear)
	{

		this.fNear = fNear;
	}

	@Override
	public void setFar(final float fFar)
	{

		this.fFar = fFar;
	}

	@Override
	public void considerAspectRatio(boolean bConsiderAspectRatio)
	{
		this.bConsiderAspectRatio = bConsiderAspectRatio;
	}

	@Override
	public void setProjectionMatrix(GL gl)
	{
		setProjectionMatrix(gl, 1);
	}

	@Override
	public void setProjectionMatrix(GL gl, float fAspectRatio)
	{
		// fAspectRatio = (float) height / (float) width;

		float fLeft = getLeft();
		float fRight = getRight();
		float fBottom = getBottom();
		float fTop = getTop();

		if (bConsiderAspectRatio)
		{
			if (fAspectRatio < 1.0)
			{
				fLeft /= fAspectRatio;
				fRight /= fAspectRatio;
			}
			else
			{
				fBottom *= fAspectRatio;
				fTop *= fAspectRatio;
			}
		}

		if (getProjectionMode().equals(EProjectionMode.ORTHOGRAPHIC))
		{
			gl.glOrtho(fLeft, fRight, fBottom, fTop, getNear(), getFar());
		}
		else
		{
			gl.glFrustum(fLeft, fRight, fBottom, fTop, getNear(), getFar());
		}

		gl.glMatrixMode(GL.GL_MODELVIEW);
	}
}
