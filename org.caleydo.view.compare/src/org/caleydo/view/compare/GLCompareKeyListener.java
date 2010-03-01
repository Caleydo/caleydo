package org.caleydo.view.compare;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLCompareKeyListener extends GLKeyListener<GLCompare> {

	private GLCompare glCompare;

	/**
	 * Constructor.
	 * 
	 * @param glCompare
	 *            Instance of the grouper that should handle the keyboard
	 *            events.
	 */
	public GLCompareKeyListener(GLCompare glCompare) {
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
