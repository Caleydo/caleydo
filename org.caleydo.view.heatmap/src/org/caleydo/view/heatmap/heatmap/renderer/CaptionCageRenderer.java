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

		RecordVirtualArray recordVA = heatMap.getTablePerspective().getRecordPerspective()
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

			gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glVertex3f(xPosition, yPosition, 0);
			// gl.glVertex3f(xPosition , yPosition - fieldHeight, 0);
			// gl.glVertex3f(xPosition + x, yPosition - fieldHeight, 0);
			gl.glVertex3f(xPosition + x, yPosition, 0);
			gl.glEnd();

			yPosition -= fieldHeight;

		}
		// }
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);

		gl.glEnd();

	}

	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}
}
