package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.clusterer.ClusterNode;

/**
 * Implementation of IVirtualArray
 * 
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public abstract class VirtualArray<ConcreteType extends VirtualArray<ConcreteType, VAType, VADelta, GroupType>, VAType extends IVAType, VADelta extends VirtualArrayDelta<?, VAType>, GroupType extends GroupList<GroupType, ConcreteType, VADelta>>
	extends AUniqueObject
	implements IVirtualArray<ConcreteType, VAType, VADelta, GroupType> {

	ArrayList<Integer> virtualArray;
	HashMap<Integer, ArrayList<Integer>> hashIDToIndex;
	boolean isHashIDToIndexDirty = true;

	GroupType groupList = null;

	/** Used to check whether elements to be removed are in descending order */
	int lastRemovedIndex = -1;

	VAType vaType;

	public VirtualArray() {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		this.virtualArray = new ArrayList<Integer>();
	}

	/**
	 * Constructor, creates an empty Virtual Array
	 */
	public VirtualArray(VAType vaType) {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
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
	public VirtualArray(VAType vaType, List<Integer> initialList) {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.VIRTUAL_ARRAY));
		this.vaType = vaType;
		this.virtualArray = new ArrayList<Integer>();
		virtualArray.addAll(initialList);
	}

	@Override
	public VAType getVAType() {
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
		isHashIDToIndexDirty = true;
		virtualArray.add(iNewElement);
	}

	@Override
	public boolean appendUnique(Integer iNewElement) {
		isHashIDToIndexDirty = true;
		if (indexOf(iNewElement) != -1)
			return false;

		append(iNewElement);
		return true;

	}

	@Override
	public void add(int iIndex, Integer iNewElement) {
		isHashIDToIndexDirty = true;
		virtualArray.add(iIndex, iNewElement);
	}

	@Override
	public void set(int iIndex, Integer iNewElement) {
		isHashIDToIndexDirty = true;
		virtualArray.set(iIndex, iNewElement);
	}

	@Override
	public void copy(int iIndex) {
		isHashIDToIndexDirty = true;
		virtualArray.add(iIndex + 1, virtualArray.get(iIndex));
	}

	@Override
	public void move(int iSrcIndex, int iTargetIndex) {
		isHashIDToIndexDirty = true;
		Integer iElement = virtualArray.remove(iSrcIndex);
		virtualArray.add(iTargetIndex, iElement);
	}

	@Override
	public void moveLeft(int iIndex) {
		isHashIDToIndexDirty = true;
		if (iIndex == 0)
			return;
		int iTemp = virtualArray.get(iIndex - 1);
		virtualArray.set(iIndex - 1, virtualArray.get(iIndex));
		virtualArray.set(iIndex, iTemp);
	}

	@Override
	public void moveRight(int iIndex) {
		isHashIDToIndexDirty = true;
		if (iIndex == size() - 1)
			return;
		int iTemp = virtualArray.get(iIndex + 1);
		virtualArray.set(iIndex + 1, virtualArray.get(iIndex));
		virtualArray.set(iIndex, iTemp);
	}

	@Override
	public Integer remove(int iIndex) {
		isHashIDToIndexDirty = true;
		// if(groupList != null){
		// groupList.removeElementOfVA(iIndex);
		// }

		return virtualArray.remove(iIndex);
	}

	@Override
	public void removeByElement(int iElement) {
		isHashIDToIndexDirty = true;
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

	// @Override
	// public void reset() {
	// init();
	// }

	@Override
	public void clear() {
		isHashIDToIndexDirty = true;
		virtualArray.clear();
	}

	@Override
	public int indexOf(int iElement) {
		if (isHashIDToIndexDirty)
			buildIDMap();
		// System.out.println("Costly indexof operation on a va of size: " + size());
		ArrayList<Integer> results = hashIDToIndex.get(iElement);
		if (results != null)
			return results.get(0);
		else
			return -1;
	}

	private void buildIDMap() {
		isHashIDToIndexDirty = false;
		if (hashIDToIndex == null)
			hashIDToIndex = new HashMap<Integer, ArrayList<Integer>>((int) (virtualArray.size() * 1.5));
		else
			hashIDToIndex.clear();

		int indexCount = 0;
		for (Integer id : virtualArray) {
			ArrayList<Integer> indexList = hashIDToIndex.get(id);
			if (indexList == null)
				indexList = new ArrayList<Integer>(3);
			indexList.add(indexCount++);

			hashIDToIndex.put(id, indexList);
		}
	}

	@Override
	public ArrayList<Integer> indicesOf(int iElement) {
		if (isHashIDToIndexDirty)
			buildIDMap();
		ArrayList<Integer> list = hashIDToIndex.get(iElement);
		if (list != null)
			return list;
		else
			return new ArrayList<Integer>(1);

	}

	@Override
	public ArrayList<Integer> getIndexList() {
		return virtualArray;
	}

	@Override
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
	// private void init() {
	// virtualArray = new ArrayList<Integer>();
	//
	// for (int iCount = 0; iCount < length; iCount++) {
	// virtualArray.add(iCount);
	// }
	// }

	@Override
	public GroupType getGroupList() {
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

	// @Override
	// public GroupType newGroupList() {
	//
	// this.groupList = new GroupList(this.size());
	//
	// return groupList;
	// }

	@Override
	public boolean setGroupList(GroupType groupList) {

		this.groupList = groupList;

		return true;
	}

	@Override
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

	public int getLastRemovedIndex() {
		return lastRemovedIndex;
	}

	public void setLastRemovedIndex(int lastRemovedIndex) {
		this.lastRemovedIndex = lastRemovedIndex;
	}

	public VAType getVaType() {
		return vaType;
	}

	public void setVaType(VAType vaType) {
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

	@Override
	public int[] getArray() {

		int[] intArray = new int[virtualArray.size()];
		for (int i = 0; i < virtualArray.size(); i++) {
			intArray[i] = virtualArray.get(i);
		}

		return intArray;
	}

	/**
	 * Function which merges the clusters determined by the cut off value to group lists used for rendering
	 * the clusters assignments in {@link GLHierarchicalHeatMap}.
	 */
	protected GroupList<GroupType, ConcreteType, VADelta> buildNewGroupList(
		GroupList<GroupType, ConcreteType, VADelta> groupList, ArrayList<ClusterNode> iAlClusterNodes) {

		// if (iAlClusterNodes.size() < 1) {
		//
		// Group temp = new Group(rootNode.getNrLeaves(), false, 0, SelectionType.NORMAL, rootNode);
		// groupList.append(temp);
		// triggerGroupListEvent();
		// return;
		// }
		//
		// if (bRenderContentTree) {
		// groupList = (GroupType) new ContentGroupList();
		// }
		// else {
		// groupList = (GroupType) new StorageGroupList();
		// }
		//
		// bEnableDepthCheck = true;

		int cnt = 0;
		int iExample = 0;

		// IVirtualArray<?, ?, ?, ?> currentVA = null;
		//
		// if (bRenderContentTree) {
		// currentVA = contentVA;
		// }
		// else {
		// currentVA = storageVA;
		// }

		for (ClusterNode iter : iAlClusterNodes) {
			// Group temp = new Group(iter.getNrElements(), false,
			// currentVA.get(iExample),
			// iter.getRepresentativeElement(), SelectionType.NORMAL, iter);
			Group temp =
				new Group(iter.getNrLeaves(), false, this.indexOf(iExample), SelectionType.NORMAL, iter);
			groupList.append(temp);
			cnt++;
			iExample += iter.getNrLeaves();
		}
		return groupList;
		// triggerGroupListEvent();
	}
}
