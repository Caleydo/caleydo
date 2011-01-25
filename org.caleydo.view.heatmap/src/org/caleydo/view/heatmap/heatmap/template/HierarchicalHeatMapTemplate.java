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
	public void setParameters() {
		Column mainColumn = new Column();
		setBaseElementLayout(mainColumn);
		mainColumn.setSizeX(1);
		mainColumn.setSizeY(1);
		mainColumn.setBottomUp(false);
		// rendererParameters.clear();

		Row mainRow = new Row();
		mainRow.setSizeY(1f);
		mainRow.setSizeX(1);
		heatMapLayout = new ElementLayout();
		heatMapLayout.setSizeX(0.806f);
		heatMapLayout.setSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(contentSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(storageSelectionRenderer);

		mainRow.appendElement(heatMapLayout);

		ElementLayout spacing = new ElementLayout();
		spacing.setSizeX(0.01f);
		mainRow.appendElement(spacing);

		// content captions
		ElementLayout contentCaptionLayout = new ElementLayout();
		contentCaptionLayout.setSizeX(1 - heatMapLayout.getSizeX());
		contentCaptionLayout.setSizeY(heatMapLayout.getSizeY());
		contentCaptionLayout.setRenderer(contentCaptionRenderer);

		mainRow.appendElement(contentCaptionLayout);

		mainColumn.appendElement(mainRow);

		Row storageCaptionRow = new Row();
		storageCaptionRow.setSizeY(bottomSpacing);
		storageCaptionRow.setScaleY(false);

		ElementLayout storageCaptionLayout = new ElementLayout();
		storageCaptionLayout.setSizeY(bottomSpacing);
		storageCaptionLayout.setRenderer(storageCaptionRenderer);
		storageCaptionRow.appendElement(storageCaptionLayout);

		mainColumn.appendElement(storageCaptionRow);

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
