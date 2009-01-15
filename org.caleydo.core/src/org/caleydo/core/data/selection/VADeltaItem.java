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
	implements IDeltaItem
{
	private EVAOperation vAOperation;
	private int iPrimaryID = -1;
	private int iSecondaryID = -1;
	private int iIndex = -1;

	/**
	 * Constructor. Constructing a VAItem externally is forbidden.
	 */
	private VADeltaItem()
	{
	}

	/**
	 * Static factory for a new delta item that appends an element to the end of
	 * a virtual array (see {@link IVirtualArray#append(Integer)})
	 * 
	 * @param iNewElement the new element
	 * @return the created object
	 */
	public static VADeltaItem append(int iNewElement)
	{
		VADeltaItem newItem = new VADeltaItem();
		newItem.vAOperation = EVAOperation.APPEND;
		newItem.iPrimaryID = iNewElement;
		return newItem;
	}

	/**
	 * Static factory for a new delta item that appends an element to the end of
	 * a virtual array (see {@link IVirtualArray#appendUnique(Integer)}) if the
	 * element does not yet exist in the virtual array
	 * 
	 * @param iNewElement the new element
	 * @return the created object
	 */
	public static VADeltaItem appendUnique(int iNewElement)
	{
		VADeltaItem newItem = new VADeltaItem();
		newItem.vAOperation = EVAOperation.APPEND_UNIQUE;
		newItem.iPrimaryID = iNewElement;
		return newItem;
	}

	/**
	 * Static factory for a new delta item that adds an element at a specific
	 * position of a virtual array
	 * 
	 * @param iIndex the place where the element is added (see
	 *            {@link IVirtualArray#add(int, Integer)})
	 * @param iNewElement the new element
	 * @return the created object
	 */
	public static VADeltaItem add(int iIndex, int iNewElement)
	{
		VADeltaItem newItem = new VADeltaItem();
		newItem.vAOperation = EVAOperation.ADD;
		newItem.iPrimaryID = iNewElement;
		newItem.iIndex = iIndex;
		return newItem;
	}

	/**
	 * Static factory for a new delta item that removes an element at a specific
	 * position of a virtual array
	 * 
	 * Take good care to remove items in the correct order, from back to front.
	 * Otherwise this can corrupt your data.
	 * 
	 * @param iIndex the place where the element is added (see
	 *            {@link IVirtualArray#remove(int)})
	 * @return the created object
	 */
	public static VADeltaItem remove(int iIndex)
	{
		VADeltaItem newItem = new VADeltaItem();
		newItem.vAOperation = EVAOperation.REMOVE;
		newItem.iIndex = iIndex;
		return newItem;
	}

	public static VADeltaItem removeElement(int iElement)
	{
		VADeltaItem newItem = new VADeltaItem();
		newItem.vAOperation = EVAOperation.REMOVE_ELEMENT;
		newItem.iPrimaryID = iElement;
		return newItem;
	}

	/**
	 * Create a new VADeltaItem with properties specified in operation. The new
	 * item may take only one parameter, therefore {@link EVAOperation#ADD} can
	 * not be passed as an argument here
	 * 
	 * @param operation the operation the delta item should carry out
	 * @param iVariable a integer variable, which can be either an index or an
	 *            element id, depending on the use case
	 * @return the created object with the properties specified in operation
	 */
	public static VADeltaItem create(EVAOperation operation, int iVariable)
	{
		switch (operation)
		{
			case APPEND:
				return append(iVariable);
			case APPEND_UNIQUE:
				return appendUnique(iVariable);
			case REMOVE:
				return remove(iVariable);
			case REMOVE_ELEMENT:
				return removeElement(iVariable);
			default:
				throw new IllegalArgumentException(
						"Illegal number of arguments for operation " + operation + ".");
		}
	}

	/**
	 * Getter for the type of operation
	 * 
	 * @return the type
	 */
	public EVAOperation getType()
	{
		return vAOperation;
	}

	/**
	 * Getter for the index.
	 * 
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
	 * 
	 * @return the element
	 * @throws IllegalStateException if operation does not use an element
	 */
	@Override
	public int getPrimaryID()
	{
		if (iPrimaryID == -1)
			throw new IllegalStateException("Operation " + vAOperation
					+ " does not need an element");

		return iPrimaryID;
	}

	@Override
	public int getSecondaryID()
	{
		return iSecondaryID;
	}

	@Override
	public void setPrimaryID(int iPrimaryID)
	{
		this.iPrimaryID = iPrimaryID;
	}

	@Override
	public void setSecondaryID(int iSecondaryID)
	{
		this.iSecondaryID = iSecondaryID;

	}

	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new IllegalStateException(
					"Something went wrong with the cloning, caught CloneNotSupportedException");
		}

	}

	@Override
	public int hashCode()
	{
		return iPrimaryID;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj.hashCode() == hashCode());
	}

}
