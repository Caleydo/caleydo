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
package org.caleydo.core.view.opengl.keyboard;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

/**
 * Key listener for gl views. It implements the SWT key listener interface and also the {@link AEventListener}
 * to integrate it in the threading system of Caleydo. When a key was pressed the abstract
 * {@link #handleKeyPressedEvent(KeyEvent)} is called. This method has to queue the key event to the event
 * queue of the view by creating a {@link WrapperKeyEvent} and may not react directly on the events. The
 * actual event handling has to be done in the {@link #handleEvent(AEvent)} method
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class GLKeyListener<T extends IListenerOwner>
	extends AEventListener<T>
	implements KeyListener {

	@Override
	public void keyPressed(KeyEvent e) {

		handleKeyPressedEvent(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		handleKeyReleasedEvent(e);
	}

	/**
	 * This method has to queue the key event to the event queue of the view by creating a
	 * {@link WrapperKeyEvent} and may not react directly on the events.
	 *
	 * @param event
	 *            the SWT key event
	 */
	protected abstract void handleKeyPressedEvent(KeyEvent event);

	/**
	 * This method has to queue the key event to the event queue of the view by creating a
	 * {@link WrapperKeyEvent} and may not react directly on the events.
	 *
	 * @param event
	 *            the SWT key event
	 */
	protected abstract void handleKeyReleasedEvent(KeyEvent event);
}
