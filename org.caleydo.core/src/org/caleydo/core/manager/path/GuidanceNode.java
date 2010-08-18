package org.caleydo.core.manager.path;

import java.util.ArrayList;
import java.util.HashMap;

public class GuidanceNode
	implements INode {

	String dataDomainType;
	ArrayList<String> interfaceTypes = new ArrayList<String>();
	HashMap<String, Boolean> interfaceVisited = new HashMap<String, Boolean>();
	String taskDescription = "<unknown>";

	public GuidanceNode(String dataDomainType, ArrayList<String> interfaceTypes, String taskDescription) {
		this.dataDomainType = dataDomainType;
		this.interfaceTypes.addAll(interfaceTypes);

		for (String interfaceType : interfaceTypes) {
			interfaceVisited.put(interfaceType, false);
		}

		this.taskDescription = taskDescription;
	}

	public GuidanceNode(String dataDomainType, String interfaceType, String taskDescription) {
		this.dataDomainType = dataDomainType;
		this.interfaceTypes.add(interfaceType);
		this.taskDescription = taskDescription;
		interfaceVisited.put(interfaceType, false);
	}

	@Override
	public String toString() {
		return "[" + dataDomainType + "]: " + interfaceTypes;
	}

	@Override
	public String getDataDomainType() {
		return dataDomainType;
	}

	public ArrayList<String> getInterfaceTypes() {
		return interfaceTypes;
	}

	public boolean isInterfaceVisited(String interfaceType) {
		return interfaceVisited.get(interfaceType);
	}

	public void setInterfaceVisited(String interfaceType) {
		interfaceVisited.put(interfaceType, true);
	}

	public boolean allInterfacesVisited() {

		for (String interfaceType : interfaceTypes) {
			if (!interfaceVisited.get(interfaceType))
				return false;
		}

		return true;
	}

	public boolean oneInterfaceVisited() {

		for (String interfaceType : interfaceTypes) {
			if (interfaceVisited.get(interfaceType))
				return true;
		}

		return false;
	}

	public String getTaskDescription() {
		return taskDescription;
	}
}
