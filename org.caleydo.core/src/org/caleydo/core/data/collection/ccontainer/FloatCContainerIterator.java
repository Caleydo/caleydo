package org.caleydo.core.data.collection.ccontainer;

import java.util.NoSuchElementException;

import org.caleydo.core.data.selection.IVirtualArray;

/**
 * Iterator for FloatCContainer Initialized by passing the container. Then provides the common iterator
 * accessors.
 * 
 * @author Alexander Lex
 */
public class FloatCContainerIterator
	extends AContainerIterator {

	private FloatCContainer floatCContainer = null;

	/**
	 * Constructor Pass the container you want to have an iterator on
	 * 
	 * @param primitiveFloatStorage
	 */
	public FloatCContainerIterator(FloatCContainer floatCContainer) {
		this.floatCContainer = floatCContainer;
		this.iSize = floatCContainer.size();
	}

	public FloatCContainerIterator(FloatCContainer floatCContainer, IVirtualArray virtualArray) {
		this(floatCContainer);
		this.virtualArray = virtualArray;
		this.vaIterator = virtualArray.iterator();
	}

	/**
	 * Returns the next element in the container Throws a NoSuchElementException if no more elements eist
	 * 
	 * @return the next element
	 */
	public float next() {
		if (virtualArray != null) {
			return floatCContainer.get(vaIterator.next());
		}
		else {
			try {
				return floatCContainer.get(++iIndex);
			}
			catch (IndexOutOfBoundsException e) {
				throw new NoSuchElementException();
			}
		}
	}

}
