package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.ATemplate;
import org.caleydo.core.view.opengl.layout.RenderParameters;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * 
 * @author Template rendering a standalone heat map
 * 
 */
public class DefaultTemplate extends AHeatMapTemplate {

	public DefaultTemplate(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void setParameters() {

		rendererParameters.clear();

		Row row = new Row();
		row.setSizeY(1);
		// heat map
		RenderParameters heatMapLayout = new RenderParameters();
		// heatMapLayout.sizeX = 0.715f;
		heatMapLayout.setSizeX(0.806f);
		heatMapLayout.setSizeY(0.883f);
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
		contentCaptionLayout.setTransformX(0.7f + ATemplate.SPACING);
		contentCaptionLayout.setRenderer(contentCaptionRenderer);

		rendererParameters.add(contentCaptionLayout);

		row.appendElement(contentCaptionLayout);

		addRenderElement(row);

		spacing = new RenderParameters();
		spacing.setSizeY(1 - heatMapLayout.getSizeY());
		addRenderElement(spacing);

	}

}
