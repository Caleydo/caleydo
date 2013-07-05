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
package org.caleydo.core.view.opengl.picking;

/**
 * Listener for several mouse events. The listener has to be registered with a view in one of two
 * combinations:
 * <ol>
 * <li>either both, a pickedObjectID (an Integer) and a pickingType (a String) - listeners are notified only
 * when the correct combination of id and type are picked, or</li>
 * <li>only a picking type - listeneres are notified when any object of the type are picked</li>
 * </ol>
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */
public interface IPickingListener {

	/**
	 * Called, when the object corresponding to this listener has been clicked.
	 * 
	 * @param pick
	 */
	public void clicked(Pick pick);

	/**
	 * Called, when the object corresponding to this listener has been double clicked.
	 * 
	 * @param pick
	 */
	public void doubleClicked(Pick pick);

	/**
	 * Called, when the object corresponding to this listener has been right clicked.
	 * 
	 * @param pick
	 */
	public void rightClicked(Pick pick);

	/**
	 * Called, when the mouse has been moved over the object corresponding to this listener.
	 * 
	 * @param pick
	 */
	public void mouseOver(Pick pick);

	/**
	 * Called, when the object corresponding to this listener has been right dragged.
	 * 
	 * @param pick
	 */
	public void dragged(Pick pick);

	/**
	 * Called, when the mouse has left the object corresponding to this listener.
	 * 
	 * @param pick
	 */
	public void mouseOut(Pick pick);

}
