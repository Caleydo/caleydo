package org.caleydo.core.data.collection;

/**
 * Interface for iterators on CContainers
 * 
 * @author Alexander Lex
 */
public interface ICContainerIterator
{

	/**
	 * Returns true if another element exists in the container
	 * 
	 * @return false if no more elements exist
	 */
	public boolean hasNext();

	/**
	 * Removes the element last called by next or previous from the virtual array. Works only if virtual arrays
	 * are enabled, throws exception if called without an enabled virtual array.
	 */
	public void remove();

}
