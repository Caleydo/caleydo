/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
