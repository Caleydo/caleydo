package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.view.datagraph.GLDataViewIntegrator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLDataGraphKeyListener extends GLKeyListener<GLDataViewIntegrator> {

	private boolean isCtrlDown;

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {

		if (event.keyCode == SWT.CTRL)
			isCtrlDown = true;

	}

	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		if (event.stateMask == SWT.CTRL)
			isCtrlDown = false;
	}

	public boolean isCtrlDown() {
		return isCtrlDown;
	}
}
