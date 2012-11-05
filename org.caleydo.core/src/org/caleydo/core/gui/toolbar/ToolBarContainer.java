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
package org.caleydo.core.gui.toolbar;

import java.util.List;

/**
 * Holds a ordered group of tool-bar items displayed as one group in the toolbar
 * 
 * @author Werner Puff
 */
public class ToolBarContainer {

	/** image path to FIXXXME: which image is this??? */
	private String imagePath;

	/** title of the container */
	private String title;

	/**  */
	private static final long serialVersionUID = 1L;

	/** list of actions within this tool bar container */
	private List<IToolBarItem> toolBarItems;

	/**
	 * Gets the list of actions currently defined within this tool bar container
	 * 
	 * @return list of actions
	 */
	public List<IToolBarItem> getToolBarItems() {
		return toolBarItems;
	}

	/**
	 * sets the list of actions for this tool bar container
	 * 
	 * @param actions
	 *            list of actions
	 */
	public void setToolBarItems(List<IToolBarItem> toolBarItems) {
		this.toolBarItems = toolBarItems;
	}

	/**
	 * FIXME: path to which image?
	 * 
	 * @return
	 */
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * Returns the title to displayed with this toolbar container
	 * 
	 * @return title of the container
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
