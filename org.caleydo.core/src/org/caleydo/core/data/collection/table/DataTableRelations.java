package org.caleydo.core.data.collection.table;

import java.util.HashMap;

public class DataTableRelations {

	DataTable leftTable;
	DataTable rightTable;

	HashMap<Integer, Integer> hashLeftToRight;
	HashMap<Integer, Integer> hashRightToLeft;
	HashMap<DataTable, HashMap<Integer, Integer>> hashSetToRelations;

	public DataTableRelations(DataTable leftTable, DataTable rightTable) {
		this.leftTable = leftTable;
		this.rightTable = rightTable;
		Integer size = (int) (leftTable.getMetaData().depth() * 1.5);
		hashLeftToRight = new HashMap<Integer, Integer>(size);
		hashRightToLeft = new HashMap<Integer, Integer>(size);
		hashSetToRelations = new HashMap<DataTable, HashMap<Integer, Integer>>(4);
		hashSetToRelations.put(leftTable, hashLeftToRight);
		hashSetToRelations.put(rightTable, hashRightToLeft);
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

	public DataTable getDataTableLeft() {
		return leftTable;
	}

	public DataTable getDataTableRight() {
		return rightTable;
	}
}
