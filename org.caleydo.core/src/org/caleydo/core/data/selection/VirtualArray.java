package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;

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
	 * Used to check whether elements to be removed are in descending order
	 */
	int iLastRemovedIndex = -1;

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
	 * @param iLVirtualArray
	 */
	public VirtualArray(int iLength, List<Integer> iLVirtualArray)
	{
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		this.iLength = iLength;
		this.iAlVirtualArray = new ArrayList<Integer>();
		iAlVirtualArray.addAll(iLVirtualArray);
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
	public void append(Integer iNewElement)
	{
		if (iNewElement < iLength)
			iAlVirtualArray.add(iNewElement);
		else
			throw new IllegalArgumentException(
					"Tried to add a element to a virtual array that is not within the "
							+ "allowed range (which is determined by the length of the collection "
							+ "on which the virtual array is applied");
	}

	@Override
	public boolean appendUnique(Integer iNewElement)
	{
		if (indexOf(iNewElement) != -1)
			return false;

		append(iNewElement);
		return true;

	}

	@Override
	public void add(int iIndex, Integer iNewElement)
	{
		if (iNewElement < iLength)
			iAlVirtualArray.add(iIndex, iNewElement);
		else
			throw new IllegalArgumentException(
					"Tried to add a element to a virtual array that is not within the "
							+ "allowed range (which is determined by the length of the collection "
							+ "on which the virtual array is applied");
	}

	@Override
	public void set(int iIndex, Integer iNewElement)
	{
		if (iNewElement < iLength)
			iAlVirtualArray.set(iIndex, iNewElement);
		else
			throw new IllegalArgumentException(
					"Tried to add a element to a virtual array that is not within the "
							+ "allowed range (which is determined by the length of the collection "
							+ "on which the virtual array is applied");
	}

	@Override
	public void copy(int iIndex)
	{
		iAlVirtualArray.add(iIndex + 1, iAlVirtualArray.get(iIndex));
	}

	@Override
	public void move(int iSrcIndex, int iTargetIndex)
	{
		Integer iElement = iAlVirtualArray.remove(iSrcIndex);
		iAlVirtualArray.add(iTargetIndex, iElement);
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
		Iterator<Integer> iter = iAlVirtualArray.iterator();
		while (iter.hasNext())
		{
			if (iter.next() == iElement)
				iter.remove();
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
	public void clear()
	{
		iAlVirtualArray.clear();
	}

	@Override
	public int indexOf(int iElement)
	{
		System.out.println("Costly indexof operation on a va of size: " + size());
		return iAlVirtualArray.indexOf(iElement);
	}

	@Override
	public ArrayList<Integer> getIndexList()
	{
		return iAlVirtualArray;
	}

	@Override
	public void setDelta(IVirtualArrayDelta delta)
	{
		for (VADeltaItem item : delta)
		{
			switch (item.getType())
			{
				case ADD:
					add(item.getIndex(), item.getPrimaryID());
					break;
				case APPEND:
					append(item.getPrimaryID());
					break;
				case APPEND_UNIQUE:
					appendUnique(item.getPrimaryID());
					break;
				case REMOVE:
					int iIndex = item.getIndex();
					if (iIndex < iLastRemovedIndex)
						throw new IllegalStateException(
								"Index of remove operation was smaller than previously used index. This is likely not intentional. Take care to remove indices from back to front.");
					iLastRemovedIndex = iIndex;
					remove(item.getIndex());
					break;
				case REMOVE_ELEMENT:
					removeByElement(item.getPrimaryID());
					break;
				case COPY:
					copy(item.getIndex());
					break;
				case MOVE:
					move(item.getIndex(), item.getTargetIndex());
					break;
				case MOVE_LEFT:
					moveLeft(item.getIndex());
					break;
				case MOVE_RIGHT:
					moveRight(item.getIndex());
					break;
				default:
					throw new IllegalStateException("Unhandled EVAOperation: "
							+ item.getType());
			}
		}
		iLastRemovedIndex = -1;
	}

	@Override
	public int containsElement(int iElement)
	{
		int iCount = 0;
		for (Integer iCompareElement : iAlVirtualArray)
		{
			if (iCompareElement == iElement)
				iCount++;
		}
		return iCount;
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
