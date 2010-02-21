package org.caleydo.core.util.clusterer;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageGroupList;
import org.caleydo.core.data.selection.StorageVirtualArray;

public class StorageClusterResult {

	StorageVirtualArray storageVA;

	/** indices of examples (cluster centers) */
	ArrayList<Integer> storageSampleElements;
	/** number of elements per cluster */
	ArrayList<Integer> storageClusterSizes;

	Tree<ClusterNode> storageTree;

	public StorageVirtualArray getStorageVA() {
		return storageVA;
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

}
