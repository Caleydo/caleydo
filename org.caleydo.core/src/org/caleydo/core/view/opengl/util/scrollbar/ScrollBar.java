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
package org.caleydo.core.view.opengl.util.scrollbar;

import org.caleydo.core.view.opengl.picking.PickingType;

/**
 * Class that holds all non-visual properties of a scroll bar. The rendering should be done by a
 * {@link ScrollBarRenderer}.
 * 
 * @author Partl
 */
public class ScrollBar {

	private int minValue;
	private int maxValue;
	private int selection;
	private int pageSize;
	private PickingType pickingType;
	private int id;
	private IScrollBarUpdateHandler scrollBarUpdateHandler;

	/**
	 * Constructor.
	 * 
	 * @param minValue
	 *            Minimum value that can be selected by the ScrollBar.
	 * @param maxValue
	 *            Maximum value that can be selected by the ScrollBar.
	 * @param selection
	 *            Currently selected value.
	 * @param pageSize
	 *            Area that shall be covered around the selected value.
	 * @param pickingType
	 * @param id
	 *            ID used for picking.
	 * @param scrollBarUpdateHandler
	 *            Handler that gets notified when the scroll bar gets updated.
	 */
	public ScrollBar(int minValue, int maxValue, int selection, int pageSize, PickingType pickingType,
		int id, IScrollBarUpdateHandler scrollBarUpdateHandler) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.selection = selection;
		this.pageSize = pageSize;
		this.pickingType = pickingType;
		this.id = id;
		this.setScrollBarUpdateHandler(scrollBarUpdateHandler);
	}

	/**
	 * @return Minimum value that can be selected by the ScrollBar.
	 */
	public int getMinValue() {
		return minValue;
	}

	/**
	 * Sets the minimum value that can be selected by the ScrollBar.
	 * 
	 * @param minValue
	 */
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	/**
	 * @return Maximum value that can be selected by the ScrollBar.
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets the maximum value that can be selected by the ScrollBar.
	 * 
	 * @param maxValue
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @return Currently selected value.
	 */
	public int getSelection() {
		return selection;
	}

	/**
	 * Sets the currently selected value.
	 * 
	 * @param selection
	 */
	public void setSelection(int selection) {
		this.selection = selection;
	}

	/**
	 * @return Area that shall be covered around the selected value.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the area that shall be covered around the selected value.
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public PickingType getPickingType() {
		return pickingType;
	}

	public void setPickingType(PickingType pickingType) {
		this.pickingType = pickingType;
	}

	/**
	 * @return ID used for picking.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the ID used for picking.
	 * 
	 * @param id
	 */
	public void tableID(int id) {
		this.id = id;
	}

	/**
	 * Sets the handler that gets notified when the scroll bar gets updated.
	 * 
	 * @param scrollBarUpdateHandler
	 */
	public void setScrollBarUpdateHandler(IScrollBarUpdateHandler scrollBarUpdateHandler) {
		this.scrollBarUpdateHandler = scrollBarUpdateHandler;
	}

	/**
	 * @return Handler that gets notified when the scroll bar gets updated.
	 */
	public IScrollBarUpdateHandler getScrollBarUpdateHandler() {
		return scrollBarUpdateHandler;
	}

}
