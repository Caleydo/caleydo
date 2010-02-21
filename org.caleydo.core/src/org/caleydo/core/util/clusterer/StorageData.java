package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageGroupList;
import org.caleydo.core.data.selection.StorageVirtualArray;

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

	Tree<ClusterNode> storageTree;

	public StorageVirtualArray getStorageVA() {
		return storageVA;
	}

	public void setStorageVA(StorageVirtualArray storageVA) {
		this.storageVA = storageVA;
	}

	public ArrayList<Integer> getStorageClusterSizes() {
		return storageClusterSizes;
	}

	public ArrayList<Integer> getStorageSampleElements() {
		return storageSampleElements;
	}

	public Tree<ClusterNode> getStorageTree() {
		return storageTree;
	}

	public void setStorageTree(Tree<ClusterNode> storageTree) {
		this.storageTree = storageTree;
	}

	void finish() {

		if (storageVA != null && storageClusterSizes != null && storageSampleElements != null) {
			StorageGroupList storageGroupList = new StorageGroupList();

			int cnt = 0;
			int iOffset = 0;
			for (Integer iter : storageClusterSizes) {

				Group temp = new Group(iter, false, storageSampleElements.get(cnt), SelectionType.NORMAL);
				storageGroupList.append(temp);
				cnt++;
				iOffset += iter;
			}
		}
	}

	@Override
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
}
