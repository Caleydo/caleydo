package org.caleydo.core.view.opengl.canvas.radial;

import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.events.KeyEvent;

public class GLRadialHierarchyKeyListener
	extends GLKeyListener {
	
	GLRadialHierarchy radialHierarchy;
	
	public GLRadialHierarchyKeyListener(GLRadialHierarchy radialHierarchy) {
		this.radialHierarchy = radialHierarchy;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
		if(event.character == 'd') {
			radialHierarchy.handleKeyboardAlternativeDiscSelection();
		}

	}

}
