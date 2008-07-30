package org.caleydo.core.data.collection;

/**
 * @author Alexander Lex Interface for iterators on CContainers
 */
public interface ICContainerIterator
{

	/**
	 * Returns true if another element exists in the container
	 * 
	 * @return false if no more elements exist
	 */
	public boolean hasNext();

}
