package org.caleydo.core.data.collection.table;

import java.util.HashMap;
import java.util.ListIterator;

import org.caleydo.core.data.collection.dimension.AColumn;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.VAIterator;

/**
 * Implementation of the iterator for sets considering a virtual array.
 * 
 * @author Alexander Lex
 */
public class DimensionIterator
	implements ListIterator<AColumn> {
	private VAIterator vaIterator;
	private HashMap<Integer, AColumn> dimensions;

	/**
	 * Constructor
	 * 
	 * @param set
	 * @param virtualArray
	 */
	public DimensionIterator(HashMap<Integer, AColumn> dimension, DimensionVirtualArray virtualArray) {
		this.vaIterator = virtualArray.iterator();
		this.dimensions = dimension;
	}

	@Override
	public void add(AColumn dimension) {
		vaIterator.add(dimension.getID());
	}

	@Override
	public boolean hasNext() {
		return vaIterator.hasNext();
	}

	@Override
	public boolean hasPrevious() {
		return vaIterator.hasPrevious();
	}

	@Override
	public AColumn next() {
		return dimensions.get(vaIterator.next());
	}

	@Override
	public int nextIndex() {
		return vaIterator.nextIndex();
	}

	@Override
	public AColumn previous() {
		return dimensions.get(vaIterator.previous());
	}

	@Override
	public int previousIndex() {
		return vaIterator.previousIndex();
	}

	@Override
	public void remove() {
		vaIterator.remove();
	}

	@Override
	public void set(AColumn dimension) {
		vaIterator.set(dimension.getID());
	}

}
