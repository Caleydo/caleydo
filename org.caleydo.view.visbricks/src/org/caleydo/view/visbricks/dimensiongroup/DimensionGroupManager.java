package org.caleydo.view.visbricks.dimensiongroup;

import java.util.ArrayList;

public class DimensionGroupManager {

	private final static int MAX_CENTER_DIMENSION_GROUPS = 4;

	private ArrayList<DimensionGroup> dimensionGroups;

	private int centerGroupStartIndex = 0;

	private int rightGroupStartIndex = 0;

	public DimensionGroupManager() {

		dimensionGroups = new ArrayList<DimensionGroup>(20);
	}

	public ArrayList<DimensionGroup> getDimensionGroups() {
		return dimensionGroups;
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
			DimensionGroup movedDimGroup, boolean dropDimensionGroupAfter) {

		int hightlightOffset = 0;
		if (dropDimensionGroupAfter)
			hightlightOffset = +1;

		int movedDimGroupIndex = dimensionGroups.indexOf(movedDimGroup);
		int refDimGroupIndex = dimensionGroups.indexOf(referenceDimGroup);

		if (refDimGroupIndex < centerGroupStartIndex) {
			centerGroupStartIndex++;
		} else if (refDimGroupIndex > centerGroupStartIndex
				&& refDimGroupIndex < rightGroupStartIndex) {

			if (movedDimGroupIndex >= rightGroupStartIndex)
				rightGroupStartIndex++;
			else if (movedDimGroupIndex < centerGroupStartIndex)
				centerGroupStartIndex--;

		} else if (refDimGroupIndex >= rightGroupStartIndex
				&& movedDimGroupIndex < rightGroupStartIndex) {
			rightGroupStartIndex--;
		}

		// if (refDimGroupIndex < centerGroupStartIndex || refDimGroupIndex >=
		// rightGroupStartIndex)
		// hightlightOffset *= -1;

		dimensionGroups.remove(movedDimGroup);
		dimensionGroups.add(
				dimensionGroups.indexOf(referenceDimGroup) + hightlightOffset,
				movedDimGroup);
	}
}
