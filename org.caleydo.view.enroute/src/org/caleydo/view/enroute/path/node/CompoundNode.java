/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
	public CompoundNode(APathwayPathRenderer pathwayPathRenderer, AGLView view, ALinearizeableNodeMode mode) {
		super(pathwayPathRenderer, view, mode);
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
