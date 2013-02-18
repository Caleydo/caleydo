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
import org.caleydo.core.view.opengl.canvas.IGLKeyListener.ESpecialKey;
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

	@Override
	public void keyPressed(KeyEvent e) {
		listener.keyPressed(wrap(e, true));
	}


	@Override
	public void keyReleased(KeyEvent e) {
		listener.keyReleased(wrap(e, false));
	}

	/**
	 * @param e
	 * @return
	 */
	private static IKeyEvent wrap(KeyEvent e, boolean pressed) {
		return new SWTKeyEventAdapter(e, pressed);
	}

	private static class SWTKeyEventAdapter implements IKeyEvent {
		private final KeyEvent event;
		private final boolean pressed;

		SWTKeyEventAdapter(KeyEvent event, boolean pressed) {
			this.event = event;
			this.pressed = pressed;
		}

		@Override
		public boolean isKey(char c) {
			return event.keyCode == Character.toLowerCase(c) || event.keyCode == Character.toUpperCase(c);
		}

		@Override
		public boolean isKey(ESpecialKey c) {
			switch (c) {
			case CONTROL:
				return event.keyCode == SWT.CONTROL;
			case SHIFT:
				return event.keyCode == SWT.SHIFT;
			case DOWN:
				return event.keyCode == SWT.ARROW_DOWN;
			case LEFT:
				return event.keyCode == SWT.ARROW_LEFT;
			case UP:
				return event.keyCode == SWT.ARROW_UP;
			case RIGHT:
				return event.keyCode == SWT.ARROW_RIGHT;
			}
			throw new IllegalStateException("unknown special key:" + c);
		}

		@Override
		public int getKeyCode() {
			return event.keyCode;
		}

		@Override
		public boolean isControlDown() {
			if (pressed)
				// was down or is currently pressed
				return (event.stateMask & SWT.CONTROL) != 0 || isKey(ESpecialKey.CONTROL);
			else
				// was down and was not currently released
				return (event.stateMask & SWT.CONTROL) != 0 && !isKey(ESpecialKey.CONTROL);
		}

		@Override
		public boolean isShiftDown() {
			if (pressed)
				return (event.stateMask & SWT.SHIFT) != 0 || isKey(ESpecialKey.SHIFT);
			else
				return (event.stateMask & SWT.SHIFT) != 0 && !isKey(ESpecialKey.SHIFT);
		}
	}

}
