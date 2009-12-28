package org.caleydo.core.view.opengl.canvas.radial;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.events.KeyEvent;

/**
 * Listener for keyboard events for the radial hierarchy.
 * 
 * @author Christian Partl
 */
public class GLRadialHierarchyKeyListener
	extends GLKeyListener<GLRadialHierarchy> {

	GLRadialHierarchy radialHierarchy;

	/**
	 * Constructor.
	 * 
	 * @param radialHierarchy
	 *            Instance of the radial hierarchy that should handle the keyboard events.
	 */
	public GLRadialHierarchyKeyListener(GLRadialHierarchy radialHierarchy) {
		this.radialHierarchy = radialHierarchy;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
		if (event.character == 'd') {
			radialHierarchy.handleKeyboardAlternativeDiscSelection();
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
