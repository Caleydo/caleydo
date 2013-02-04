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
/**
 *
 */
package org.caleydo.view.enroute.path.node;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.enroute.path.PathwayPathRenderer;
import org.caleydo.view.enroute.path.node.mode.ALinearizeableNodeMode;
import org.caleydo.view.enroute.path.node.mode.CompoundNodeLinearizedMode;
import org.caleydo.view.enroute.path.node.mode.CompoundNodePreviewMode;

/**
 * Node that represents a compound in a pathway.
 *
 * @author Christian Partl
 *
 */
public class CompoundNode extends ALinearizableNode {

	protected final static float[] DEFAULT_CIRCLE_COLOR = new float[] { 1, 1, 1, 0 };

	/**
	 * @param pixelGLConverter
	 */
	public CompoundNode(PathwayPathRenderer pathwayPathRenderer, AGLView view, int nodeId) {
		super(pathwayPathRenderer, view, nodeId);
	}

	@Override
	protected ALinearizeableNodeMode getLinearizedMode() {
		return new CompoundNodeLinearizedMode(view, pathwayPathRenderer);
	}

	@Override
	protected ALinearizeableNodeMode getPreviewMode() {
		return new CompoundNodePreviewMode(view, pathwayPathRenderer);
	}

	@Override
	public String getLabel() {
		return pathwayVertexRep.getName();
	}

	@Override
	public String getProviderName() {
		return "Compound Node";
	}

}
