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
package org.caleydo.core.view.opengl.renderstyle;

import org.caleydo.core.view.opengl.util.vislink.EVisLinkStyleType;

public class ConnectionLineRenderStyle {

	public static final float[] CONNECTION_AREA_COLOR = { 0.812f, 0.812f, 0.116f, 0.65f };

	public static final float[] CONNECTION_LINE_COLOR_1 = { 1f, 1f, 0f, 1f };
	public static final float[] CONNECTION_LINE_COLOR_2 = { 0.25f, 0.6f, 1f, 1f };

	public static float[] CONNECTION_LINE_COLOR = { 1f, 1f, 0f, 1f };// SelectionType.MOUSE_OVER.getColor();
	// public static float[] CONNECTION_LINE_COLOR = { 0.54f, 0.17f, 0.89f, 1f}; // blue-violet
	// public static float[] CONNECTION_LINE_COLOR = { 0.79f, 1f, 0.44f, 1f}; // dark olive green
	// public static float[] CONNECTION_LINE_COLOR = { 1f, 0.49f, 0.31f, 1f}; // coral

	public static float CONNECTION_LINE_WIDTH = 2.0f;

	public static final float CONNECTION_LINE_SHADOW_WIDTH_FACTOR = 1.75f;

	public static final float CONNECTION_LINE_HALO_WIDTH_FACTOR = 2.0f;

	public static final float[] CONNECTION_LINE_SHADOW_COLOR = { 0.4f, 0.4f, 0.4f, 0.8f };

	public static EVisLinkStyleType CONNECTION_LINE_STYLE = EVisLinkStyleType.HALO_VISLINK;
	public static boolean ANIMATION = true;
	public static boolean ANIMATED_HIGHLIGHTING = false;

	public static final int ANIMATION_SPEED_IN_MILLIS = 350;
	public static final float CONNECTION_LINE_SEGMENT_LENGTH = 0.05f;
}
