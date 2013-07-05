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
