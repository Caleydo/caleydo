/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.vis.lineup.model;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;

/**
 * @author Samuel Gratzl
 *
 */
public class ColumnDragInfo implements IDragInfo {
	private final ARankColumnModel model;
	private final Vec2f shift;

	public ColumnDragInfo(ARankColumnModel model, Vec2f shift) {
		this.model = model;
		this.shift = shift;
	}

	/**
	 * @return the shift, see {@link #shift}
	 */
	public Vec2f getShift() {
		return shift;
	}

	/**
	 * @return the model, see {@link #model}
	 */
	public ARankColumnModel getModel() {
		return model;
	}

	@Override
	public String getLabel() {
		return model.getTitle();
	}

}
