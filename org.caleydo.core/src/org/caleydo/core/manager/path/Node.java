package org.caleydo.core.manager.path;

public class Node {

	String dataDomainType;
	String viewType;

	public Node(String dataDomainType, String viewType) {
		this.dataDomainType = dataDomainType;
		this.viewType = viewType;
	}

	@Override
	public String toString() {
		return "[" + dataDomainType + ":" + viewType + "]";
	}
	
	public String getDataDomainType() {
		return dataDomainType;
	}
	
	public String getViewType() {
		return viewType;
	}
}
