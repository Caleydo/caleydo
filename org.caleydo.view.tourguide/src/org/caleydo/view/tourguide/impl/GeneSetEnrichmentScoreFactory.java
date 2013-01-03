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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.CombinedScore;
import org.caleydo.view.tourguide.api.score.DefaultComputedStratificationScore;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;
import org.caleydo.view.tourguide.api.score.ui.ACreateGroupScoreDialog;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.algorithm.GSEAAlgorithm;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class GeneSetEnrichmentScoreFactory implements IScoreFactory {
	private IRegisteredScore createGSEA(String label, TablePerspective reference, Group group) {
		return new GeneSetScore(label, new GSEAAlgorithm(reference, group, 1.0f));
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

		GSEAAlgorithm algorithm = new GSEAAlgorithm(strat, group, 1.0f);
		IScore gsea = new GeneSetScore(strat.getRecordPerspective().getLabel(), algorithm);
		IScore pValue = new GeneSetScore(gsea.getLabel() + " (P-V)", algorithm.asPValue());
		IScore combined = new CombinedScore(gsea.getLabel() + " Quality", ECombinedOperator.PRODUCT,
				CombinedScore.wrap(Arrays.asList(gsea, pValue)));
		col.add(new ScoreEntry("Gene Set Enrichment Analysis of Group", gsea, pValue, combined));
		return col;
	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		return mode == EDataDomainQueryMode.GENE_SET;
	}

	@Override
	public Dialog createCreateDialog(Shell shell, ScoreQueryUI sender) {
		return new CreateGSEADialog(shell, sender);
	}

	class CreateGSEADialog extends ACreateGroupScoreDialog {
		public CreateGSEADialog(Shell shell, ScoreQueryUI sender) {
			super(shell, sender);
		}

		@Override
		protected String getLabel() {
			return "Gene Set Enrichment Analysis of Group";
		}

		@Override
		protected void addTypeSpecific(Composite c) {

		}

		@Override
		protected IRegisteredScore createScore(String label, TablePerspective strat, Group g) {
			return createGSEA(label, strat, g);
		}
	}

	public static class GeneSetScore extends DefaultComputedStratificationScore {
		public GeneSetScore(String label, IStratificationAlgorithm algorithm) {
			super(label, algorithm, ComputeScoreFilters.ALL);
		}

		@Override
		public boolean supports(EDataDomainQueryMode mode) {
			return mode == EDataDomainQueryMode.GENE_SET;
		}
	}
}
