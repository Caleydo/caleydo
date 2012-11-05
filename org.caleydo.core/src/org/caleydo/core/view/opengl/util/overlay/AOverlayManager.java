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
package org.caleydo.core.view.opengl.util.overlay;

import java.awt.Point;
import org.caleydo.core.view.opengl.util.AGLGUIElement;

/**
 * Base class for overlays such as info areas or context menus
 * 
 * @author Alexander Lex
 */
public abstract class AOverlayManager
	extends AGLGUIElement {

	/** The 2D coordinates where the mouse was clicked */
	protected Point pickedPoint;
	/** The width of the window where the overlay is rendered */
	protected int windowWidth;
	/** The height of the window where the overlay is rendered */
	protected int windowHeight;

	/** The opengl coordinates of the left border in the z plane where the menu is rendered */
	protected float fLeftBorder;
	/** The opengl coordinates of the right border in the z plane where the menu is rendered */
	protected float fRightBorder;
	/** The opengl coordinates of the top border in the z plane where the menu is rendered */
	protected float fTopBorder;
	/** The opengl coordinates of the bottom border in the z plane where the menu is rendered */
	protected float fBottomBorder;

	/** Flag determining whether the overlay is enabled and should be rendered or not */
	protected boolean isEnabled = false;
	/**
	 * Flag determining whether the overlay is beeing rendered for the first time. This is important since the
	 * overlay should not follow the mouse, but remain static once initialized
	 */
	protected boolean isFirstTime = false;

	/**
	 * Set the location where the overlay should appear. The picked point specifies the region next to which
	 * the overlay will be appear, windowWidth and windowHeight are needed for optimal placement of the
	 * rendered elements
	 * 
	 * @param pickedPoint
	 * @param windowWidth
	 * @param windowHeight
	 */
	public void setLocation(Point pickedPoint, int windowWidth, int windowHeight) {
		this.pickedPoint = pickedPoint;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		isFirstTime = true;
		isEnabled = true;
	}

	/**
	 * Disables the overlay and resets the relevant elements. This method is intended to be overridden but
	 * should be called by its overriding method
	 */
	public void flush() {
		pickedPoint = null;
		isEnabled = false;
	}

}
