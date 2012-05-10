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
package org.caleydo.view.grouper;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * Keyboard listener for GLGrouper.
 * 
 * @author Christian
 * 
 */
public class GLGrouperKeyListener extends GLKeyListener<GLGrouper> {

	private GLGrouper glGrouper;

	/**
	 * Constructor.
	 * 
	 * @param glGrouper
	 *            Instance of the grouper that should handle the keyboard
	 *            events.
	 */
	public GLGrouperKeyListener(GLGrouper glGrouper) {
		this.glGrouper = glGrouper;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
		// Nils -- key code events described here:
		// http://book.javanb.com/swt-the-standard-widget-toolkit/ch02lev1sec2.html
		if (event.keyCode == SWT.CONTROL || event.keyCode == SWT.MOD1) {
			glGrouper.setControlPressed(true);
		}

	}

	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		// Nils -- key code events described here:
		// http://book.javanb.com/swt-the-standard-widget-toolkit/ch02lev1sec2.html
		if (event.keyCode == SWT.CONTROL || event.keyCode == SWT.MOD1) {
			glGrouper.setControlPressed(false);
		}
	}

}
