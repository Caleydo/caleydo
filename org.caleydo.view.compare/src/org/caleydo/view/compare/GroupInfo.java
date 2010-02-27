package org.caleydo.view.compare;

import org.caleydo.core.data.selection.Group;

public class GroupInfo {

	private Group group;
	private int groupIndex;
	private int lowerBoundIndex;
	private int upperBoundIndex;

	public GroupInfo(Group group, int groupIndex, int lowerBoundIndex) {
		this.group = group;
		this.groupIndex = groupIndex;
		this.lowerBoundIndex = lowerBoundIndex;
		this.upperBoundIndex = lowerBoundIndex + group.getNrElements() - 1;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public int getLowerBoundIndex() {
		return lowerBoundIndex;
	}

	public void setLowerBoundIndex(int lowerBoundIndex) {
		this.lowerBoundIndex = lowerBoundIndex;
	}

	public int getUpperBoundIndex() {
		return upperBoundIndex;
	}

	public void setUpperBoundIndex(int upperBoundIndex) {
		this.upperBoundIndex = upperBoundIndex;
	}
	
	public int getGroupIndex() {
		return groupIndex;
	}

	public void setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
	}
}
