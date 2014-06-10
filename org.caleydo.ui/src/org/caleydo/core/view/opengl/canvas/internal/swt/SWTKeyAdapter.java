/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
		public boolean isKeyDown(char c) {
			if (pressed)
				return event.keyCode == Character.toLowerCase(c) || event.keyCode == Character.toUpperCase(c);
			else
				return false;

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
			case ALT:
				return event.keyCode == SWT.ALT;
			case HOME:
				return event.keyCode == SWT.HOME;
			case END:
				return event.keyCode == SWT.END;
			case PAGE_UP:
				return event.keyCode == SWT.PAGE_UP;
			case PAGE_DOWN:
				return event.keyCode == SWT.PAGE_DOWN;
			case DELETE:
				return event.keyCode == SWT.DEL;
			case INSERT:
				return event.keyCode == SWT.INSERT;
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

		@Override
		public boolean isAltDown() {

			if (pressed)
				return (event.stateMask & SWT.ALT) != 0 || isKey(ESpecialKey.ALT);
			else
				return (event.stateMask & SWT.ALT) != 0 && !isKey(ESpecialKey.ALT);
		}

		@Override
		public boolean isUpDown() {

			if (pressed)
				return (event.stateMask & SWT.UP) != 0 || isKey(ESpecialKey.UP);
			else
				return (event.stateMask & SWT.UP) != 0 && !isKey(ESpecialKey.UP);
		}

		@Override
		public boolean isDownDown() {

			if (pressed)
				return (event.stateMask & SWT.DOWN) != 0 || isKey(ESpecialKey.DOWN);
			else
				return (event.stateMask & SWT.DOWN) != 0 && !isKey(ESpecialKey.DOWN);
		}
	}

}
