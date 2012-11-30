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
 * Forwarding renderer that can be used as base class for wrappers/decorators of {@link IRenderer}.
 *
 * @author Christian Partl
 *
 */
public abstract class AForwardingRenderer implements IRenderer {

	/**
	 * Renderer that is forwarded to.
	 */
	protected IRenderer renderer;

	public AForwardingRenderer() {
	}

	public AForwardingRenderer(IRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void setLimits(float minX, float minY, float maxX, float maxY) {
		renderer.setLimits(minX, minY, maxX, maxY);
	}

	@Override
	public boolean prepare() {
		return renderer.prepare();
	}

	@Override
	public void render(GL2 gl) {
		renderer.render(gl);
	}

	@Override
	public boolean permitsWrappingDisplayLists() {
		return renderer.permitsWrappingDisplayLists();
	}

	@Override
	public int getMinHeightPixels() {
		return renderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		return renderer.getMinWidthPixels();
	}

	/**
	 * @param renderer
	 *            setter, see {@link renderer}
	 */
	public void setRenderer(IRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @return the renderer, see {@link #renderer}
	 */
	public IRenderer getRenderer() {
		return renderer;
	}

}
