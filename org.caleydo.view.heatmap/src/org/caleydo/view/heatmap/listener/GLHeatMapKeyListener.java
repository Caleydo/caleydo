/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLHeatMapKeyListener extends GLKeyListener<GLHeatMap> {

	private GLHeatMap glHeatMap;

	public GLHeatMapKeyListener(GLHeatMap glHeatMap) {

		this.glHeatMap = glHeatMap;
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
			glHeatMap.upDownSelect(true);
			break;
		case SWT.ARROW_DOWN:
			glHeatMap.upDownSelect(false);
			break;
		case SWT.ARROW_LEFT:
			glHeatMap.leftRightSelect(true);
			break;
		case SWT.ARROW_RIGHT:
			glHeatMap.leftRightSelect(false);
			break;
		case SWT.CR:
			glHeatMap.enterPressedSelect();
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
