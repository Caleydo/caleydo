package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.util.clusterer.ClusterNode;

@XmlType
@XmlRootElement
public class ContentVirtualArray
	extends VirtualArray<ContentVirtualArray,  ContentVADelta, ContentGroupList> {

	public ContentVirtualArray() {
		super(Set.CONTENT);
	}

	/**
	 * Constructor, creates an empty Virtual Array
	 */
	public ContentVirtualArray(String vaType) {
		super(vaType);

	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined array list of indices on the
	 * collection. This will serve as the starting point for the virtual array.
	 * 
	 * @param initialList
	 */
	public ContentVirtualArray(String vaType, List<Integer> initialList) {
		super(vaType, initialList);
	}

	@Override
	public ContentVirtualArray getNewInstance() {
		return new ContentVirtualArray();
	}

	/**
	 * Creates a new group list based on the cluster nodes supplied and sets it to the group list memeber of
	 * this virtual array
	 * 
	 * @param iAlClusterNodes
	 *            the list of cluster nodes on which the group list is based
	 * @return a reference to the local groupList variable
	 */
	public ContentGroupList buildNewGroupList(ArrayList<ClusterNode> iAlClusterNodes) {
		groupList = (ContentGroupList) buildNewGroupList(new ContentGroupList(), iAlClusterNodes);
		return groupList;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.VirtualArray#getGroupList()
	 */
	@Override
	public ContentGroupList getGroupList() {
		// TODO Auto-generated method stub
		return super.getGroupList();
	}

}
