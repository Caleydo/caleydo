package org.caleydo.core.data.selection;

import java.util.ListIterator;
import javax.naming.OperationNotSupportedException;

public class VAIterator
	implements ListIterator<Integer>
{
	

	int iCount = 0;
	VirtualArray virtualArray;
	boolean bLastMoveOperationWasPrevious = false;

	/**
	 * Constructor
	 * 
	 * @param virtualArray the virtual array on which the iterator is executed
	 */
	public VAIterator(VirtualArray virtualArray)
	{
		this.virtualArray = virtualArray;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	@Override
	public void add(Integer iNewElement)
	{
		virtualArray.add(++iCount, iNewElement);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#hasNext()
	 */
	@Override
	public boolean hasNext()
	{
		if (iCount < virtualArray.size() - 1)
			return true;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#hasPrevious()
	 */
	@Override
	public boolean hasPrevious()
	{
		if (iCount > 0)
			return true;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#next()
	 */
	@Override
	public Integer next()
	{
		bLastMoveOperationWasPrevious = false;
		return virtualArray.get(++iCount);
	
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#nextIndex()
	 */
	@Override
	public int nextIndex()
	{
		return iCount + 1;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#previous()
	 */
	@Override
	public Integer previous()
	{
		bLastMoveOperationWasPrevious = true;
		return virtualArray.get(iCount--);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#previousIndex()
	 */
	@Override
	public int previousIndex()
	{
		return iCount - 1;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#remove()
	 */
	@Override
	public void remove()
	{
		if(bLastMoveOperationWasPrevious)		
			virtualArray.remove(iCount);
		else
			virtualArray.remove(--iCount);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	@Override
	public void set(Integer iNewElement)
	{
		if(bLastMoveOperationWasPrevious)
			virtualArray.set(iCount, iNewElement);
		else
			virtualArray.set(iCount - 1, iNewElement);

	}

}
