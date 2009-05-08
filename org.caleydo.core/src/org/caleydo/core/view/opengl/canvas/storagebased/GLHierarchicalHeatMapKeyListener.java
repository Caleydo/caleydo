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
			case SWT.CTRL:
				System.out.println("CTRL");
				break;
			case SWT.ALT:
				System.out.println("ALT");
				break;
			case SWT.END:
				System.out.println("END");
				break;
			case SWT.DEL:
				System.out.println("DEL");
				break;
			default:
				System.out.println("default");
			break;
		}

	}
}
