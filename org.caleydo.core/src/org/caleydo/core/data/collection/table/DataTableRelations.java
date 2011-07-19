package org.caleydo.core.data.collection.table;

import java.util.HashMap;

public class DataTableRelations {

	DataTable setLeft;
	DataTable setRight;

	HashMap<Integer, Integer> hashLeftToRight;
	HashMap<Integer, Integer> hashRightToLeft;
	HashMap<DataTable, HashMap<Integer, Integer>> hashSetToRelations;

	public DataTableRelations(DataTable setLeft, DataTable setRight) {
		this.setLeft = setLeft;
		this.setRight = setRight;
		Integer size = (int) (setLeft.depth() * 1.5);
		hashLeftToRight = new HashMap<Integer, Integer>(size);
		hashRightToLeft = new HashMap<Integer, Integer>(size);
		hashSetToRelations = new HashMap<DataTable, HashMap<Integer, Integer>>(4);
		hashSetToRelations.put(setLeft, hashLeftToRight);
		hashSetToRelations.put(setRight, hashRightToLeft);
	}

	//
	// public HashMap<Integer, Integer> getHashToRight() {
	// return hashLeftToRight;
	// }
	//
	/**
	 * Returns the mapping from the supplied set to the related set
	 * 
	 * @param set
	 *            the "from" set
	 */
	public HashMap<Integer, Integer> getMapping(DataTable set) {
		return hashSetToRelations.get(set);
	}

	public Integer getEquivalentID(DataTable set, Integer id) {
		return hashSetToRelations.get(set).get(id);
	}

	public DataTable getSetLeft() {
		return setLeft;
	}

	public DataTable getSetRight() {
		return setRight;
	}
}
