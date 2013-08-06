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

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Renders the node info of a {@link PathwayVertexRep}.
 *
 * @author Christian Partl
 *
 */
public class GLNodeInfo extends GLElementContainer {

	protected PathwayVertexRep vertexRep;

	{
		setRenderer(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.color(1f, 0f, 0f, 1f).fillRoundedRect(0, 0, w, h, 10);
				g.drawText(vertexRep.getName(), 0, 0, w, 16);
			}
		});
	}

	/**
	 *
	 */
	public GLNodeInfo() {
	}

	public GLNodeInfo(PathwayVertexRep vertexRep) {
		this.vertexRep = vertexRep;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		super.renderImpl(g, w, h);
	}

	/**
	 * @return the vertexRep, see {@link #vertexRep}
	 */
	public PathwayVertexRep getVertexRep() {
		return vertexRep;
	}

	/**
	 * @param vertexRep
	 *            setter, see {@link vertexRep}
	 */
	public void setVertexRep(PathwayVertexRep vertexRep) {
		this.vertexRep = vertexRep;
	}

}
