package org.caleydo.view.grouper;

import org.caleydo.core.event.AEvent;
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
		// Nils -- key code events described here: http://book.javanb.com/swt-the-standard-widget-toolkit/ch02lev1sec2.html
		if (event.keyCode == SWT.CONTROL || event.keyCode == SWT.MOD1) {
			glGrouper.setControlPressed(true);
		}

	}

	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		// Nils -- key code events described here: http://book.javanb.com/swt-the-standard-widget-toolkit/ch02lev1sec2.html
		if (event.keyCode == SWT.CONTROL || event.keyCode == SWT.MOD1) {
			glGrouper.setControlPressed(false);
		}
	}

}
