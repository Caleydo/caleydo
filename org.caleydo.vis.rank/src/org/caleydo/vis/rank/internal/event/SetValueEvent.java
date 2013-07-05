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
