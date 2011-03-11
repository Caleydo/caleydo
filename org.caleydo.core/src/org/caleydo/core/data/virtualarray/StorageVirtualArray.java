package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.data.virtualarray.group.StorageGroupList;
import org.caleydo.core.util.clusterer.ClusterNode;

@XmlType
@XmlRootElement
public class StorageVirtualArray
	extends VirtualArray<StorageVirtualArray, StorageVADelta, StorageGroupList> {

	public StorageVirtualArray() {
		super("STORAGE");
	}

	/**
	 * Constructor, creates an empty Virtual Array
	 */
	public StorageVirtualArray(String vaType) {
		super(vaType);
	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined array list of indices on the
	 * collection. This will serve as the starting point for the virtual array.
	 * 
	 * @param initialList
	 */
	public StorageVirtualArray(String vaType, List<Integer> initialList) {
		super(vaType, initialList);
	}

	@Override
	public StorageVirtualArray getNewInstance() {
		return new StorageVirtualArray();
	}

	/**
	 * Creates a new group list based on the cluster nodes supplied and sets it to the group list memeber of
	 * this virtual array
	 * 
	 * @param iAlClusterNodes
	 *            the list of cluster nodes on which the group list is based
	 * @return a reference to the local groupList variable
	 */
	public StorageGroupList buildNewGroupList(ArrayList<ClusterNode> iAlClusterNodes) {
		groupList = (StorageGroupList) buildNewGroupList(new StorageGroupList(), iAlClusterNodes);
		return groupList;
	}
}
