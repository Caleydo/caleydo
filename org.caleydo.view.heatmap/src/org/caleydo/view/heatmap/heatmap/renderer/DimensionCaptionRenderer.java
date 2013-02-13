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

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Rendering the dimension captions for the GLHeatmap
 *
 * @author Alexander Lex
 *
 */
public class DimensionCaptionRenderer extends AHeatMapRenderer {

	private float fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR / 1.2f;

	public DimensionCaptionRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	@Override
	public void renderContent(GL2 gl) {

		VirtualArray dimensionVA = heatMap.getTablePerspective()
				.getDimensionPerspective().getVirtualArray();
		heatMap.getTextRenderer().setColor(0, 0, 0, 1);

		float xPosition = 0;
		float fieldWidth = recordSpacing.getFieldWidth();
		// offset to center the caption
		float xOffset = 0;

		float height = (float) heatMap
				.getTextRenderer()
				.getScaledBounds(gl, "TEST", fontScaling,
						HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE).getHeight();

		if (fieldWidth > height)
			xOffset = (fieldWidth - height) / 2;
		for (Integer dimensionID : dimensionVA) {
			String label = heatMap.getDataDomain().getDimensionLabel(dimensionID);

			float fRotation = -90;
			float yOffset = elementLayout.getSizeScaledY(); // =y

			gl.glTranslatef(xPosition + xOffset, yOffset, 0);
			gl.glRotatef(fRotation, 0, 0, 1);
			heatMap.getTextRenderer().renderText(gl, label, 0, 0f, 0, fontScaling,
					HeatMapRenderStyle.LABEL_TEXT_MIN_SIZE);
			gl.glRotatef(-fRotation, 0, 0, 1);
			gl.glTranslatef(-xPosition - xOffset, -yOffset, 0);
			xPosition += fieldWidth;

		}
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
