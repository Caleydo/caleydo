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

/**
 * Implementors of this interface are notified when ScrollBars they registered for are updated.
 * 
 * @author Partl
 */
public interface IScrollBarUpdateHandler {

	/**
	 * This method is called, when a ScrollBar was updated (e.g. dragged by a user).
	 * 
	 * @param scrollBar
	 *            The ScrollBar that was updated.
	 */
	public void handleScrollBarUpdate(ScrollBar scrollBar);

}
