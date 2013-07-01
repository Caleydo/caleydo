/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
