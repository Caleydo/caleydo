package org.caleydo.core.data.collection.table;

import java.util.HashMap;
import java.util.ListIterator;

import org.caleydo.core.data.collection.dimension.ADimension;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.VAIterator;

/**
 * Implementation of the iterator for sets considering a virtual array.
 * 
 * @author Alexander Lex
 */
public class DimensionIterator
	implements ListIterator<ADimension> {
	private VAIterator vaIterator;
	private HashMap<Integer, ADimension> dimensions;

	/**
	 * Constructor
	 * 
	 * @param set
	 * @param virtualArray
	 */
	public DimensionIterator(HashMap<Integer, ADimension> dimension, DimensionVirtualArray virtualArray) {
		this.vaIterator = virtualArray.iterator();
		this.dimensions = dimension;
	}

	@Override
	public void add(ADimension dimension) {
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
	public ADimension next() {
		return dimensions.get(vaIterator.next());
	}

	@Override
	public int nextIndex() {
		return vaIterator.nextIndex();
	}

	@Override
	public ADimension previous() {
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
	public void set(ADimension dimension) {
		vaIterator.set(dimension.getID());
	}

}
