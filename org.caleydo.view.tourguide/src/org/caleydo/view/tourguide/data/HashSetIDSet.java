package org.caleydo.view.tourguide.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HashSetIDSet implements IDSet {
	private final Set<Integer> s = new HashSet<>(1024);

	@Override
	public boolean contains(int id) {
		return s.contains(id);
	}

	@Override
	public void set(int id) {
		s.add(id);
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
}