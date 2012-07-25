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
package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.id.IDType;

@XmlType
@XmlRootElement
public class RecordVirtualArray
	extends VirtualArray<RecordVirtualArray, RecordVADelta, RecordGroupList> {

	public RecordVirtualArray() {
		super();
	}

	/**
	 * Constructor, creates an empty Virtual Array
	 */
	public RecordVirtualArray(IDType idType) {
		super(idType);

	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined array list of indices on the
	 * collection. This will serve as the starting point for the virtual array.
	 * 
	 * @param initialList
	 */
	public RecordVirtualArray(IDType idType, List<Integer> initialList) {
		super(idType, initialList);
	}

	@Override
	public RecordVirtualArray getNewInstance() {
		return new RecordVirtualArray(idType);
	}

	@Override
	public RecordVADelta getConcreteVADeltaInstance() {
		return new RecordVADelta();
	}

	/**
	 * Creates a new group list based on the cluster nodes supplied and sets it to the group list memeber of
	 * this virtual array
	 * 
	 * @param clusterNodes
	 *            the list of cluster nodes on which the group list is based
	 * @return a reference to the local groupList variable
	 */
	public RecordGroupList buildNewGroupList(ArrayList<ClusterNode> clusterNodes) {
		groupList = (RecordGroupList) buildNewGroupList(new RecordGroupList(), clusterNodes);
		return groupList;
	}
}
