/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.core.view.opengl.keyboard;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.events.KeyEvent;

/**
 * Keyboard listener for turning FPS counter on and off using F
 * 
 * @author Marc Streit
 * 
 */
public class GLFPSKeyListener extends GLKeyListener<AGLView> {

	private AGLView glView;

	/**
	 * Constructor.
	 * 
	 */
	public GLFPSKeyListener(AGLView glView) {
		this.glView = glView;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
		if (event.character == 'f')
			glView.toggleFPSCounter();
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
