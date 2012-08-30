package org.caleydo.view.stratomex.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.view.stratomex.vendingmachine.VendingMachine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class VendingMachineKeyListener extends GLKeyListener<VendingMachine> {

	private VendingMachine vendingMachine;

	public VendingMachineKeyListener(VendingMachine vendingMachine) {

		this.vendingMachine = vendingMachine;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {

		// if ctrl, alt, or shift is pressed do nothing --> HHM handles this
		// events
		if (event.stateMask == SWT.CTRL || event.stateMask == SWT.ALT
				|| event.stateMask == SWT.SHIFT)
			return;

		switch (event.keyCode) {
		case SWT.ARROW_UP:
			vendingMachine.highlightNextPreviousVisBrick(false);
			break;
		case SWT.ARROW_DOWN:
			vendingMachine.highlightNextPreviousVisBrick(true);
			break;
		case SWT.CR:
			vendingMachine.selectChoice();
			break;
		}

	}

	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		// TODO Auto-generated method stub

	}

}
