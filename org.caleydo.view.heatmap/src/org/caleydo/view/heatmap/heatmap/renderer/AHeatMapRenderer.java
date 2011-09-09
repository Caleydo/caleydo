package org.caleydo.view.heatmap.heatmap.renderer;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.RecordSpacing;

/**
 * Base class for all elements that render heat map content elements (e.g. the
 * heat map row itself, the content caption etc.)
 * 
 * @author Alexander Lex
 * 
 */
public abstract class AHeatMapRenderer extends LayoutRenderer {

	protected RecordSpacing recordSpacing;

	protected GLHeatMap heatMap;

	public AHeatMapRenderer(GLHeatMap heatMap) {
		this.heatMap = heatMap;
	}

	public void setRecordSpacing(RecordSpacing contentSpacing) {
		this.recordSpacing = contentSpacing;
	}
}
