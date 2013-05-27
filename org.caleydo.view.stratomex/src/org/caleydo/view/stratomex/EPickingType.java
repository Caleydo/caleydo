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
package org.caleydo.view.stratomex;

/**
 *
 * Picking Types for VisBricks
 *
 * @author Alexander Lex
 *
 */
public enum EPickingType {

	BRICK,
	BRICK_EXPAND_BUTTON,
	BRICK_COLLAPSE_BUTTON,
	BRICK_CLOSE_BUTTON,
	BRICK_DETAIL_MODE_BUTTON,
	BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
	BRICK_VIEW_SWITCHING_MODE_BUTTON,
	BRICK_LOCK_RESIZING_BUTTON,
	BRICK_CONNECTION_BAND,
	BRICK_SPACER,
	BRICK_TITLE,
	RESIZE_HANDLE_UPPER_LEFT,
	RESIZE_HANDLE_UPPER_RIGHT,
	RESIZE_HANDLE_LOWER_LEFT,
	RESIZE_HANDLE_LOWER_RIGHT,
	MOVE_VERTICALLY_HANDLE,
	MOVE_HORIZONTALLY_HANDLE,
	EXPAND_LEFT_HANDLE,
	EXPAND_RIGHT_HANDLE,

	DIMENSION_GROUP_CLUSTER_BUTTON,
	DIMENSION_GROUP,
	DIMENSION_GROUP_SPACER,
	REMOVE_COLUMN_BUTTON;
}
