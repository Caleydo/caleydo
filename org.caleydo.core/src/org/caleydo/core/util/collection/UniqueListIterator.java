/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
