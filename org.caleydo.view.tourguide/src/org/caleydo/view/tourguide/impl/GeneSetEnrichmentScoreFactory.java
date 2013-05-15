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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.DefaultComputedStratificationScore;
import org.caleydo.view.tourguide.api.score.ui.ACreateGroupScoreDialog;
import org.caleydo.view.tourguide.api.state.ISelectGroupState;
import org.caleydo.view.tourguide.api.state.ISelectReaction;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.SimpleState;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.impl.algorithm.AGSEAAlgorithm;
import org.caleydo.view.tourguide.impl.algorithm.AGSEAAlgorithm.GSEAAlgorithmPValue;
import org.caleydo.view.tourguide.impl.algorithm.GSEAAlgorithm;
import org.caleydo.view.tourguide.impl.algorithm.PGSEAAlgorithm;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
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
public class GeneSetEnrichmentScoreFactory implements IScoreFactory {
	private final static Color color = Color.decode("#80ffb3");
	private final static Color bgColor = Color.decode("#e3f4d7");

	private IRegisteredScore createGSEA(String label, Perspective reference, Group group) {
		if (label == null)
			label = reference.getLabel() + " " + group.getLabel();
		return new GeneSetScore(label, new GSEAAlgorithm(reference, group, 1.0f), false);
	}

	private IRegisteredScore createPGSEA(String label, Perspective reference, Group group) {
		if (label == null)
			label = reference.getLabel() + " " + group.getLabel();
		return new GeneSetScore(label, new PGSEAAlgorithm(reference, group),
				false);
	}

	@Override
	public void fillStateMachine(IStateMachine stateMachine, Object eventReceiver, List<TablePerspective> existing) {
		if (existing.isEmpty())
			return;
		IState source = stateMachine.get(IStateMachine.ADD_PATHWAY);
		IState target = stateMachine.addState("GSEA", new CreateGSEAState());
		stateMachine.addTransition(source, new SimpleTransition(target,
				"Find with GSEA based on displayed stratification"));

		// Find with GSEA based on strat. not displayed -> n x n -> no
		// first select a stratification then a pathway
	}

	@Override
	public Iterable<ScoreEntry> createStratEntries(TablePerspective strat) {
		return Collections.emptyList();
	}

	@Override
	public Iterable<ScoreEntry> createGroupEntries(TablePerspective strat, Group group) {
		// FIXME hack
		if (!strat.getDataDomain().getLabel().toLowerCase().contains("mrna"))
			return Collections.emptyList();
		Collection<ScoreEntry> col = new ArrayList<>();

		{
			GSEAAlgorithm algorithm = new GSEAAlgorithm(strat.getRecordPerspective(), group, 1.0f);
			IScore gsea = new GeneSetScore(strat.getRecordPerspective().getLabel(), algorithm, false);
			IScore pValue = new GeneSetScore(gsea.getLabel() + " (P-V)", algorithm.asPValue(), true);
			col.add(new ScoreEntry("Gene Set Enrichment Analysis of Group", gsea, pValue));
		}

		{
			PGSEAAlgorithm algorithm = new PGSEAAlgorithm(strat.getRecordPerspective(), group);
			IScore gsea = new GeneSetScore(strat.getRecordPerspective().getLabel(), algorithm, false);
			IScore pValue = new GeneSetScore(gsea.getLabel() + " (P-V)", algorithm.asPValue(), true);
			col.add(new ScoreEntry("Parametric Gene Set Enrichment Analysis of Group", gsea, pValue));
		}
		return col;
	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		return mode == EDataDomainQueryMode.PATHWAYS;
	}

	@Override
	public Dialog createCreateDialog(Shell shell, Object sender) {
		return new CreateGSEADialog(shell, sender);
	}

	private class CreateGSEAState extends SimpleState implements ISelectGroupState {
		public CreateGSEAState() {
			super("Select query stratification by clicking on the header brick of one of the displayed columns");
		}

		@Override
		public boolean apply(Pair<TablePerspective, Group> pair) {
			return true;
		}

		@Override
		public void select(TablePerspective tablePerspective, Group group, ISelectReaction reactions) {
			//now we have the data for the stuff
			reactions.addScoreToTourGuide(EDataDomainQueryMode.PATHWAYS,
					createGSEA(null, tablePerspective.getRecordPerspective(), group));

			// switch to a preview pathwa
			reactions.switchTo(reactions.getState(IStateMachine.BROWSE_PATHWAY));
		}
	}

	class CreateGSEADialog extends ACreateGroupScoreDialog {
		private Button parametricUI;

		public CreateGSEADialog(Shell shell, Object sender) {
			super(shell, sender);
		}

		@Override
		protected String getLabel() {
			return "Gene Set Enrichment Analysis of Group";
		}

		@Override
		protected void addTypeSpecific(Composite c) {
			Label l = new Label(c, SWT.NONE);
			l.setText("");
			parametricUI = new Button(c, SWT.CHECK);
			parametricUI.setText("Parametric Gene Set Enrichment Analysis");
		}

		@Override
		protected IRegisteredScore createScore(String label, Perspective strat, Group g) {
			if (parametricUI.getSelection()) {
				return createPGSEA(label, strat, g);
			} else
				return createGSEA(label, strat, g);
		}
	}

	public static class GeneSetScore extends DefaultComputedStratificationScore {
		private final boolean isPValue;

		public GeneSetScore(String label, IStratificationAlgorithm algorithm, boolean isPValue) {
			super(label, algorithm, ComputeScoreFilters.ALL, isPValue ? color.darker() : color, bgColor);
			this.isPValue = isPValue;
		}

		@Override
		public boolean supports(EDataDomainQueryMode mode) {
			return mode == EDataDomainQueryMode.PATHWAYS;
		}

		@Override
		public PiecewiseMapping createMapping() {
			PiecewiseMapping m;
			if (isPValue) {
				m = new PiecewiseMapping(0, 1);
				m.put(0, 1);
				m.put(1, 0);
			} else {
				m = new PiecewiseMapping(0, Float.NaN);
			}
			return m;
		}
	}

	public static Pair<Perspective, Group> resolve(IStratificationAlgorithm algorithm) {
		if (algorithm instanceof GSEAAlgorithmPValue)
			algorithm = ((GSEAAlgorithmPValue) algorithm).getUnderlying();
		if (algorithm instanceof AGSEAAlgorithm)
			return Pair.make(((AGSEAAlgorithm) algorithm).getPerspective(), ((AGSEAAlgorithm) algorithm).getGroup());
		return null;
	}
}
