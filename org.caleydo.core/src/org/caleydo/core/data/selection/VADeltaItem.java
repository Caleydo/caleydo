package org.caleydo.core.data.selection;

/**
 * A VADeltaItem contains all information for the modification of a single item
 * in a virtual array. Typically several items are held in a collection, a delta
 * (see {@link VirtualArrayDelta})
 * 
 * Several types of operations are defined in {@link EVAOperation}
 * 
 * The item is implemented using static factories thereby guaranteeing that only
 * valid parameters are contained.
 * 
 * @author Alexander Lex
 * 
 */

public class VADeltaItem
{
	private EVAOperation vAOperation;
	private int iElement = -1;
	private int iIndex = -1;

	/**
	 * Constructor. Constructing a VAItem externally is forbidden.
	 */
	@SuppressWarnings("unused")
	private void VAItem()
	{
	}

	/**
	 * Static factory for a new delta item that appends an element to the end of
	 * a virtual array (see {@link IVirtualArray#add(Integer)})
	 * 
	 * @param iNewElement the new element
	 * @return the created object
	 */
	public static VADeltaItem append(int iNewElement)
	{
		VADeltaItem newItem = new VADeltaItem();
		newItem.vAOperation = EVAOperation.APPEND;
		newItem.iElement = iNewElement;
		return newItem;
	}

	/**
	 * Static factory for a new delta item that adds an element at a specific
	 * position of a virtual array
	 * 
	 * @param iIndex the place where the element is added (see {@link IVirtualArray#add(int, Integer)})
	 * @param iNewElement the new element
	 * @return the created object
	 */
	public static VADeltaItem add(int iIndex, int iNewElement)
	{
		VADeltaItem newItem = new VADeltaItem();
		newItem.vAOperation = EVAOperation.ADD;
		newItem.iElement = iNewElement;
		newItem.iIndex = iIndex;
		return newItem;
	}

	/**
	 * Static factory for a new delta item that removes an element at a specific
	 * position of a virtual array
	 * 
	 * @param iIndex the place where the element is added (see {@link IVirtualArray#remove(int)})
	 * @return the created object
	 */
	public static VADeltaItem remove(int iIndex)
	{
		VADeltaItem newItem = new VADeltaItem();
		newItem.vAOperation = EVAOperation.REMOVE;
		newItem.iIndex = iIndex;
		return newItem;
	}

	/** 
	 * Getter for the type of operation
	 * @return the type 
	 */
	public EVAOperation getType()
	{
		return vAOperation;
	}

	/**
	 * Getter for the index. 
	 * @return the index
	 * @throws IllegalStateException if operation does not use an index
	 */
	public int getIndex()
	{
		if (iIndex == -1)
			throw new IllegalStateException("Operation " + vAOperation
					+ " does not need an index");

		return iIndex;
	}

	/**
	 * Getter for the element
	 * @return the element
	 * @throws IllegalStateException if operation does not use an element
	 */
	public int getElement()
	{
		if (iElement == -1)
			throw new IllegalStateException("Operation " + vAOperation
					+ " does not need an element");

		return iElement;
	}

}
