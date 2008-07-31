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

}
