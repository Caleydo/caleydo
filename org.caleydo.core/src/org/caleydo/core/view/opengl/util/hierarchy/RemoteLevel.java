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
