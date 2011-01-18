package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.LayoutParameters;
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

		rendererParameters.clear();

		Row row = new Row();
		row.setSizeY(1);
		// heat map
		heatMapLayout = new LayoutParameters();
		// heatMapLayout.sizeX = 0.715f;
		heatMapLayout.setSizeX(0.806f);
		heatMapLayout.setSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		rendererParameters.add(heatMapLayout);
		

		LayoutParameters contentSelectionLayout = new LayoutParameters();
		contentSelectionLayout.setIsBackground(true);
		contentSelectionLayout.setSizeX(heatMapLayout.getSizeX());
		contentSelectionLayout.setRenderer(contentSelectionRenderer);
		rendererParameters.add(contentSelectionLayout);

		LayoutParameters storageSelectionLayout = new LayoutParameters();
		storageSelectionLayout.setIsBackground(true);
		// contentSelectionLayout.sizeX = 1;
		storageSelectionLayout.setSizeY(heatMapLayout.getSizeY());
		storageSelectionLayout.setRenderer(storageSelectionRenderer);
		rendererParameters.add(storageSelectionLayout);
		row.appendElement(contentSelectionLayout);
		row.appendElement(storageSelectionLayout);
		row.appendElement(heatMapLayout);

		LayoutParameters spacing = new LayoutParameters();
		spacing.setSizeX(0.01f);
		row.appendElement(spacing);

		// content captions
		LayoutParameters contentCaptionLayout = new LayoutParameters();
		contentCaptionLayout.setSizeX(1 - heatMapLayout.getSizeX());
		contentCaptionLayout.setSizeY(heatMapLayout.getSizeY());
		// heatMapLayout.grabY = true;
		contentCaptionLayout.setTransformX(0.7f + SPACING);
		contentCaptionLayout.setRenderer(contentCaptionRenderer);

		rendererParameters.add(contentCaptionLayout);

		row.appendElement(contentCaptionLayout);

		addRenderElement(row);

		Row storageCaptionRow = new Row();
		storageCaptionRow.setSizeY(bottomSpacing);
		storageCaptionRow.setScaleY(false);

		LayoutParameters storageCaptionLayout = new LayoutParameters();
		storageCaptionLayout.setSizeY(bottomSpacing);
		storageCaptionLayout.setRenderer(storageCaptionRenderer);
		storageCaptionRow.appendElement(storageCaptionLayout);

		rendererParameters.add(storageCaptionLayout);

		addRenderElement(storageCaptionRow);

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
