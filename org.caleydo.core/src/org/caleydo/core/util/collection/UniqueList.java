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
package org.caleydo.core.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>
 * An ordered and unique list. Most behavior similar to {@link ArrayList}, however every element is contained only once.
 * Therefore no add to a specific index is supported.
 * 
 * FIXME: what is the difference to a SortedSet, Set?
 * </p>
 * <p>
 * Performance for {@link #contains(Object)} and {@link #containsAll(Collection)} is as fast as for a {@link HashMap},
 * respectively n lookups in the map for containsAll
 * </p>
 *
 *
 * @author Alexander Lex
 * @param <E>
 *            an arbitrary type. Make sure that {@link Object#equals(Object)} and {@link Object#hashCode()} are
 *            correctly implemented
 */
public class UniqueList<E>
	implements Iterable<E>, Collection<E> {

	ArrayList<E> alElements;
	HashMap<E, Boolean> hashElements;

	/**
	 * Constructor
	 */
	public UniqueList() {
		alElements = new ArrayList<E>();
		hashElements = new HashMap<E, Boolean>();
	}

	public E get(int arg0) {
		return alElements.get(arg0);
	}

	@Override
	public int size() {
		return alElements.size();
	}

	@Override
	public Iterator<E> iterator() {
		return new UniqueListIterator<E>(this);
	}

	/**
	 * Attempts to add the element to the collection. Adds nothing if the element is already contained.
	 *
	 * @return true, if element was added, false if element was already in the collection
	 */
	@Override
	public boolean add(E e) {
		if (hashElements.containsKey(e))
			return false;

		hashElements.put(e, null);
		return alElements.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		boolean bChanged = false;
		for (E element : collection) {
			if (add(element)) {
				bChanged = true;
			}
		}
		return bChanged;
	}

	@Override
	public void clear() {
		alElements.clear();
		hashElements.clear();

	}

	@Override
	public boolean contains(Object o) {
		return hashElements.containsKey(o);
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		boolean bChanged = true;
		for (Object element : collection) {
			if (!hashElements.containsKey(element)) {
				bChanged = false;
			}
		}
		return bChanged;
	}

	@Override
	public boolean isEmpty() {
		return alElements.isEmpty();
	}

	@Override
	public boolean remove(Object o) {
		hashElements.remove(o);
		return alElements.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean bChanged = false;
		for (Object element : collection) {
			if (hashElements.remove(element) != null) {
				bChanged = true;
			}
		}
		alElements.removeAll(collection);
		return bChanged;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return alElements.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return alElements.toArray(a);
	}

	@Override
	public String toString() {
		return "S: " + alElements.size() + " " + alElements.toString();
	}

}
