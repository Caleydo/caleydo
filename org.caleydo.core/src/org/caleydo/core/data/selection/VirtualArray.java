package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;

/**
 * Implementation of IVirtualArray
 * 
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class VirtualArray
	extends AUniqueObject
	implements IVirtualArray {

	ArrayList<Integer> virtualArray;

	GroupList groupList = null;

	int length;

	/** Used to check whether elements to be removed are in descending order */
	int lastRemovedIndex = -1;

	EVAType vaType;

	public VirtualArray() {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
	}

	/**
	 * Constructor. Pass the length of the managed collection
	 * 
	 * @param iLength
	 *            the length of the managed collection
	 */
	public VirtualArray(EVAType vaType, int iLength) {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		this.vaType = vaType;
		this.length = iLength;
		init();
	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined array list of indices on the
	 * collection. This will serve as the starting point for the virtual array.
	 * 
	 * @param iLength
	 * @param iLVirtualArray
	 */
	public VirtualArray(EVAType vaType, int iLength, List<Integer> iLVirtualArray) {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		this.vaType = vaType;
		this.length = iLength;
		this.virtualArray = new ArrayList<Integer>();
		virtualArray.addAll(iLVirtualArray);
	}

	@Override
	public EVAType getVAType() {
		return vaType;
	}

	@Override
	public VAIterator iterator() {
		return new VAIterator(this);
	}

	@Override
	public Integer get(int iIndex) {
		return virtualArray.get(iIndex);
	}

	@Override
	public void append(Integer iNewElement) {
		if (iNewElement < length) {
			virtualArray.add(iNewElement);
		}
		else
			throw new IllegalArgumentException("Tried to add an element (" + iNewElement
				+ ") to a virtual array that is not within the " + "allowed range  (" + length
				+ "), which is determined by the length of the collection"
				+ "on which the virtual array is applied");
	}

	@Override
	public boolean appendUnique(Integer iNewElement) {
		if (indexOf(iNewElement) != -1)
			return false;

		append(iNewElement);
		return true;

	}

	@Override
	public void add(int iIndex, Integer iNewElement) {
		if (iNewElement < length) {
			virtualArray.add(iIndex, iNewElement);
		}
		else
			throw new IllegalArgumentException(
				"Tried to add a element to a virtual array that is not within the "
					+ "allowed range (which is determined by the length of the collection "
					+ "on which the virtual array is applied");
	}

	@Override
	public void set(int iIndex, Integer iNewElement) {
		if (iNewElement < length) {
			virtualArray.set(iIndex, iNewElement);
		}
		else
			throw new IllegalArgumentException(
				"Tried to add a element to a virtual array that is not within the "
					+ "allowed range (which is determined by the length of the collection "
					+ "on which the virtual array is applied");
	}

	@Override
	public void copy(int iIndex) {
		virtualArray.add(iIndex + 1, virtualArray.get(iIndex));
	}

	@Override
	public void move(int iSrcIndex, int iTargetIndex) {
		Integer iElement = virtualArray.remove(iSrcIndex);
		virtualArray.add(iTargetIndex, iElement);
	}

	@Override
	public void moveLeft(int iIndex) {
		if (iIndex == 0)
			return;
		int iTemp = virtualArray.get(iIndex - 1);
		virtualArray.set(iIndex - 1, virtualArray.get(iIndex));
		virtualArray.set(iIndex, iTemp);
	}

	@Override
	public void moveRight(int iIndex) {
		if (iIndex == size() - 1)
			return;
		int iTemp = virtualArray.get(iIndex + 1);
		virtualArray.set(iIndex + 1, virtualArray.get(iIndex));
		virtualArray.set(iIndex, iTemp);
	}

	@Override
	public Integer remove(int iIndex) {

		// if(groupList != null){
		// groupList.removeElementOfVA(iIndex);
		// }

		return virtualArray.remove(iIndex);
	}

	@Override
	public void removeByElement(int iElement) {

		// if(groupList != null){
		// groupList.removeElementOfVA(virtualArray.indexOf(iElement));
		// }

		Iterator<Integer> iter = virtualArray.iterator();
		while (iter.hasNext()) {
			if (iter.next() == iElement) {
				iter.remove();
			}
		}
	}

	@Override
	public Integer size() {
		return virtualArray.size();
	}

	@Override
	public void reset() {
		init();
	}

	@Override
	public void clear() {
		virtualArray.clear();
	}

	@Override
	public int indexOf(int iElement) {
		// System.out.println("Costly indexof operation on a va of size: " + size());
		return virtualArray.indexOf(iElement);
	}

	@Override
	public ArrayList<Integer> indicesOf(int iElement) {
		ArrayList<Integer> alIndices = new ArrayList<Integer>();
		int iCount = 0;
		for (Integer iCompareElement : virtualArray) {
			if (iCompareElement == iElement) {
				alIndices.add(iCount);
			}
			iCount++;
		}

		return alIndices;
	}

	@Override
	public ArrayList<Integer> getIndexList() {
		return virtualArray;
	}

	@Override
	public void setDelta(IVirtualArrayDelta delta) {
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

	@Override
	public int containsElement(int iElement) {
		int iCount = 0;
		for (Integer iCompareElement : virtualArray) {
			if (iCompareElement == iElement) {
				iCount++;
			}
		}
		return iCount;
	}

	/**
	 * Initialize Virtual Array
	 */
	private void init() {
		virtualArray = new ArrayList<Integer>(length);

		for (int iCount = 0; iCount < length; iCount++) {
			virtualArray.add(iCount);
		}
	}

	@Override
	public GroupList getGroupList() {
		return groupList;
	}

	@Override
	public ArrayList<Integer> getGeneIdsOfGroup(int iGroupIdx) {

		if (groupList == null)
			return null;

		ArrayList<Integer> alGeneIds = new ArrayList<Integer>();

		int iNrElements = size();
		int iCounter = 0;
		int iOffset = 0;

		for (int i = 0; i < iGroupIdx; i++) {
			iOffset += groupList.get(i).getNrElements();
		}

		for (int i = iOffset; i < iNrElements; i++) {

			alGeneIds.add(iCounter, get(i));

			iCounter++;

			if (groupList.get(iGroupIdx).getNrElements() == iCounter)
				break;
		}

		return alGeneIds;
	}

	@Override
	public GroupList newGroupList() {

		this.groupList = new GroupList(this.size());

		return groupList;
	}

	@Override
	public boolean setGroupList(GroupList groupList) {

		this.groupList = groupList;

		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IVirtualArray clone() {
		VirtualArray va;
		try {
			va = (VirtualArray) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Clone not supportet: " + e.getMessage());
		}
		// va.iUniqueID = (GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		va.length = length;
		va.virtualArray = (ArrayList<Integer>) virtualArray.clone();
		if (groupList != null)
			va.setGroupList((GroupList) groupList.clone());
		va.lastRemovedIndex = lastRemovedIndex;
		return va;
	}

	@Override
	public void setID(int iUniqueID) {
		this.iUniqueID = iUniqueID;
	}

	@XmlElementWrapper
	public ArrayList<Integer> getVirtualArray() {
		return virtualArray;
	}

	public void setVirtualArray(ArrayList<Integer> virtualArray) {
		this.virtualArray = virtualArray;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLastRemovedIndex() {
		return lastRemovedIndex;
	}

	public void setLastRemovedIndex(int lastRemovedIndex) {
		this.lastRemovedIndex = lastRemovedIndex;
	}

	public EVAType getVaType() {
		return vaType;
	}

	public void setVaType(EVAType vaType) {
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
}
