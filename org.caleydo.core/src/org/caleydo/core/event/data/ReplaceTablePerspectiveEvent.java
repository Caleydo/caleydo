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
package org.caleydo.core.event.data;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;

/**
 * Event signaling that an old {@link TablePerspective} should be replaced with
 * a new one in a view identified through it's id.
 * 
 * @author Alexander Lex
 * 
 */
public class ReplaceTablePerspectiveEvent extends AEvent {

	private Integer viewID = null;
	private TablePerspective oldPerspective;
	private TablePerspective newPerspective;

	public ReplaceTablePerspectiveEvent() {

	}

	public ReplaceTablePerspectiveEvent(Integer viewID, TablePerspective newPerspective,
			TablePerspective oldPerspective) {
		this.viewID = viewID;
		this.oldPerspective = oldPerspective;
		this.newPerspective = newPerspective;
	}

	/**
	 * @param viewID
	 *            setter, see {@link #viewID}
	 */
	public void setViewID(Integer viewID) {
		this.viewID = viewID;
	}

	/**
	 * @return the viewID, see {@link #viewID}
	 */
	public Integer getViewID() {
		return viewID;
	}

	/**
	 * @param oldPerspective
	 *            setter, see {@link #oldPerspective}
	 */
	public void setOldPerspective(TablePerspective oldPerspective) {
		this.oldPerspective = oldPerspective;
	}

	/**
	 * @return the oldPerspective, see {@link #oldPerspective}
	 */
	public TablePerspective getOldPerspective() {
		return oldPerspective;
	}

	/**
	 * @param newPerspective
	 *            setter, see {@link #newPerspective}
	 */
	public void setNewPerspective(TablePerspective newPerspective) {
		this.newPerspective = newPerspective;
	}

	/**
	 * @return the newPerspective, see {@link #newPerspective}
	 */
	public TablePerspective getNewPerspective() {
		return newPerspective;
	}

	@Override
	public boolean checkIntegrity() {
		if (oldPerspective != null && newPerspective != null && viewID != null)
			return true;
		return false;
	}

}
