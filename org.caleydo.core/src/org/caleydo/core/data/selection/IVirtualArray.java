package org.caleydo.core.data.selection;

import java.util.ArrayList;
import org.caleydo.core.data.IUniqueObject;

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
	extends Iterable<Integer>, IUniqueObject
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
	 * Adds an element to the end of the list.
	 * 
	 * @param iNewElement the index to the collection
	 * @exception IllegalArgumentException if the value of the new element is
	 *                larger than allowed. The maximum allowed value is the
	 *                length of the collection which is managed - 1
	 */
	public void append(Integer iNewElement);

	/**
	 * Adds an element to the end of the list, if the element is not already
	 * contained.
	 * 
	 * @param iNewElement the index to the collection
	 * @exception IllegalArgumentException if the value of the new element is
	 *                larger than allowed. The maximum allowed value is the
	 *                length of the collection which is managed - 1
	 * @return true if the array was modified, else false
	 */
	public boolean appendUnique(Integer iNewElement);

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 * 
	 * @param iIndex the position on which to insert the new element
	 * @param iNewElement the index to the collection
	 * 
	 * @throws IllegalArgumentException if the value of the new element is
	 *             larger than allowed. The maximum allowed value is the length
	 *             of the collection which is managed - 1
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
	 * Moves the element at iIndex to the left
	 * 
	 * @param iIndex the index of the element to be moved
	 */
	public void moveLeft(int iIndex);

	/**
	 * Moves the element at the specified src index to the target index. The
	 * element formerly at iSrcIndex is at iTargetIndex after this operation.
	 * The rest of the elements can change the index.
	 * 
	 * @param iSrcIndex the src index of the element
	 * @param iTargetIndex the target index of the element
	 */
	public void move(int iSrcIndex, int iTargetIndex);

	/**
	 * Moves the element at iIndex to the right
	 * 
	 * @param iIndex the index of the element to be moved
	 */
	public void moveRight(int iIndex);

	/**
	 * Removes the element at the specified index. Shifts any subsequent
	 * elements to the left (subtracts one from their indices).
	 * 
	 * @param iIndex the index of the element to be removed
	 * @return the Element that was removed from the list
	 */
	public Integer remove(int iIndex);

	/**
	 * <p>
	 * Remove all occurrences of an element from the list. Shifts any subsequent
	 * elements to the left (subtracts one from their indices).
	 * </p>
	 * <p>
	 * Notice that this has a complexity of O(n) TODO: probably add a higher
	 * performance version, with an additional hash map
	 * </p>
	 * 
	 * @param iElement the element to be removed
	 */
	public void removeByElement(int iElement);

	/**
	 * Returns the size of the virtual array
	 * 
	 * @return the size
	 */
	public Integer size();

	/**
	 * Reset the virtual array to the indices in the managed data entity
	 */
	public void reset();

	/**
	 * Reset the virtual array to contain no elements
	 */
	public void clear();

	/**
	 * Returns the index of the first occurrence of the specified element in
	 * this list, or -1 if this list does not contain the element. More
	 * formally, returns the lowest index i such that (o==null ? get(i)==null :
	 * o.equals(get(i))), or -1 if there is no such index.
	 * 
	 * @param iElement element to search for
	 * @return the index of the first occurrence of the specified element in
	 *         this list, or -1 if this list does not contain the element
	 */
	public int indexOf(int iElement);

	/**
	 * Returns the array list which contains the list of storage indices. DO NOT
	 * EDIT THIS LIST
	 * 
	 * @return the list containing the storage indices
	 */
	public ArrayList<Integer> getIndexList();

	/**
	 * Applies the operations specified in the delta to the virtual array
	 * 
	 * @param delta
	 */
	public void setDelta(IVirtualArrayDelta delta);

	/**
	 * Checks whether and how often an element is contained in the virtual
	 * array. Returns the number of occurrences, 0 if it does not occur.
	 * 
	 * @param iElement the lement to be checked
	 * @return the number of occurences
	 */
	public int containsElement(int iElement);

}
