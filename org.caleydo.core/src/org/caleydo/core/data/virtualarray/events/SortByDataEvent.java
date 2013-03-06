/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals that a virtual array should be sorted based on it's values
 *
 * @author Alexander Lex
 *
 */
public class SortByDataEvent extends AEvent {

	// private IDType idType;
	private String dataDomainID;
	/** The table persepctive that is the basis for the sorting */
	private String tablePerspectiveKey;
	/** The id of the perspective to be sorted */
	private String perspectiveID;
	/** The id of the row/column out of the "other" perspective that is used for determining the sorting */
	private Integer id;

	/**
	 *
	 */
	public SortByDataEvent() {
	}

	public SortByDataEvent(String dataDomainID, String tablePerspectiveKey, String perspectiveID, Integer id) {
		this.dataDomainID = dataDomainID;
		this.tablePerspectiveKey = tablePerspectiveKey;
		this.perspectiveID = perspectiveID;
		this.id = id;
	}

	/**
	 * @param dataDomainID
	 *            setter, see {@link dataDomainID}
	 */
	public void setDataDomainID(String dataDomainID) {
		this.dataDomainID = dataDomainID;
	}

	/**
	 * @return the dataDomainID, see {@link #dataDomainID}
	 */
	public String getDataDomainID() {
		return dataDomainID;
	}

	/**
	 * @param perspectiveID
	 *            setter, see {@link perspectiveID}
	 */
	public void setPerspectiveID(String perspectiveID) {
		this.perspectiveID = perspectiveID;
	}

	/**
	 * @return the perspectiveID, see {@link #perspectiveID}
	 */
	public String getPerspectiveID() {
		return perspectiveID;
	}

	/**
	 * @param tablePerspectiveKey
	 *            setter, see {@link tablePerspectiveKey}
	 */
	public void setTablePerspectiveKey(String tablePerspectiveKey) {
		this.tablePerspectiveKey = tablePerspectiveKey;
	}

	/**
	 * @return the tablePerspectiveKey, see {@link #tablePerspectiveKey}
	 */
	public String getTablePerspectiveKey() {
		return tablePerspectiveKey;
	}

	/**
	 * @param id
	 *            setter, see {@link id}
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public Integer getId() {
		return id;
	}

	@Override
	public boolean checkIntegrity() {
		if (id != null && tablePerspectiveKey != null && dataDomainID != null && perspectiveID != null)
			return true;

		return false;
	}

}
