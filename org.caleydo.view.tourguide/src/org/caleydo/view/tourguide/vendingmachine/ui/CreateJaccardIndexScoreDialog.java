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
package org.caleydo.view.tourguide.vendingmachine.ui;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.data.score.IGroupScore;
import org.caleydo.view.tourguide.data.score.JaccardIndexScore;
import org.caleydo.view.tourguide.vendingmachine.ScoreQueryUI;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class CreateJaccardIndexScoreDialog extends ACreateGroupScoreDialog {
	public CreateJaccardIndexScoreDialog(Shell shell, ScoreQueryUI sender) {
		super(shell, sender);
	}

	@Override
	protected String getLabel() {
		return "Jaccard Index Score";
	}

	@Override
	protected void addTypeSpecific(Composite c) {

	}

	@Override
	protected IGroupScore createScore(String label, TablePerspective strat, Group g) {
		return new JaccardIndexScore(label, strat, g);
	}
}
