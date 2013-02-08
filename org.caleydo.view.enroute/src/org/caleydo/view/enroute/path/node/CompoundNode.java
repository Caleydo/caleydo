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
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.mode.ALinearizeableNodeMode;

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
	public CompoundNode(APathwayPathRenderer pathwayPathRenderer, AGLView view, int nodeId, ALinearizeableNodeMode mode) {
		super(pathwayPathRenderer, view, nodeId, mode);
	}

	@Override
	public String getLabel() {
		return getPrimaryPathwayVertexRep().getName();
	}

	@Override
	public String getProviderName() {
		return "Compound Node";
	}

}
