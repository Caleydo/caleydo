package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
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
		Column mainColumn = new Column();
		setBaseElementLayout(mainColumn);
		mainColumn.setSizeX(1);
		mainColumn.setSizeY(1);

		Row heatMapRow = new Row();
		heatMapRow.setSizeY(1);
		// heat map
		heatMapLayout = new ElementLayout();
		// heatMapLayout.sizeX = 0.715f;
		heatMapLayout.setSizeX(0.806f);
		heatMapLayout.setSizeY(0.883f);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(contentSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(storageSelectionRenderer);
//		rendererParameters.add(heatMapLayout);
		
		
		heatMapRow.appendElement(heatMapLayout);

		ElementLayout spacing = new ElementLayout();
		spacing.setSizeX(0.01f);
		heatMapRow.appendElement(spacing);

		// content captions
		ElementLayout contentCaptionLayout = new ElementLayout();
		contentCaptionLayout.setSizeX(1 - heatMapLayout.getSizeX());
		contentCaptionLayout.setSizeY(heatMapLayout.getSizeY());
		contentCaptionLayout.setRenderer(contentCaptionRenderer);

//		rendererParameters.add(contentCaptionLayout);

		heatMapRow.appendElement(contentCaptionLayout);

		mainColumn.appendElement(heatMapRow);

//		spacing = new ElementLayout();
//		spacing.setSizeY(1 - heatMapLayout.getSizeY());
//		addRenderElement(spacing);

	}

}
