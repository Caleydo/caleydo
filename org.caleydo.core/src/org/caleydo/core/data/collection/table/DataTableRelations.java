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

	public DataTable getTableLeft() {
		return leftTable;
	}

	public DataTable getTableRight() {
		return rightTable;
	}
}
