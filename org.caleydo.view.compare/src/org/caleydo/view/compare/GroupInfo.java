package org.caleydo.view.compare;

import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;

public class GroupInfo {

	private Group group;
	private int groupIndex;
	private int lowerBoundIndex;
	private int upperBoundIndex;
	private ContentVirtualArray contentVA;

	/**
	 * The number of genes which are a match for this group
	 */
	private int containedNrGenes = 0;

	public ContentVirtualArray getContentVA() {
		return contentVA;
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		this.contentVA = contentVA;
	}

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

	public void increaseContainedNumberOfGenesByOne() {
		containedNrGenes++;
	}

	public int getContainedNrGenes() {
		return containedNrGenes;
	}
}
