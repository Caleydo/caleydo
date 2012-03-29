package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;

@XmlType
@XmlRootElement
public class DimensionVirtualArray
	extends VirtualArray<DimensionVirtualArray, DimensionVADelta, DimensionGroupList> {

	public DimensionVirtualArray() {
	}

	/**
	 * Constructor, creates an empty Virtual Array
	 */
	public DimensionVirtualArray(IDType idType) {
		super(idType);
	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined array list of indices on the
	 * collection. This will serve as the starting point for the virtual array.
	 * 
	 * @param initialList
	 */
	public DimensionVirtualArray(IDType idType, List<Integer> initialList) {
		super(idType, initialList);
	}

	@Override
	public DimensionVirtualArray getNewInstance() {
		return new DimensionVirtualArray(idType);
	}

	@Override
	public DimensionVADelta getConcreteVADeltaInstance() {
		return new DimensionVADelta();
	}

	/**
	 * Creates a new group list based on the cluster nodes supplied and sets it to the group list memeber of
	 * this virtual array
	 * 
	 * @param clusterNodes
	 *            the list of cluster nodes on which the group list is based
	 * @return a reference to the local groupList variable
	 */
	public DimensionGroupList buildNewGroupList(ArrayList<ClusterNode> clusterNodes) {
		groupList = (DimensionGroupList) buildNewGroupList(new DimensionGroupList(), clusterNodes);
		return groupList;
	}

}
