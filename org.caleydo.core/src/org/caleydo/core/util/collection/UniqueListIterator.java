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

import java.util.Iterator;

/**
 * Iterator for the {@link UniqueList}.
 * 
 * @author Alexander Lex
 * @param <E>
 */
public class UniqueListIterator<E>
	implements Iterator<E> {
	public UniqueList<E> list;
	public Iterator<E> listIterator;
	E element = null;

	public UniqueListIterator(UniqueList<E> list) {
		this.list = list;
		this.listIterator = list.alElements.iterator();
	}

	@Override
	public boolean hasNext() {
		return listIterator.hasNext();
	}

	@Override
	public E next() {
		element = listIterator.next();
		return element;
	}

	@Override
	public void remove() {
		listIterator.remove();
		list.hashElements.remove(element);
	}

}
