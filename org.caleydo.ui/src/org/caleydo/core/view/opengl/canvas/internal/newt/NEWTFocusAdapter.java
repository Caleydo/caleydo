/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.newt;

import org.caleydo.core.view.opengl.canvas.IGLFocusListener;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;

/**
 * @author Samuel Gratzl
 *
 */
final class NEWTFocusAdapter extends WindowAdapter {

	private final IGLFocusListener listener;

	public NEWTFocusAdapter(IGLFocusListener listener) {
		this.listener = listener;
	}


	/**
	 * @return the listener
	 */
	public IGLFocusListener getListener() {
		return listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jogamp.newt.event.WindowAdapter#windowGainedFocus(com.jogamp.newt.event.WindowEvent)
	 */
	@Override
	public void windowGainedFocus(WindowEvent e) {
		listener.focusGained();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jogamp.newt.event.WindowAdapter#windowLostFocus(com.jogamp.newt.event.WindowEvent)
	 */
	@Override
	public void windowLostFocus(WindowEvent e) {
		listener.focusLost();
	}
}
