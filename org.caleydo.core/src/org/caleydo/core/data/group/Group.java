package org.caleydo.core.data.group;

import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.clusterer.ClusterNode;

public class Group {

	/**
	 * number of elements in the group/cluster
	 */
	private int nrElements;

	/**
	 * The virtual array index of the first element of the group
	 */
	private int startIndex;

	/**
	 * The index of the group in the group lst
	 */
	private int groupIndex;

	private boolean collapsed;

	/**
	 * index of the representative element in the VA
	 */
	private int idxExample;

	private SelectionType selectionType;

	/**
	 * In case of groups determined in dendrogram view the corresponding node in the tree must be stored for
	 * use in HHM
	 */
	private ClusterNode clusterNode;

	/**
	 * array with mean expression values --> representative element
	 */
	private float[] fArRepresentativeElement;

	private int visibleNrGenes = 0;

	public Group() {
	}

	/**
	 * Constructor
	 * 
	 * @param iNrElements
	 * @param bCollapsed
	 * @param iIdxExample
	 * @param SelectionType
	 */
	public Group(int iNrElements, boolean bCollapsed, int iIdxExample, SelectionType SelectionType) {
		this.setNrElements(iNrElements);
		this.setCollapsed(bCollapsed);
		this.setIdxExample(iIdxExample);
		this.setRepresentativeElement(fArRepresentativeElement);
		this.setSelectionType(SelectionType);
	}

	/**
	 * Constructor with cluster node included
	 * 
	 * @param iNrElements
	 * @param bCollapsed
	 * @param iIdxExample
	 * @param SelectionType
	 * @param clusterNode
	 */
	public Group(int iNrElements, boolean bCollapsed, int iIdxExample, SelectionType SelectionType,
		ClusterNode clusterNode) {
		this.setNrElements(iNrElements);
		this.setCollapsed(bCollapsed);
		this.setIdxExample(iIdxExample);
		this.setSelectionType(SelectionType);
		this.setClusterNode(clusterNode);
	}

	public void setNrElements(int iNrElements) {
		this.nrElements = iNrElements;
	}

	public int getNrElements() {
		return nrElements;
	}

	public int getGroupIndex() {
		return groupIndex;
	}

	public void setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return startIndex + nrElements - 1;
	}

	public void setCollapsed(boolean bCollapsed) {
		this.collapsed = bCollapsed;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setIdxExample(int iIdxExample) {
		this.idxExample = iIdxExample;
	}

	public int getIdxExample() {
		return idxExample;
	}

	public void setSelectionType(SelectionType SelectionType) {
		this.selectionType = SelectionType;
	}

	@XmlTransient
	public SelectionType getSelectionType() {
		return selectionType;
	}

	public void togglSelectionType() {
		this.selectionType =
			(selectionType == SelectionType.SELECTION) ? SelectionType.NORMAL : SelectionType.SELECTION;
	}

	public void setClusterNode(ClusterNode clusterNode) {
		this.clusterNode = clusterNode;
	}

	public ClusterNode getClusterNode() {
		return clusterNode;
	}

	public void setRepresentativeElement(float[] fArRepresentativeElement) {
		this.fArRepresentativeElement = fArRepresentativeElement;
	}

	public float[] getRepresentativeElement() {
		return fArRepresentativeElement;
	}

	@Override
	public String toString() {
		return "NrElem.: " + nrElements;
	}

	public void increaseContainedNumberOfGenesByOne() {
		visibleNrGenes++;
	}

	public int getContainedNrGenes() {
		return visibleNrGenes;
	}

	public void resetVisualGenesCounter() {
		visibleNrGenes = 0;
	}
}
