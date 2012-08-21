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

import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.node.mode.ALinearizeableNodeMode;
import org.caleydo.view.enroute.node.mode.GeneNodeLinearizedMode;
import org.caleydo.view.enroute.node.mode.GeneNodePreviewMode;

/**
 * Renderer for a node that belongs to a gene.
 * 
 * @author Christian
 * 
 */
public class GeneNode extends ALinearizableNode implements ILabelHolder {

	public static final int TEXT_SPACING_PIXELS = 3;

	protected CaleydoTextRenderer textRenderer;

	/**
	 * The caption displayed on the node.
	 */
	protected String label = "";

	/**
	 * @param pixelGLConverter
	 */
	public GeneNode(PixelGLConverter pixelGLConverter, CaleydoTextRenderer textRenderer,
			GLEnRoutePathway view, int nodeId) {
		super(pixelGLConverter, view, nodeId);
		this.textRenderer = textRenderer;
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
		return new GeneNodeLinearizedMode(view);
	}

	@Override
	protected ALinearizeableNodeMode getPreviewMode() {
		return new GeneNodePreviewMode(view);
	}

	@Override
	public String getProviderName() {
		return "Gene Node";
	}

	@Override
	public void setLabel(String label, boolean isLabelDefault) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

}
