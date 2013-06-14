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

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.ISpacingStrategy;

public class UniformSpacingCalculator implements ISpacingStrategy {

	/**
	 * @author Samuel Gratzl
	 *
	 */
	static final class UniformSpacingImpl implements ISpacingLayout {
		private final float fieldHeight;

		UniformSpacingImpl(float fieldHeight) {
			this.fieldHeight = fieldHeight;
		}

		@Override
		public float getPosition(int index) {
			return index * fieldHeight;
		}

		@Override
		public float getSize(int index) {
			return fieldHeight;
		}
	}

	@Override
	public ISpacingLayout apply(Perspective perspective, SelectionManager selectionManager, boolean hideHidden,
			float size) {
		int nrRecordElements = perspective.getVirtualArray().size();
		if (hideHidden) {
			nrRecordElements -= selectionManager.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		}

		final float fieldHeight = size / nrRecordElements;

		return new UniformSpacingImpl(fieldHeight);
	}
}
