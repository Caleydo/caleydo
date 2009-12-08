package org.caleydo.core.data.selection;

import org.caleydo.core.util.clusterer.ClusterNode;

public class Group {

	/**
	 * number of elements in the group/cluster
	 */
	private int nrElements;

	private boolean collapsed;

	/**
	 * index of the representative element in the VA
	 */
	private int idxExample;

	private ESelectionType selectionType;

	/**
	 * In case of groups determined in dendrogram view the corresponding node in the tree must be stored for
	 * use in HHM
	 */
	private ClusterNode clusterNode;

	/**
	 * array with mean expression values --> representative element
	 */
	private float[] fArRepresentativeElement;

	public Group() {
	}

	/**
	 * Constructor
	 * 
	 * @param iNrElements
	 * @param bCollapsed
	 * @param iIdxExample
	 * @param eSelectionType
	 */
	public Group(int iNrElements, boolean bCollapsed, int iIdxExample, ESelectionType eSelectionType) {
		this.setNrElements(iNrElements);
		this.setCollapsed(bCollapsed);
		this.setIdxExample(iIdxExample);
		this.setRepresentativeElement(fArRepresentativeElement);
		this.setSelectionType(eSelectionType);
	}

	/**
	 * Constructor with cluster node included
	 * 
	 * @param iNrElements
	 * @param bCollapsed
	 * @param iIdxExample
	 * @param eSelectionType
	 * @param clusterNode
	 */
	public Group(int iNrElements, boolean bCollapsed, int iIdxExample, ESelectionType eSelectionType,
		ClusterNode clusterNode) {
		this.setNrElements(iNrElements);
		this.setCollapsed(bCollapsed);
		this.setIdxExample(iIdxExample);
		this.setSelectionType(eSelectionType);
		this.setClusterNode(clusterNode);
	}

	/**
	 * Constructor with representative element included
	 * 
	 * @param iNrElements
	 * @param bCollapsed
	 * @param iIdxExample
	 * @param fArRepresentativeElement
	 * @param eSelectionType
	 */
	public Group(int iNrElements, boolean bCollapsed, int iIdxExample, float[] fArRepresentativeElement,
		ESelectionType eSelectionType) {
		this.setNrElements(iNrElements);
		this.setCollapsed(bCollapsed);
		this.setIdxExample(iIdxExample);
		this.setRepresentativeElement(fArRepresentativeElement);
		this.setSelectionType(eSelectionType);
	}

	/**
	 * Constructor with representative element and cluster node included
	 * 
	 * @param iNrElements
	 * @param bCollapsed
	 * @param iIdxExample
	 * @param fArRepresentativeElement
	 * @param eSelectionType
	 * @param clusterNode
	 */
	public Group(int iNrElements, boolean bCollapsed, int iIdxExample, float[] fArRepresentativeElement,
		ESelectionType eSelectionType, ClusterNode clusterNode) {
		this.setNrElements(iNrElements);
		this.setCollapsed(bCollapsed);
		this.setIdxExample(iIdxExample);
		this.setRepresentativeElement(fArRepresentativeElement);
		this.setSelectionType(eSelectionType);
		this.setClusterNode(clusterNode);
	}

	public void setNrElements(int iNrElements) {
		this.nrElements = iNrElements;
	}

	public int getNrElements() {
		return nrElements;
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

	public void setSelectionType(ESelectionType eSelectionType) {
		this.selectionType = eSelectionType;
	}

	public ESelectionType getSelectionType() {
		return selectionType;
	}

	public void toggleSelectionType() {
		this.selectionType =
			(selectionType == ESelectionType.SELECTION) ? ESelectionType.NORMAL : ESelectionType.SELECTION;
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
}
