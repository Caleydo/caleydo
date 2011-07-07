package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * A Virtual Array provides an association between a modifiable index in the virtual arrays and the static
 * indices in the storages and sets. It therefore allows the virtual modification (deleting, adding,
 * duplicating) of entries in the storages.
 * 
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public abstract class VirtualArray<ConcreteType extends VirtualArray<ConcreteType, VADelta, GroupType>, VADelta extends VirtualArrayDelta<?>, GroupType extends GroupList<GroupType, ConcreteType, VADelta>>
	extends AUniqueObject
	implements Iterable<Integer>, IUniqueObject, Cloneable {

	ArrayList<Integer> virtualArray;
	HashMap<Integer, ArrayList<Integer>> hashIDToIndex;
	boolean isHashIDToIndexDirty = true;

	GroupType groupList = null;

	/** Used to check whether elements to be removed are in descending order */
	int lastRemovedIndex = -1;

	private String vaType;

	public VirtualArray() {
		super(GeneralManager.get().getIDCreator().createID(EManagedObjectType.VIRTUAL_ARRAY));
		this.virtualArray = new ArrayList<Integer>();
	}

	/**
	 * Constructor, creates an empty Virtual Array
	 */
	public VirtualArray(String vaType) {
		super(GeneralManager.get().getIDCreator().createID(EManagedObjectType.VIRTUAL_ARRAY));
		this.vaType = vaType;
		this.virtualArray = new ArrayList<Integer>();
		// init();
	}

	public abstract ConcreteType getNewInstance();

	/**
	 * Constructor. Pass the length of the managed collection and a predefined array list of indices on the
	 * collection. This will serve as the starting point for the virtual array.
	 * 
	 * @param initialList
	 */
	public VirtualArray(String vaType, List<Integer> initialList) {
		super(GeneralManager.get().getIDCreator().createID(EManagedObjectType.VIRTUAL_ARRAY));
		this.vaType = vaType;
		this.virtualArray = new ArrayList<Integer>();
		virtualArray.addAll(initialList);
	}

	public String getVaType() {
		return vaType;
	}

	/**
	 * Returns an Iterator<Integer> of type VAIterator, which allows to iterate over the virtual array
	 */
	public VAIterator iterator() {
		return new VAIterator(this);
	}

	/**
	 * Returns the element at the specified index in the virtual array
	 * 
	 * @param iIndex
	 *            the index
	 * @return the element at the index
	 */
	public Integer get(int iIndex) {
		return virtualArray.get(iIndex);
	}

	/**
	 * Adds an element to the end of the list.
	 * 
	 * @param iNewElement
	 *            the index to the collection
	 * @exception IllegalArgumentException
	 *                if the value of the new element is larger than allowed. The maximum allowed value is the
	 *                length of the collection which is managed - 1
	 */
	public void append(Integer iNewElement) {
		isHashIDToIndexDirty = true;
		virtualArray.add(iNewElement);
	}

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
	public boolean appendUnique(Integer iNewElement) {
		isHashIDToIndexDirty = true;
		if (indexOf(iNewElement) != -1)
			return false;

		append(iNewElement);
		return true;

	}

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
	public void add(int iIndex, Integer iNewElement) {
		isHashIDToIndexDirty = true;
		virtualArray.add(iIndex, iNewElement);
	}

	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 * 
	 * @param iIndex
	 * @param iNewElement
	 * @throws CaleydoRuntimeException
	 *             if the value of the new element is larger than allowed. The maximum allowed value is the
	 *             length of the collection which is managed - 1
	 */
	public void set(int iIndex, Integer iNewElement) {
		isHashIDToIndexDirty = true;
		virtualArray.set(iIndex, iNewElement);
	}

	/**
	 * Copies the element at index iIndex to the next index. Shifts the element currently at that position (if
	 * any) and any subsequent elements to the right (adds one to their indices).
	 * 
	 * @param iIndex
	 *            the index of the element to be copied
	 */
	public void copy(int iIndex) {
		isHashIDToIndexDirty = true;
		virtualArray.add(iIndex + 1, virtualArray.get(iIndex));
	}

	/**
	 * Moves the element at the specified src index to the target index. The element formerly at iSrcIndex is
	 * at iTargetIndex after this operation. The rest of the elements can change the index.
	 * 
	 * @param iSrcIndex
	 *            the src index of the element
	 * @param iTargetIndex
	 *            the target index of the element
	 */
	public void move(int iSrcIndex, int iTargetIndex) {
		isHashIDToIndexDirty = true;
		Integer iElement = virtualArray.remove(iSrcIndex);
		virtualArray.add(iTargetIndex, iElement);
	}

	/**
	 * Moves the element at iIndex to the left
	 * 
	 * @param iIndex
	 *            the index of the element to be moved
	 */
	public void moveLeft(int iIndex) {
		isHashIDToIndexDirty = true;
		if (iIndex == 0)
			return;
		int iTemp = virtualArray.get(iIndex - 1);
		virtualArray.set(iIndex - 1, virtualArray.get(iIndex));
		virtualArray.set(iIndex, iTemp);
	}

	/**
	 * Moves the element at iIndex to the right
	 * 
	 * @param iIndex
	 *            the index of the element to be moved
	 */
	public void moveRight(int iIndex) {
		isHashIDToIndexDirty = true;
		if (iIndex == size() - 1)
			return;
		int iTemp = virtualArray.get(iIndex + 1);
		virtualArray.set(iIndex + 1, virtualArray.get(iIndex));
		virtualArray.set(iIndex, iTemp);
	}

	/**
	 * Removes the element at the specified index. Shifts any subsequent elements to the left (subtracts one
	 * from their indices).
	 * 
	 * @param iIndex
	 *            the index of the element to be removed
	 * @return the Element that was removed from the list
	 */
	public Integer remove(int iIndex) {
		isHashIDToIndexDirty = true;
		// if(groupList != null){
		// groupList.removeElementOfVA(iIndex);
		// }
		Integer id = virtualArray.remove(iIndex);

		return id;
	}

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
	public void removeByElement(int iElement) {
		ArrayList<Integer> indices = indicesOf(iElement);
		isHashIDToIndexDirty = true;
		if (indices.size() > 1) {
			System.out.println(indices);
			Collections.sort(indices);
			// for(Integer index : indices)
			// {
			// System.out.println("in va: " + virtualArray.get(index));
			// }
			// System.out.println(indices);
		}
		for (int count = indices.size() - 1; count >= 0; count--) {
			int index = indices.get(count);
			if (index < 0 || index > virtualArray.size()) {
//				Logger.log(new Status(Status.WARNING, "core", "When removing element in VA, id: " + iElement
//					+ " does not map to an index, produces: " + index));
				continue;
			}
			remove(index);
		}
	}

	/**
	 * Returns the size of the virtual array
	 * 
	 * @return the size
	 */
	public Integer size() {
		return virtualArray.size();
	}

	/**
	 * Reset the virtual array to contain no elements
	 */
	public void clear() {
		isHashIDToIndexDirty = true;
		virtualArray.clear();
	}

	/**
	 * <p>
	 * Returns the index of the first occurrence of the specified element in this list, or -1 if this list
	 * does not contain the element. More formally, returns the lowest index i such that (o==null ?
	 * get(i)==null : o.equals(get(i))), or -1 if there is no such index.
	 * </p>
	 * <p>
	 * The runtime complexity of this function depends on whether there has been a change to the VA recently.
	 * If, for example, an element has been removed prior to this call, the runtime is O(n). Otherwise the
	 * runtime is O(1).
	 * </p>
	 * 
	 * @param id
	 *            element to search for
	 * @return the index of the first occurrence of the specified element in this list, or -1 if this list
	 *         does not contain the element
	 */
	public int indexOf(Integer id) {

		return virtualArray.indexOf(id);

		// if (isHashIDToIndexDirty)
		// buildIDMap();
		//
		// ArrayList<Integer> results = hashIDToIndex.get(id);
		// if (results != null) {
		// if (results.size() > 1)
		// System.out.println("Ignored multi-mapping");
		// return results.get(0);
		// }
		// else
		// return -1;
	}

	private void buildIDMap() {
		if (hashIDToIndex == null)
			hashIDToIndex = new HashMap<Integer, ArrayList<Integer>>((int) (virtualArray.size() * 1.6));
		else
			hashIDToIndex.clear();

		for (int index = 0; index < virtualArray.size(); index++) {
			Integer id = virtualArray.get(index);
			ArrayList<Integer> indexList = hashIDToIndex.get(id);
			if (indexList == null)
				indexList = new ArrayList<Integer>(5);
			indexList.add(index);
			hashIDToIndex.put(id, indexList);
		}
		isHashIDToIndexDirty = false;
	}

	/**
	 * <p>
	 * Returns the indices of all occurrences of the specified element in this list, or an empty list if the
	 * list does not contain the element.
	 * </p>
	 * <p>
	 * The runtime complexity of this function depends on whether there has been a change to the VA recently.
	 * If, for example, an element has been removed prior to this call, the runtime is O(n). Otherwise the
	 * runtime is O(1).
	 * </p>
	 * 
	 * @param id
	 *            element to search for
	 * @return a list of all the indices of all occurrences of the element or an empty list if no such
	 *         elements exist
	 */
	public ArrayList<Integer> indicesOf(Integer id) {

		ArrayList<Integer> indices = new ArrayList<Integer>();
		indices.add(indexOf(id));
		return indices;
		//

		//
		// if (isHashIDToIndexDirty)
		// buildIDMap();
		// ArrayList<Integer> list = hashIDToIndex.get(id);
		// if (list != null)
		// return list;
		// else
		// return new ArrayList<Integer>(1);
	}

	/**
	 * Checks whether element is contained in the virtual array.
	 * 
	 * @param id
	 *            the id to be checked
	 * @return true if element occurs at least once, else false
	 */
	public boolean contains(Integer id) {
		if (isHashIDToIndexDirty)
			buildIDMap();
		return hashIDToIndex.get(id) == null ? false : true;
	}

	/**
	 * Checks the number of occurrences of an id. Returns 0 if it does not occur.
	 * 
	 * @param id
	 *            the id to be checked
	 * @return the number of occurrences (0 if none)
	 */
	public int occurencesOf(Integer id) {
		if (isHashIDToIndexDirty)
			buildIDMap();
		return hashIDToIndex.get(id).size();
	}

	/**
	 * Returns the array list which contains the list of storage indices. DO NOT EDIT THIS LIST
	 * 
	 * @return the list containing the storage indices
	 */
	public ArrayList<Integer> getIndexList() {
		return virtualArray;
	}

	/**
	 * Applies the operations specified in the delta to the virtual array
	 * 
	 * @param delta
	 */
	public void setDelta(VADelta delta) {
		for (VADeltaItem item : delta) {
			switch (item.getType()) {
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
					if (iIndex < lastRemovedIndex)
						throw new IllegalStateException(
							"Index of remove operation was smaller than previously used index. This is likely not intentional. Take care to remove indices from back to front.");
					lastRemovedIndex = iIndex;
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
					throw new IllegalStateException("Unhandled EVAOperation: " + item.getType());
			}
		}
		lastRemovedIndex = -1;
	}

	/**
	 * Returns the group list. If no group list exits null will be returned.
	 * 
	 * @param
	 * @return the group list
	 */
	public GroupType getGroupList() {
		return groupList;
	}

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
	 * Returns an ArrayList with indexes of one group (genes/experiments) determined by iGroupIdx.
	 * 
	 * @param groupID
	 *            index of group in groupList
	 * @return ArrayList<Integer> containing all indexes of one group determined by iGroupIdx. Null will be
	 *         returned in case of groupList is null.
	 */
	public ArrayList<Integer> getIDsOfGroup(int groupID) {

		if (groupList == null)
			return null;

		ArrayList<Integer> alGeneIds = new ArrayList<Integer>();

		Group group = null;
		int iCounter = 0;
		int offset = 0;

		// throws exception
		// CLEMENS HAS CHANGED THIS. FIXME ALEX
		/*
		 * for (int i = 0; i < groupID; i++) { iOffset += groupList.get(i).getSize(); }
		 */

		for (Group igroup : groupList) {
			if (igroup.getID() < groupID) {
				offset += igroup.getSize();
			}
			else if (igroup.getID() == groupID) {
				group = igroup;
				break;
			}
		}

		for (int i = offset; i < group.getSize() + offset; i++) {
			alGeneIds.add(iCounter, get(i));
			iCounter++;
		}

		return alGeneIds;
	}

	/**
	 * Sets group list in VA, used especially by affinity clusterer.
	 * 
	 * @param groupList
	 *            new group list
	 * @return true if operation executed correctly otherwise false
	 */
	public boolean setGroupList(GroupType groupList) {

		this.groupList = groupList;

		return true;
	}

	@SuppressWarnings("unchecked")
	public ConcreteType clone() {
		ConcreteType va;
		try {
			va = (ConcreteType) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Clone not supportet: " + e.getMessage());
		}
		// va.iUniqueID = (GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		va.virtualArray = (ArrayList<Integer>) virtualArray.clone();
		if (groupList != null)
			va.setGroupList(groupList.clone());
		va.lastRemovedIndex = lastRemovedIndex;
		va.isHashIDToIndexDirty = true;
		return va;
	}

	/**
	 * Replace the internally created ID with the specified. Used when this VA replaces another VA
	 * 
	 * @param uniqueID
	 */
	public void setID(int iUniqueID) {
		this.uniqueID = iUniqueID;
	}

	@XmlElementWrapper
	public ArrayList<Integer> getVirtualArray() {
		return virtualArray;
	}

	public void setVirtualArray(ArrayList<Integer> virtualArray) {
		this.virtualArray = virtualArray;
		isHashIDToIndexDirty = true;
	}

	public int getLastRemovedIndex() {
		return lastRemovedIndex;
	}

	// public void setLastRemovedIndex(int lastRemovedIndex) {
	// this.lastRemovedIndex = lastRemovedIndex;
	// }

	public void setVaType(String vaType) {
		this.vaType = vaType;
	}

	@Override
	public String toString() {

		boolean hasGrouping = false;
		if (groupList != null)
			hasGrouping = true;

		return "ID: " + getID() + ", Type: " + vaType + ", size: " + virtualArray.size() + ", hasGrouping: "
			+ hasGrouping;
	}

	/**
	 * Function which merges the clusters determined by the cut off value to group lists used for rendering
	 * the clusters assignments in {@link GLHierarchicalHeatMap}.
	 */
	protected GroupList<GroupType, ConcreteType, VADelta> buildNewGroupList(
		GroupList<GroupType, ConcreteType, VADelta> groupList, ArrayList<ClusterNode> clusterNodes) {

		int sampleElementIndex = 0;

		for (ClusterNode node : clusterNodes) {

			Group temp = new Group(node.getNrLeaves(), sampleElementIndex, node);
			groupList.append(temp);
			sampleElementIndex += node.getNrLeaves();
		}
		return groupList;

	}
}
