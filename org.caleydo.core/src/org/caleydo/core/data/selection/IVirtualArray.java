package org.caleydo.core.data.selection;

import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * A Virtual Array provides an association between a modifiable index in the
 * virtual arrays and the static indices in the storages and sets. It therefore
 * allows the virtual modification (deleting, adding, duplicating) of entries in
 * the storages.
 * 
 * @author Alexander Lex
 * 
 */
public interface IVirtualArray
	extends Iterable<Integer>//, IManagedObject
{

	/**
	 * Returns an Iterator<Integer> of type VAIterator, which allows to iterate
	 * over the virtual array
	 */
	public VAIterator iterator();

	/**
	 * Returns the element at the specified index in the virtual array
	 * 
	 * @param iIndex the index
	 * @return the element at the index
	 */
	public Integer get(int iIndex);

	/**
	 * Adds a element to the end of the list.
	 * 
	 * @param iNewElement the index to the collection
	 * @throws CaleydoRuntimeException if the value of the new element is larger
	 *             than allowed. The maximum allowed value is the length of the
	 *             collection which is managed - 1
	 */
	public void add(Integer iNewElement);

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 * 
	 * @param iIndex the position on which to insert the new element
	 * @param iNewElement the index to the collection
	 * 
	 * @throws CaleydoRuntimeException if the value of the new element is larger
	 *             than allowed. The maximum allowed value is the length of the
	 *             collection which is managed - 1
	 */
	public void add(int iIndex, Integer iNewElement);

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 * 
	 * @param iIndex
	 * @param iNewElement
	 * 
	 * @throws CaleydoRuntimeException if the value of the new element is larger
	 *             than allowed. The maximum allowed value is the length of the
	 *             collection which is managed - 1
	 */
	public void set(int iIndex, Integer iNewElement);

	/**
	 * Copies the element at index iIndex to the next index. Shifts the element
	 * currently at that position (if any) and any subsequent elements to the
	 * right (adds one to their indices).
	 * 
	 * @param iIndex the index of the element to be copied
	 */
	public void copy(int iIndex);

	/**
	 * Removes the element at the specified index
	 * 
	 * @param iIndex the index of the element to be removed
	 * @return the Element that was removed from the list
	 */
	public Integer remove(int iIndex);

	/**
	 * Returns the size of the virtual array
	 * 
	 * @return the size
	 */
	public Integer size();

	/**
	 * Reset the virtual array to the indices in the storage
	 */
	public void reset();

}
