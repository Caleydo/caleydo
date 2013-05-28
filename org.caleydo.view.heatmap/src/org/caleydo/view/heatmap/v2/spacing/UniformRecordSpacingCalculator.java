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
package org.caleydo.view.heatmap.v2.spacing;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class UniformRecordSpacingCalculator implements IRecordSpacingStrategy {

	@Override
	public IRecordSpacingLayout apply(TablePerspective tablePerspective, SelectionManager recordManager,
			boolean hideHidden, float width, float height, int minSelectedFieldHeight) {
		int nrRecordElements = tablePerspective.getRecordPerspective().getVirtualArray().size();
		SelectionManager selectionManager = recordManager;
		if (hideHidden) {
			nrRecordElements -= selectionManager.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		}
		int nrDimensionElements = tablePerspective.getDimensionPerspective().getVirtualArray().size();

		final float fieldWidth = width / nrDimensionElements;
		final float fieldHeight = height / nrRecordElements;

		// FIXME
		// if (height / nrRecordElements > glSelectedFieldHeight || heatMap.getZoomedElements().size() == 0) {
		//
		// spacingCalculator = new NormalSpacingCalculator(heatMap, height, nrRecordElements);
		// useFishEye = false;
		//
		// } else {
		// useFishEye = true;
		// // spacingCalculator = new SelectedLargerSpacingCalculator(heatMap,
		// // y,
		// // contentElements);
		// spacingCalculator = new FishEyeSpacingCalculator(heatMap, height, nrRecordElements, minSelectedFieldHeight);
		//
		// }

		return new IRecordSpacingLayout() {

			@Override
			public float getYPosition(int lineIndex) {
				return fieldHeight * lineIndex;
			}

			@Override
			public float getFieldWidth() {
				return fieldWidth;
			}

			@Override
			public float getFieldHeight(int recordIndex) {
				return fieldHeight;
			}
		};
	}
}
