/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;


/**
 * @author Samuel Gratzl
 *
 */
public interface IGLKeyListener {

	/**
	 * @param e
	 */
	void keyPressed(IKeyEvent e);

	/**
	 * @param e
	 */
	void keyReleased(IKeyEvent e);


	public interface IKeyEvent {
		boolean isKey(char c);

		boolean isKey(ESpecialKey c);

		int getKeyCode();

		/**
		 * @return whether the shift key was already down or become down
		 */
		boolean isShiftDown();

		/**
		 * @return whether the control key was already down or become down
		 */
		boolean isControlDown();

		boolean isAltDown();

		boolean isUpDown();

		boolean isDownDown();

		boolean isKeyDown(char c);

	}

	public enum ESpecialKey {
		ALT, CONTROL, SHIFT, LEFT, RIGHT, UP, DOWN, PAGE_UP, PAGE_DOWN, HOME, END
	}
}
