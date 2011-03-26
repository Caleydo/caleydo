package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.renderer.DetailToolBar;

/**
 * Render LayoutTemplate for HeatMap in GLBucketView
 * 
 * @author Alexander Lex
 * 
 */
public class BrickHeatMapTemplate extends AHeatMapTemplate {

	public BrickHeatMapTemplate(GLHeatMap heatMap) {
		super(heatMap);
		minSelectedFieldHeight = 0.1f;
		fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR * 1.8f;

	}

	@Override
	public void setStaticLayouts() {
		Column mainColumn = new Column();
		setBaseElementLayout(mainColumn);
		mainColumn.setRatioSizeX(1);
		mainColumn.setRatioSizeY(1);

		minSelectedFieldHeight = HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;

		Row hmRow = new Row();
		// hmRow.grabY = true;
		// heat map
		heatMapLayout = new ElementLayout();
		heatMapLayout.setGrabX(true);
		heatMapLayout.setRatioSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(contentSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(storageSelectionRenderer);

		boolean renderCaptions = false;
		if (heatMap.isShowCaptions() || heatMap.isActive())
			renderCaptions = true;
		ElementLayout caption = null;
		ElementLayout spacing = null;
		if (renderCaptions) {
			// content cage

			spacing = new ElementLayout();
			spacing.setAbsoluteSizeX(0.01f);

			// content captions
			caption = new ElementLayout();
			caption.setRatioSizeX(0.29f);
			caption.setRatioSizeY(1);

			caption.setRenderer(contentCaptionRenderer);
//			caption.addBackgroundRenderer(captionCageRenderer);

			// rendererParameters.add(caption);
		}

//		if (isLeft) {
			if (renderCaptions) {

				hmRow.append(spacing);
				hmRow.append(caption);
			}

			hmRow.append(heatMapLayout);

//		} else {
//
//			hmRow.append(heatMapLayout);
//
//			if (renderCaptions) {
//
//				hmRow.append(spacing);
//				hmRow.append(caption);
//			}
//		}

		mainColumn.append(hmRow);

		
//		Column mainColumn = new Column();
//		setBaseElementLayout(mainColumn);
//		mainColumn.setRatioSizeX(1);
//		mainColumn.setRatioSizeY(1);
//
//		contentCaptionRenderer.setFontScaling(fontScaling);
//
//		Row hmRow = new Row();
//		
//		heatMapLayout = new ElementLayout();
//		heatMapLayout.setGrabX(true);
//		heatMapLayout.setRatioSizeY(1f);
//		heatMapLayout.setRenderer(heatMapRenderer);
//		heatMapLayout.addForeGroundRenderer(contentSelectionRenderer);
//		heatMapLayout.addForeGroundRenderer(storageSelectionRenderer);
//
//		boolean renderCaptions = false;
//		if (heatMap.isShowCaptions())
//			renderCaptions = true;
//		ElementLayout caption = null;
//		ElementLayout spacing = null;
//		if (renderCaptions) {
//
//			spacing = new ElementLayout();
//			spacing.setAbsoluteSizeX(0.01f);
//
//			// content captions
//			caption = new ElementLayout();
//			caption.setAbsoluteSizeX(1f);
//			caption.setRatioSizeY(1f);
//			caption.setRenderer(contentCaptionRenderer);
//			caption.addBackgroundRenderer(captionCageRenderer);
//
//			// rendererParameters.add(caption);
//		}
//
//		hmRow.append(heatMapLayout);
//
//		if (renderCaptions) {
//			hmRow.append(spacing);
//			hmRow.append(caption);
//		}
//
//		mainColumn.append(hmRow);

	}

}
