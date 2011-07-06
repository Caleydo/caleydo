package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Render template for the embedded heat map of the uncertainty heat map.
 * Assumes a static spacing at the bottom.
 * 
 * @author Alexander Lex
 * 
 */
public class UncertaintyDetailHeatMapTemplate extends AHeatMapTemplate {

	public UncertaintyDetailHeatMapTemplate(GLHeatMap heatMap) {
		super(heatMap);
	}

	public float bottomSpacing = 0;

	@Override
	public void setStaticLayouts() {
		Column mainColumn = new Column("mainColumn");		
		setBaseElementLayout(mainColumn);
		mainColumn.setRatioSizeX(1);
		mainColumn.setRatioSizeY(1);
		mainColumn.setBottomUp(false);
		// rendererParameters.clear();
		
		float heatMapSizeX = 0.806f;

		Row mainRow = new Row("heatMapRow");
		mainRow.setGrabY(true);
		mainRow.setRatioSizeX(1);
		
		
		barPlotLayout = new ElementLayout("BarPlotLayout");
		
		barPlotLayout.setRenderer(barPlotRenderer);
		barPlotLayout.setAbsoluteSizeX(0.5f);
		//barPlotLayout.setPixelGLConverter(heatMap.getParentGLCanvas().getPixelGLConverter());
		//barPlotLayout.setPixelSizeX(60);
		//barPlotLayout.addForeGroundRenderer(contentSelectionRenderer);
		//barPlotLayout.addForeGroundRenderer(storageSelectionRenderer);
		mainRow.append(barPlotLayout);
		
		heatMapLayout = new ElementLayout("hmlayout");
		heatMapLayout.setGrabX(true);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(contentSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(storageSelectionRenderer);

		mainRow.append(heatMapLayout);

		ElementLayout spacing = new ElementLayout();
		spacing.setAbsoluteSizeX(0.12f);
		mainRow.append(spacing);

		// content captions
		
		ElementLayout contentCaptionLayout = new ElementLayout("contentCaption");
//		contentCaptionLayout.setRatioSizeX(heatMapSizeX);
		contentCaptionLayout.setRatioSizeY(1);
		contentCaptionLayout.setAbsoluteSizeX(0.6f);
		contentCaptionLayout.setRenderer(contentCaptionRenderer);

		mainRow.append(contentCaptionLayout);

		mainColumn.append(mainRow);
		
		ElementLayout ySpacing = new ElementLayout();
		ySpacing.setAbsoluteSizeY(0.05f);
		mainColumn.append(ySpacing);
		

		Row storageCaptionRow = new Row("storageCaptionRow");
		storageCaptionRow.setAbsoluteSizeY(0.35f);

		ElementLayout storageCaptionLayout = new ElementLayout("storageCaption");
		storageCaptionLayout.setRatioSizeY(1);
		storageCaptionLayout.setGrabX(true);
		storageCaptionLayout.setRenderer(storageCaptionRenderer);
		
		ElementLayout leadSpacingLayout = new ElementLayout();
		leadSpacingLayout.setAbsoluteSizeX(0.9f);
		storageCaptionRow.append(leadSpacingLayout);
		
		storageCaptionRow.append(storageCaptionLayout);
		
		ElementLayout postSpacingLayout = new ElementLayout();
		postSpacingLayout.setAbsoluteSizeX(0.65f);
		
		storageCaptionRow.append(postSpacingLayout);

		mainColumn.append(storageCaptionRow);
		
		

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
