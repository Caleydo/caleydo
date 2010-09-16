package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.group.GroupList;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;

/**
 * A Virtual Array provides an association between a modifiable index in the virtual arrays and the static
 * indices in the storages and sets. It therefore allows the virtual modification (deleting, adding,
 * duplicating) of entries in the storages.
 * 
 * @author Alexander Lex
 */
public interface IVirtualArray<ConcreteType extends IVirtualArray<ConcreteType, VADelta, GroupType>, VADelta extends VirtualArrayDelta<?>, GroupType extends GroupList<?, ?, ?>>
	extends Iterable<Integer>, IUniqueObject, Cloneable {

	public String getVaType();

	/**
	 * Returns an Iterator<Integer> of type VAIterator, which allows to iterate over the virtual array
	 */
	@Override
	public VAIterator iterator();

	/**
	 * Returns the element at the specified index in the virtual array
	 * 
	 * @param iIndex
	 *            the index
	 * @return the element at the index
	 */
	public Integer get(int iIndex);

	/**
	 * Adds an element to the end of the list.
	 * 
	 * @param iNewElement
	 *            the index to the collection
	 * @exception IllegalArgumentException
	 *                if the value of the new element is larger than allowed. The maximum allowed value is the
	 *                length of the collection which is managed - 1
	 */
	public void append(Integer iNewElement);

	/**
	 * Adds an element to the end of the list, if the element is not already contained.
	 * 
	 * @param iNewElement
	 *            the index to the collection
	 * @exception IllegalArgumentException
	 *                if the value of the new element is larger than allowed. The maximum allowed value is the
	 *                length of the collection which is managed - 1
	 * @return true if the array was modified, else false
	 */
	public boolean appendUnique(Integer iNewElement);

	/**
	 * Inserts the specified element at the specified position in this list. Shifts the element currently at
	 * that position (if any) and any subsequent elements to the right (adds one to their indices).
	 * 
	 * @param iIndex
	 *            the position on which to insert the new element
	 * @param iNewElement
	 *            the index to the collection
	 * @throws IllegalArgumentException
	 *             if the value of the new element is larger than allowed. The maximum allowed value is the
	 *             length of the collection which is managed - 1
	 */
	public void add(int iIndex, Integer iNewElement);

	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 * 
	 * @param iIndex
	 * @param iNewElement
	 * @throws CaleydoRuntimeException
	 *             if the value of the new element is larger than allowed. The maximum allowed value is the
	 *             length of the collection which is managed - 1
	 */
	public void set(int iIndex, Integer iNewElement);

	/**
	 * Copies the element at index iIndex to the next index. Shifts the element currently at that position (if
	 * any) and any subsequent elements to the right (adds one to their indices).
	 * 
	 * @param iIndex
	 *            the index of the element to be copied
	 */
	public void copy(int iIndex);

	/**
	 * Moves the element at iIndex to the left
	 * 
	 * @param iIndex
	 *            the index of the element to be moved
	 */
	public void moveLeft(int iIndex);

	/**
	 * Moves the element at the specified src index to the target index. The element formerly at iSrcIndex is
	 * at iTargetIndex after this operation. The rest of the elements can change the index.
	 * 
	 * @param iSrcIndex
	 *            the src index of the element
	 * @param iTargetIndex
	 *            the target index of the element
	 */
	public void move(int iSrcIndex, int iTargetIndex);

	/**
	 * Moves the element at iIndex to the right
	 * 
	 * @param iIndex
	 *            the index of the element to be moved
	 */
	public void moveRight(int iIndex);

	/**
	 * Removes the element at the specified index. Shifts any subsequent elements to the left (subtracts one
	 * from their indices).
	 * 
	 * @param iIndex
	 *            the index of the element to be removed
	 * @return the Element that was removed from the list
	 */
	public Integer remove(int iIndex);

	/**
	 * <p>
	 * Remove all occurrences of an element from the list. Shifts any subsequent elements to the left
	 * (subtracts one from their indices).
	 * </p>
	 * <p>
	 * The implementation if based on a hash-table, performance is in constant time.
	 * </p>
	 * 
	 * @param iElement
	 *            the element to be removed
	 */
	public void removeByElement(int iElement);

	/**
	 * Returns the size of the virtual array
	 * 
	 * @return the size
	 */
	public Integer size();

	// /**
	// * Reset the virtual array to the indices in the managed data entity
	// */
	// public void reset();

	/**
	 * Reset the virtual array to contain no elements
	 */
	public void clear();

	/**
	 * Returns the index of the first occurrence of the specified element in this list, or -1 if this list
	 * does not contain the element. More formally, returns the lowest index i such that (o==null ?
	 * get(i)==null : o.equals(get(i))), or -1 if there is no such index.
	 * 
	 * @param iElement
	 *            element to search for
	 * @return the index of the first occurrence of the specified element in this list, or -1 if this list
	 *         does not contain the element
	 */
	public int indexOf(int iElement);

	/**
	 * Returns the indices of all occurrences of the specified element in this list, or an empty list if the
	 * list does not contain the element.
	 * 
	 * @param iElement
	 *            element to search for
	 * @return a list of all the indices of all occurrences of the element or an empty list if no such
	 *         elements exist
	 */
	public ArrayList<Integer> indicesOf(int iElement);

	/**
	 * Returns the array list which contains the list of storage indices. DO NOT EDIT THIS LIST
	 * 
	 * @return the list containing the storage indices
	 */
	public ArrayList<Integer> getIndexList();

	/**
	 * Applies the operations specified in the delta to the virtual array
	 * 
	 * @param delta
	 */
	public void setDelta(VADelta delta);

	/**
	 * Checks whether and how often an element is contained in the virtual array. Returns the number of
	 * occurrences, 0 if it does not occur.
	 * 
	 * @param iElement
	 *            the element to be checked
	 * @return the number of occurences
	 */
	public int containsElement(int iElement);

	/**
	 * Returns the group list. If no group list exits null will be returned.
	 * 
	 * @param
	 * @return the group list
	 */
	public GroupType getGroupList();

	/**
	 * Returns an ArrayList with indexes of one group (genes/experiments) determined by iGroupIdx.
	 * 
	 * @param iGroupIdx
	 *            index of group in groupList
	 * @return ArrayList<Integer> containing all indexes of one group determined by iGroupIdx. Null will be
	 *         returned in case of groupList is null.
	 */
	public ArrayList<Integer> getGeneIdsOfGroup(int iGroupIdx);

	/**
	 * Sets group list in VA, used especially by affinity clusterer.
	 * 
	 * @param groupList
	 *            new group list
	 * @return true if operation executed correctly otherwise false
	 */
	public boolean setGroupList(GroupType groupList);

	/**
	 * Produces a clone of the virtual array
	 * 
	 * @return
	 */
	public ConcreteType clone();

	/**
	 * Replace the internally created ID with the specified. Used when this VA replaces another VA
	 * 
	 * @param iUniqueID
	 */
	public void setID(int iUniqueID);

	/**
	 * Returns an int array representation of the virtual array.
	 * 
	 * @return Int array VA representation
	 */
	public int[] getArray();

}
