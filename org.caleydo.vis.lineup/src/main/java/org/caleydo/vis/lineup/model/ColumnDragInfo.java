/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.vis.lineup.model;

import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;

/**
 * @author Samuel Gratzl
 *
 */
public class ColumnDragInfo implements IDragInfo {
	private final ARankColumnModel model;

	public ColumnDragInfo(ARankColumnModel model) {
		this.model = model;
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
