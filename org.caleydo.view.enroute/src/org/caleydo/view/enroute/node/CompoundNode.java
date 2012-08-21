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
package org.caleydo.view.enroute.node;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.node.mode.ALinearizeableNodeMode;
import org.caleydo.view.enroute.node.mode.CompoundNodeLinearizedMode;
import org.caleydo.view.enroute.node.mode.CompoundNodePreviewMode;

/**
 * Node that represents a compound in a pathway.
 * 
 * @author Christian
 * 
 */
public class CompoundNode extends ALinearizableNode {

	protected final static float[] DEFAULT_CIRCLE_COLOR = new float[] { 1, 1, 1, 0 };

	/**
	 * The vertex in the graph this compound belongs to.
	 */
	protected PathwayVertexRep pathwayVertexRep;

	/**
	 * @param pixelGLConverter
	 */
	public CompoundNode(PixelGLConverter pixelGLConverter, GLEnRoutePathway view,
			int nodeId) {
		super(pixelGLConverter, view, nodeId);
	}

	/**
	 * @param pathwayVertexRep
	 *            setter, see {@link #pathwayVertexRep}
	 */
	public void setPathwayVertexRep(PathwayVertexRep pathwayVertexRep) {
		this.pathwayVertexRep = pathwayVertexRep;
	}

	@Override
	public PathwayVertexRep getPathwayVertexRep() {
		return pathwayVertexRep;
	}

	@Override
	protected ALinearizeableNodeMode getLinearizedMode() {
		return new CompoundNodeLinearizedMode(view);
	}

	@Override
	protected ALinearizeableNodeMode getPreviewMode() {
		return new CompoundNodePreviewMode(view);
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
