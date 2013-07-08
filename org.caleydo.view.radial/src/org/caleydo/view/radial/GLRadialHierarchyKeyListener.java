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
package org.caleydo.view.radial;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.events.KeyEvent;

/**
 * Listener for keyboard events for the radial hierarchy.
 * 
 * @author Christian Partl
 */
public class GLRadialHierarchyKeyListener extends GLKeyListener<GLRadialHierarchy> {

	GLRadialHierarchy radialHierarchy;

	/**
	 * Constructor.
	 * 
	 * @param radialHierarchy
	 *            Instance of the radial hierarchy that should handle the
	 *            keyboard events.
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
