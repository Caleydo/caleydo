package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.BarPlotRenderer;
import org.caleydo.view.heatmap.uncertainty.GLUncertaintyHeatMap;

/**
 * Render template for the embedded heat map of the uncertainty heat map.
 * Assumes a static spacing at the bottom.
 * 
 * @author Alexander Lex
 * 
 */
public class UncertaintyDetailHeatMapTemplate extends AHeatMapTemplate {
	protected BarPlotRenderer barPlotRenderer;
	
	public UncertaintyDetailHeatMapTemplate(GLHeatMap heatMap, GLUncertaintyHeatMap glUncertaintyHeatMap) {
		super(heatMap);

		barPlotRenderer = new BarPlotRenderer(heatMap, glUncertaintyHeatMap);
		barPlotRenderer.setContentSpacing(contentSpacing);
		
		minSelectedFieldHeight *= 2; 
	}
	
	public float bottomSpacing = 0;

	@Override
	public void setStaticLayouts() {
		pixelGLConverter = heatMap.getPixelGLConverter();
		Column mainColumn = new Column("mainColumn");
		setBaseElementLayout(mainColumn);
		mainColumn.setRatioSizeX(1);
		mainColumn.setRatioSizeY(1);
		mainColumn.setBottomUp(false);
		// rendererParameters.clear();

		Row mainRow = new Row("heatMapRow");
//		mainRow.setDebug(true);
		mainRow.setGrabY(true);
		mainRow.setRatioSizeX(1);

		int barPlotPixelWidth = 60;
		
		barPlotLayout = new ElementLayout("BarPlotLayout");

		barPlotLayout.setRenderer(barPlotRenderer);

		barPlotLayout.setPixelGLConverter(heatMap.getPixelGLConverter());
		barPlotLayout.setPixelSizeX(barPlotPixelWidth);
		// barPlotLayout.addForeGroundRenderer(contentSelectionRenderer);
		// barPlotLayout.addForeGroundRenderer(dimensionSelectionRenderer);

		mainRow.append(barPlotLayout);

		heatMapLayout = new ElementLayout("hmlayout");
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(contentSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(dimensionSelectionRenderer);

		mainRow.append(heatMapLayout);

		ElementLayout spacing = new ElementLayout();
		spacing.setAbsoluteSizeX(0.12f);
		mainRow.append(spacing);

		// content captions

		int contentCaptionPixelWidth = 200;
		ElementLayout contentCaptionLayout = new ElementLayout("contentCaption");
		contentCaptionLayout.setPixelGLConverter(pixelGLConverter);
		// contentCaptionLayout.setRatioSizeX(heatMapSizeX);
		contentCaptionLayout.setPixelSizeX(contentCaptionPixelWidth);
		contentCaptionLayout.setRenderer(contentCaptionRenderer);

		mainRow.append(contentCaptionLayout);

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

		ElementLayout leadSpacingLayout = new ElementLayout();

		leadSpacingLayout.setPixelGLConverter(heatMap.getPixelGLConverter());
		leadSpacingLayout.setPixelSizeX(barPlotPixelWidth);

		dimensionCaptionRow.append(leadSpacingLayout);

		dimensionCaptionRow.append(dimensionCaptionLayout);

		ElementLayout postSpacingLayout = new ElementLayout();
		postSpacingLayout.setPixelGLConverter(pixelGLConverter);
		postSpacingLayout.setPixelSizeX(contentCaptionPixelWidth);

		dimensionCaptionRow.append(postSpacingLayout);

		mainColumn.append(dimensionCaptionRow);
	}

	/**
	 * set the static spacing at the bottom (for the caption). This needs to be
	 * done before it is rendered the first time.
	 * 
	 * @param bottomSpacing
	 */
	public void setBottomSpacing(float bottomSpacing) {
		this.bottomSpacing = bottomSpacing;
	}

}
