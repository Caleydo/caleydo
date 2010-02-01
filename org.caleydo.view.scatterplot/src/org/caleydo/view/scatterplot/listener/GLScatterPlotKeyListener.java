package org.caleydo.view.scatterplot.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.view.scatterplot.GLScatterplot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLScatterPlotKeyListener extends GLKeyListener<GLScatterplot> {

	private GLScatterplot glScatterplot;

	public GLScatterPlotKeyListener(GLScatterplot glHeatMap) {

		this.glScatterplot = glHeatMap;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {

		// // if ctrl, alt, or shift is pressed do nothing --> HHM handles this
		// events
		// if (event.stateMask == SWT.CTRL || event.stateMask == SWT.ALT ||
		// event.stateMask == SWT.SHIFT)
		// return;

		if (event.character == 'b') {
			glScatterplot.toggleSpecialAxisMode();
			return;
		}

		if (event.character == 'p') {
			glScatterplot.togglePointType();
			return;
		}

		if (event.character == 'd') {
			glScatterplot.toggleDetailLevel();
			return;
		}
		
		if (event.character == 'm') {
			glScatterplot.toggleMatrixMode();
			return;
		}
		
		if (event.character == 'c') {
			glScatterplot.toggleColorMode();
			return;
		}
		
		switch (event.keyCode) {
			case SWT.ARROW_UP :
				glScatterplot.upDownSelect(false);
				break;
			case SWT.ARROW_DOWN :
				glScatterplot.upDownSelect(true);
				break;
			case SWT.ARROW_LEFT :
				glScatterplot.leftRightSelect(false);
				break;
			case SWT.ARROW_RIGHT :
				glScatterplot.leftRightSelect(true);
				break;
			case SWT.HOME :
				glScatterplot.ResetSelection();
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
