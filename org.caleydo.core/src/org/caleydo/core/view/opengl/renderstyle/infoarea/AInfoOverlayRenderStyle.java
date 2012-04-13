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
package org.caleydo.core.view.opengl.renderstyle.infoarea;

import java.awt.Color;

/**
 * Render style for info overlay panel in OpenGL2 views.
 * 
 * @author Marc Streit
 */
public class AInfoOverlayRenderStyle {

	public final static int MAX_OVERLAY_HEIGHT = 300;

	public final static int OVERLAY_WIDTH = 800;

	public final static int LINE_HEIGHT = 20;

	public final static Color fontColor = Color.WHITE;

	public final static Color backgroundColor = new Color(0, 0, 0, 0.3f);

	public final static Color borderColor = Color.DARK_GRAY;

	public enum VerticalPosition {
		TOP,
		CENTER,
		BOTTOM
	}
}
