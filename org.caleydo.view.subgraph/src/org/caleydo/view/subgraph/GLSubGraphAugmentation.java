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
package org.caleydo.view.subgraph;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * Renders all Elements on top of {@link GLSubGraph} such as visual links.
 *
 * @author Christian Partl
 *
 */
public class GLSubGraphAugmentation extends GLElement {

	private List<IGLRenderer> renderers = new ArrayList<>();

	public static class ConnectionRenderer implements IGLRenderer {

		protected final Rectangle2D loc1;
		protected final Rectangle2D loc2;

		public ConnectionRenderer(Rectangle2D loc1, Rectangle2D loc2) {
			this.loc1 = loc1;
			this.loc2 = loc2;
		}

		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			g.incZ(0.5f);
			g.color(0, 1, 0, 1)
					.lineWidth(2)
					.drawLine((float) loc1.getCenterX(), (float) loc1.getCenterY(), (float) loc2.getCenterX(),
							(float) loc2.getCenterY());
			g.drawRect((float) loc1.getX(), (float) loc1.getY(), (float) loc1.getWidth(), (float) loc1.getHeight());
			g.drawRect((float) loc2.getX(), (float) loc2.getY(), (float) loc2.getWidth(), (float) loc2.getHeight());
			g.lineWidth(1);
			// g.color(0, 1, 0, 1).fillCircle((float) loc1.getX(), (float) loc1.getY(), 50);
			// g.color(0, 1, 0, 1).fillCircle((float) loc2.getX(), (float) loc2.getY(), 50);
			g.incZ(-0.5f);
		}

	}

	protected List<Rectangle2D> path;

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		if (path != null) {
			for (Rectangle2D rect : path) {
				g.incZ(0.5f);
				g.color(1, 0, 0, 0.5f);
				g.fillRect((float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float) rect.getHeight());
				g.incZ(-0.5f);
			}
		}

		for (IGLRenderer renderer : renderers) {
			renderer.render(g, w, h, this);
		}
	}

	public void addRenderer(IGLRenderer renderer) {
		if (renderer != null) {
			renderers.add(renderer);
			repaint();
		}
	}

	public void clearRenderers() {
		renderers.clear();
		repaint();
	}

	/**
	 * @param path
	 *            setter, see {@link path}
	 */
	public void setPath(List<Rectangle2D> path) {
		this.path = path;
	}

}
