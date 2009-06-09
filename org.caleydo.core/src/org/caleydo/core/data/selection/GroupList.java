package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;

public class GroupList
	implements IGroupList {

	private ArrayList<Group> iAlGroup;

	public GroupList(int iNrElements) {
		// Group initialGroup = new Group(iNrElements);
		this.iAlGroup = new ArrayList<Group>();
		// iAlGroup.add(initialGroup);
	}

	@Override
	public void add(int index, Group newElement) {
		iAlGroup.add(index, newElement);
	}

	@Override
	public void append(Group newElement) {
		iAlGroup.add(newElement);
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
		iAlGroup.clear();

	}

	@Override
	public boolean containsElement(Group element) {
		for (Group compareElement : iAlGroup) {
			if (compareElement == element) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void copy(int index) {
		iAlGroup.add(index + 1, iAlGroup.get(index));

	}

	@Override
	public Group get(int index) {
		return iAlGroup.get(index);
	}

	@Override
	public int indexOf(Group element) {
		return iAlGroup.indexOf(element);
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
		return iAlGroup.remove(index);
	}

	@Override
	public void removeByElement(Group element) {
		Iterator<Group> iter = iAlGroup.iterator();
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
		Group initialGroup = new Group(0);
		this.iAlGroup = new ArrayList<Group>();
		iAlGroup.add(initialGroup);
	}

	@Override
	public void reset() {
		init();
	}

	@Override
	public void set(int index, Group newElement) {
		iAlGroup.set(index, newElement);
	}

	@Override
	public void setDelta(IVirtualArrayDelta delta) {
		try {
			throw new Exception("Not implemented yet!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Integer size() {
		return iAlGroup.size();
	}

	@Override
	public boolean interchange(IVirtualArray virtualArray, int index1, int index2) {

		int iFirstIdxG1 = 0;
		int iLastIdxG1 = 0;
		int iFirstIdxG2 = 0;
		int iLastIdxG2 = 0;
		int iNrElemG1 = iAlGroup.get(index1).getNrElements();
		int iNrElemG2 = iAlGroup.get(index2).getNrElements();

		ArrayList<Integer> altemp1 = new ArrayList<Integer>();
		ArrayList<Integer> altemp2 = new ArrayList<Integer>();

		int iCnt = 0;
		for (Group iter : iAlGroup) {
			iLastIdxG1 += iter.getNrElements();

			if (iCnt == index1)
				break;
			iCnt++;
		}
		iFirstIdxG1 = iLastIdxG1 - iAlGroup.get(index1).getNrElements();

		iCnt = 0;
		for (Group iter : iAlGroup) {
			iLastIdxG2 += iter.getNrElements();

			if (iCnt == index2)
				break;
			iCnt++;
		}
		iFirstIdxG2 = iLastIdxG2 - iAlGroup.get(index2).getNrElements();

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

		Group temp = iAlGroup.get(index1);
		iAlGroup.set(index1, iAlGroup.get(index2));
		iAlGroup.set(index2, temp);
		iAlGroup.get(index1).setSelectionType(ESelectionType.NORMAL);
		iAlGroup.get(index2).setSelectionType(ESelectionType.NORMAL);

		return true;
	}

	@Override
	public boolean merge(IVirtualArray virtualArray, int index1, int index2) {

		// int iFirstIdxG1 = 0;
		int iLastIdxG1 = 0;
		int iFirstIdxG2 = 0;
		int iLastIdxG2 = 0;
		int iNrElemG1 = iAlGroup.get(index1).getNrElements();
		int iNrElemG2 = iAlGroup.get(index2).getNrElements();

		ArrayList<Integer> altemp = new ArrayList<Integer>();

		int iCnt = 0;
		for (Group iter : iAlGroup) {
			iLastIdxG1 += iter.getNrElements();

			if (iCnt == index1)
				break;
			iCnt++;
		}
		// iFirstIdxG1 = iLastIdxG1 - iAlGroup.get(index1).getNrElements();

		iCnt = 0;
		for (Group iter : iAlGroup) {
			iLastIdxG2 += iter.getNrElements();

			if (iCnt == index2)
				break;
			iCnt++;
		}
		iFirstIdxG2 = iLastIdxG2 - iAlGroup.get(index2).getNrElements();

		for (int i = 0; i < iNrElemG2; i++) {

			altemp.add(virtualArray.get(iFirstIdxG2));
			virtualArray.remove(iFirstIdxG2);
		}

		for (int i = 0; i < iNrElemG2; i++) {
			virtualArray.add(iLastIdxG1 + i, altemp.get(i));
		}

		iAlGroup.remove(index2);
		iAlGroup.get(index1).setNrElements(iNrElemG1 + iNrElemG2);
		iAlGroup.get(index1).setSelectionType(ESelectionType.SELECTION);

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

		for (Group iter : iAlGroup) {
			iLastIdx += iter.getNrElements();

			if (iCnt == index)
				break;
			iCnt++;
		}

		iFirstIdx = iLastIdx - iAlGroup.get(index).getNrElements();

		if (idx1 < iFirstIdx || idx2 > iLastIdx)
			return false;

		// 1 new group
		if (idx1 == iFirstIdx) {

			int iNrElements = idx2 - idx1;
			Group newGroup = new Group(iNrElements);

			iAlGroup.get(index).setCollapsed(false);
			iAlGroup.get(index).setSelectionType(ESelectionType.NORMAL);
			iAlGroup.get(index).setNrElements(iAlGroup.get(index).getNrElements() - iNrElements);
			iAlGroup.add(index, newGroup);

			return true;
		}

		// 1 new group
		if (idx2 == iLastIdx) {
			int iNrElements = idx2 - idx1;
			Group newGroup = new Group(iNrElements);

			iAlGroup.get(index).setCollapsed(false);
			iAlGroup.get(index).setSelectionType(ESelectionType.NORMAL);
			iAlGroup.get(index).setNrElements(iAlGroup.get(index).getNrElements() - iNrElements);
			iAlGroup.add(index + 1, newGroup);

			return true;
		}

		// 2 new groups
		int iNrElements2 = idx2 - idx1;
		Group newGroup2 = new Group(iNrElements2);
		int iNrElements3 = iLastIdx - idx2;
		Group newGroup3 = new Group(iNrElements3);

		iAlGroup.get(index).setCollapsed(false);
		iAlGroup.get(index).setSelectionType(ESelectionType.NORMAL);
		iAlGroup.get(index).setNrElements(idx1 - iFirstIdx);
		iAlGroup.add(index + 1, newGroup2);
		iAlGroup.add(index + 2, newGroup3);

		return true;
	}

	@Override
	public boolean move(IVirtualArray virtualArray, int srcIndex, int targetIndex) {

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
}
