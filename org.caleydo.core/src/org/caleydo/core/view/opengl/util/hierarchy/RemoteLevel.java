package org.caleydo.core.view.opengl.util.hierarchy;

import java.util.ArrayList;

/**
 * Representation of a remote rendered level.
 * 
 * @author Marc Streit
 */
public class RemoteLevel {
	private int iCapacity;

	private String sName;

	private RemoteLevel parentLevel;
	private RemoteLevel childLevel;

	private ArrayList<RemoteLevelElement> alRemoteLevelElement;

	/**
	 * Constructor.
	 * 
	 * @param iCapacity
	 */
	public RemoteLevel(final int iCapacity, String sName, RemoteLevel parentLevel, RemoteLevel childLevel) {
		this.sName = sName;
		this.iCapacity = iCapacity;
		this.parentLevel = parentLevel;
		this.childLevel = childLevel;

		alRemoteLevelElement = new ArrayList<RemoteLevelElement>();

		// Intialize elements
		for (int iElementIndex = 0; iElementIndex < iCapacity; iElementIndex++) {
			addElement(new RemoteLevelElement(this));
		}
	}

	public int getCapacity() {
		return iCapacity;
	}

	public String getName() {
		return sName;
	}

	public RemoteLevel getParentLevel() {
		return parentLevel;
	}

	public RemoteLevel getChildLevel() {
		return childLevel;
	}

	public void addElement(RemoteLevelElement element) {
		alRemoteLevelElement.add(element);
	}

	// public void removeElement(RemoteLevelElement element)
	// {
	// if (!alRemoteLevelElement.contains(element))
	// return;
	//		
	// alRemoteLevelElement.remove(element);
	// }

	public boolean containsElement(RemoteLevelElement element) {
		return alRemoteLevelElement.contains(element);
	}

	public void replaceElement(RemoteLevelElement newElement, int iPositionIndex) {
		if (alRemoteLevelElement.size() < iPositionIndex) {
			addElement(newElement);
		}
		else {
			alRemoteLevelElement.set(iPositionIndex, newElement);
		}
	}

	public void replaceElement(RemoteLevelElement newElement, RemoteLevelElement oldElement) {
		if (!alRemoteLevelElement.contains(oldElement))
			return;

		alRemoteLevelElement.set(alRemoteLevelElement.indexOf(oldElement), newElement);
	}

	public boolean hasFreePosition() {
		boolean bHasFree = false;

		for (RemoteLevelElement element : alRemoteLevelElement) {
			bHasFree = element.isFree();

			if (bHasFree == true) {
				break;
			}
		}

		return bHasFree;
	}

	public RemoteLevelElement getNextFree() {
		for (RemoteLevelElement element : alRemoteLevelElement) {
			if (element.isFree())
				return element;
		}

		throw new IllegalStateException("No empty space left in " + sName);
	}

	public ArrayList<RemoteLevelElement> getAllElements() {
		return alRemoteLevelElement;
	}

	public RemoteLevelElement getElementByPositionIndex(int iPositionIndex) {
		return alRemoteLevelElement.get(iPositionIndex);
	}

	public int getPositionIndexByElementID(RemoteLevelElement element) {
		return alRemoteLevelElement.indexOf(element);
	}
}
