package org.caleydo.core.data.selection;

import org.caleydo.core.util.clusterer.ClusterNode;

public class Group {

	private int nrElements;
	private boolean collapsed;
	private int idxExample;
	private ESelectionType selectionType;
	private ClusterNode clusterNode;
	
	public Group() {
		
	}
	
	public Group(int iNrElements) {
		this.setNrElements(iNrElements);
		this.setCollapsed(false);
		this.setIdxExample(0);
		this.setSelectionType(ESelectionType.NORMAL);
	}

	public Group(int iNrElements, boolean bCollapsed, int iIdxExample, ESelectionType eSelectionType) {
		this.setNrElements(iNrElements);
		this.setCollapsed(bCollapsed);
		this.setIdxExample(iIdxExample);
		this.setSelectionType(eSelectionType);
	}
	
	public Group(int iNrElements, boolean bCollapsed, int iIdxExample, ESelectionType eSelectionType, ClusterNode clusterNode) {
		this.setNrElements(iNrElements);
		this.setCollapsed(bCollapsed);
		this.setIdxExample(iIdxExample);
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
	
	public void toggleSelectionType(){
		this.selectionType = (selectionType == ESelectionType.SELECTION) ? ESelectionType.NORMAL : ESelectionType.SELECTION;
	}

	public void setClusterNode(ClusterNode clusterNode) {
		this.clusterNode = clusterNode;
	}

	public ClusterNode getClusterNode() {
		return clusterNode;
	}
}
