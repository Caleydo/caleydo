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

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.DefaultComputedStratificationScore;
import org.caleydo.view.tourguide.api.score.ui.ACreateGroupScoreDialog;
import org.caleydo.view.tourguide.impl.algorithm.AGSEAAlgorithm;
import org.caleydo.view.tourguide.impl.algorithm.AGSEAAlgorithm.GSEAAlgorithmPValue;
import org.caleydo.view.tourguide.impl.algorithm.GSEAAlgorithm;
import org.caleydo.view.tourguide.impl.algorithm.PGSEAAlgorithm;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.rank.model.PiecewiseLinearMapping;
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

	private IRegisteredScore createGSEA(String label, TablePerspective reference, Group group) {
		return new GeneSetScore(label, new GSEAAlgorithm(reference, group, 1.0f), false);
	}

	private IRegisteredScore createPGSEA(String label, TablePerspective reference, Group group) {
		return new GeneSetScore(label, new PGSEAAlgorithm(reference, group), false);
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
			GSEAAlgorithm algorithm = new GSEAAlgorithm(strat, group, 1.0f);
			IScore gsea = new GeneSetScore(strat.getRecordPerspective().getLabel(), algorithm, false);
			IScore pValue = new GeneSetScore(gsea.getLabel() + " (P-V)", algorithm.asPValue(), true);
			col.add(new ScoreEntry("Gene Set Enrichment Analysis of Group", gsea, pValue));
		}

		{
			PGSEAAlgorithm algorithm = new PGSEAAlgorithm(strat, group);
			IScore gsea = new GeneSetScore(strat.getRecordPerspective().getLabel(), algorithm, false);
			IScore pValue = new GeneSetScore(gsea.getLabel() + " (P-V)", algorithm.asPValue(), true);
			col.add(new ScoreEntry("Parametric Gene Set Enrichment Analysis of Group", gsea, pValue));
		}
		return col;
	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		return mode == EDataDomainQueryMode.GENE_SET;
	}

	@Override
	public Dialog createCreateDialog(Shell shell, Object sender) {
		return new CreateGSEADialog(shell, sender);
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
		protected IRegisteredScore createScore(String label, TablePerspective strat, Group g) {
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
			return mode == EDataDomainQueryMode.GENE_SET;
		}

		@Override
		public PiecewiseLinearMapping createMapping() {
			PiecewiseLinearMapping m;
			if (isPValue) {
				m = new PiecewiseLinearMapping(0, 1);
				m.put(0, 1);
				m.put(1, 0);
			} else {
				m = new PiecewiseLinearMapping(0, Float.NaN);
			}
			return m;
		}
	}

	public static Pair<TablePerspective, Group> resolve(IStratificationAlgorithm algorithm) {
		if (algorithm instanceof GSEAAlgorithmPValue)
			algorithm = ((GSEAAlgorithmPValue) algorithm).getUnderlying();
		if (algorithm instanceof AGSEAAlgorithm)
			return Pair.make(((AGSEAAlgorithm) algorithm).getStratification(), ((AGSEAAlgorithm) algorithm).getGroup());
		return null;
	}
}
