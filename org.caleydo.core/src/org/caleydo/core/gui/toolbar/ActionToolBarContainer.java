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
 * ToolBarContainer for toolbar groups that only contains actions.
 * 
 * @author Werner Puff
 */
public class ActionToolBarContainer
	extends ToolBarContainer {

	/** list of actions within this tool bar container */
	private List<IToolBarItem> actions;

	/**
	 * Gets the list of actions currently defined within this tool bar container
	 * 
	 * @return list of actions
	 */
	@Override
	public List<IToolBarItem> getToolBarItems() {
		return actions;
	}

	/**
	 * sets the list of actions for this tool bar container
	 * 
	 * @param actions
	 *            list of actions
	 */
	@Override
	public void setToolBarItems(List<IToolBarItem> actions) {
		this.actions = actions;
	}

}
