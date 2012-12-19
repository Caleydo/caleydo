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

	private TablePerspective tablePerspective1;
	private TablePerspective tablePerspective2;
	/**
	 * A fold change can be calculated between the records or the dimensions of the two specified containers.
	 * If this flag is set to be true, it is calculated for the records, els for the dimensions
	 */
	private boolean betweenRecords;

	/**
	 * @param tablePerspective1
	 *            set to {@link #tablePerspective1}
	 * @param tablePerspective2
	 *            set to {@link #tablePerspective2}
	 * @param betweenRecords
	 *            set to {@link #betweenRecords}
	 */
	public StatisticsFoldChangeReductionEvent(TablePerspective tablePerspective1, TablePerspective tablePerspective2,
		boolean betweenRecords) {
		this.tablePerspective1 = tablePerspective1;
		this.tablePerspective2 = tablePerspective2;
		this.betweenRecords = betweenRecords;
	}

	/**
	 * @return the tablePerspective1, see {@link #tablePerspective1}
	 */
	public TablePerspective getTablePerspective1() {
		return tablePerspective1;
	}

	/**
	 * @param tablePerspective1
	 *            setter, see {@link #tablePerspective1}
	 */
	public void setTablePerspective1(TablePerspective tablePerspective1) {
		this.tablePerspective1 = tablePerspective1;
	}

	/**
	 * @return the tablePerspective2, see {@link #tablePerspective2}
	 */
	public TablePerspective getTablePerspective2() {
		return tablePerspective2;
	}

	/**
	 * @param tablePerspective2
	 *            setter, see {@link #tablePerspective2}
	 */
	public void setTablePerspective2(TablePerspective tablePerspective2) {
		this.tablePerspective2 = tablePerspective2;
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

		if (tablePerspective1 == null || tablePerspective2 == null)
			return false;

		return true;
	}
}
