package org.caleydo.core.manager.path;


public class GuidanceNode implements INode {

	String dataDomainType;
	String interfaceType;

	public GuidanceNode(String dataDomainType, String interfaceType) {
		this.dataDomainType = dataDomainType;
		this.interfaceType = interfaceType;
	}

	@Override
	public String toString() {
		return "[" + dataDomainType + "]: " +interfaceType;
	}

	public String getDataDomainType() {
		return dataDomainType;
	}

	public String getInterfaceType() {
		return interfaceType;
	}
}
