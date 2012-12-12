package org.caleydo.view.tourguide.data.compute;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HashSetIDSet implements IDSet {
	private final Set<Integer> s;

	public HashSetIDSet() {
		this.s = new HashSet<>(1024);
	}

	/**
	 * !! uses the given set for data storage
	 * 
	 * @param s
	 */
	public HashSetIDSet(final Set<Integer> s) {
		this.s = s;
	}

	@Override
	public boolean contains(int id) {
		return s.contains(id);
	}

	@Override
	public void set(int id) {
		s.add(id);
	}

	@Override
	public void setAll(Collection<Integer> ids) {
		s.addAll(ids);
	}

	@Override
	public int size() {
		return s.size();
	}

	@Override
	public void clear() {
		s.clear();
	}

	@Override
	public boolean isFastIteration() {
		return true;
	}

	@Override
	public Iterator<Integer> iterator() {
		return s.iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("HashSetIDSet (%d)", size());
	}

}