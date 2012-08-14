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
import java.util.HashMap;

/**
 * Package private class used as hash-backing of virtual arrays to increase indexof performance to linear
 * time.
 * 
 * @author Alexander Lex
 */
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
