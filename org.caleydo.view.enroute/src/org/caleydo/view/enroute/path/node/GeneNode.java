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

import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.enroute.path.PathwayPathRenderer;
import org.caleydo.view.enroute.path.node.mode.ALinearizeableNodeMode;
import org.caleydo.view.enroute.path.node.mode.GeneNodeLinearizedMode;
import org.caleydo.view.enroute.path.node.mode.GeneNodePreviewMode;

/**
 * Renderer for a node that belongs to a gene.
 *
 * @author Christian Partl
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
	public GeneNode(PathwayPathRenderer pathwayPathRenderer, CaleydoTextRenderer textRenderer, AGLView view, int nodeId) {
		super(pathwayPathRenderer, view, nodeId);
		this.textRenderer = textRenderer;
	}

	@Override
	protected ALinearizeableNodeMode getLinearizedMode() {
		return new GeneNodeLinearizedMode(view, pathwayPathRenderer);
	}

	@Override
	protected ALinearizeableNodeMode getPreviewMode() {
		return new GeneNodePreviewMode(view, pathwayPathRenderer);
	}

	@Override
	public String getProviderName() {
		return "Gene Node";
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

}
