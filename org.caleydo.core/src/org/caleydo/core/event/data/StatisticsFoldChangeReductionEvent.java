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
 * @author Alexander Lex
 * @author Marc Streit
 */
public class StatisticsFoldChangeReductionEvent
	extends AEvent {

	private TablePerspective dataContainer1;
	private TablePerspective dataContainer2;
	/**
	 * A fold change can be calculated between the records or the dimensions of the two specified containers.
	 * If this flag is set to be true, it is calculated for the records, els for the dimensions
	 */
	private boolean betweenRecords;

	/**
	 * @param dataContainer1
	 *            set to {@link #dataContainer1}
	 * @param dataContainer2
	 *            set to {@link #dataContainer2}
	 * @param betweenRecords
	 *            set to {@link #betweenRecords}
	 */
	public StatisticsFoldChangeReductionEvent(TablePerspective dataContainer1, TablePerspective dataContainer2,
		boolean betweenRecords) {
		this.dataContainer1 = dataContainer1;
		this.dataContainer2 = dataContainer2;
		this.betweenRecords = betweenRecords;
	}

	/**
	 * @return the dataContainer1, see {@link #dataContainer1}
	 */
	public TablePerspective getDataContainer1() {
		return dataContainer1;
	}

	/**
	 * @param dataContainer1
	 *            setter, see {@link #dataContainer1}
	 */
	public void setDataContainer1(TablePerspective dataContainer1) {
		this.dataContainer1 = dataContainer1;
	}

	/**
	 * @return the dataContainer2, see {@link #dataContainer2}
	 */
	public TablePerspective getDataContainer2() {
		return dataContainer2;
	}

	/**
	 * @param dataContainer2
	 *            setter, see {@link #dataContainer2}
	 */
	public void setDataContainer2(TablePerspective dataContainer2) {
		this.dataContainer2 = dataContainer2;
	}

	/**
	 * @return the betweenRecords, see {@link #betweenRecords}
	 */
	public boolean isBetweenRecords() {
		return betweenRecords;
	}

	/**
	 * @param betweenRecords
	 *            setter, see {@link #betweenRecords}
	 */
	public void setBetweenRecords(boolean betweenRecords) {
		this.betweenRecords = betweenRecords;
	}

	@Override
	public boolean checkIntegrity() {

		if (dataContainer1 == null || dataContainer2 == null)
			return false;

		return true;
	}
}
