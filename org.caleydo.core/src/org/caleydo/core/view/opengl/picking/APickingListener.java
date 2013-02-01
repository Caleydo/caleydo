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
 * Convenience class that can be extended instead of implementing {@link IPickingListener} if not all methods
 * need to be implemented, and nothing else speaks against inheritance.
 *
 * @author Christian Partl
 */

public abstract class APickingListener
	implements IPickingListener {

	@Override
	public void pick(Pick pick) {
		switch (pick.getPickingMode()) {
		case CLICKED:
			clicked(pick);
			break;
		case DOUBLE_CLICKED:
			doubleClicked(pick);
			break;
		case DRAGGED:
			dragged(pick);
			break;
		case MOUSE_OUT:
			mouseOut(pick);
			break;
		case MOUSE_OVER:
			mouseOver(pick);
			break;
		case RIGHT_CLICKED:
			rightClicked(pick);
			break;
		case MOUSE_MOVED:
			mouseMoved(pick);
		}
	}

	/**
	 * Called, when the object corresponding to this listener has been clicked.
	 *
	 * @param pick
	 */
	protected void clicked(Pick pick) {

	}

	/**
	 * Called, when the object corresponding to this listener has been double clicked.
	 *
	 * @param pick
	 */
	protected void doubleClicked(Pick pick) {

	}

	/**
	 * Called, when the object corresponding to this listener has been right clicked.
	 *
	 * @param pick
	 */
	protected void rightClicked(Pick pick) {

	}

	/**
	 * Called, when the mouse has been moved over the object corresponding to this listener.
	 *
	 * @param pick
	 */
	protected void mouseOver(Pick pick) {

	}

	/**
	 * Called, when the object corresponding to this listener has been right dragged.
	 *
	 * @param pick
	 */
	protected void dragged(Pick pick) {

	}

	/**
	 * Called, when the mouse has left the object corresponding to this listener.
	 *
	 * @param pick
	 */
	protected void mouseOut(Pick pick) {

	}

	/**
	 * Called, when the mouse was moved within the object
	 * 
	 * @param pick
	 */
	protected void mouseMoved(Pick pick) {

	}

}
