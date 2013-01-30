package org.caleydo.core.data.collection.column.container;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class ContainerIterator<DATATYPE> implements Iterator<DATATYPE> {
	private final IContainer<DATATYPE> container;
	/**
	 * Index of element to be returned by subsequent call to next.
	 */
	private int cursor = 0;

	public ContainerIterator(IContainer<DATATYPE> container) {
		this.container = container;
	}

	@Override
	public boolean hasNext() {
		return cursor != container.size();
	}

	@Override
	public DATATYPE next() {
		try {
			int i = cursor;
			DATATYPE next = container.get(i);
			cursor = i + 1;
			return next;
		} catch (IndexOutOfBoundsException e) {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}