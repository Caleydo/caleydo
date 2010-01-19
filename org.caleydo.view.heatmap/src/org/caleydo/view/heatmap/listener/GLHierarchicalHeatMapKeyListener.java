package org.caleydo.view.heatmap.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.keyboard.WrapperKeyEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.view.heatmap.GLHierarchicalHeatMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLHierarchicalHeatMapKeyListener
		extends
			GLKeyListener<GLHierarchicalHeatMap> {

	private GLHierarchicalHeatMap glHierarchicalHeatMap;

	public GLHierarchicalHeatMapKeyListener(
			GLHierarchicalHeatMap glHierarchicalHeatMap) {

		this.glHierarchicalHeatMap = glHierarchicalHeatMap;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
		glHierarchicalHeatMap.queueEvent(this, new WrapperKeyEvent(event));

	}

	@Override
	public void handleEvent(AEvent event) {
		WrapperKeyEvent wrapperKeyEvent;
		if (event instanceof WrapperKeyEvent) {
			wrapperKeyEvent = (WrapperKeyEvent) event;
		} else
			return;

		KeyEvent keyEvent = wrapperKeyEvent.getKeyEvent();

		if (keyEvent.character == 'e') {
			glHierarchicalHeatMap.handleDendrogramActivation(false);
			return;
		} else if (keyEvent.character == 'g') {
			glHierarchicalHeatMap.handleDendrogramActivation(true);
			return;
		}

		switch (keyEvent.keyCode) {
			case SWT.ARROW_UP :

				if (keyEvent.stateMask == SWT.CTRL) {
					glHierarchicalHeatMap.handleArrowAndCtrlPressed(true);
				} else if (keyEvent.stateMask == SWT.ALT) {
					glHierarchicalHeatMap.handleArrowAndAltPressed(true);
				} else {
					glHierarchicalHeatMap.handleArrowPressed(true);
				}

				break;
			case SWT.ARROW_DOWN :

				if (keyEvent.stateMask == SWT.CTRL) {
					glHierarchicalHeatMap.handleArrowAndCtrlPressed(false);
				} else if (keyEvent.stateMask == SWT.ALT) {
					glHierarchicalHeatMap.handleArrowAndAltPressed(false);
				} else {
					glHierarchicalHeatMap.handleArrowPressed(false);
				}

				break;
			case SWT.ARROW_LEFT :

				if (keyEvent.stateMask == SWT.SHIFT) {
					glHierarchicalHeatMap.handleArrowAndShiftPressed(true);
				}
				break;
			case SWT.ARROW_RIGHT :

				if (keyEvent.stateMask == SWT.SHIFT) {
					glHierarchicalHeatMap.handleArrowAndShiftPressed(false);
				}
				break;
		}
	}

	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		// TODO Auto-generated method stub

	}
}
