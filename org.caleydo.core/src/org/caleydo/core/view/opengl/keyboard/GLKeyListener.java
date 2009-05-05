package org.caleydo.core.view.opengl.keyboard;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;


public abstract class GLKeyListener
extends KeyAdapter {
	
	@Override
	public void keyPressed(KeyEvent e) {

		handleKeyPressedEvent(e);
	}
	
	protected abstract void handleKeyPressedEvent(KeyEvent event);
}
