package org.caleydo.core.data.collection.ccontainer;

import java.util.NoSuchElementException;
import org.caleydo.core.data.collection.ICContainerIterator;

/**
 * Iterator for PrimitiveIntCContainer. Initialized by passing the container.
 * Provides the common iterator accessors.
 * 
 * @author Alexander Lex
 */
public class PrimitiveIntCContainerIterator
	implements ICContainerIterator
{

	int iIndex = 0;

	PrimitiveIntCContainer primitiveIntCContainer = null;

	/**
	 * Constructor
	 * 
	 * @param primitiveIntCContainer the container over which to iterate
	 */
	public PrimitiveIntCContainerIterator(PrimitiveIntCContainer primitiveIntCContainer)
	{

		this.primitiveIntCContainer = primitiveIntCContainer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICContainerIterator#hasNext()
	 */
	@Override
	public boolean hasNext()
	{

		if (iIndex < primitiveIntCContainer.size() - 1)
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
			return primitiveIntCContainer.get(++iIndex);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new NoSuchElementException();
		}
	}
}