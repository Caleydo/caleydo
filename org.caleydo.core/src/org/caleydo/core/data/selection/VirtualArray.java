package org.caleydo.core.data.selection;

import java.util.ArrayList;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Implementation of IVirtualArray
 * 
 * @author Alexander Lex
 * 
 */

public class VirtualArray
	extends AUniqueObject
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
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		
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
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		this.iLength = iLength;
		this.iAlVirtualArray = iAlVirtualArray;
	}

	@Override
	public VAIterator iterator()
	{
		return new VAIterator(this);
	}

	@Override
	public Integer get(int iIndex)
	{
		return iAlVirtualArray.get(iIndex);
	}

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

	@Override
	public void copy(int iIndex)
	{
		iAlVirtualArray.add(iIndex + 1, iAlVirtualArray.get(iIndex));
	}

	@Override
	public void moveLeft(int iIndex)
	{
		int iTemp = iAlVirtualArray.get(iIndex - 1);
		iAlVirtualArray.set(iIndex - 1, iAlVirtualArray.get(iIndex));
		iAlVirtualArray.set(iIndex, iTemp);
	}

	@Override
	public void moveRight(int iIndex)
	{
		int iTemp = iAlVirtualArray.get(iIndex + 1);
		iAlVirtualArray.set(iIndex + 1, iAlVirtualArray.get(iIndex));
		iAlVirtualArray.set(iIndex, iTemp);
	}

	@Override
	public Integer remove(int iIndex)
	{
		return iAlVirtualArray.remove(iIndex);
	}

	@Override
	public void removeByElement(int iElement)
	{
		int iIndex = 0;
		for(int iCurrent : iAlVirtualArray)
		{
			if(iCurrent == iElement)
				remove(iIndex);
			iIndex++;
		}
	}
	
	@Override
	public Integer size()
	{
		return iAlVirtualArray.size();
	}

	@Override
	public void reset()
	{
		init();
	}
	
	@Override
	public int indexOf(int iElement)
	{
		return iAlVirtualArray.indexOf(iElement);
	}
	
	@Override
	public ArrayList<Integer> getIndexList()
	{
		return iAlVirtualArray;
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


}
