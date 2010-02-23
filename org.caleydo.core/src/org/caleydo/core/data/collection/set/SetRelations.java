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
	
	public HashMap<Integer, Integer> getHashLeftToRight() {
		return hashLeftToRight;
	}
	
	public HashMap<Integer, Integer> getHashRightToLeft() {
		return hashRightToLeft;
	}
	
	public ISet getSetLeft() {
		return setLeft;
	}
	
	public ISet getSetRight() {
		return setRight;
	}
}
