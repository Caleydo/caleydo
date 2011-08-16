package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

class IDMap {

	HashMap<Integer, ArrayList<Integer>> hashIDToIndex;
	boolean isHashIDToIndexDirty = true;
	ArrayList<Integer> virtualArrayList;

	IDMap(ArrayList<Integer> virtualArrayList) {
		this.virtualArrayList = virtualArrayList;
		hashIDToIndex = new HashMap<Integer, ArrayList<Integer>>((int) (virtualArrayList.size() / 0.7));
	}

	void setDirty() {
		isHashIDToIndexDirty = true;
	}

	private void checkIDMap() {
		if (!isHashIDToIndexDirty)
			return;

		// Logger.log(new Status(Status.INFO, "core", "Rebuilding index map for VA:" + virtualArray));

		hashIDToIndex.clear();

		for (int index = 0; index < virtualArrayList.size(); index++) {
			Integer id = virtualArrayList.get(index);
			ArrayList<Integer> indexList = hashIDToIndex.get(id);
			if (indexList == null)
				indexList = new ArrayList<Integer>(1);
			indexList.add(index);
			hashIDToIndex.put(id, indexList);
		}
		isHashIDToIndexDirty = false;
	}

	Integer indexOf(Integer id) {
		checkIDMap();

		ArrayList<Integer> list = hashIDToIndex.get(id);
		if (list == null || list.size() == 0)
			return -1;
		return list.get(0);
	}

	ArrayList<Integer> indicesOf(Integer id) {
		checkIDMap();

		ArrayList<Integer> list = hashIDToIndex.get(id);
		if (list != null)
			return list;
		else
			return new ArrayList<Integer>(1);
	}

	boolean contains(Integer id) {
		checkIDMap();
		return hashIDToIndex.get(id) == null ? false : true;
	}

	int occurencesOf(Integer id) {
		checkIDMap();
		return hashIDToIndex.get(id).size();
	}

}
