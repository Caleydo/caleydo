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
package org.caleydo.view.tourguide.internal.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.util.collection.Pair;

import com.google.common.collect.Iterators;

/**
 * a special list a like, that consists of multiple sub mini lists, where each of them has in addition an offset
 * 
 * @author Samuel Gratzl
 * 
 */
public class OffsetList<E> extends AbstractList<E> {
	private final List<Pair<Integer, List<E>>> subLists = new ArrayList<>(1);

	public OffsetList(int offset, List<E> data) {
		addSubList(offset, data);
	}

	@Override
	public E get(int index) {
		if (subLists.size() == 1)
			return subLists.get(0).getSecond().get(index);
		Pair<Integer, List<E>> r = resolve(index);
		return r.getSecond().get(r.getFirst());
	}

	@Override
	public E set(int index, E element) {
		if (subLists.size() == 1)
			return subLists.get(0).getSecond().set(index, element);
		Pair<Integer, List<E>> r = resolve(index);
		return r.getSecond().set(r.getFirst(), element);
	}

	@Override
	public Iterator<E> iterator() {
		if (subLists.size() == 1)
			return subLists.get(0).getSecond().iterator();
		@SuppressWarnings("unchecked")
		Iterator<E>[] its = new Iterator[subLists.size()];
		for (int i = 0; i < its.length; ++i) {
			its[i] = subLists.get(i).getSecond().iterator();
		}
        return Iterators.concat(its);
    }

	@Override
	public int size() {
		int sum = 0;
		for (Pair<Integer, List<E>> entry : subLists)
			sum += entry.getSecond().size();
		return sum;
	}

	public Iterable<Pair<Integer, List<E>>> subLists() {
		return subLists;
	}

	public void addSubList(int offset, List<E> data) {
		if (!canMerge(offset, data))
			subLists.add(Pair.make(offset, data));
	}

	private boolean canMerge(int offset, List<E> data) {
		if (subLists.isEmpty() || !(data instanceof CustomSubList))
			return false;
		CustomSubList<E> cdata = (CustomSubList<E>) data;
		Pair<Integer, List<E>> last = subLists.get(subLists.size() - 1);
		if (!(last.getSecond() instanceof CustomSubList)) // wrong type
			return false;
		CustomSubList<E> clast = (CustomSubList<E>) last.getSecond();
		if (cdata.getBackend() != clast.getBackend()) // not matching
			return false;
		if ((clast.size() + clast.getOffset()) != offset) { // not successive
			return false;
		}
		last.setSecond(new CustomSubList<>(clast.getBackend(), clast.getOffset(), clast.size() + data.size()));
		return true;
	}

	public int getMaxIndex() {
		int max = 0;
		for (Pair<Integer, List<E>> entry : subLists)
			max = Math.max(max, entry.getFirst() + entry.getSecond().size());
		return max;
	}

	private Pair<Integer, List<E>> resolve(int index) {
		for (Pair<Integer, List<E>> entry : subLists) {
			int size = entry.getSecond().size();
			if (index < size)
				return Pair.make(index, entry.getSecond());
			index -= size;
		}
		throw new IndexOutOfBoundsException();
	}

	public boolean isDummy() {
		return subLists.size() == 1 && subLists.get(0).getFirst() == 0;
	}
}

