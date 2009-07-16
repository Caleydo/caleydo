package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLHeatMapKeyListener
	extends GLKeyListener<GLHeatMap> {

	private GLHeatMap glHeatMap;

	public GLHeatMapKeyListener(GLHeatMap glHeatMap) {

		this.glHeatMap = glHeatMap;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
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
		}

	}

	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub
		
	}

}
