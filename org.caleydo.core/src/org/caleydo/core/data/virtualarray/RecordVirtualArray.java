package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.util.clusterer.ClusterNode;

@XmlType
@XmlRootElement
public class RecordVirtualArray
	extends VirtualArray<RecordVirtualArray,  RecordVADelta, RecordGroupList> {

	public RecordVirtualArray() {
		super();
	}

	/**
	 * Constructor, creates an empty Virtual Array
	 */
	public RecordVirtualArray(String vaType) {
		super(vaType);

	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined array list of indices on the
	 * collection. This will serve as the starting point for the virtual array.
	 * 
	 * @param initialList
	 */
	public RecordVirtualArray(String vaType, List<Integer> initialList) {
		super(vaType, initialList);
	}

	@Override
	public RecordVirtualArray getNewInstance() {
		return new RecordVirtualArray();
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

	@Override
	public RecordGroupList getGroupList() {
		// TODO Auto-generated method stub
		return super.getGroupList();
	}

}
