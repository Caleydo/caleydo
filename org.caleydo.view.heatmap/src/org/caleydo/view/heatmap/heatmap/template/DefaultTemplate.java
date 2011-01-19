package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.ATemplate;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.RenderableLayoutElement;
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
		verticalLayoutElements.clear();
//		rendererParameters.clear();

		Row row = new Row();
		row.setSizeY(1);
		// heat map
		RenderableLayoutElement heatMapLayout = new RenderableLayoutElement();
		// heatMapLayout.sizeX = 0.715f;
		heatMapLayout.setSizeX(0.806f);
		heatMapLayout.setSizeY(0.883f);
		heatMapLayout.setRenderer(heatMapRenderer);
//		rendererParameters.add(heatMapLayout);
		
		RenderableLayoutElement contentSelectionLayout = new RenderableLayoutElement();
		contentSelectionLayout.setIsBackground(true);
		contentSelectionLayout.setSizeX(heatMapLayout.getSizeX());
		contentSelectionLayout.setRenderer(contentSelectionRenderer);
//		rendererParameters.add(contentSelectionLayout);

		RenderableLayoutElement storageSelectionLayout = new RenderableLayoutElement();
		storageSelectionLayout.setIsBackground(true);
		// contentSelectionLayout.sizeX = 1;
		storageSelectionLayout.setSizeY(heatMapLayout.getSizeY());
		storageSelectionLayout.setRenderer(storageSelectionRenderer);
//		rendererParameters.add(storageSelectionLayout);
		row.appendElement(contentSelectionLayout);
		row.appendElement(storageSelectionLayout);
		row.appendElement(heatMapLayout);

		ElementLayout spacing = new ElementLayout();
		spacing.setSizeX(0.01f);
		row.appendElement(spacing);

		// content captions
		RenderableLayoutElement contentCaptionLayout = new RenderableLayoutElement();
		contentCaptionLayout.setSizeX(1 - heatMapLayout.getSizeX());
		contentCaptionLayout.setSizeY(heatMapLayout.getSizeY());
		// heatMapLayout.grabY = true;
		contentCaptionLayout.setTransformX(0.7f + ATemplate.SPACING);
		contentCaptionLayout.setRenderer(contentCaptionRenderer);

//		rendererParameters.add(contentCaptionLayout);

		row.appendElement(contentCaptionLayout);

		addRenderElement(row);

		spacing = new ElementLayout();
		spacing.setSizeY(1 - heatMapLayout.getSizeY());
		addRenderElement(spacing);

	}

}
