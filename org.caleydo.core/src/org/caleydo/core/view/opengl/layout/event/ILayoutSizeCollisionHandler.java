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
package org.caleydo.core.view.opengl.layout.event;

import org.caleydo.core.event.IListenerOwner;

/**
 * Interface for classes that want to handle collisions in the layouts
 * 
 * @author Alexander Lex
 */
public interface ILayoutSizeCollisionHandler
	extends IListenerOwner {

	/**
	 * Handle a size collision in a layout
	 * 
	 * @param managingClassID
	 *            the identifier for the class managing the calling layout
	 * @param externalID
	 *            the id of the layout (arbitrarily chose by the managing class)
	 * @param toBigBy
	 *            how much the layouts are to big by
	 */
	public void handleLayoutSizeCollision(int managingClassID, int layoutID, float toBigBy);

}
