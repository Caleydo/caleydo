/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.ui;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;

/**
 * Renderer for a line of text for kaplan meier summary.
 * 
 * @author Marc Streit
 * 
 */
public class KaplanMeierSummaryRenderer extends LabelRenderer {

	public KaplanMeierSummaryRenderer(AGLView view, String caption, String pickingType,
			int id) {
		super(view, view, pickingType, id);
	}

	@Override
	public int getMinHeightPixels() {
		return 20;
	}

	@Override
	public int getMinWidthPixels() {
		return 110;
	}

}
