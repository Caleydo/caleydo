/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener.IKeyEvent;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;

/**
 * wrappers for canvas listeners that ensure that the adaptee are called within the OpenGL thread
 *
 * requires that the key listeners are scanned by an {@link EventListenerManager}
 *
 * @author Samuel Gratzl
 *
 */
public class GLThreadListenerWrapper {

	public static IGLKeyListener wrap(IGLKeyListener adaptee) {
		return new KeyListenerAdapter(adaptee);
	}

	public static IGLMouseListener wrap(IGLMouseListener adaptee) {
		return new MouseListenerAdapter(adaptee);
	}

	public static IGLFocusListener wrap(IGLFocusListener adaptee) {
		return new FocusListenerAdapter(adaptee);
	}

	private static class KeyListenerAdapter implements IGLKeyListener {
		private final IGLKeyListener adaptee;

		public KeyListenerAdapter(IGLKeyListener adaptee) {
			this.adaptee = adaptee;
		}

		@ListenTo(sendToMe = true)
		private void onKeyEventEvent(KeyEventEvent event) {
			switch (event.type) {
			case 0:
				adaptee.keyPressed(event.event);
				break;
			case 1:
				adaptee.keyReleased(event.event);
				break;
			default:
				throw new IllegalStateException();
			}
		}

		private void fire(int type, IKeyEvent event) {
			EventPublisher.trigger(new KeyEventEvent(event, type).to(this));
		}

		@Override
		public void keyPressed(IKeyEvent e) {
			fire(0, e);
		}

		@Override
		public void keyReleased(IKeyEvent e) {
			fire(1, e);
		}

	}

	private static class MouseListenerAdapter implements IGLMouseListener {
		private final IGLMouseListener adaptee;

		public MouseListenerAdapter(IGLMouseListener adaptee) {
			this.adaptee = adaptee;
		}

		@ListenTo(sendToMe = true)
		private void onMouseEventEvent(MouseEventEvent event) {
			final IMouseEvent mouseEvent = event.event;
			switch (event.type) {
			case 0:
				adaptee.mousePressed(mouseEvent);
				break;
			case 1:
				adaptee.mouseMoved(mouseEvent);
				break;
			case 2:
				adaptee.mouseClicked(mouseEvent);
				break;
			case 3:
				adaptee.mouseReleased(mouseEvent);
				break;
			case 4:
				adaptee.mouseDragged(mouseEvent);
				break;
			case 5:
				adaptee.mouseWheelMoved(mouseEvent);
				break;
			case 6:
				adaptee.mouseEntered(mouseEvent);
				break;
			case 7:
				adaptee.mouseExited(mouseEvent);
				break;
			default:
				throw new IllegalStateException();
			}
		}

		private void fire(int type, IMouseEvent event) {
			EventPublisher.trigger(new MouseEventEvent(event, type).to(this));
		}

		@Override
		public void mousePressed(IMouseEvent mouseEvent) {
			fire(0, mouseEvent);
		}

		@Override
		public void mouseMoved(IMouseEvent mouseEvent) {
			fire(1, mouseEvent);
		}

		@Override
		public void mouseClicked(IMouseEvent mouseEvent) {
			fire(2, mouseEvent);
		}

		@Override
		public void mouseReleased(IMouseEvent mouseEvent) {
			fire(3, mouseEvent);
		}

		@Override
		public void mouseDragged(IMouseEvent mouseEvent) {
			fire(4, mouseEvent);
		}

		@Override
		public void mouseWheelMoved(IMouseEvent mouseEvent) {
			fire(5, mouseEvent);
		}

		@Override
		public void mouseEntered(IMouseEvent mouseEvent) {
			fire(6, mouseEvent);
		}

		@Override
		public void mouseExited(IMouseEvent mouseEvent) {
			fire(7, mouseEvent);
		}

	}

	private static class FocusListenerAdapter implements IGLFocusListener {
		private final IGLFocusListener adaptee;

		public FocusListenerAdapter(IGLFocusListener adaptee) {
			this.adaptee = adaptee;
		}

		@ListenTo(sendToMe = true)
		private void onFocusEventEvent(FocusEventEvent event) {
			switch (event.type) {
			case 0:
				adaptee.focusGained();
				break;
			case 1:
				adaptee.focusLost();
				break;
			default:
				throw new IllegalStateException();
			}
		}

		private void fire(int type) {
			EventPublisher.trigger(new FocusEventEvent(type).to(this));
		}

		@Override
		public void focusGained() {
			fire(0);
		}

		@Override
		public void focusLost() {
			fire(1);
		}

	}

	public static class KeyEventEvent extends ADirectedEvent {
		private final IKeyEvent event;
		private final int type;

		public KeyEventEvent(IKeyEvent event, int type) {
			this.event = event;
			this.type = type;
		}
	}

	public static class MouseEventEvent extends ADirectedEvent {
		private final IMouseEvent event;
		private final int type;

		public MouseEventEvent(IMouseEvent event, int type) {
			this.event = event;
			this.type = type;
		}
	}

	public static class FocusEventEvent extends ADirectedEvent {
		private final int type;

		public FocusEventEvent(int type) {
			this.type = type;
		}
	}
}
