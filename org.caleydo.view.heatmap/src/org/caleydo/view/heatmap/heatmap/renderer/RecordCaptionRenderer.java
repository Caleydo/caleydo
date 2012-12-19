/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class RecordCaptionRenderer extends AHeatMapRenderer {

	float fontScaling = HeatMapRenderStyle.SMALL_FONT_SCALING_FACTOR;
	int fontSize = 24;
	float spacing = 0;

	public RecordCaptionRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void renderContent(GL2 gl) {

		float yPosition = y;
		float fieldHeight = 0;

		RecordVirtualArray recordVA = heatMap.getTablePerspective().getRecordPerspective()
				.getVirtualArray();

		for (Integer recordID : recordVA) {

			if (heatMap.isHideElements()
					&& heatMap.getRecordSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, recordID)) {
				continue;
			}

			fieldHeight = recordSpacing.getFieldHeight(recordID);

			if (fieldHeight < HeatMapRenderStyle.MIN_FIELD_HEIGHT_FOR_CAPTION)
				continue;

			try {
				yPosition = recordSpacing.getYDistances().get(recordVA.indexOf(recordID));
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
			label = "No mapping";

		spacing = (recordSpacing.getFieldHeight(recordIndex));

		if (spacing < 0)
			spacing = 0;

		// //FIXME just for karl big captions
		if (spacing > 0.1f)
			spacing = 0.1f;

		heatMap.getTextRenderer().renderTextInBounds(gl, label, xOrigin, yOrigin, 0, x,
				spacing);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
