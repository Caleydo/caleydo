package org.caleydo.view.tourguide.data.compute;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

public class BitSetIDSet implements IDSet {
	private final BitSet s = new BitSet(10000);
	private int size = 0;

	@Override
	public boolean contains(int id) {
		return s.get(id);
	}

	@Override
	public void set(int id) {
		s.set(id);
		this.size++;
	}

	@Override
	public void setAll(Collection<Integer> ids) {
		for (int id : ids)
			set(id);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		s.clear();
		this.size = 0;
	}

	@Override
	public boolean isFastIteration() {
		return false;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			int indexOfLastSetBit = 0;

			@Override
			public boolean hasNext() {
				return (s.nextSetBit(indexOfLastSetBit) != -1);
			}

			@Override
			public Integer next() {
				int index = s.nextSetBit(indexOfLastSetBit);
				indexOfLastSetBit = index + 1;
				return index;
			}

			@Override
			public void remove() {
				throw new IllegalStateException();
			}

		};
	}
}