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
package org.caleydo.view.tourguide.event;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;

/**
 * Event for opening the vending machine within Stratomex with a specified reference table perspective and the brick
 * column.
 *
 * @author Marc Streit
 *
 */
public class ScoreTablePerspectiveEvent extends AEvent {

	public enum EScoreType {
		ADJUSTED_RAND, JACCARD_ALL, JACCARD, JACCARD_ALL_MUTUAL_EXCLUSIVE, JACCARD_MUTUAL_EXCLUSIVE;
	}

	private TablePerspective stratification;

	private TablePerspective group;

	private EScoreType mode;


	public ScoreTablePerspectiveEvent() {

	}

	public ScoreTablePerspectiveEvent(EScoreType mode, TablePerspective stratification) {
		this(mode, stratification, null);
	}

	public ScoreTablePerspectiveEvent(EScoreType mode, TablePerspective stratification,
			TablePerspective group) {
		this.mode = mode;
		this.group = group;
		this.stratification = stratification;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public TablePerspective getGroup() {
		return group;
	}

	/**
	 * @return the stratification, see {@link #stratification}
	 */
	public TablePerspective getStratification() {
		return stratification;
	}

	/**
	 * @return the mode, see {@link #mode}
	 */
	public EScoreType getMode() {
		return mode;
	}
}
