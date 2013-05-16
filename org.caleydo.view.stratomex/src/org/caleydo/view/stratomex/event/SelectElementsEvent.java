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
package org.caleydo.view.stratomex.event;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.id.IDType;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectElementsEvent extends ADirectedEvent {
	private Iterable<Integer> ids;
	private IDType idType;
	private SelectionType selectionType;


	public SelectElementsEvent(Iterable<Integer> ids, IDType idType, SelectionType selectionType) {
		this.ids = ids;
		this.selectionType = selectionType;
		this.idType = idType;
	}

	@Override
	public boolean checkIntegrity() {
		return ids != null && idType != null && selectionType != null;
	}

	/**
	 * @return the ids, see {@link #ids}
	 */
	public Iterable<Integer> getIds() {
		return ids;
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}

	/**
	 * @return
	 */
	public SelectionType getSelectionType() {
		return selectionType;
	}

}
