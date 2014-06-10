/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

/**
 * Forwarding renderer that can be used as base class for wrappers/decorators of {@link ALayoutRenderer}.
 *
 * @author Christian Partl
 *
 */
public class AForwardingRenderer extends ALayoutRenderer {

	/**
	 * Renderer that is forwarded to.
	 */
	protected ALayoutRenderer currentRenderer;

	public AForwardingRenderer() {
	}

	public AForwardingRenderer(ALayoutRenderer renderer) {
		this.currentRenderer = renderer;
	}

	@Override
	public void setDisplayListDirty(boolean isDisplayListDirty) {
		super.setDisplayListDirty(isDisplayListDirty);
		currentRenderer.setDisplayListDirty(isDisplayListDirty);
	}

	@Override
	public void setElementLayout(ElementLayout elementLayout) {
		super.setElementLayout(elementLayout);
		currentRenderer.setElementLayout(elementLayout);
	}

	@Override
	public void destroy(GL2 gl) {
		super.destroy(gl);
		if (currentRenderer != null)
			currentRenderer.destroy(gl);
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);
		currentRenderer.setLimits(x, y);
	}

	@Override
	protected void prepare() {
		currentRenderer.prepare();
		super.setDisplayListDirty(currentRenderer.isDisplayListDirty());
	}

	@Override
	protected void renderContent(GL2 gl) {
		currentRenderer.renderContent(gl);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return currentRenderer.permitsWrappingDisplayLists();
	}

	@Override
	public int getMinHeightPixels() {
		return currentRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		return currentRenderer.getMinWidthPixels();
	}

	/**
	 * @param renderer
	 *            setter, see {@link renderer}
	 */
	public void setRenderer(ALayoutRenderer renderer) {
		this.currentRenderer = renderer;
	}

	/**
	 * @return the renderer, see {@link #currentRenderer}
	 */
	public ALayoutRenderer getRenderer() {
		return currentRenderer;
	}

}
