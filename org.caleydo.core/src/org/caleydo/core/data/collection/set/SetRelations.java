package org.caleydo.core.data.collection.set;

import java.util.HashMap;

import org.caleydo.core.data.collection.ISet;

public class SetRelations {

	ISet setLeft;
	ISet setRight;

	HashMap<Integer, Integer> hashLeftToRight;
	HashMap<Integer, Integer> hashRightToLeft;

	public SetRelations(ISet setLeft, ISet setRight) {
		this.setLeft = setLeft;
		this.setRight = setRight;
		hashLeftToRight = new HashMap<Integer, Integer>();
		hashRightToLeft = new HashMap<Integer, Integer>();
	}
	
}
