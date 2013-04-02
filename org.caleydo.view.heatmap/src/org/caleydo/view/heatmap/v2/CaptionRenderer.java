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
package org.caleydo.view.heatmap.v2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.spacing.RecordSpacing;

public class CaptionRenderer implements IGLRenderer {

	private final TablePerspective tablePerspective;
	private final boolean isDimension;
	private final RecordSpacing recordSpacing;

	public CaptionRenderer(TablePerspective tablePerspective, boolean isDimension, RecordSpacing recordSpacing) {
		this.tablePerspective = tablePerspective;
		this.isDimension = isDimension;
		this.recordSpacing = recordSpacing;
	}

	private void renderDimension(GLGraphics g, float w, float h) {
		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

		// float x = 0;
		// float fieldWidth = recordSpacing.getFieldWidth();
		// // offset to center the caption
		// float xOffset = 0;
		//
		// float height = (float) heatMap.getTextRenderer()
		// .getScaledBounds(gl, "TEST", fontScaling, HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE).getHeight();
		//
		// if (fieldWidth > height)
		// xOffset = (fieldWidth - height) / 2;
		//
		// for (Integer dimensionID : dimensionVA) {
		// String label = dataDomain.getDimensionLabel(dimensionID);
		// if (label == null)
		// label = "No mapping";
		//
		// float fRotation = -90;
		// float yOffset = elementLayout.getSizeScaledY(); // =y
		//
		// gl.glTranslatef(x + xOffset, yOffset, 0);
		// gl.glRotatef(fRotation, 0, 0, 1);
		// heatMap.getTextRenderer().renderText(gl, label, 0, 0f, 0, fontScaling,
		// HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE);
		// gl.glRotatef(-fRotation, 0, 0, 1);
		// gl.glTranslatef(-x - xOffset, -yOffset, 0);
		// x += fieldWidth;
		// }
	}

	private void renderRecord(GLGraphics g, float w, float h) {
		VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

		for (int i = 0; i < recordVA.size(); ++i) {
			int recordID = recordVA.get(i);
			// FIXME
			// if (heatMap.isHideElements()
			// && heatMap.getRecordSelectionManager().checkStatus(
			// GLHeatMap.SELECTION_HIDDEN, recordID)) {
			// continue;
			// }

			String label = dataDomain.getRecordLabel(recordID);
			if (label == null)
				label = "No mapping";

			float fieldHeight = recordSpacing.getFieldHeight(recordID);

			// FIXME
			// if (fieldHeight < HeatMapRenderStyle.MIN_FIELD_HEIGHT_FOR_CAPTION)
			// continue;

			float y = recordSpacing.getYDistances().get(i);

			g.drawText(label, 0, y, w, fieldHeight);
		}
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		if (this.isDimension) {
			renderDimension(g, w, h);
		} else
			renderRecord(g, w, h);
	}
}
