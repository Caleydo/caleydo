/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer;

import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.RecordSpacing;

/**
 * Base class for all elements that render heat map content elements (e.g. the
 * heat map row itself, the content caption etc.)
 * 
 * @author Alexander Lex
 * 
 */
public abstract class AHeatMapRenderer extends ALayoutRenderer {

	protected RecordSpacing recordSpacing;

	protected GLHeatMap heatMap;

	public AHeatMapRenderer(GLHeatMap heatMap) {
		this.heatMap = heatMap;
	}

	public void setRecordSpacing(RecordSpacing contentSpacing) {
		this.recordSpacing = contentSpacing;
	}

	@Override
	public void updateSpacing() {
	}
}
