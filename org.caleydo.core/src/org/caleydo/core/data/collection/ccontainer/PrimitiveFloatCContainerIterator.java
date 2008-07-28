package org.caleydo.core.data.collection.ccontainer;

import java.util.NoSuchElementException;

import org.caleydo.core.data.collection.ICContainerIterator;

/**
 * 
 * @author Alexander Lex
 * 
 * Iterator for PrimitiveFloatCContainer
 * 
 * Initialized by passing the container. Then provides the common iterator accessors.
 *
 */
public class PrimitiveFloatCContainerIterator
implements ICContainerIterator
{
	private int iIndex = 0;
	private PrimitiveFloatCContainer primitiveFloatCContainer = null;
	
	/**
	 * Constructor
	 * 
	 * Pass the container you want to have an iterator on
	 * 
	 * @param primitiveFloatStorage
	 */
	
	public PrimitiveFloatCContainerIterator(PrimitiveFloatCContainer primitiveFloatStorage)
	{
		this.primitiveFloatCContainer = primitiveFloatStorage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IPrimitiveCContainerIterator#hasNext()
	 */
	public boolean hasNext()
	{
		if(iIndex < primitiveFloatCContainer.size() -1)
			return true;
		else
			return false;
				
	}
	
	/**
	 * Returns the next element in the container
	 * 
	 * Throws a NoSuchElementException if no more elements eist
	 * 
	 * @return the next element
	 */
	public float next()
	{
		try
		{
			return primitiveFloatCContainer.get(++iIndex);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new NoSuchElementException();
		}
	}	
	
}
