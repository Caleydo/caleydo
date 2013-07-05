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
package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

/**
 * Forwarding renderer that can be used as base class for wrappers/decorators of {@link LayoutRenderer}.
 *
 * @author Christian Partl
 *
 */
public class AForwardingRenderer extends LayoutRenderer {

	/**
	 * Renderer that is forwarded to.
	 */
	protected LayoutRenderer currentRenderer;

	public AForwardingRenderer() {
	}

	public AForwardingRenderer(LayoutRenderer renderer) {
		this.currentRenderer = renderer;
	}

	@Override
	public void setDisplayListDirty() {
		super.setDisplayListDirty();
		currentRenderer.setDisplayListDirty();
	}

	@Override
	protected void setElementLayout(ElementLayout elementLayout) {
		super.setElementLayout(elementLayout);
		currentRenderer.setElementLayout(elementLayout);
	}

	@Override
	public void destroy(GL2 gl) {
		super.destroy(gl);
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
		if (currentRenderer.isDisplayListDirty())
			super.setDisplayListDirty();
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
	public void setRenderer(LayoutRenderer renderer) {
		this.currentRenderer = renderer;
	}

	/**
	 * @return the renderer, see {@link #currentRenderer}
	 */
	public LayoutRenderer getRenderer() {
		return currentRenderer;
	}

}
