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
package org.caleydo.core.data.virtualarray.delta;

import org.caleydo.core.data.selection.delta.IDeltaItem;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.VirtualArray;

/**
 * A VADeltaItem contains all information for the modification of a single item in a virtual array. Typically
 * several items are held in a collection, a delta (see {@link VirtualArrayDelta}) Several types of operations
 * are defined in {@link EVAOperation} The item is implemented using static factories thereby guaranteeing
 * that only valid parameters are contained.
 * 
 * @author Alexander Lex
 */

public class VADeltaItem
	implements IDeltaItem {
	private EVAOperation vaOperation;
	private Integer elementID = -1;
	private int index = -1;
	private int targetIndex = -1;

	/**
	 * Constructor. Constructing a VAItem externally is forbidden.
	 */
	private VADeltaItem() {
	}

	/**
	 * Static factory for a new delta item that appends an element to the end of a virtual array
	 * 
	 * @param elementID
	 *            the new element
	 * @return the created object
	 */
	public static VADeltaItem append(Integer elementID) {
		VADeltaItem newItem = new VADeltaItem();
		newItem.vaOperation = EVAOperation.APPEND;
		newItem.elementID = elementID;
		return newItem;
	}

	/**
	 * Static factory for a new delta item that appends an element to the end of a virtual array (see
	 * {@link IVirtualArray#appendUnique(Integer)}) if the element does not yet exist in the virtual array
	 * 
	 * @param iNewElement
	 *            the new element
	 * @return the created object
	 */
	public static VADeltaItem appendUnique(Integer newElementID) {
		VADeltaItem newItem = new VADeltaItem();
		newItem.vaOperation = EVAOperation.APPEND_UNIQUE;
		newItem.elementID = newElementID;
		return newItem;
	}

	/**
	 * Static factory for a new delta item that adds an element at a specific position of a virtual array
	 * 
	 * @param index
	 *            the place where the element is added (see {@link VirtualArray#add(int, Integer)})
	 * @param newElementID
	 *            the new element
	 * @return the created object
	 */
	public static VADeltaItem add(int index, int newElementID) {
		VADeltaItem newItem = new VADeltaItem();
		newItem.vaOperation = EVAOperation.ADD;
		newItem.elementID = newElementID;
		newItem.index = index;
		return newItem;
	}

	/**
	 * Static factory for a new delta item that removes an element at a specific position of a virtual array
	 * Take good care to remove items in the correct order, from back to front. Otherwise this can corrupt
	 * your data.
	 * 
	 * @param index
	 *            the place where the element is added (see {@link IVirtualArray#remove(int)})
	 * @return the created object
	 */
	public static VADeltaItem remove(int index) {
		VADeltaItem newItem = new VADeltaItem();
		newItem.vaOperation = EVAOperation.REMOVE;
		newItem.index = index;
		return newItem;
	}

	/**
	 * Static Factory for new delta item that removes all instances of a specific element. See
	 * {@link IVirtualArray#removeByElement(int)} for further details.
	 * 
	 * @param elementID
	 *            the element to be removed
	 * @return the created object
	 */
	public static VADeltaItem removeElement(Integer elementID) {
		VADeltaItem newItem = new VADeltaItem();
		newItem.vaOperation = EVAOperation.REMOVE_ELEMENT;
		newItem.elementID = elementID;
		return newItem;
	}

	/**
	 * Static Factory for new delta item that moves the element at the specified src index to the specified
	 * target index. See {@link IVirtualArray#move(int, int)} for further details.
	 * 
	 * @param srcIndex
	 *            the src index
	 * @param targetIndex
	 *            the target index
	 * @return the created object
	 */
	public static VADeltaItem move(int srcIndex, int targetIndex) {
		VADeltaItem newItem = new VADeltaItem();
		newItem.vaOperation = EVAOperation.MOVE;
		newItem.index = srcIndex;
		newItem.targetIndex = targetIndex;
		return newItem;
	}

	/**
	 * Static Factory for new delta item that copies the element at the specified index and adds the new
	 * element at iIndex + 1 {@link IVirtualArray#copy(int)} for further details.
	 * 
	 * @param index
	 *            the element to be copied
	 * @return the created object
	 */
	public static VADeltaItem copy(int index) {
		VADeltaItem newItem = new VADeltaItem();
		newItem.vaOperation = EVAOperation.COPY;
		newItem.index = index;
		return newItem;
	}

	/**
	 * Create a new VADeltaItem with properties specified in operation. The new item may take only one
	 * parameter, therefore {@link EVAOperation#ADD} and {@link EVAOperation#MOVE} can not be passed as an
	 * argument here
	 * 
	 * @param operation
	 *            the operation the delta item should carry out
	 * @param value
	 *            a integer variable, which can be either an index or an element id, depending on the use case
	 * @return the created object with the properties specified in operation
	 */
	public static VADeltaItem create(EVAOperation operation, Integer value) {
		switch (operation) {
			case APPEND:
				return append(value);
			case APPEND_UNIQUE:
				return appendUnique(value);
			case REMOVE:
				return remove(value);
			case REMOVE_ELEMENT:
				return removeElement(value);
			case COPY:
				return copy(value);
			default:
				throw new IllegalArgumentException("Illegal number of arguments for operation " + operation
					+ ".");
		}
	}

	/**
	 * Getter for the type of operation
	 * 
	 * @return the type
	 */
	public EVAOperation getType() {
		return vaOperation;
	}

	/**
	 * Getter for the index.
	 * 
	 * @return the index
	 * @throws IllegalStateException
	 *             if operation does not use an index
	 */
	public int getIndex() {
		if (index == -1)
			throw new IllegalStateException("Operation " + vaOperation + " does not need an index");

		return index;
	}

	/**
	 * Getter for the target index used by the {@link EVAOperation#MOVE}
	 * 
	 * @return the target index
	 * @throws IllegalStateException
	 *             if operation does not use a target index
	 */
	public int getTargetIndex() {
		if (targetIndex == -1)
			throw new IllegalStateException("Operation " + vaOperation + " does not need a target index");
		return targetIndex;
	}

	/**
	 * Getter for the element (the content of the va)
	 * 
	 * @return the element
	 * @throws IllegalStateException
	 *             if operation does not use an element
	 */
	@Override
	public int getID() {
		if (elementID == -1)
			throw new IllegalStateException("Operation " + vaOperation + " does not need an element");

		return elementID;
	}

	@Override
	public void setID(Integer elementID) {
		this.elementID = elementID;
	}

	@Override
	public VADeltaItem clone() {
		try {
			return (VADeltaItem) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new IllegalStateException(
				"Something went wrong with the cloning, caught CloneNotSupportedException");
		}
	}

	@Override
	public int hashCode() {
		return elementID;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.hashCode() == hashCode();
	}

	@Override
	public String toString() {
		String message = vaOperation.toString();
		switch (vaOperation) {
			case MOVE:
				message += " from: " + index + " to: " + targetIndex;
				break;
			case REMOVE:
				message += "; Index: " + index;
				break;

		}
		return message;
	}

}
