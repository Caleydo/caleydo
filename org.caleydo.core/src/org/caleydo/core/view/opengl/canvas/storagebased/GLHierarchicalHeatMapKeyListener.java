package org.caleydo.core.view.opengl.canvas.storagebased;

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
			// case SWT.CTRL:
			// System.out.println("CTRL");
			// break;
			// case SWT.ALT:
			// System.out.println("ALT");
			// break;
			// case SWT.END:
			// System.out.println("END");
			// break;
			// case SWT.DEL:
			// System.out.println("DEL");
			// break;
			case SWT.ARROW_UP:
				glHierarchicalHeatMap.getEmbeddedHeatMap().upDownSelect(true);
				break;
			case SWT.ARROW_DOWN:
				glHierarchicalHeatMap.getEmbeddedHeatMap().upDownSelect(false);
				break;
			case SWT.ARROW_LEFT:
				glHierarchicalHeatMap.getEmbeddedHeatMap().leftRightSelect(true);
				break;
			case SWT.ARROW_RIGHT:
				glHierarchicalHeatMap.getEmbeddedHeatMap().leftRightSelect(false);
				break;
			case SWT.F2:
				glHierarchicalHeatMap.pageUpDownSelected(true);
				break;
			case SWT.F3:
				glHierarchicalHeatMap.pageUpDownSelected(false);
				break;
			// default:
			// System.out.println(event.keyCode);
			// break;
		}

	}
}
