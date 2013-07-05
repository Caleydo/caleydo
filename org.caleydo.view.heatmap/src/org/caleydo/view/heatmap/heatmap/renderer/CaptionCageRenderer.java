/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class CaptionCageRenderer extends AHeatMapRenderer {

	public CaptionCageRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void renderContent(GL2 gl) {

		float yPosition = y;
		float xPosition = 0;
		float fieldHeight = 0;

		gl.glColor3f(0.6f, 0.6f, 0.6f);
		gl.glLineWidth(1);

		// if (!contentSpacing.isUseFishEye()) {

		VirtualArray recordVA = heatMap.getTablePerspective().getRecordPerspective()
				.getVirtualArray();

		for (Integer recordID : recordVA) {
			if (heatMap.isHideElements()
					&& heatMap.getRecordSelectionManager().checkStatus(
							GLHeatMap.SELECTION_HIDDEN, recordID)) {
				continue;
			}
			// else if (heatMap.getContentSelectionManager().checkStatus(
			// SelectionType.SELECTION, recordIndex)
			// || heatMap.getContentSelectionManager().checkStatus(
			// SelectionType.MOUSE_OVER, recordIndex)) {
			// fieldHeight = selectedFieldHeight;
			//
			// } else {
			//
			// fieldHeight = normalFieldHeight;
			// }
			fieldHeight = recordSpacing.getFieldHeight(recordID);

			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex3f(xPosition, yPosition, 0);
			// gl.glVertex3f(xPosition , yPosition - fieldHeight, 0);
			// gl.glVertex3f(xPosition + x, yPosition - fieldHeight, 0);
			gl.glVertex3f(xPosition + x, yPosition, 0);
			gl.glEnd();

			yPosition -= fieldHeight;

		}
		// }
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);

		gl.glEnd();

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
