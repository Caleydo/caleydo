/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.view.filterpipeline;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * Keyboard listener for GLFilterPipeline
 * 
 * @author Thomas Geymayer
 * 
 */
public class GLFilterPipelineKeyListener extends GLKeyListener<GLFilterPipeline> {

	private GLFilterPipeline glFilterPipeline;

	/**
	 * Constructor.
	 * 
	 * @param glFilterPipeline
	 *            Instance of the filterpipeline that should handle the keyboard
	 *            events.
	 */
	public GLFilterPipelineKeyListener(GLFilterPipeline glFilterPipeline) {
		this.glFilterPipeline = glFilterPipeline;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.caleydo.core.view.opengl.keyboard.GLKeyListener#handleKeyPressedEvent
	 * (org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
		if (event.keyCode == SWT.CONTROL || event.keyCode == SWT.MOD1)
			glFilterPipeline.setControlPressed(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.caleydo.core.view.opengl.keyboard.GLKeyListener#handleKeyReleasedEvent
	 * (org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		if (event.keyCode == SWT.CONTROL || event.keyCode == SWT.MOD1)
			glFilterPipeline.setControlPressed(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.caleydo.core.manager.event.AEventListener#handleEvent(org.caleydo
	 * .core.manager.event.AEvent)
	 */
	@Override
	public void handleEvent(AEvent event) {
		// TODO Auto-generated method stub

	}

}
