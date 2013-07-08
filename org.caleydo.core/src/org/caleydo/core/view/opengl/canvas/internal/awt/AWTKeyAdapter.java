/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener.ESpecialKey;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent;

/**
 * @author Samuel Gratzl
 *
 */
final class AWTKeyAdapter implements KeyListener {

	private final IGLKeyListener listener;

	/**
	 * @param listener
	 */
	public AWTKeyAdapter(IGLKeyListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public IGLKeyListener getListener() {
		return listener;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		listener.keyPressed(wrap(e));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
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
		return new AWTKeyEventAdapter(e);
	}


	private static class AWTKeyEventAdapter implements IKeyEvent {
		private final KeyEvent event;

		AWTKeyEventAdapter(KeyEvent event) {
			this.event = event;
		}

		@Override
		public boolean isKey(char c) {
			return event.getKeyCode() == Character.toLowerCase(c) || event.getKeyCode() == Character.toUpperCase(c);
		}

		@Override
		public boolean isKeyDown(char c) {
			// ToDo ToDo Fix Me and check key state
			return event.getKeyCode() == Character.toLowerCase(c) || event.getKeyCode() == Character.toUpperCase(c);
		}


		@Override
		public boolean isUpDown() {
			//ToDo ToDo Fix Me and check key state
			return event.getKeyCode() == KeyEvent.VK_UP;
		}

		@Override
		public boolean isDownDown() {
			//ToDo ToDo Fix Me and check key state
			return event.getKeyCode() == KeyEvent.VK_DOWN;
		}

		@Override
		public boolean isKey(ESpecialKey c) {
			switch (c) {
			case CONTROL:
				return event.getKeyCode() == KeyEvent.VK_CONTROL;
			case SHIFT:
				return event.getKeyCode() == KeyEvent.VK_SHIFT;
			case DOWN:
				return event.getKeyCode() == KeyEvent.VK_DOWN;
			case LEFT:
				return event.getKeyCode() == KeyEvent.VK_LEFT;
			case UP:
				return event.getKeyCode() == KeyEvent.VK_UP;
			case RIGHT:
				return event.getKeyCode() == KeyEvent.VK_RIGHT;
			case ALT:
				return event.getKeyCode() == KeyEvent.VK_ALT;
			case HOME:
				return event.getKeyCode() == KeyEvent.VK_HOME;
			case END:
				return event.getKeyCode() == KeyEvent.VK_END;
			case PAGE_UP:
				return event.getKeyCode() == KeyEvent.VK_PAGE_UP;
			case PAGE_DOWN:
				return event.getKeyCode() == KeyEvent.VK_PAGE_DOWN;
			}
			throw new IllegalStateException("unknown special key:" + c);
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

		@Override
		public boolean isAltDown() {
			return event.isAltDown();
		}



	}

}
