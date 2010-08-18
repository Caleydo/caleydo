package org.caleydo.view.heatmap.heatmap.renderer;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.ContentSpacing;

public abstract class AContentRenderer extends ARenderer {

	protected ContentSpacing contentSpacing;

	// protected float selectedFieldHeight;
	// protected float normalFieldHeight;
	// protected float fieldWidth;

	protected GLHeatMap heatMap;

	public AContentRenderer(GLHeatMap heatMap) {
		this.heatMap = heatMap;
	}

	public void setContentSpacing(ContentSpacing contentSpacing) {
		this.contentSpacing = contentSpacing;
		// fieldWidth = contentSpacing.getFieldWidth();
		// selectedFieldHeight = contentSpacing.getSelectedFieldHeight();
		// normalFieldHeight = contentSpacing.getNormalFieldHeight();
	}

}
