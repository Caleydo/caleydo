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
package org.caleydo.core.event.view;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals that the minimum size of an object has changed.
 *
 * @author Christian Partl
 *
 */
public class MinSizeUpdateEvent extends AEvent {

	protected int minHeight;
	protected int minWidth;
	protected Object minSizeObject;

	public MinSizeUpdateEvent(Object minSizeObject) {
		this.minSizeObject = minSizeObject;
	}

	public MinSizeUpdateEvent(Object minSizeObject, int minWidth, int minHeight) {
		this(minSizeObject);
		this.minHeight = minHeight;
		this.minWidth = minWidth;
	}

	@Override
	public boolean checkIntegrity() {
		return minSizeObject != null;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public void setMinViewSize(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	/**
	 * @param minSizeObject
	 *            setter, see {@link minSizeObject}
	 */
	public void setMinSizeObject(Object minSizeObject) {
		this.minSizeObject = minSizeObject;
	}

	/**
	 * @return the minSizeObject, see {@link #minSizeObject}
	 */
	public Object getMinSizeObject() {
		return minSizeObject;
	}

}
