/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.adapter.DataDomainModes;
import org.caleydo.view.tourguide.api.score.DefaultComputedGroupScore;
import org.caleydo.view.tourguide.api.score.GroupSelectors;
import org.caleydo.view.tourguide.api.score.MultiScore;
import org.caleydo.view.tourguide.api.state.ABrowseState;
import org.caleydo.view.tourguide.api.state.BrowseOtherState;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.PreviewRenderer;
import org.caleydo.view.tourguide.api.state.RootState;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.spi.IScoreFactory2;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.spi.score.IDecoratedScore;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.config.RankTableConfigBase;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class LogRankMetricFactory implements IScoreFactory2 {
	@Override
	public void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source) {

		final RootState other = Iterables.find(stateMachine.getRootStates(), RootState.ARE_INHOMOGENOUSVARIABLES);
		for (RootState start : Iterables.filter(stateMachine.getRootStates(), RootState.ARE_STRATIFICATIONS)) {
			if (mode == EWizardMode.GLOBAL) {
				BothUpdateLogRankState browse = new BothUpdateLogRankState(start.getAdapter());
				stateMachine.addState("LogRankBrowse", browse);
				IState target = stateMachine.addState("LogRank", new CreateLogRankState(other.getAdapter(), browse));
				stateMachine.addTransition(start, new SimpleTransition(target,
						"Based on log-rank test score (survival)", null));
			} else if (mode == EWizardMode.INDEPENDENT) {
				IState browseStratification = start.getBrowseState();
				stateMachine.addTransition(start, new CreateLogRankTransition(browseStratification, source));
			}
		}
	}

	@Override
	public Iterable<ScoreEntry> createGroupEntries(TablePerspective strat, Group group) {
		return Collections.emptyList();
	}

	@Override
	public Iterable<ScoreEntry> createStratEntries(TablePerspective strat) {
		return Collections.emptyList();
	}

	@Override
	public boolean supports(ITourGuideDataMode mode) {
		return DataDomainModes.areStratificatins(mode);
	}

	private static MultiScore createLogRankScore(TablePerspective numerical) {
		int dimId = numerical.getDimensionPerspective().getVirtualArray().get(0);
		String label = String.format("Sig. change of %s", numerical.getLabel());
		ATableBasedDataDomain clinical = numerical.getDataDomain();
		LogRankMetric metric = new LogRankMetric("LogRank", dimId, clinical);
		LogRankPValue pvalue = new LogRankPValue("-log(p-value)", metric);

		MultiScore multiScore = new MultiScore(label, wrap(clinical.getColor()),
 darker(clinical.getColor()),
				RankTableConfigBase.NESTED_MODE);
		multiScore.add(pvalue);
		multiScore.add(metric);
		return multiScore;
	}

	private class CreateLogRankState extends BrowseOtherState {
		private final BothUpdateLogRankState target;

		public CreateLogRankState(ITourGuideAdapter adapter, BothUpdateLogRankState target) {
			super(adapter, "Select a numerical value in the LineUp as a starting point for finding a stratification.");
			this.target = target;
		}

		@Override
		public void onUpdateOther(TablePerspective tablePerspective, IReactions adapter) {
			TablePerspective numerical = tablePerspective;
			adapter.replaceTemplate(new PreviewRenderer(adapter.createPreview(numerical), adapter.getGLView(),
					"Browse for a stratification"));

			MultiScore multiScore = createLogRankScore(numerical);
			adapter.addScoreToTourGuide(multiScore);
			target.numerical = numerical;
			adapter.switchTo(target);
		}
	}

	private class BothUpdateLogRankState extends ABrowseState {
		private TablePerspective numerical;

		public BothUpdateLogRankState(ITourGuideAdapter adapter) {
			super(adapter, "From list");
		}

		@Override
		public void onUpdateStratification(TablePerspective tablePerspective, IReactions adapter) {
			TablePerspective tp = tablePerspective;
			adapter.replaceTemplate(tp, true);
			adapter.replaceOtherTemplate(tp.getRecordPerspective(), numerical, true, true);
		}
	}

	private class CreateLogRankTransition implements ITransition {
		private final IState target;
		private final TablePerspective numerical;

		public CreateLogRankTransition(IState target, TablePerspective numerical) {
			this.target = target;
			this.numerical = numerical;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public String getDisabledReason() {
			return null;
		}

		@Override
		public String getLabel() {
			return "Based on log-rank test score (survival)";
		}

		@Override
		public void apply(IReactions adapter) {
			adapter.addScoreToTourGuide(createLogRankScore(numerical));
			adapter.switchTo(target);
		}
	}

	public static class LogRankMetric extends DefaultComputedGroupScore {
		private final Integer clinicalVariable;

		public LogRankMetric(String label, final Integer clinicalVariable, final ATableBasedDataDomain clinical) {
			super(label, new IGroupAlgorithm() {
				final IGroupAlgorithm underlying = LogRank.get(clinicalVariable, clinical);

				@Override
				public void init(IProgressMonitor monitor) {
					underlying.init(monitor);
				}

				@Override
				public IDType getTargetType(IComputeElement a, IComputeElement b) {
					return underlying.getTargetType(a, b);
				}

				@Override
				public String getAbbreviation() {
					return underlying.getAbbreviation();
				}

				@Override
				public String getDescription() {
					return "Log Rank of ";
				}

				@Override
				public double compute(Set<Integer> a, Group ag, Set<Integer> b, Group bg, IProgressMonitor monitor) {
					// me versus the rest
					return underlying.compute(a, ag, Sets.difference(b, a), bg, monitor);
				}
			}, null, GroupSelectors.MAX_ABS, wrap(clinical.getColor()), darker(clinical
					.getColor()));
			this.clinicalVariable = clinicalVariable;
		}

		public Integer getClinicalVariable() {
			return clinicalVariable;
		}

		@Override
		public PiecewiseMapping createMapping() {
			return new PiecewiseMapping(0, Float.NaN);
		}
	}

	public static class LogRankPValue extends DefaultLabelProvider implements IRegisteredScore, IDecoratedScore {
		private final LogRankMetric logRankScore;

		public LogRankPValue(String label, LogRankMetric logRankScore) {
			super(label);
			this.logRankScore = logRankScore;
		}

		@Override
		public void onRegistered() {

		}

		@Override
		public String getAbbreviation() {
			return "LR-P";
		}

		@Override
		public String getDescription() {
			return "Log Rank p-Value of " + getLabel();
		}

		@Override
		public Color getColor() {
			return logRankScore.getColor().darker();
		}

		@Override
		public Color getBGColor() {
			return logRankScore.getBGColor();
		}

		@Override
		public PiecewiseMapping createMapping() {
			return Utils.createPValueMapping();
		}

		@Override
		public boolean supports(ITourGuideDataMode mode) {
			return DataDomainModes.areStratificatins(mode);
		}

		@Override
		public IScore getUnderlying() {
			return logRankScore;
		}

		@Override
		public final double apply(IComputeElement elem, Group g) {
			return LogRank.getPValue(logRankScore.apply(elem, g));
		}

		public Integer getClinicalVariable() {
			return logRankScore.getClinicalVariable();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((logRankScore == null) ? 0 : logRankScore.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LogRankPValue other = (LogRankPValue) obj;
			if (logRankScore == null) {
				if (other.logRankScore != null)
					return false;
			} else if (!logRankScore.equals(other.logRankScore))
				return false;
			return true;
		}
	}

	private static Color wrap(org.caleydo.core.util.color.Color color) {
		return new Color(color.r, color.g, color.b, color.a);
	}

	private static Color darker(org.caleydo.core.util.color.Color color) {
		Color c = new Color(color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a);
		return c;
	}

}

