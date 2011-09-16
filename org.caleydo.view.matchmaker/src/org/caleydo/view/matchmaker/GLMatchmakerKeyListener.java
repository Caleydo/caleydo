package org.caleydo.view.matchmaker;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLMatchmakerKeyListener extends GLKeyListener<GLMatchmaker> {

	private GLMatchmaker glCompare;

	/**
	 * Constructor.
	 * 
	 * @param glCompare
	 *            Instance of the grouper that should handle the keyboard
	 *            events.
	 */
	public GLMatchmakerKeyListener(GLMatchmaker glCompare) {
		this.glCompare = glCompare;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
		if (event.keyCode == SWT.CONTROL) {
			glCompare.setControlPressed(true);
		}

	}

	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		if (event.keyCode == SWT.CONTROL) {
			glCompare.setControlPressed(false);
		}
	}

}
