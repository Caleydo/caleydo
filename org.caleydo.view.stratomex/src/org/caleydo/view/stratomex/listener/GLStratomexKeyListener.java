/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.view.stratomex.GLStratomex;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLStratomexKeyListener extends GLKeyListener<GLStratomex> {

	private boolean isCtrlDown;

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {

		if (event.keyCode == SWT.CTRL)
			isCtrlDown = true;

	}

	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		if (event.stateMask == SWT.CTRL)
			isCtrlDown = false;
	}

	public boolean isCtrlDown() {
		return isCtrlDown;
	}
}
