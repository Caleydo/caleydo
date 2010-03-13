package org.caleydo.core.data.collection.set;

import java.util.HashMap;

import org.caleydo.core.data.collection.ISet;

public class SetRelations {

	ISet setLeft;
	ISet setRight;

	HashMap<Integer, Integer> hashLeftToRight;
	HashMap<Integer, Integer> hashRightToLeft;
	HashMap<ISet, HashMap<Integer, Integer>> hashSetToRelations;

	public SetRelations(ISet setLeft, ISet setRight) {
		this.setLeft = setLeft;
		this.setRight = setRight;
		Integer size = (int) (setLeft.depth() * 1.5);
		hashLeftToRight = new HashMap<Integer, Integer>(size);
		hashRightToLeft = new HashMap<Integer, Integer>(size);
		hashSetToRelations = new HashMap<ISet, HashMap<Integer, Integer>>(4);
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
	public HashMap<Integer, Integer> getMapping(ISet set) {
		return hashSetToRelations.get(set);
	}

	public Integer getEquivalentID(ISet set, Integer id) {
		return hashSetToRelations.get(set).get(id);
	}

	public ISet getSetLeft() {
		return setLeft;
	}

	public ISet getSetRight() {
		return setRight;
	}
}
