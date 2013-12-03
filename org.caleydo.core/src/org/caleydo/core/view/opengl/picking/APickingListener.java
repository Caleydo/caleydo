/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
			break;
		case MOUSE_RELEASED:
			mouseReleased(pick);
			break;
		case MOUSE_WHEEL:
			mouseWheel(pick);
			break;
		case DRAG_DETECTED:
			dragDetected(pick);
			break;
		}
	}

	/**
	 * Called, when the object corresponding to this listener was not clicked anymore, opposite of clicked
	 *
	 * @param pick
	 */
	protected void mouseReleased(Pick pick) {

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

	/**
	 * Called, when the mouse wheel was moved within the object
	 *
	 * @param pick
	 */
	protected void mouseWheel(Pick pick) {

	}

	/**
	 * Called, when a drag operation is detected
	 *
	 * @param pick
	 */
	protected void dragDetected(Pick pick) {

	}

}
