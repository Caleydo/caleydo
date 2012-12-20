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
package org.caleydo.view.tourguide.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.api.score.CollapseScore;
import org.caleydo.view.tourguide.api.score.DefaultComputedReferenceGroupScore;
import org.caleydo.view.tourguide.api.score.ui.ACreateGroupScoreDialog;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.algorithm.JaccardIndex;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class JaccardIndexScoreFactory implements IScoreFactory {
	private IRegisteredScore createJaccard(String label, TablePerspective reference, Group group) {
		return new DefaultComputedReferenceGroupScore(label, reference, group, JaccardIndex.get(), null);
	}

	private IRegisteredScore createJaccardME(String label, TablePerspective reference, Group group) {
		return new DefaultComputedReferenceGroupScore(label, reference, group, JaccardIndex.get(),
				ComputeScoreFilters.MUTUAL_EXCLUSIVE);
	}

	@Override
	public Iterable<Pair<String, IScore>> createGroupEntries(TablePerspective strat, Group group) {
		Collection<Pair<String, IScore>> col = new ArrayList<>();
		col.add(Pair.make("Score group", (IScore) createJaccard(null, strat, group)));
		col.add(Pair.make("Score group  (Mutual Exclusive)", (IScore) createJaccardME(null, strat, group)));
		return col;
	}

	@Override
	public Iterable<Pair<String, IScore>> createStratEntries(TablePerspective strat) {
		Collection<Pair<String, IScore>> col = new ArrayList<>();
		CollapseScore composite = new CollapseScore(strat.getRecordPerspective().getLabel());
		for (Group group : strat.getRecordPerspective().getVirtualArray().getGroupList()) {
			composite.add(createJaccard(null, strat, group));
		}
		col.add(Pair.make("Score all groups in column", (IScore) composite));
		composite = new CollapseScore(strat.getRecordPerspective().getLabel());
		for (Group group : strat.getRecordPerspective().getVirtualArray().getGroupList()) {
			composite.add(createJaccardME(null, strat, group));
		}
		col.add(Pair.make("Score all groups in column (Mutual Exclusive)", (IScore) composite));
		return col;
	}

	@Override
	public Dialog createCreateDialog(Shell shell, ScoreQueryUI sender) {
		return new CreateJaccardIndexScoreDialog(shell, sender);
	}

	class CreateJaccardIndexScoreDialog extends ACreateGroupScoreDialog {
		private Button mututalExclusiveUI;

		public CreateJaccardIndexScoreDialog(Shell shell, ScoreQueryUI sender) {
			super(shell, sender);
		}

		@Override
		protected String getLabel() {
			return "Jaccard Index Score";
		}

		@Override
		protected void addTypeSpecific(Composite c) {
			Label l = new Label(c, SWT.NONE);
			l.setText("");
			mututalExclusiveUI = new Button(c, SWT.CHECK);
			mututalExclusiveUI.setText("Mutual Exclusive");
		}

		@Override
		protected IRegisteredScore createScore(String label, TablePerspective strat, Group g) {
			boolean m = mututalExclusiveUI.getSelection();
			if (m)
				return createJaccardME(label, strat, g);
			else
				return createJaccard(label, strat, g);
		}
	}
}

