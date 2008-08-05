package org.caleydo.core.data.collection.ccontainer;

import java.util.NoSuchElementException;

/**
 * Iterator for IntCContainer. Initialized by passing the container. Provides
 * the common iterator accessors.
 * 
 * @author Alexander Lex
 */
public class IntCContainerIterator
	extends AContainerIterator
{

	int iIndex = 0;

	IntCContainer intCContainer = null;

	/**
	 * Constructor
	 * 
	 * @param intCContainer the container over which to iterate
	 */
	public IntCContainerIterator(IntCContainer intCContainer)
	{

		this.intCContainer = intCContainer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICContainerIterator#hasNext()
	 */
	@Override
	public boolean hasNext()
	{

		if (iIndex < intCContainer.size() - 1)
			return true;
		else
			return false;

	}

	/**
	 * Returns the next element in the container Throws a NoSuchElementException
	 * if no more elements exist
	 * 
	 * @return the next element
	 */
	public int next()
	{

		try
		{
			return intCContainer.get(++iIndex);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new NoSuchElementException();
		}
	}
}