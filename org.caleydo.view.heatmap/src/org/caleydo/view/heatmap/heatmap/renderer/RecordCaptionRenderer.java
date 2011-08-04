package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class RecordCaptionRenderer extends AContentRenderer {

	float fontScaling = HeatMapRenderStyle.SMALL_FONT_SCALING_FACTOR;
	int fontSize = 24;
	float spacing = 0;

	public RecordCaptionRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void render(GL2 gl) {

		float yPosition = y;
		float fieldHeight = 0;

		RecordVirtualArray recordVA = heatMap.getRecordVA();

		for (Integer recordID : recordVA) {

			if (heatMap.isHideElements()
					&& heatMap.getContentSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, recordID)) {
				continue;
			}

			fieldHeight = contentSpacing.getFieldHeight(recordID);
			
			if (fieldHeight < HeatMapRenderStyle.MIN_FIELD_HEIGHT_FOR_CAPTION)
				continue;
			
			try {
				yPosition = contentSpacing.getYDistances().get(
						recordVA.indexOf(recordID));
			} catch (Exception e) {
				// TODO: handle exception
			}
			heatMap.getTextRenderer().setColor(0, 0, 0, 1);

			renderCaption(gl, recordID, 0, yPosition, 0);
		}
	}

	private String getID(Integer recordID, boolean beVerbose) {
		return heatMap.getDataDomain().getRecordLabel(recordID);
	}

	private void renderCaption(GL2 gl, int recordIndex, float xOrigin, float yOrigin,
			float zOrigin) {

		String label = getID(recordIndex, false);
		if (label == null)
			label = "Unknown";

		spacing = (contentSpacing.getFieldHeight(recordIndex));

		if (spacing < 0)
			spacing = 0;
		//FIXME just for karl big captions
		if (spacing >0.1f)
			spacing = 0.1f;

		heatMap.getTextRenderer().renderTextInBounds(gl, label, xOrigin, yOrigin, 0, x,
				spacing);
	}

}
