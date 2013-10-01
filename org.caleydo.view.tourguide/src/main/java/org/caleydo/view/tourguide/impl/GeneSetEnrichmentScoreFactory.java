/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.tourguide.api.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.DefaultComputedStratificationScore;
import org.caleydo.view.tourguide.api.score.MultiScore;
import org.caleydo.view.tourguide.api.score.ui.ACreateGroupScoreDialog;
import org.caleydo.view.tourguide.api.state.ABrowseState;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.ISelectGroupState;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.SimpleState;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.api.util.PathwayOracle;
import org.caleydo.view.tourguide.impl.algorithm.AGSEAAlgorithm;
import org.caleydo.view.tourguide.impl.algorithm.AGSEAAlgorithm.GSEAAlgorithmPValue;
import org.caleydo.view.tourguide.impl.algorithm.GSEAAlgorithm;
import org.caleydo.view.tourguide.impl.algorithm.GeneSetMappedAlgorithm;
import org.caleydo.view.tourguide.impl.algorithm.PAGEAlgorithm;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;
import org.caleydo.view.tourguide.spi.score.IDecoratedScore;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.config.RankTableConfigBase;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;
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
	private final static Color color = new Color("#80ffb3");
	private final static Color bgColor = new Color("#e3f4d7");

	private IRegisteredScore createGSEA(String label, Perspective reference, Group group) {
		if (label == null)
			label = reference.getLabel() + " " + group.getLabel();
		return new GeneSetScore(label, new GSEAAlgorithm(reference, group, 1, false), new PiecewiseMapping(-1, 1));
	}

	private IRegisteredScore createPAGE(String label, Perspective reference, Group group) {
		if (label == null)
			label = reference.getLabel() + " " + group.getLabel();
		return new GeneSetScore(label, new PAGEAlgorithm(reference, group), dynamicAbsolute());
	}

	@Override
	public void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source) {
		IState start = stateMachine.get(IStateMachine.ADD_PATHWAY);

		if (mode == EWizardMode.GLOBAL) {
			String disabled = !hasGoodOnes(existing) ? "At least one valid numerical GENE stratification must already be visible"
					: null;
			UpdateAndBrowsePathways browseG = new UpdateAndBrowsePathways(true);
			stateMachine.addState("browseAndUpdateGSEA", browseG);
			UpdateAndBrowsePathways browseP = new UpdateAndBrowsePathways(false);
			stateMachine.addState("browseAndUpdatePAGE", browseP);
			IState target = stateMachine.addState("GSEA", new CreateGSEAState(browseG, true));
			IState target2 = stateMachine.addState("PAGE", new CreateGSEAState(browseP, false));
			stateMachine.addTransition(start, new SimpleTransition(target,
					"Find with GSEA based on displayed stratification", disabled));
			stateMachine.addTransition(start, new SimpleTransition(target2,
					"Find with PAGE based on displayed stratification", disabled));
		}

		// auto mode
		// doesn't work as we have to browse for a group not a stratification
		// {
		// // Find with GSEA based on strat. not displayed -> n x n -> no
		// // first select a stratification then a pathway
		// IState target = stateMachine.addState("GSEAUnkown", new CreateGSEAState(browse, true));
		// // IState target2 = stateMachine.addState("PGSEA", new CreateGSEAState(browse, false));
		// stateMachine.addTransition(source, new SimpleTransition(target,
		// "Find with GSEA based on displayed stratification"));
		// // stateMachine.addTransition(source, new SimpleTransition(target2,
		// // "Find with PGSEA based on displayed stratification"));
		// }

	}

	/**
	 * @param existing
	 * @return
	 */
	private static boolean hasGoodOnes(List<TablePerspective> existing) {
		for (TablePerspective t : existing) {
			if (isGoodDataDomain(t.getDataDomain()) && !(t instanceof PathwayTablePerspective))
				return true;
		}
		return false;
	}

	@Override
	public Iterable<ScoreEntry> createStratEntries(TablePerspective strat) {
		return Collections.emptyList();
	}

	@Override
	public Iterable<ScoreEntry> createGroupEntries(TablePerspective strat, Group group) {
		if (!isGoodDataDomain(strat.getDataDomain()))
			return Collections.emptyList();
		Collection<ScoreEntry> col = new ArrayList<>();

		{
			GSEAAlgorithm algorithm = new GSEAAlgorithm(strat.getRecordPerspective(), group, 1, false);
			IScore gsea = new GeneSetScore(strat.getRecordPerspective().getLabel(), algorithm, new PiecewiseMapping(-1,
					+1));
			// IScore pValue = new GeneSetScore(gsea.getLabel() + " (P-V)", algorithm.asPValue(), true);
			col.add(new ScoreEntry("Gene Set Enrichment Analysis of group", gsea));
		}

		{
			PAGEAlgorithm algorithm = new PAGEAlgorithm(strat.getRecordPerspective(), group);
			IScore gsea = new GeneSetScore(strat.getRecordPerspective().getLabel(), algorithm, dynamicAbsolute());
			IScore pValue = new GeneSetPValueScore("-log(p-value)", algorithm.asPValue(), gsea);
			col.add(new ScoreEntry("Parametric Gene Set Enrichment Analysis of group", gsea, pValue));
		}
		return col;
	}

	private static PiecewiseMapping dynamicAbsolute() {
		PiecewiseMapping m = new PiecewiseMapping(Float.NaN, Float.NaN);
		// absolute with max borders
		m.fromJavaScript("return linear(0,Math.max(abs(value_min),abs(value_max)),abs(value),0,1)");
		return m;
	}

	/**
	 * @param dataDomain
	 * @return
	 */
	private static boolean isGoodDataDomain(IDataDomain dataDomain) {
		if (!PathwayOracle.canBeUnderlying(dataDomain) || !(dataDomain instanceof ATableBasedDataDomain))
			return false;
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;
		return d.getTable().isDataHomogeneous() && d.getTable() instanceof NumericalTable;
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
		private final UpdateAndBrowsePathways target;
		private final boolean createGSEA;

		public CreateGSEAState(UpdateAndBrowsePathways target, boolean createGSEA) {
			super("Select query group by clicking on a framed block of one of the displayed columns");
			this.target = target;
			this.createGSEA = createGSEA;
		}

		@Override
		public boolean apply(Pair<TablePerspective, Group> pair) {
			return isGoodDataDomain(pair.getFirst().getDataDomain())
					&& !(pair.getFirst() instanceof PathwayTablePerspective);
		}

		@Override
		public boolean isSelectAllSupported() {
			return false;
		}

		@Override
		public void select(TablePerspective strat, Group group, IReactions reactions) {
			IScore[] score = createScore(strat, group, createGSEA);
			reactions.addScoreToTourGuide(EDataDomainQueryMode.PATHWAYS, score);

			// switch to a preview pathway
			target.setUnderlying(strat.getRecordPerspective());
			reactions.switchTo(target);
		}
	}

	private class UpdateAndBrowsePathways extends ABrowseState implements ISelectGroupState {
		protected Perspective underlying;
		protected PathwayGraph pathway;
		private final boolean createGSEA;

		public UpdateAndBrowsePathways(boolean createGSEA) {
			super(EDataDomainQueryMode.PATHWAYS, "Select a pathway in the LineUp to preview.\n"
					+ "Then confirm or cancel your selection" + "Change query by clicking on other block at any time");
			this.createGSEA = createGSEA;
		}

		@Override
		public boolean apply(Pair<TablePerspective, Group> pair) {
			return true;
		}

		@Override
		public boolean isSelectAllSupported() {
			return true;
		}

		/**
		 * @param underlying
		 *            setter, see {@link underlying}
		 */
		public final void setUnderlying(Perspective underlying) {
			this.underlying = underlying;
		}

		@Override
		public void onUpdatePathway(PathwayGraph pathway, IReactions adapter) {
			this.pathway = pathway;
			adapter.replacePathwayTemplate(underlying, pathway, false, true);
		}

		@Override
		public void select(TablePerspective strat, Group group, IReactions reactions) {
			IScore[] score = createScore(strat, group, createGSEA);
			reactions.addScoreToTourGuide(EDataDomainQueryMode.PATHWAYS, score);

			// switch to a preview pathway
			underlying = strat.getRecordPerspective();
			if (pathway != null)
				reactions.replacePathwayTemplate(underlying, pathway, false, true);
		}
	}

	// private class CreateAndBrowseGSEAState extends BrowseStratificationState {
	// private final BrowsePathwayState target;
	// private final boolean createGSEA;
	//
	// public CreateAndBrowseGSEAState(BrowsePathwayState target, boolean createGSEA) {
	// super("Select query stratification by clicking on the header block of one of the displayed columns");
	// this.target = target;
	// this.createGSEA = createGSEA;
	// }
	//
	// @Override
	// public void onUpdate(UpdateStratificationPreviewEvent event, ISelectReaction adapter) {
	// TablePerspective tp = event.getTablePerspective();
	// if (!tp.is)
	// if (DataDomainOracle.isCategoricalDataDomain(tp.getDataDomain()))
	// adapter.replaceTemplate(tp, new CategoricalDataConfigurer(tp));
	// else
	// adapter.replaceTemplate(tp, null);
	// }
	// @Override
	// public void select(TablePerspective strat, Group group, ISelectReaction reactions) {
	// // now we have the data for the stuff
	// AGSEAAlgorithm algorithm;
	// if (createGSEA)
	// algorithm = new GSEAAlgorithm(strat.getRecordPerspective(), group, 1.0f);
	// else
	// algorithm = new PGSEAAlgorithm(strat.getRecordPerspective(), group);
	// IScore gsea = new GeneSetScore(strat.getRecordPerspective().getLabel(), algorithm, false);
	// IScore pValue = new GeneSetScore(gsea.getLabel() + " (P-V)", algorithm.asPValue(), true);
	//
	// reactions.addScoreToTourGuide(EDataDomainQueryMode.PATHWAYS, gsea, pValue);
	//
	// // switch to a preview pathway
	// target.setUnderlying(strat.getRecordPerspective());
	// reactions.switchTo(target);
	// }
	// }

	class CreateGSEADialog extends ACreateGroupScoreDialog {
		private Button parametricUI;

		public CreateGSEADialog(Shell shell, Object sender) {
			super(shell, sender);
		}

		@Override
		protected String getLabel() {
			return "Gene Set Enrichment Analysis of group";
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
				return createPAGE(label, strat, g);
			} else
				return createGSEA(label, strat, g);
		}
	}

	public static class GeneSetScore extends DefaultComputedStratificationScore {
		private final PiecewiseMapping mapping;

		public GeneSetScore(String label, IStratificationAlgorithm algorithm, PiecewiseMapping mapping) {
			super(label, algorithm, ComputeScoreFilters.ALL, color, bgColor);
			this.mapping = mapping;
		}

		@Override
		public boolean supports(EDataDomainQueryMode mode) {
			return mode == EDataDomainQueryMode.PATHWAYS;
		}

		@Override
		public PiecewiseMapping createMapping() {
			return mapping.clone();
		}
	}

	public static class GeneSetPValueScore extends DefaultComputedStratificationScore implements IDecoratedScore {
		private final IScore underlying;

		public GeneSetPValueScore(String label, IStratificationAlgorithm algorithm,
				IScore underlying) {
			super(label, algorithm, ComputeScoreFilters.ALL, color.darker(), bgColor);
			this.underlying = underlying;
		}

		/**
		 * @return the underlying, see {@link #underlying}
		 */
		@Override
		public IScore getUnderlying() {
			return underlying;
		}

		@Override
		public boolean supports(EDataDomainQueryMode mode) {
			return mode == EDataDomainQueryMode.PATHWAYS;
		}

		@Override
		public PiecewiseMapping createMapping() {
			return Utils.createPValueMapping();
		}
	}

	public static class GeneSetMatchedScore extends DefaultComputedStratificationScore {
		private final boolean isPercentage;

		public GeneSetMatchedScore(String label, IStratificationAlgorithm algorithm, boolean isPercentage) {
			super(label, algorithm, ComputeScoreFilters.ALL, isPercentage ? color.darker() : color, bgColor);
			this.isPercentage = isPercentage;
		}

		@Override
		public boolean supports(EDataDomainQueryMode mode) {
			return mode == EDataDomainQueryMode.PATHWAYS;
		}

		@Override
		public PiecewiseMapping createMapping() {
			if (isPercentage) {
				return new PiecewiseMapping(0, 1);
			} else {
				return null;
			}
		}
	}

	public static Pair<Perspective, Group> resolve(IStratificationAlgorithm algorithm) {
		if (algorithm instanceof GSEAAlgorithmPValue)
			algorithm = ((GSEAAlgorithmPValue) algorithm).getUnderlying();
		if (algorithm instanceof AGSEAAlgorithm)
			return Pair.make(((AGSEAAlgorithm) algorithm).getPerspective(), ((AGSEAAlgorithm) algorithm).getGroup());
		return null;
	}

	/**
	 * @param strat
	 * @param group
	 * @param createGSEA
	 * @return
	 */
	public static IScore[] createScore(TablePerspective strat, Group group, boolean createGSEA) {
		if (group == null) {
			// TODO
			return new IScore[0];
		} else {
			// now we have the data for the stuff
			AGSEAAlgorithm algorithm;
			Perspective perspective = strat.getRecordPerspective();
			Perspective genes = strat.getDimensionPerspective();
			String prefix = createGSEA ? "GSEA" : "PAGE";
			String label = String.format(prefix + " of %s", perspective.getLabel());
			IScore s;
			if (createGSEA) {
				algorithm = new GSEAAlgorithm(perspective, group, 1, false);
				s = new GeneSetScore(label, algorithm, new PiecewiseMapping(-1, 1));
			} else {
				algorithm = new PAGEAlgorithm(perspective, group);
				IScore gsea = new GeneSetScore(prefix, algorithm, dynamicAbsolute());
				IScore pValue = new GeneSetPValueScore("-log(p-value)", algorithm.asPValue(), gsea);
				MultiScore s2 = new MultiScore(label, color, bgColor, RankTableConfigBase.NESTED_MODE);
				s2.add(pValue);
				s2.add(gsea);
				s = s2;
			}

			IScore matched = new GeneSetMatchedScore("# Mapped Genes", new GeneSetMappedAlgorithm(genes, false), false);
			IScore matchedP = new GeneSetMatchedScore("% Mapped Genes", new GeneSetMappedAlgorithm(genes, true), true);
			return new IScore[] { matchedP, matched, s };
		}

	}
}
