package org.caleydo.core.data.collection.ccontainer;

import java.util.ArrayList;

import org.caleydo.core.data.selection.IVirtualArray;

/**
 * Base class for CContainer which can use Generics
 * 
 * @author Alexander Lex
 * @param <T>
 *            the Type of the container
 */

public abstract class ATypedCContainer<T>
	implements ICContainer {
	ArrayList<T> alContainer;

	/**
	 * Returns the element of type T at the index iIndex
	 * 
	 * @param iIndex
	 *            the index
	 * @return the value at iIndex of type T
	 */
	public T get(int iIndex) {
		return alContainer.get(iIndex);
	}

	@Override
	public int size() {
		return alContainer.size();
	}

	/**
	 * Returns an iterator on the data Do not use the iterators remove, add or set function, since it will
	 * cause an UnsupportedOperationException.
	 * 
	 * @return the iterator
	 */
	public ContainerIterator<T> iterator() {
		return new ContainerIterator<T>(this, alContainer.listIterator());
	}

	/**
	 * Returns an iterator on the container which iterates based on a virtual array
	 * 
	 * @param virtualArray
	 *            the virtual array which the iteration is based on
	 * @return the iterator
	 */
	public ContainerIterator<T> iterator(IVirtualArray virtualArray) {
		return new ContainerIterator<T>(this, virtualArray);
	}

}
