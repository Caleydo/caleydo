package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.layout.ATemplate;
import org.caleydo.view.heatmap.heatmap.layout.RenderParameters;
import org.caleydo.view.heatmap.heatmap.layout.Row;

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
		heatMapLayout = new RenderParameters();
		// heatMapLayout.sizeX = 0.715f;
		heatMapLayout.setSizeX(0.806f);
		heatMapLayout.setSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		rendererParameters.add(heatMapLayout);
		

		RenderParameters contentSelectionLayout = new RenderParameters();
		contentSelectionLayout.setIsBackground(true);
		contentSelectionLayout.setSizeX(heatMapLayout.getSizeX());
		contentSelectionLayout.setRenderer(contentSelectionRenderer);
		rendererParameters.add(contentSelectionLayout);

		RenderParameters storageSelectionLayout = new RenderParameters();
		storageSelectionLayout.setIsBackground(true);
		// contentSelectionLayout.sizeX = 1;
		storageSelectionLayout.setSizeY(heatMapLayout.getSizeY());
		storageSelectionLayout.setRenderer(storageSelectionRenderer);
		rendererParameters.add(storageSelectionLayout);
		row.appendElement(contentSelectionLayout);
		row.appendElement(storageSelectionLayout);
		row.appendElement(heatMapLayout);

		RenderParameters spacing = new RenderParameters();
		spacing.setSizeX(0.01f);
		row.appendElement(spacing);

		// content captions
		RenderParameters contentCaptionLayout = new RenderParameters();
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

		RenderParameters storageCaptionLayout = new RenderParameters();
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
