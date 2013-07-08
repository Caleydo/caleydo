/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords;

/**
 * @author alexsb
 *
 */
public enum EPickingType {
	// parallel coordinates
	POLYLINE_SELECTION,
	X_AXIS_SELECTION,
	Y_AXIS_SELECTION,
	GATE_TIP_SELECTION,
	GATE_BODY_SELECTION,
	GATE_BOTTOM_SELECTION,
	ADD_GATE,
	ADD_MASTER_GATE,
	REMOVE_GATE,
	PC_ICON_SELECTION,
	MOVE_AXIS,
	REMOVE_AXIS,
	DUPLICATE_AXIS,
	ANGULAR_UPPER,
	ANGULAR_LOWER,
	/** Type for selection of views in the parallel coordinates, currently the heat map */
	PCS_VIEW_SELECTION,
	REMOVE_NAN,

}
