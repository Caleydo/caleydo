package org.caleydo.core.view.opengl.canvas.storagebased;

import org.caleydo.core.manager.event.view.keyboard.ArrowDownAltPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowDownCtrlPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowDownPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowLeftPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowRightPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowUpAltPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowUpCtrlPressedEvent;
import org.caleydo.core.manager.event.view.keyboard.ArrowUpPressedEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLHierarchicalHeatMapKeyListener
	extends GLKeyListener {

	private GLHierarchicalHeatMap glHierarchicalHeatMap;

	public GLHierarchicalHeatMapKeyListener(GLHierarchicalHeatMap glHierarchicalHeatMap) {

		this.glHierarchicalHeatMap = glHierarchicalHeatMap;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
		switch (event.keyCode) {
			case SWT.ARROW_UP:

				if (event.stateMask == SWT.CTRL) {
					ArrowUpCtrlPressedEvent arrowUpCtrlPressedEvent = new ArrowUpCtrlPressedEvent();
					GeneralManager.get().getEventPublisher().triggerEvent(arrowUpCtrlPressedEvent);
				}
				else if (event.stateMask == SWT.ALT) {
					ArrowUpAltPressedEvent arrowUpAltPressedEvent = new ArrowUpAltPressedEvent();
					GeneralManager.get().getEventPublisher().triggerEvent(arrowUpAltPressedEvent);
				}
				else {
					ArrowUpPressedEvent arrowUpPressedEvent = new ArrowUpPressedEvent();
					GeneralManager.get().getEventPublisher().triggerEvent(arrowUpPressedEvent);
				}

				break;
			case SWT.ARROW_DOWN:

				if (event.stateMask == SWT.CTRL) {
					ArrowDownCtrlPressedEvent arrowDownCtrlPressedEvent = new ArrowDownCtrlPressedEvent();
					GeneralManager.get().getEventPublisher().triggerEvent(arrowDownCtrlPressedEvent);
				}
				else if (event.stateMask == SWT.ALT) {
					ArrowDownAltPressedEvent arrowDownAltPressedEvent = new ArrowDownAltPressedEvent();
					GeneralManager.get().getEventPublisher().triggerEvent(arrowDownAltPressedEvent);
				}
				else {
					ArrowDownPressedEvent arrowDownPressedEvent = new ArrowDownPressedEvent();
					GeneralManager.get().getEventPublisher().triggerEvent(arrowDownPressedEvent);
				}

				break;
			case SWT.ARROW_LEFT:

				ArrowLeftPressedEvent arrowLeftPressedEvent = new ArrowLeftPressedEvent();
				GeneralManager.get().getEventPublisher().triggerEvent(arrowLeftPressedEvent);

				break;
			case SWT.ARROW_RIGHT:

				ArrowRightPressedEvent arrowRightPressedEvent = new ArrowRightPressedEvent();
				GeneralManager.get().getEventPublisher().triggerEvent(arrowRightPressedEvent);

				break;
		}
	}
}
