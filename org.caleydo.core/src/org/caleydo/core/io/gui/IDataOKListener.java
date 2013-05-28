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
/**
 * 
 */
package org.caleydo.core.io.gui;

/**
 * <p>
 * Interface used for nested GUI dialogs, so that they can be notified when the data of a sub-widget is ready
 * to be processed.
 * </p>
 * <p>
 * By calling {@link #dataOK()} the sub-widget tells the parent that it's data is correct. The parent then
 * needs to check whether it's data is ready as well and enable the ok button if this is the case.
 * </p>
 * 
 * @author Alexander Lex
 */
public interface IDataOKListener {

	/**
	 * Called by a sub-widget to it's parent which implements this interface when all it's data is complete.
	 */
	public void dataOK();

}
