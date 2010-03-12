package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.util.clusterer.ClusterHelper;

@XmlType
public abstract class GroupList<ConcreteType extends IGroupList<ConcreteType, VA, VADelta>, VA extends VirtualArray<?, ?, ?, ?>, VADelta extends VirtualArrayDelta<?, ?>>
	implements IGroupList<ConcreteType, VA, VADelta> {

	private ArrayList<Group> groups;

	/**
	 * default no-arg constructor to create a group list with no contained groups
	 */
	public GroupList() {
		this.groups = new ArrayList<Group>();
	}

	@Override
	public void add(int index, Group newElement) {
		groups.add(index, newElement);
	}

	@Override
	public void append(Group newElement) {
		groups.add(newElement);
	}

	@Override
	public boolean appendUnique(Group newElement) {
		if (indexOf(newElement) != -1)
			return false;

		append(newElement);
		return true;
	}

	@Override
	public void clear() {
		groups.clear();

	}

	@Override
	public boolean containsElement(Group element) {
		for (Group compareElement : groups) {
			if (compareElement == element) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void copy(int index) {
		groups.add(index + 1, groups.get(index));

	}

	@Override
	public Group get(int index) {
		return groups.get(index);
	}

	@Override
	public int indexOf(Group element) {
		return groups.indexOf(element);
	}

	@Override
	public GroupIterator iterator() {
		return new GroupIterator(this);
	}

	// @Override
	// public void move(int srcIndex, int targetIndex) {
	// Group element = iAlGroup.remove(srcIndex);
	// iAlGroup.add(targetIndex, element);
	//
	// System.out.println("Reordering of indexes in the corresponding VA not implemented yet!");
	// }
	//
	// @Override
	// public void moveLeft(int index) {
	// if (index == 0)
	// return;
	// Group temp = iAlGroup.get(index - 1);
	// iAlGroup.set(index - 1, iAlGroup.get(index));
	// iAlGroup.set(index, temp);
	//
	// System.out.println("Reordering of indexes in the corresponding VA not implemented yet!");
	// }
	//
	// @Override
	// public void moveRight(int index) {
	// if (index == size() - 1)
	// return;
	// Group temp = iAlGroup.get(index + 1);
	// iAlGroup.set(index + 1, iAlGroup.get(index));
	// iAlGroup.set(index, temp);
	//
	// System.out.println("Reordering of indexes in the corresponding VA not implemented yet!");
	// }

	@Override
	public Group remove(int index) {
		return groups.remove(index);
	}

	@Override
	public void removeByElement(Group element) {
		Iterator<Group> iter = groups.iterator();
		while (iter.hasNext()) {
			if (iter.next() == element) {
				iter.remove();
			}
		}
	}

	/**
	 * Initialize group list
	 */
	private void init() {
		// Group initialGroup = new Group(iAlVirtualArray.size());
		Group initialGroup = new Group(0, false, 0, SelectionType.NORMAL);
		this.groups = new ArrayList<Group>();
		groups.add(initialGroup);
	}

	@Override
	public void reset() {
		init();
	}

	@Override
	public void set(int index, Group newElement) {
		groups.set(index, newElement);
	}

	@Override
	public void setDelta(VADelta delta) {
		try {
			throw new Exception("Not implemented yet!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Integer size() {
		return groups.size();
	}

	@Override
	public boolean interchange(VA virtualArray, int index1, int index2) {

		int iFirstIdxG1 = 0;
		int iLastIdxG1 = 0;
		int iFirstIdxG2 = 0;
		int iLastIdxG2 = 0;
		int iNrElemG1 = groups.get(index1).getNrElements();
		int iNrElemG2 = groups.get(index2).getNrElements();

		ArrayList<Integer> altemp1 = new ArrayList<Integer>();
		ArrayList<Integer> altemp2 = new ArrayList<Integer>();

		int iCnt = 0;
		for (Group iter : groups) {
			iLastIdxG1 += iter.getNrElements();

			if (iCnt == index1)
				break;
			iCnt++;
		}
		iFirstIdxG1 = iLastIdxG1 - groups.get(index1).getNrElements();

		iCnt = 0;
		for (Group iter : groups) {
			iLastIdxG2 += iter.getNrElements();

			if (iCnt == index2)
				break;
			iCnt++;
		}
		iFirstIdxG2 = iLastIdxG2 - groups.get(index2).getNrElements();

		for (int i = 0; i < iNrElemG2; i++) {

			altemp2.add(virtualArray.get(iFirstIdxG2));
			virtualArray.remove(iFirstIdxG2);
		}

		for (int i = 0; i < iNrElemG1; i++) {

			altemp1.add(virtualArray.get(iFirstIdxG1));
			virtualArray.remove(iFirstIdxG1);
		}

		for (int i = 0; i < iNrElemG2; i++) {
			virtualArray.add(iFirstIdxG1 + i, altemp2.get(i));
		}

		for (int i = 0; i < iNrElemG1; i++) {
			virtualArray.add(iFirstIdxG2 - iNrElemG1 + iNrElemG2 + i, altemp1.get(i));
		}

		Group temp = groups.get(index1);
		groups.set(index1, groups.get(index2));
		groups.set(index2, temp);
		groups.get(index1).setSelectionType(SelectionType.NORMAL);
		groups.get(index2).setSelectionType(SelectionType.NORMAL);

		return true;
	}

	@Override
	public boolean merge(VA virtualArray, int index1, int index2) {

		// int iFirstIdxG1 = 0;
		int iLastIdxG1 = 0;
		int iFirstIdxG2 = 0;
		int iLastIdxG2 = 0;
		int iNrElemG1 = groups.get(index1).getNrElements();
		int iNrElemG2 = groups.get(index2).getNrElements();

		ArrayList<Integer> altemp = new ArrayList<Integer>();

		int iCnt = 0;
		for (Group iter : groups) {
			iLastIdxG1 += iter.getNrElements();

			if (iCnt == index1)
				break;
			iCnt++;
		}
		// iFirstIdxG1 = iLastIdxG1 - iAlGroup.get(index1).getNrElements();

		iCnt = 0;
		for (Group iter : groups) {
			iLastIdxG2 += iter.getNrElements();

			if (iCnt == index2)
				break;
			iCnt++;
		}
		iFirstIdxG2 = iLastIdxG2 - groups.get(index2).getNrElements();

		for (int i = 0; i < iNrElemG2; i++) {

			altemp.add(virtualArray.get(iFirstIdxG2));
			virtualArray.remove(iFirstIdxG2);
		}

		for (int i = 0; i < iNrElemG2; i++) {
			virtualArray.add(iLastIdxG1 + i, altemp.get(i));
		}

		groups.remove(index2);
		groups.get(index1).setNrElements(iNrElemG1 + iNrElemG2);
		groups.get(index1).setSelectionType(SelectionType.SELECTION);

		return true;
	}

	@Override
	public boolean split(int index, int idx1, int idx2) {

		int iFirstIdx = 0;
		int iLastIdx = 0;

		int iCnt = 0;

		if (idx2 < idx1) {
			int temp = idx1;
			idx1 = idx2;
			idx2 = temp;
		}

		for (Group iter : groups) {
			iLastIdx += iter.getNrElements();

			if (iCnt == index)
				break;
			iCnt++;
		}

		iFirstIdx = iLastIdx - groups.get(index).getNrElements();

		if (idx1 < iFirstIdx || idx2 > iLastIdx)
			return false;

		// 1 new group
		if (idx1 == iFirstIdx) {

			int iNrElements = idx2 - idx1;
			Group newGroup = new Group(iNrElements, false, 0, SelectionType.NORMAL);

			groups.get(index).setCollapsed(false);
			groups.get(index).setSelectionType(SelectionType.NORMAL);
			groups.get(index).setNrElements(groups.get(index).getNrElements() - iNrElements);
			groups.add(index, newGroup);

			return true;
		}

		// 1 new group
		if (idx2 == iLastIdx) {
			int iNrElements = idx2 - idx1;
			Group newGroup = new Group(iNrElements, false, 0, SelectionType.NORMAL);

			groups.get(index).setCollapsed(false);
			groups.get(index).setSelectionType(SelectionType.NORMAL);
			groups.get(index).setNrElements(groups.get(index).getNrElements() - iNrElements);
			groups.add(index + 1, newGroup);

			return true;
		}

		// 2 new groups
		int iNrElements2 = idx2 - idx1;
		Group newGroup2 = new Group(iNrElements2, false, 0, SelectionType.NORMAL);
		int iNrElements3 = iLastIdx - idx2;
		Group newGroup3 = new Group(iNrElements3, false, 0, SelectionType.NORMAL);

		groups.get(index).setCollapsed(false);
		groups.get(index).setSelectionType(SelectionType.NORMAL);
		groups.get(index).setNrElements(idx1 - iFirstIdx);
		groups.add(index + 1, newGroup2);
		groups.add(index + 2, newGroup3);

		return true;
	}

	@Override
	public boolean move(VA virtualArray, int srcIndex, int targetIndex) {

		int icurrentIdx = -1;

		if (srcIndex == targetIndex) {
			// nothing to do
			return false;
		}
		else if (srcIndex < targetIndex) {

			icurrentIdx = srcIndex;
			while (icurrentIdx < targetIndex) {
				interchange(virtualArray, icurrentIdx, icurrentIdx + 1);
				icurrentIdx++;
			}
		}
		else {

			icurrentIdx = srcIndex;
			while (icurrentIdx > targetIndex) {
				interchange(virtualArray, icurrentIdx - 1, icurrentIdx);
				icurrentIdx--;
			}
		}
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ConcreteType clone() {
		GroupList groupList;
		try {
			groupList = (GroupList) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Clone not supportet: " + e.getMessage());
		}
		groupList.groups = (ArrayList<Group>) groups.clone();
		return (ConcreteType) groupList;
	}

	public ArrayList<Group> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<Group> groups) {
		this.groups = groups;
	}

	public void removeElementOfVA(int indexOfElement) {

		int iOffset = 0;
		int iGroupIdx = 0;
		for (int index = 0; index < groups.size(); index++) {
			iOffset += groups.get(index).getNrElements();
			if (iOffset > indexOfElement)
				break;
			iGroupIdx++;
		}

		groups.get(iGroupIdx).setNrElements(groups.get(iGroupIdx).getNrElements() - 1);
		if (groups.get(iGroupIdx).getNrElements() == 0)
			groups.remove(iGroupIdx);

	}

	public ArrayList<Float> determineRepresentativeElement(ISet set, ContentVirtualArray contentVA,
		StorageVirtualArray storageVA, int iGroupNr, boolean bGeneGroup) {

		ArrayList<Float> representative = new ArrayList<Float>();

		int iNrElementsInGroup = groups.get(iGroupNr).getNrElements();
		int iOffset = 0;
		for (int index = 0; index < iGroupNr; index++) {
			iOffset += groups.get(index).getNrElements();
		}

		float[] fArExpressionValues = null;

		if (bGeneGroup) {
			for (Integer iStorageIndex : storageVA) {
				fArExpressionValues = new float[iNrElementsInGroup];
				for (int index = 0; index < iNrElementsInGroup; index++) {
					fArExpressionValues[index] +=
						set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
							contentVA.get(iOffset + index));
				}
				representative.add(ClusterHelper.arithmeticMean(fArExpressionValues));
			}
		}
		else {
			for (Integer iContentIndex : contentVA) {
				fArExpressionValues = new float[iNrElementsInGroup];
				for (int index = 0; index < iNrElementsInGroup; index++) {
					fArExpressionValues[index] +=
						set.get(storageVA.get(iOffset + index)).getFloat(EDataRepresentation.NORMALIZED,
							iContentIndex);
				}
				representative.add(ClusterHelper.arithmeticMean(fArExpressionValues));
			}
		}

		return representative;
	}
	
	public Group getGroupOfVAIndex(int index)
	{
		int from = 0;
		int to = 0;
		for(Group group : groups)
		{
			to += group.getNrElements();
			if(index >= from && index <= to)
				return group;
			from = to;
		}
		return null;
		
	}
	
	@Override
	public String toString() {
		return groups.toString();
	}
}
