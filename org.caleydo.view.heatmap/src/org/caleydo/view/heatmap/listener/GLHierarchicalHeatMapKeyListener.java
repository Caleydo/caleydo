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
package org.caleydo.view.heatmap.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.view.keyboard.WrapperKeyEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLHierarchicalHeatMapKeyListener extends
		GLKeyListener<GLHierarchicalHeatMap> {

	private GLHierarchicalHeatMap glHierarchicalHeatMap;

	public GLHierarchicalHeatMapKeyListener(GLHierarchicalHeatMap glHierarchicalHeatMap) {

		this.glHierarchicalHeatMap = glHierarchicalHeatMap;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {
		glHierarchicalHeatMap.queueEvent(this, new WrapperKeyEvent(event));

	}

	@Override
	public void handleEvent(AEvent event) {
		WrapperKeyEvent wrapperKeyEvent;
		if (event instanceof WrapperKeyEvent) {
			wrapperKeyEvent = (WrapperKeyEvent) event;
		} else
			return;

		KeyEvent keyEvent = wrapperKeyEvent.getKeyEvent();

		if (keyEvent.character == 'e') {
			glHierarchicalHeatMap.handleDendrogramActivation(false);
			return;
		} else if (keyEvent.character == 'g') {
			glHierarchicalHeatMap.handleDendrogramActivation(true);
			return;
		}

		switch (keyEvent.keyCode) {
		case SWT.ARROW_UP:

			if (keyEvent.stateMask == SWT.CTRL) {
				glHierarchicalHeatMap.handleArrowAndCtrlPressed(true);
			} else if (keyEvent.stateMask == SWT.ALT) {
				glHierarchicalHeatMap.handleArrowAndAltPressed(true);
			} else {
				glHierarchicalHeatMap.handleArrowPressed(true);
			}

			break;
		case SWT.ARROW_DOWN:

			if (keyEvent.stateMask == SWT.CTRL) {
				glHierarchicalHeatMap.handleArrowAndCtrlPressed(false);
			} else if (keyEvent.stateMask == SWT.ALT) {
				glHierarchicalHeatMap.handleArrowAndAltPressed(false);
			} else {
				glHierarchicalHeatMap.handleArrowPressed(false);
			}

			break;
		case SWT.ARROW_LEFT:

			if (keyEvent.stateMask == SWT.SHIFT) {
				glHierarchicalHeatMap.handleArrowAndShiftPressed(true);
			}
			break;
		case SWT.ARROW_RIGHT:

			if (keyEvent.stateMask == SWT.SHIFT) {
				glHierarchicalHeatMap.handleArrowAndShiftPressed(false);
			}
			break;
		}
	}

	@Override
	protected void handleKeyReleasedEvent(KeyEvent event) {
		// TODO Auto-generated method stub

	}
}
