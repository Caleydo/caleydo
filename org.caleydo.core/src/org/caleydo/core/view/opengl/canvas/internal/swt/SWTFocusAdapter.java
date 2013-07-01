/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.swt;


import org.caleydo.core.view.opengl.canvas.IGLFocusListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

/**
 * @author Samuel Gratzl
 *
 */
final class SWTFocusAdapter implements FocusListener {

	private final IGLFocusListener listener;

	public SWTFocusAdapter(IGLFocusListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public IGLFocusListener getListener() {
		return listener;
	}

	@Override
	public void focusGained(FocusEvent e) {
		listener.focusGained();
	}

	@Override
	public void focusLost(FocusEvent e) {
		listener.focusLost();
	}

}
