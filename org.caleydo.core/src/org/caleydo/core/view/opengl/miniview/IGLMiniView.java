package org.caleydo.core.view.opengl.miniview;

import javax.media.opengl.GL2;

/**
 * Interface for all kinds of mini views.
 * 
 * @author Marc Streit
 */
public interface IGLMiniView {

	public abstract void render(GL2 gl, float fXOrigin, float fYOrigin, float fZOrigin);

	public float getWidth();

	public float getHeight();

	public void setWidth(final float fWidth);

	public void setHeight(final float fHeight);
}
