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
package org.caleydo.view.stratomex.dimensiongroup;

import java.util.ArrayList;
import java.util.HashMap;

public class DimensionGroupManager {

	private final static int MAX_CENTER_DIMENSION_GROUPS = 4;

	private ArrayList<DimensionGroup> dimensionGroups = new ArrayList<DimensionGroup>(20);;

	private HashMap<Integer, DimensionGroupSpacingRenderer> dimensionGroupSpacers = new HashMap<Integer, DimensionGroupSpacingRenderer>();

	private int centerGroupStartIndex = 0;

	private int rightGroupStartIndex = 0;

	public ArrayList<DimensionGroup> getDimensionGroups() {
		return dimensionGroups;
	}

	public HashMap<Integer, DimensionGroupSpacingRenderer> getDimensionGroupSpacers() {
		return dimensionGroupSpacers;
	}

	public int getRightGroupStartIndex() {
		return rightGroupStartIndex;
	}

	public int getCenterGroupStartIndex() {
		return centerGroupStartIndex;
	}

	public void setRightGroupStartIndex(int rightGroupStartIndex) {
		this.rightGroupStartIndex = rightGroupStartIndex;
	}

	public void setCenterGroupStartIndex(int centerGroupStartIndex) {
		this.centerGroupStartIndex = centerGroupStartIndex;
	}

	public void calculateGroupDivision() {
		if (dimensionGroups.size() > MAX_CENTER_DIMENSION_GROUPS) {
			centerGroupStartIndex = (dimensionGroups.size() - MAX_CENTER_DIMENSION_GROUPS) / 2;
			rightGroupStartIndex = centerGroupStartIndex + MAX_CENTER_DIMENSION_GROUPS;
		} else {
			centerGroupStartIndex = 0;
			rightGroupStartIndex = dimensionGroups.size();
		}
	}

	public void moveGroupDimension(DimensionGroup referenceDimGroup,
			DimensionGroupSpacingRenderer spacer) {

		// int movedDimGroupIndex = dimensionGroups.indexOf(movedDimGroup);
		// int refDimGroupIndex = dimensionGroups.indexOf(referenceDimGroup);
		//
		// if (refDimGroupIndex < centerGroupStartIndex) {
		// centerGroupStartIndex++;
		// } else if (refDimGroupIndex > centerGroupStartIndex
		// && refDimGroupIndex < rightGroupStartIndex) {
		//
		// if (movedDimGroupIndex >= rightGroupStartIndex)
		// rightGroupStartIndex++;
		// else if (movedDimGroupIndex < centerGroupStartIndex)
		// centerGroupStartIndex--;
		//
		// } else if (refDimGroupIndex >= rightGroupStartIndex
		// && movedDimGroupIndex < rightGroupStartIndex) {
		// rightGroupStartIndex--;
		// }

		// if (refDimGroupIndex < centerGroupStartIndex || refDimGroupIndex >=
		// rightGroupStartIndex)
		// hightlightOffset *= -1;

		// dimensionGroups.remove(movedDimGroup);
		// dimensionGroups.add(
		// dimensionGroups.indexOf(referenceDimGroup) + hightlightOffset,
		// movedDimGroup);
	}

	public int indexOfDimensionGroup(DimensionGroup dimensionGroup) {
		return dimensionGroups.indexOf(dimensionGroup);
	}
}
