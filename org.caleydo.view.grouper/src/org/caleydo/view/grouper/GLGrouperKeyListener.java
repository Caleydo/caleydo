package org.caleydo.view.grouper;

import org.caleydo.core.manager.event.AEvent;
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
		if (event.keyCode == SWT.CONTROL) {
			glGrouper.setControlPressed(true);
		}

	}

	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		if (event.keyCode == SWT.CONTROL) {
			glGrouper.setControlPressed(false);
		}
	}

}
