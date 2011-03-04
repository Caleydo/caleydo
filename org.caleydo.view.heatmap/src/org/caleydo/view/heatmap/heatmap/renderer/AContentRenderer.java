package org.caleydo.view.heatmap.heatmap.renderer;

import org.caleydo.core.view.opengl.layout.Renderer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.ContentSpacing;

/**
 * Base class for all elements that render heat map content elements (e.g. the
 * heat map row itself, the content caption etc.)
 * 
 * @author Alexander Lex
 * 
 */
public abstract class AContentRenderer extends Renderer {

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
		// selectedFieldHeight = scontentSpacing.getSelectedFieldHeight();
		// normalFieldHeight = contentSpacing.getNormalFieldHeight();
	}

}
