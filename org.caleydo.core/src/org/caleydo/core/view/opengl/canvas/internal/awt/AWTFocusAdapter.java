/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import org.caleydo.core.view.opengl.canvas.IGLFocusListener;

/**
 * @author Samuel Gratzl
 *
 */
final class AWTFocusAdapter implements FocusListener {

	private final IGLFocusListener listener;

	public AWTFocusAdapter(IGLFocusListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public IGLFocusListener getListener() {
		return listener;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		listener.focusGained();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		listener.focusLost();
	}

}
