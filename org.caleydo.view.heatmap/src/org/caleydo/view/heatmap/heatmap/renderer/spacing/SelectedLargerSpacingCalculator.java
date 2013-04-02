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
package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class SelectedLargerSpacingCalculator extends ASpacingCalculator {

	private float selectedFieldHeight;
	private float normalFieldHeight;

	public SelectedLargerSpacingCalculator(GLHeatMap heatMap, float height, int numElements) {
		super(heatMap, height, numElements);
	}

	@Override
	public void calculateFieldHeights() {
		selectedFieldHeight = heatMap.getPixelGLConverter().getGLHeightForPixelHeight(
				minSelectedFieldHeight);
		int nrZoomedElements = heatMap.getZoomedElements().size();
		normalFieldHeight = (height - (nrZoomedElements * selectedFieldHeight))
				/ (numElements - nrZoomedElements);
	}

	@Override
	public float getFieldHeight(int recordID) {

		if (heatMap.getRecordSelectionManager().checkStatus(SelectionType.SELECTION,
				recordID)
				|| heatMap.getRecordSelectionManager().checkStatus(
						SelectionType.MOUSE_OVER, recordID)) {
			return selectedFieldHeight;
		}
		return normalFieldHeight;
	}

}
