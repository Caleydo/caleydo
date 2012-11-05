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
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

public class GLHeatMapKeyListener extends GLKeyListener<GLHeatMap> {

	private GLHeatMap glHeatMap;

	public GLHeatMapKeyListener(GLHeatMap glHeatMap) {

		this.glHeatMap = glHeatMap;
	}

	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {

		// if ctrl, alt, or shift is pressed do nothing --> HHM handles this
		// events
		if (event.stateMask == SWT.CTRL || event.stateMask == SWT.ALT
				|| event.stateMask == SWT.SHIFT)
			return;

		switch (event.keyCode) {
		case SWT.ARROW_UP:
			glHeatMap.upDownSelect(true);
			break;
		case SWT.ARROW_DOWN:
			glHeatMap.upDownSelect(false);
			break;
		case SWT.ARROW_LEFT:
			glHeatMap.leftRightSelect(true);
			break;
		case SWT.ARROW_RIGHT:
			glHeatMap.leftRightSelect(false);
			break;
		case SWT.CR:
			glHeatMap.enterPressedSelect();
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
