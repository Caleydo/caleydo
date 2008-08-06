package org.caleydo.core.data.selection;

import java.util.ArrayList;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Implementation of IVirtualArray
 * 
 * @author Alexander Lex
 * 
 */

public class VirtualArray
	// extends AManagedObject
	implements IVirtualArray

{
	ArrayList<Integer> iAlVirtualArray;

	int iLength;

	/**
	 * Constructor. Pass the length of the managed collection
	 * 
	 * @param iLength the length of the managed collection
	 */
	public VirtualArray(int iLength)
	{
		this.iLength = iLength;
		init();
	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined
	 * array list of indices on the collection. This will serve as the starting
	 * point for the virtual array.
	 * 
	 * @param iLength
	 * @param iAlVirtualArray
	 */
	public VirtualArray(int iLength, ArrayList<Integer> iAlVirtualArray)
	{
		this.iLength = iLength;
		this.iAlVirtualArray = iAlVirtualArray;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public VAIterator iterator()
	{
		return new VAIterator(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#get(int)
	 */
	@Override
	public Integer get(int iIndex)
	{
		return iAlVirtualArray.get(iIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#add(java.lang.Integer)
	 */
	@Override
	public void add(Integer iNewElement)
	{
		if (iNewElement < iLength)
			iAlVirtualArray.add(iNewElement);
		else
			throw new CaleydoRuntimeException(
					"Tried to add a element to a virtual array that is not within the "
							+ "allowed range (which is determined by the length of the collection "
							+ "on which the virtual array is applied",
					CaleydoRuntimeExceptionType.DATAHANDLING);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#add(int,
	 * java.lang.Integer)
	 */
	@Override
	public void add(int iIndex, Integer iNewElement)
	{
		if (iNewElement < iLength)
			iAlVirtualArray.add(iIndex, iNewElement);
		else
			throw new CaleydoRuntimeException(
					"Tried to add a element to a virtual array that is not within the "
							+ "allowed range (which is determined by the length of the collection "
							+ "on which the virtual array is applied",
					CaleydoRuntimeExceptionType.DATAHANDLING);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#set(int,
	 * java.lang.Integer)
	 */
	@Override
	public void set(int iIndex, Integer iNewElement)
	{
		if (iNewElement < iLength)
			iAlVirtualArray.set(iIndex, iNewElement);
		else
			throw new CaleydoRuntimeException(
					"Tried to add a element to a virtual array that is not within the "
							+ "allowed range (which is determined by the length of the collection "
							+ "on which the virtual array is applied",
					CaleydoRuntimeExceptionType.DATAHANDLING);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#copy(int)
	 */
	@Override
	public void copy(int iIndex)
	{
		iAlVirtualArray.add(iIndex + 1, iAlVirtualArray.get(iIndex));
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#moveLeft(int)
	 */
	@Override
	public void moveLeft(int iIndex)
	{
		int iTemp = iAlVirtualArray.get(iIndex - 1);
		iAlVirtualArray.set(iIndex - 1, iAlVirtualArray.get(iIndex));
		iAlVirtualArray.set(iIndex, iTemp);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#moveRight(int)
	 */
	@Override
	public void moveRight(int iIndex)
	{
		int iTemp = iAlVirtualArray.get(iIndex + 1);
		iAlVirtualArray.set(iIndex + 1, iAlVirtualArray.get(iIndex));
		iAlVirtualArray.set(iIndex, iTemp);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#remove(int)
	 */
	@Override
	public Integer remove(int iIndex)
	{
		return iAlVirtualArray.remove(iIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#size()
	 */
	@Override
	public Integer size()
	{
		return iAlVirtualArray.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.selection.IVirtualArray#reset()
	 */
	@Override
	public void reset()
	{
		init();
	}

	/**
	 * Initialize Virtual Array
	 */
	private void init()
	{
		iAlVirtualArray = new ArrayList<Integer>(iLength);

		for (int iCount = 0; iCount < iLength; iCount++)
		{
			iAlVirtualArray.add(iCount);
		}
	}
	
	public int indexOf(int iElement)
	{
		return iAlVirtualArray.indexOf(iElement);
	}

}
