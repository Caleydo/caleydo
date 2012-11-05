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
package org.caleydo.core.view.opengl.canvas.internal.swt;


import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

/**
 * @author Samuel Gratzl
 *
 */
final class SWTKeyAdapter implements KeyListener {

	private final IGLKeyListener listener;

	/**
	 * @param listener
	 */
	public SWTKeyAdapter(IGLKeyListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public IGLKeyListener getListener() {
		return listener;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		listener.keyPressed(wrap(e));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		listener.keyReleased(wrap(e));
	}

	/**
	 * @param e
	 * @return
	 */
	private static IKeyEvent wrap(KeyEvent e) {
		return new SWTKeyEventAdapter(e);
	}

	private static class SWTKeyEventAdapter implements IKeyEvent {
		private final KeyEvent event;

		SWTKeyEventAdapter(KeyEvent event) {
			this.event = event;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent#getKeyCode()
		 */
		@Override
		public int getKeyCode() {
			return event.keyCode;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent#isControlDown()
		 */
		@Override
		public boolean isControlDown() {
			return (event.stateMask & SWT.CONTROL) != 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent#isShiftDown()
		 */
		@Override
		public boolean isShiftDown() {
			return (event.stateMask & SWT.SHIFT) != 0;
		}
	}

}
