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

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.tourguide.vendingmachine.EScoreReferenceMode;

/**
 * Event for opening the vending machine within Stratomex with a specified
 * reference table perspective and the brick column.
 *
 * @author Marc Streit
 *
 */
public class ScoreTablePerspectiveEvent
	extends AEvent {

	/**
	 * Table perspectives that will be used for the scoring.
	 */
	private List<TablePerspective> referenceTablePerspectives;

	private EScoreReferenceMode scoreReferenceMode;

	/**
	 * The StratomeX reference column on which the user triggered the scoring.
	 * This can be null if not triggered via StratomeX.
	 */
	private BrickColumn referenceBrickColumn;

	/**
	 * Constructor.
	 *
	 * @param referenceTablePerspective TablePerspective to which the scoring
	 *            will be calculated.
	 */
	public ScoreTablePerspectiveEvent(EScoreReferenceMode scoreReferenceMode,
			List<TablePerspective> referenceTablePerspectives, BrickColumn referenceBrickColumn) {

		this.scoreReferenceMode = scoreReferenceMode;
		this.referenceTablePerspectives = referenceTablePerspectives;
		this.referenceBrickColumn = referenceBrickColumn;
	}

	public ScoreTablePerspectiveEvent(EScoreReferenceMode scoreReferenceMode,
			TablePerspective referenceTablePerspective, BrickColumn referenceBrickColumn) {

		this.scoreReferenceMode = scoreReferenceMode;
		this.referenceBrickColumn = referenceBrickColumn;
		this.referenceTablePerspectives = new ArrayList<TablePerspective>();
		referenceTablePerspectives.add(referenceTablePerspective);
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the referenceTablePerspectives, see
	 *         {@link #referenceTablePerspectives}
	 */
	public List<TablePerspective> getReferenceTablePerspectives() {
		return referenceTablePerspectives;
	}

	/**
	 * @param referenceTablePerspectives setter, see
	 *            {@link #referenceTablePerspectives}
	 */
	public void setReferenceTablePerspectives(List<TablePerspective> referenceTablePerspectives) {
		this.referenceTablePerspectives = referenceTablePerspectives;
	}

	/**
	 * @param referenceBrickColumn setter, see {@link #referenceBrickColumn}
	 */
	public void setReferenceBrickColumn(BrickColumn referenceBrickColumn) {
		this.referenceBrickColumn = referenceBrickColumn;
	}

	/**
	 * @return the referenceBrickColumn, see {@link #referenceBrickColumn}
	 */
	public BrickColumn getReferenceBrickColumn() {
		return referenceBrickColumn;
	}

	/**
	 * @param scoreReferenceMode setter, see {@link #scoreReferenceMode}
	 */
	public void setScoreReferenceMode(EScoreReferenceMode scoreReferenceMode) {
		this.scoreReferenceMode = scoreReferenceMode;
	}

	/**
	 * @return the scoreReferenceMode, see {@link #scoreReferenceMode}
	 */
	public EScoreReferenceMode getScoreReferenceMode() {
		return scoreReferenceMode;
	}
}
