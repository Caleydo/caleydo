package org.caleydo.core.data.collection.table;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.StorageGroupList;
import org.caleydo.core.util.clusterer.ClusterNode;

/**
 * Class that summarizes all information around a storageVA and its tree. No field is initialized by default.
 * 
 * @author Alexander Lex
 */
public class StorageData
	implements Cloneable {

	StorageVirtualArray storageVA;

	/** indices of examples (cluster centers) */
	ArrayList<Integer> storageSampleElements;
	/** number of elements per cluster */
	ArrayList<Integer> storageClusterSizes;

	ClusterTree storageTree;

	boolean isDefaultTree = true;

	/** Root node for storage hierarchy which is only set in metaSets */
	private ClusterNode storageTreeRoot = null;

	public StorageVirtualArray getStorageVA() {
		return storageVA;
	}

	public void setStorageVA(StorageVirtualArray storageVA) {
		this.storageVA = storageVA;
	}

	public ArrayList<Integer> getStorageClusterSizes() {
		return storageClusterSizes;
	}

	public void setStorageClusterSizes(ArrayList<Integer> storageClusterSizes) {
		this.storageClusterSizes = storageClusterSizes;
	}

	public ArrayList<Integer> getStorageSampleElements() {
		return storageSampleElements;
	}

	public void setStorageSampleElements(ArrayList<Integer> storageSampleElements) {
		this.storageSampleElements = storageSampleElements;
	}

	public ClusterTree getStorageTree() {
		return storageTree;
	}

	public void setStorageTree(ClusterTree storageTree) {
		this.storageTree = storageTree;
	}

	public void setDefaultTree(boolean isDefaultTree) {
		this.isDefaultTree = isDefaultTree;
	}

	public boolean isDefaultTree() {
		return isDefaultTree;
	}

	public ClusterNode getStorageTreeRoot() {
		if (storageTreeRoot == null)
			return storageTree.getRoot();
		return storageTreeRoot;
	}

	public void setStorageTreeRoot(ClusterNode storageTreeRoot) {
		this.storageTreeRoot = storageTreeRoot;
	}

	public void finish() {

		if (storageVA != null && storageClusterSizes != null && storageSampleElements != null) {
			StorageGroupList storageGroupList = new StorageGroupList();

			int cnt = 0;
			int iOffset = 0;
			for (Integer size : storageClusterSizes) {

				Group temp = new Group(size, storageSampleElements.get(cnt));
				storageGroupList.append(temp);
				cnt++;
				iOffset += size;
			}
			storageVA.setGroupList(storageGroupList);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public StorageData clone() {
		StorageData clone = null;
		try {
			clone = (StorageData) super.clone();
		}
		catch (CloneNotSupportedException e) {
			// TODO: handle exception
		}
		if (storageClusterSizes != null)
			clone.storageClusterSizes = (ArrayList<Integer>) storageClusterSizes.clone();
		if (storageSampleElements != null)
			clone.storageSampleElements = (ArrayList<Integer>) storageSampleElements.clone();
		clone.storageVA = storageVA.clone();
		// FIXME this is a bad hack since it is not a clone
		clone.storageTree = storageTree;

		return clone;

	}

	public void reset() {
		storageVA = null;
		storageClusterSizes = null;
		storageSampleElements = null;
		storageTree = null;
		storageTreeRoot = null;
	}
}
