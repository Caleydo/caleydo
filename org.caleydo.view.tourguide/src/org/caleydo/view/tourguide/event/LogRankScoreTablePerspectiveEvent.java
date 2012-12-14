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

public class LogRankScoreTablePerspectiveEvent
	extends AEvent {

	private EScoreReferenceMode mode;
	private TablePerspective stratification;
	private TablePerspective group;
	private Integer clinicalVariable;


	public LogRankScoreTablePerspectiveEvent() {

	}

	public LogRankScoreTablePerspectiveEvent(Integer clinicalVariable, EScoreReferenceMode mode,
			TablePerspective stratification) {
		this(clinicalVariable, mode, stratification, null);
	}

	public LogRankScoreTablePerspectiveEvent(Integer clinicalVariable, EScoreReferenceMode mode,
			TablePerspective stratification,
			TablePerspective group) {
		this.clinicalVariable = clinicalVariable;
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
	 * @return the clinicalVariable, see {@link #clinicalVariable}
	 */
	public Integer getClinicalVariable() {
		return clinicalVariable;
	}

	/**
	 * @return the stratification, see {@link #stratification}
	 */
	public TablePerspective getStratification() {
		return stratification;
	}

	/**
	 * @return the scoreReferenceMode, see {@link #mode}
	 */
	public EScoreReferenceMode getMode() {
		return mode;
	}
}
