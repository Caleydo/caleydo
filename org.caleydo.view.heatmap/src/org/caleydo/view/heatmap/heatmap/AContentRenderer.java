package org.caleydo.view.heatmap.heatmap;

public class AContentRenderer extends ARenderer {

	protected float selectedFieldHeight;
	protected float normalFieldHeight;
	protected float fieldWidth;

	protected GLHeatMap heatMap;

	public AContentRenderer(GLHeatMap heatMap) {
		this.heatMap = heatMap;
	}

	public void setContentSpacing(ContentSpacing contentSpacing) {
		fieldWidth = contentSpacing.getFieldWidth();
		selectedFieldHeight = contentSpacing.getSelectedFieldHeight();
		normalFieldHeight = contentSpacing.getNormalFieldHeight();
	}

}
