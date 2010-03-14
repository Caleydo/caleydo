package org.caleydo.view.compare;

public class GroupInfo {

	/**
	 * The number of genes which are a match for this group
	 */
	private int containedNrGenes = 0;

	public GroupInfo() {
	}

	public void increaseContainedNumberOfGenesByOne() {
		containedNrGenes++;
	}

	public int getContainedNrGenes() {
		return containedNrGenes;
	}
}
