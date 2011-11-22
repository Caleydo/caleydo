package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Render template for the embedded heat map of the hierarchical heat map.
 * Assumes a static spacing at the bottom.
 * 
 * @author Alexander Lex
 * 
 */
public class HierarchicalHeatMapTemplate extends AHeatMapTemplate {

	public HierarchicalHeatMapTemplate(GLHeatMap heatMap) {
		super(heatMap);
	}

	public float bottomSpacing = 0;

	@Override
	public void setStaticLayouts() {
		Column mainColumn = new Column("mainColumn");
		baseElementLayout = mainColumn;
		mainColumn.setRatioSizeX(1);
		mainColumn.setRatioSizeY(1);
		mainColumn.setBottomUp(false);
		// rendererParameters.clear();

		// float heatMapSizeX = 0.806f;

		Row mainRow = new Row("heatMapRow");
		mainRow.setGrabY(true);
		mainRow.setRatioSizeX(1);

		heatMapLayout = new ElementLayout("hmlayout");
		heatMapLayout.setGrabX(true);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(recordSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(dimensionSelectionRenderer);

		mainRow.append(heatMapLayout);

		ElementLayout spacing = new ElementLayout();
		spacing.setAbsoluteSizeX(0.12f);
		mainRow.append(spacing);

		// record captions
		ElementLayout recordCaptionLayout = new ElementLayout("recordCaption");
		recordCaptionLayout.setRatioSizeY(1);
		recordCaptionLayout.setAbsoluteSizeX(0.6f);
		recordCaptionLayout.setRenderer(recordCaptionRenderer);
		mainRow.append(recordCaptionLayout);

		mainColumn.append(mainRow);

		ElementLayout ySpacing = new ElementLayout();
		ySpacing.setAbsoluteSizeY(0.05f);
		mainColumn.append(ySpacing);

		Row dimensionCaptionRow = new Row("dimensionCaptionRow");
		dimensionCaptionRow.setAbsoluteSizeY(0.35f);

		ElementLayout dimensionCaptionLayout = new ElementLayout("dimensionCaption");
		dimensionCaptionLayout.setRatioSizeY(1);
		dimensionCaptionLayout.setGrabX(true);
		dimensionCaptionLayout.setRenderer(dimensionCaptionRenderer);
		dimensionCaptionRow.append(dimensionCaptionLayout);

		ElementLayout spacingLayout = new ElementLayout();
		spacingLayout.setAbsoluteSizeX(0.65f);

		dimensionCaptionRow.append(spacingLayout);

		mainColumn.append(dimensionCaptionRow);

	}

	/**
	 * set the static spacing at the bottom (for the caption). This needs to be
	 * done before it is renedered the first time.
	 * 
	 * @param bottomSpacing
	 */
	public void setBottomSpacing(float bottomSpacing) {
		this.bottomSpacing = bottomSpacing;
	}

}
