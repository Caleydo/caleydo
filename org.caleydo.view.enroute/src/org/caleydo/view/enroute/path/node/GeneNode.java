/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute.path.node;

import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.mode.ALinearizeableNodeMode;

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
	public GeneNode(APathwayPathRenderer pathwayPathRenderer, CaleydoTextRenderer textRenderer, AGLView view,
			ALinearizeableNodeMode mode) {
		super(pathwayPathRenderer, view, mode);
		this.textRenderer = textRenderer;
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
