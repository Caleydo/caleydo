/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.path;

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
