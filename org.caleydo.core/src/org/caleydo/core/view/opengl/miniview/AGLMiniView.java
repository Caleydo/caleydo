package org.caleydo.core.view.opengl.miniview;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.dimension.ADimension;

/**
 * Abstract class for all kinds of mini views.
 * 
 * @author Marc Streit
 */
public abstract class AGLMiniView
	implements IGLMiniView {

	protected float fHeight;

	protected float fWidth;

	protected ArrayList<ADimension> alStorage;

	public void setData(ArrayList<ADimension> alStorages) {

	}

	@Override
	public abstract void render(GL2 gl, float fXOrigin, float fYOrigin, float fZOrigin);

	@Override
	public final float getWidth() {

		return fWidth;
	}

	@Override
	public final float getHeight() {

		return fHeight;
	}

	@Override
	public void setWidth(final float fWidth) {

		this.fWidth = fWidth;
	}

	@Override
	public void setHeight(final float fHeight) {

		this.fHeight = fHeight;
	}
}
