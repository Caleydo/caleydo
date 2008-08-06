package org.caleydo.core.data.collection.ccontainer;

import java.util.NoSuchElementException;
import org.caleydo.core.data.selection.IVirtualArray;

/**
 * Iterator for IntCContainer. Initialized by passing the container. Provides
 * the common iterator accessors.
 * 
 * @author Alexander Lex
 */
public class IntCContainerIterator
	extends AContainerIterator
{

	private int iIndex = 0;

	private IntCContainer intCContainer = null;

	/**
	 * Constructor
	 * 
	 * @param intCContainer the container over which to iterate
	 */
	public IntCContainerIterator(IntCContainer intCContainer)
	{
		this.intCContainer = intCContainer;
	}

	/**
	 * Constructor
	 * 
	 * @param intCContainer
	 * @param iUniqueID
	 */
	public IntCContainerIterator(IntCContainer intCContainer, IVirtualArray virtualArray)
	{
		this(intCContainer);
		this.virtualArray = virtualArray;
		this.vaIterator = virtualArray.iterator();
	}

	/**
	 * Returns the next element in the container Throws a NoSuchElementException
	 * if no more elements exist
	 * 
	 * @return the next element
	 */
	public int next()
	{
		if (virtualArray != null)
		{
			return intCContainer.get(vaIterator.next());
		}
		else
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
}