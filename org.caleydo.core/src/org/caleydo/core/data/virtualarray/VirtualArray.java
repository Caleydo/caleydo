/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * A VirtualArray is a list of indices referring to a collection. It is most
 * commonly used in combination with {@link AColumn}s or Records (which are the
 * records in the ADimensions). A VirtualArray is of use when the collections
 * themselves are immutable, or are shared between multiple clients (i.e. views)
 * using different VirtualArrays on them.
 * </p>
 * <p>
 * FIXME: Groups and group interactions are somewhat undefined
 * </p>
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class VirtualArray
	implements Iterable<Integer>, Cloneable {

	private static int VIRTUAL_ARRAY_ID_COUNTER = 0;

	/** unique ID */
	private int id = 0;

	@XmlTransient
	protected IDType idType;

	@XmlElement
	// @XmlList //would save xml space
	ArrayList<Integer> virtualArrayList;
	IDMap idMap;

	GroupList groupList = null;

	public VirtualArray() {
	}

	public VirtualArray(IDType idType) {
		setIdType(idType);
	}

	{
		id = VIRTUAL_ARRAY_ID_COUNTER++;
		this.virtualArrayList = new ArrayList<Integer>();
		idMap = new IDMap(virtualArrayList);
	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined
	 * array list of indices on the collection. This will serve as the starting
	 * point for the virtual array.
	 *
	 * @param initialList
	 */
	public VirtualArray(IDType idType, List<Integer> initialList) {
		setIdType(idType);
		if (initialList != null)
			virtualArrayList.addAll(initialList);
	}

	/**
	 * @param idType setter, see {@link #idType}
	 */
	public void setIdType(IDType idType) {
		this.idType = idType;
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	@XmlTransient
	public IDType getIdType() {
		return idType;
	}

	void setHashDirty() {
		idMap.setDirty();
	}



	/**
	 * Returns an Iterator<Integer> of type VAIterator, which allows to iterate
	 * over the virtual array
	 */
	@Override
	public VAIterator iterator() {
		return new VAIterator(this);
	}

	/**
	 * Returns the element at the specified index in the virtual array
	 *
	 * @param index the index
	 * @return the element at the index
	 */
	public Integer get(int index) {
		return virtualArrayList.get(index);
	}

	/**
	 * Adds an element to the end of the list.
	 *
	 * @param newElementID the id of the new element (which corresponds to the
	 *            index of the collection)
	 * @exception IllegalArgumentException if the value of the new element is
	 *                larger than allowed. The maximum allowed value is the
	 *                length of the collection which is managed - 1
	 */
	public void append(Integer newElementID) {
		idMap.setDirty();
		virtualArrayList.add(newElementID);
	}

	/**
	 * Adds an element to the end of the list, if the element is not already
	 * contained.
	 *
	 * @param newElement the index to the collection
	 * @exception IllegalArgumentException if the value of the new element is
	 *                larger than allowed. The maximum allowed value is the
	 *                length of the collection which is managed - 1
	 * @return true if the array was modified, else false
	 */
	public boolean appendUnique(Integer newElement) {
		idMap.setDirty();
		if (indexOf(newElement) != -1)
			return false;

		append(newElement);
		return true;

	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 *
	 * @param index the position on which to insert the new element
	 * @param newElement the id refering to the index of the collection
	 */
	public void add(int index, Integer newElement) {
		idMap.setDirty();
		virtualArrayList.add(index, newElement);
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 *
	 * @param index
	 * @param newElement
	 */
	public void set(int index, Integer newElement) {
		idMap.setDirty();
		virtualArrayList.set(index, newElement);
	}

	/**
	 * Copies the element at index to the next index. Shifts the element
	 * currently at that position (if any) and any subsequent elements to the
	 * right (adds one to their indices).
	 *
	 * @param index the index of the element to be copied
	 */
	public void copy(int index) {
		virtualArrayList.add(index + 1, virtualArrayList.get(index));
		idMap.setDirty();
	}

	/**
	 * Moves the element at the specified source index to the target index. The
	 * element formerly at srcIndex is at targetIndex after this operation.
	 *
	 * @param srcIndex the src index of the element
	 * @param targetIndex the target index of the element
	 */
	public void move(int srcIndex, int targetIndex) {
		idMap.setDirty();
		Integer element = virtualArrayList.remove(srcIndex);
		virtualArrayList.add(targetIndex, element);
	}

	/**
	 * Removes the element at the specified index. Shifts any subsequent
	 * elements to the left (subtracts one from their indices).
	 *
	 * @param iIndex the index of the element to be removed
	 * @return the Element that was removed from the list
	 */
	public Integer remove(int index) {
		idMap.setDirty();
		groupList.removeElementOfVA(index);
		return virtualArrayList.remove(index);
	}

	public void removeInBulk(ArrayList<Integer> indices) {
		Collections.sort(indices);
		int previousIndex = Integer.MAX_VALUE;
		for (int count = indices.size() - 1; count >= 0; count--) {
			Integer currentIndex = indices.get(count);
			if (currentIndex == previousIndex || currentIndex < 0
					|| currentIndex > previousIndex) {
				Logger.log(new Status(IStatus.INFO, "org.caleydo.core", "Cannot remove index: "
						+ currentIndex + " from VA " + this));
				continue;
			}
			previousIndex = currentIndex;
			remove(currentIndex);
		}
	}

	/**
	 * <p>
	 * Remove all occurrences of an element from the list. Shifts any subsequent
	 * elements to the left (subtracts one from their indices).
	 * </p>
	 * <p>
	 * The implementation if based on a hash-table, performance is in constant
	 * time.
	 * </p>
	 *
	 * @param element the element to be removed
	 */
	public void removeByElement(Integer element) {
		ArrayList<Integer> indices = indicesOf(element);
		removeInBulk(indices);
		idMap.setDirty();
	}

	/**
	 * Returns the size of the virtual array
	 *
	 * @return the size
	 */
	public int size() {
		return virtualArrayList.size();
	}

	/**
	 * Reset the virtual array to contain no elements
	 */
	public void clear() {
		idMap.setDirty();
		virtualArrayList.clear();
	}

	/**
	 * <p>
	 * Returns the index of the first occurrence of the specified element in
	 * this list, or -1 if this list does not contain the element. More
	 * formally, returns the lowest index i such that (o==null ? get(i)==null :
	 * o.equals(get(i))), or -1 if there is no such index.
	 * </p>
	 * <p>
	 * The runtime complexity of this function depends on whether there has been
	 * a change to the VA recently. If, for example, an element has been removed
	 * prior to this call, the runtime is O(n). Otherwise the runtime is O(1).
	 * </p>
	 *
	 * @param id element to search for
	 * @return the index of the first occurrence of the specified element in
	 *         this list, or -1 if this list does not contain the element
	 */
	public int indexOf(Integer id) {
		return idMap.indexOf(id);
	}

	/**
	 * <p>
	 * Returns the indices of all occurrences of the specified element in this
	 * list, or an empty list if the list does not contain the element.
	 * </p>
	 * <p>
	 * The runtime complexity of this function depends on whether there has been
	 * a change to the VA recently. If, for example, an element has been removed
	 * prior to this call, the runtime is O(n). Otherwise the runtime is O(1).
	 * </p>
	 *
	 * @param id element to search for
	 * @return a list of all the indices of all occurrences of the element or an
	 *         empty list if no such elements exist
	 */
	public ArrayList<Integer> indicesOf(Integer id) {
		return idMap.indicesOf(id);
	}

	/**
	 * Checks whether element is contained in the virtual array.
	 *
	 * @param id the id to be checked
	 * @return true if element occurs at least once, else false
	 */
	public boolean contains(Integer id) {
		return idMap.contains(id);
	}

	/**
	 * Checks the number of occurrences of an id. Returns 0 if it does not
	 * occur.
	 *
	 * @param id the id to be checked
	 * @return the number of occurrences (0 if none)
	 */
	public int occurencesOf(Integer id) {
		return idMap.occurencesOf(id);
	}

	/**
	 * Returns the array list which contains the list of vaIDs. DO NOT EDIT THIS
	 * LIST
	 *
	 * @return the list containing the dimension indices
	 */
	public ArrayList<Integer> getIDs() {
		return virtualArrayList;
	}

	/**
	 * Applies the operations specified in the delta to the virtual array
	 *
	 * @param delta
	 */
	public void setDelta(VirtualArrayDelta delta) {
		if (!delta.getIDType().equals(idType))
			throw new IllegalStateException("Incompatible ID Types");
		ArrayList<Integer> indicesToBeRemoved = new ArrayList<Integer>();
		for (VADeltaItem item : delta) {
			switch (item.getType()) {
				case ADD:
					add(item.getIndex(), item.getID());
					break;
				case APPEND:
					append(item.getID());
					break;
				case APPEND_UNIQUE:
					appendUnique(item.getID());
					break;
				case REMOVE:
					indicesToBeRemoved.add(item.getIndex());
					break;
				case REMOVE_ELEMENT:
					indicesToBeRemoved.add(indexOf(item.getID()));
					break;
				case COPY:
					copy(item.getIndex());
					break;
				case MOVE:
					move(item.getIndex(), item.getTargetIndex());
					break;
				default:
					throw new IllegalStateException("Unhandled EVAOperation: "
							+ item.getType());
			}
		}
		removeInBulk(indicesToBeRemoved);
	}

	/**
	 * Returns the group list or null if no group list exits
	 *
	 * @return the group list
	 */
	public GroupList getGroupList() {
		return groupList;
	}

	/** DOCUMENT ME! */
	public List<Group> getGroupOf(Integer id) {
		ArrayList<Group> resultGroups = new ArrayList<Group>(1);
		ArrayList<Integer> indices = indicesOf(id);

		if (indices.size() > 1)
			System.out.println("wu");

		for (Integer index : indices) {
			Group group = groupList.getGroupOfVAIndex(index);
			if (group != null)
				resultGroups.add(group);
		}

		return resultGroups;

	}

	/**
	 * Returns a List of all VAIDs of one group. The returned list is backed by
	 * this list, so non-structural changes in the returned list are reflected
	 * in this list, and vice-versa.
	 *
	 * @param groupIndex the index of the group in the groupList. Can be
	 *            retrieved using {@link Group#getGroupIndex()}
	 * @return ArrayList<Integer> containing all IDs that belong to this group,
	 *         or null if no groupList is available
	 */
	public List<Integer> getIDsOfGroup(int groupIndex) {

		if (groupList == null)
			return null;

		if (groupIndex >= groupList.size()) {
			throw new IllegalArgumentException(
					"groupIndex was bigger than the size of the groupList.");
		}
		Group group = groupList.get(groupIndex);

		List<Integer> vaIDs = virtualArrayList.subList(group.getStartIndex(),
				group.getStartIndex() + group.getSize());

		return vaIDs;
	}

	/**
	 * Sets group list in VA, used especially by affinity clusterer.
	 *
	 * @param groupList new group list
	 * @return true if operation executed correctly otherwise false
	 */
	public boolean setGroupList(GroupList groupList) {

		this.groupList = groupList;

		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public VirtualArray clone() {
		VirtualArray va = new VirtualArray(idType);

		// va.iUniqueID =
		// (GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		va.virtualArrayList = (ArrayList<Integer>) virtualArrayList.clone();
		if (groupList != null)
			va.setGroupList(groupList.clone());
		va.idMap = new IDMap(va.virtualArrayList);
		va.idMap.setDirty();
		return va;
	}

	/**
	 * Replace the internally created ID with the specified. Used when this VA
	 * replaces another VA
	 *
	 * @param id
	 */
	public void tableID(int id) {
		this.id = id;
	}

	@Override
	public String toString() {

		return "ID: " + getID() + ", size: " + virtualArrayList.size() + ", Nr. ouf Groups: "
				+ groupList.size();

	}

	/**
	 * Function which merges the clusters determined by the cut off value to
	 * group lists used for rendering the clusters assignments in
	 * {@link GLHierarchicalHeatMap}.
	 */
	public GroupList buildNewGroupList(ArrayList<ClusterNode> clusterNodes) {

		int sampleElementIndex = 0;

		for (ClusterNode node : clusterNodes) {
			Group temp = new Group(node.getNrLeaves(), sampleElementIndex, node);
			groupList.append(temp);
			sampleElementIndex += node.getNrLeaves();
		}
		return groupList;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public int getID() {
		return id;
	}
}
