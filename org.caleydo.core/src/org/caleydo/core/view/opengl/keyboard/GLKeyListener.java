package org.caleydo.core.view.opengl.keyboard;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.keyboard.WrapperKeyEvent;
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

	}

	/**
	 * This method has to queue the key event to the event queue of the view by creating a
	 * {@link WrapperKeyEvent} and may not react directly on the events.
	 * 
	 * @param event
	 *            the SWT key event
	 */
	protected abstract void handleKeyPressedEvent(KeyEvent event);
}
