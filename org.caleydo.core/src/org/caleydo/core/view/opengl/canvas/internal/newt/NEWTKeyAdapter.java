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


import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

/**
 * @author Samuel Gratzl
 *
 */
final class NEWTKeyAdapter implements KeyListener {

	private final IGLKeyListener listener;

	/**
	 * @param listener
	 */
	public NEWTKeyAdapter(IGLKeyListener listener) {
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
	 * @see com.jogamp.newt.event.KeyListener#keyPressed(com.jogamp.newt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		listener.keyPressed(wrap(e));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jogamp.newt.event.KeyListener#keyReleased(com.jogamp.newt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		listener.keyReleased(wrap(e));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jogamp.newt.event.KeyListener#keyTyped(com.jogamp.newt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * @param e
	 * @return
	 */
	private static IKeyEvent wrap(KeyEvent e) {
		return new AWTKeyEventAdapter(e);
	}


	private static class AWTKeyEventAdapter implements IKeyEvent {
		private final KeyEvent event;

		AWTKeyEventAdapter(KeyEvent event) {
			this.event = event;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent#getKeyCode()
		 */
		@Override
		public int getKeyCode() {
			return event.getKeyCode();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent#isControlDown()
		 */
		@Override
		public boolean isControlDown() {
			return event.isControlDown();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent#isShiftDown()
		 */
		@Override
		public boolean isShiftDown() {
			return event.isShiftDown();
		}
	}

}
