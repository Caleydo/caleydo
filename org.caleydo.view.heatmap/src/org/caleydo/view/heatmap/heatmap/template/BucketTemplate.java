package org.caleydo.view.heatmap.heatmap.template;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Render Template for HeatMap in GLBucketView
 * 
 * @author Alexander Lex
 * 
 */
public class BucketTemplate extends AHeatMapTemplate {

	public BucketTemplate(GLHeatMap heatMap) {
		super(heatMap);
		minSelectedFieldHeight = 0.5f;
		fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR * 1.8f;

	}

	@Override
	public void setParameters() {
		Column mainColumn = new Column();
		setBaseElementLayout(mainColumn);
		mainColumn.setSizeX(1);
		mainColumn.setSizeY(1);
//		mainColumn.setBottomUp(false);

		contentCaptionRenderer.setFontScaling(fontScaling);

		Row hmRow = new Row();
		// hmRow.grabY = true;
		// heat map
		heatMapLayout = new ElementLayout();
		heatMapLayout.setGrabX(true);
		heatMapLayout.setSizeY(1f);
		heatMapLayout.setRenderer(heatMapRenderer);
		heatMapLayout.addForeGroundRenderer(contentSelectionRenderer);
		heatMapLayout.addForeGroundRenderer(storageSelectionRenderer);

		boolean renderCaptions = false;
		if (heatMap.isShowCaptions())
			renderCaptions = true;
		ElementLayout caption = null;
		ElementLayout spacing = null;
		if (renderCaptions) {
			// content cage

			// cage = new ElementLayout();
			// cage.setSizeX(0.1f);
			// cage.setSizeY(1f);
			// cage.setIsBackground(true);

			// cage.setRenderer();
			// rendererParameters.add(cage);

			spacing = new ElementLayout();
			spacing.setSizeX(0.01f);

			// content captions
			caption = new ElementLayout();
			caption.setSizeX(0.09f);
			caption.setSizeY(1f);
			caption.setRenderer(contentCaptionRenderer);
			caption.addBackgroundRenderer(captionCageRenderer);

			// rendererParameters.add(caption);
		}

		hmRow.appendElement(heatMapLayout);

		if (renderCaptions) {
			hmRow.appendElement(spacing);
			hmRow.appendElement(caption);
		}

		mainColumn.appendElement(hmRow);

	}

}
