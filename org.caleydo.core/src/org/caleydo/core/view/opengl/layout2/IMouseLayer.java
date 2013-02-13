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
package org.caleydo.core.view.opengl.layout2;

import java.util.List;

import org.caleydo.core.util.collection.Pair;

/**
 * a abstract form of the mouse layer, i.e. the canvas that moves with the mouse
 *
 * @author Samuel Gratzl
 *
 */
public interface IMouseLayer {

	/**
	 * adds an invisible draggable element, e.g. a marker that something is dragged
	 *
	 * @param info
	 */
	void addDraggable(IDragInfo info);
	/**
	 * adds a draggable element to this mouse layer
	 *
	 * @param element
	 */
	void addDraggable(GLElement element);

	/**
	 * see {@link #addDraggable(GLElement)} with dedicated meta data information
	 *
	 * @param element
	 * @param info
	 */
	void addDraggable(GLElement element, IDragInfo info);

	/**
	 * see {@link #hasDraggable(Class)} with {@link IDragInfo} as parameter
	 *
	 * @return if any draggable elements exists
	 */
	boolean hasDraggables();

	/**
	 * checks if any draggable element exists, which has {@link IDragInfo} of the given type
	 *
	 * @param type
	 *            the type to check
	 * @return if any draggable element exists, fullfilling the criteria
	 */
	boolean hasDraggable(Class<? extends IDragInfo> type);

	/**
	 * see {@link #hasDraggable(Class)} for a concrete {@link IDragInfo} instance
	 *
	 * @param info
	 * @return
	 */
	boolean hasDraggable(IDragInfo info);

	/**
	 * returns the first element pair that has a {@link IDragInfo} of the given type
	 *
	 * @param type
	 * @return the first matching pair or null if no was found
	 */
	<T extends IDragInfo> Pair<GLElement, T> getFirstDraggable(Class<T> type);

	/**
	 * see {@link #getFirstDraggable(Class)} for a specific {@link IDragInfo} instance
	 *
	 * @param info
	 * @return
	 */
	<T extends IDragInfo> Pair<GLElement, T> getFirstDraggable(T info);

	/**
	 * returns all element pairs that have a {@link IDragInfo} of the given type
	 *
	 * @param type
	 * @return a list of matching pairs or an empty list
	 */
	<T extends IDragInfo> List<Pair<GLElement, T>> getDraggables(Class<T> type);

	/**
	 * removes a draggable element
	 *
	 * @param element
	 * @return if it was successfully removed
	 */
	boolean removeDraggable(GLElement element);

	/**
	 * removes a draggable element identified by its {@link IDragInfo}
	 *
	 * @param info
	 * @return
	 */
	boolean removeDraggable(IDragInfo info);



	/**
	 * marks whether the identified object can be dropped here or not
	 *
	 * @param info
	 * @param dropAble
	 */
	void setDropable(IDragInfo info, boolean dropAble);

	/**
	 * see {@link #setDropable(IDragInfo, boolean)} for a generic {@link IDragInfo} type
	 */
	void setDropable(Class<? extends IDragInfo> type, boolean dropAble);

	/**
	 *
	 * @param info
	 * @return
	 */
	boolean isDropable(IDragInfo info);

	/**
	 * sets the tooltip component of the mouse with a specific element
	 *
	 * @param element
	 */
	void setToolTip(GLElement element);

	/**
	 * sets the tooltip with a text a default gl element will be created
	 *
	 * @param text
	 */
	void setToolTip(String text);

	/**
	 * removes the current tooltip
	 *
	 * @return
	 */
	boolean clearToolTip();

	/**
	 * marker interface for dragging meta data
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IDragInfo {

	}
}

