package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class ContentCaptionRenderer extends AContentRenderer {

	float fontScaling = HeatMapRenderStyle.SMALL_FONT_SCALING_FACTOR;
	int fontSize = 24;
	float spacing = 0;

	public ContentCaptionRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void render(GL2 gl) {

		float yPosition = y;
		float fieldHeight = 0;

		ContentVirtualArray contentVA = heatMap.getContentVA();

		for (Integer contentID : contentVA) {

			if (heatMap.isHideElements()
					&& heatMap.getContentSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, contentID)) {
				continue;
			}

			fieldHeight = contentSpacing.getFieldHeight(contentID);
			if (fieldHeight < HeatMapRenderStyle.MIN_FIELD_HEIGHT_FOR_CAPTION)
				continue;

			yPosition = contentSpacing.getYDistances().get(contentVA.indexOf(contentID));

			heatMap.getTextRenderer().setColor(0, 0, 0, 1);

			renderCaption(gl, contentID, 0, yPosition, 0);

		}
	}


	private String getID(Integer contentID, boolean beVerbose) {
		return heatMap.getDataDomain().getContentLabel(contentID);
	}

	private void renderCaption(GL2 gl, int contentIndex, float xOrigin, float yOrigin,
			float zOrigin) {

		String sLabel = getID(contentIndex, false);
		if (sLabel == null)
			sLabel = "Unknown";

		spacing = (contentSpacing.getFieldHeight(contentIndex));
		
		if (spacing < 0)
			spacing = 0;
	
		heatMap.getTextRenderer().renderTextInBounds(gl, sLabel, xOrigin, yOrigin, 0, x, spacing);
	}

}
