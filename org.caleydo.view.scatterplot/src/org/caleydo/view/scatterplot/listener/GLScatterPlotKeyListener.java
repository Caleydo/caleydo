package org.caleydo.view.scatterplot.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.view.scatterplot.GLScatterPlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLScatterPlotKeyListener extends GLKeyListener<GLScatterPlot> {

	private GLScatterPlot glScatterplot;

	public GLScatterPlotKeyListener(GLScatterPlot glHeatMap) {

		this.glScatterplot = glHeatMap;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {

		if (event.stateMask == SWT.CTRL) {
			switch (event.keyCode) {
			case SWT.ARROW_UP:
				glScatterplot.upDownSelect2Axis(false);
				break;
			case SWT.ARROW_DOWN:
				glScatterplot.upDownSelect2Axis(true);
				break;
			case SWT.ARROW_LEFT:
				glScatterplot.leftRightSelect2Axis(false);
				break;
			case SWT.ARROW_RIGHT:
				glScatterplot.leftRightSelect2Axis(true);
				break;
			}
			return;
		}

		// || event.stateMask == SWT.ALT ||
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

		if (event.character == 'z') {
			glScatterplot.toggleMatrixZoom();
			return;
		}

		if (event.character == 'a') {
			glScatterplot.addSelectionType();
			return;
		}

		if (event.character == 'r') {
			glScatterplot.removeSelectionType();
			return;
		}

		if (event.character == 'o') {
			glScatterplot.toggleMainViewZoom();
			return;
		}

		switch (event.keyCode) {
		case SWT.ARROW_UP:
			glScatterplot.upDownSelect(false);
			break;
		case SWT.ARROW_DOWN:
			glScatterplot.upDownSelect(true);
			break;
		case SWT.ARROW_LEFT:
			glScatterplot.leftRightSelect(false);
			break;
		case SWT.ARROW_RIGHT:
			glScatterplot.leftRightSelect(true);
			break;
		 case SWT.CR:
		      this.glScatterplot.confirmCurrentSelection();
		      break;
		case SWT.HOME:
			glScatterplot.clearAllSelections();
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
