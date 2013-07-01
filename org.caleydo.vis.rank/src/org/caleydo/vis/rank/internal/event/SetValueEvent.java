/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.internal.event;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.mixin.ISetableColumnMixin;

/**
 * simple generic event for filtering changes
 *
 * @author Samuel Gratzl
 *
 */
public class SetValueEvent extends ADirectedEvent {
	private IRow row;
	private ISetableColumnMixin col;
	private String value;

	public SetValueEvent(IRow row, ISetableColumnMixin col, String value) {
		this.row = row;
		this.col = col;
		this.value = value;
	}

	public IRow getRow() {
		return row;
	}

	public ISetableColumnMixin getCol() {
		return col;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean checkIntegrity() {
		return col != null;
	}
}
