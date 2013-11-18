/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl;

import static org.caleydo.view.tourguide.impl.MutualExclusiveScoreFilter.canHaveMutualExclusiveScore;
import static org.caleydo.view.tourguide.impl.MutualExclusiveScoreFilter.getProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.adapter.DataDomainModes;
import org.caleydo.view.tourguide.api.score.DefaultComputedReferenceGroupScore;
import org.caleydo.view.tourguide.api.score.MultiScore;
import org.caleydo.view.tourguide.api.state.ABrowseState;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.ISelectGroupState;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.RootState;
import org.caleydo.view.tourguide.api.state.SimpleState;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.spi.IScoreFactory2;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class SetSimilarityScoreFactory implements IScoreFactory2 {
	private final static Color color = new Color("#ffb380");
	private final static Color bgColor = new Color("#ffe6d5");

	private IRegisteredScore createJaccard(String label, Perspective reference, Group group) {
		return new DefaultComputedReferenceGroupScore(label, reference, group, JaccardIndex.get(),
 null, null, color,
				bgColor);
	}

	private IRegisteredScore createMutualExclusive(String label, Perspective reference, Group group,
			CategoryProperty<?> property) {
		return new DefaultComputedReferenceGroupScore(label, reference, group, JaccardIndex.get(),
				new MutualExclusiveScoreFilter(property), null, color, bgColor);
	}

	@Override
	public void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source) {
		if (mode != EWizardMode.GLOBAL) // nothing to compare
			return;
		for (RootState start : Iterables.filter(stateMachine.getRootStates(), RootState.ARE_STRATIFICATIONS)) {
			String disabled = existing.isEmpty() ? "At least one stratification must already be visible" : null;
			IState browse = stateMachine.addState("JaccardIndexBrowse",
					new UpdateAndBrowseJaccardIndex(start.getAdapter()));
			IState target = stateMachine.addState("JaccardIndex", new CreateJaccardScoreState(browse));
			stateMachine.addTransition(start, new SimpleTransition(target, "Based on overlap with displayed set",
					disabled));
		}
	}

	private void createJaccardScore(TablePerspective tablePerspective, Group group, IReactions reactions) {
		IScore[] scores;
		if (group == null) {
			if (canHaveMutualExclusiveScore(tablePerspective.getDataDomain())) {
				String label = String.format("Sim. to %s %s", tablePerspective.getDataDomain().getLabel(),
						tablePerspective.getRecordPerspective().getLabel());
				MultiScore s = new MultiScore(label, color, bgColor);
				MultiScore me = new MultiScore("ME " + label, color, bgColor);
				// binary categorical -> add mutual exclusive score
				for (Group g : tablePerspective.getRecordPerspective().getVirtualArray().getGroupList()) {
					label = String.format("Sim. to %s %s %s", tablePerspective.getDataDomain().getLabel(),
							tablePerspective.getRecordPerspective().getLabel(), g);

					@SuppressWarnings("unchecked")
					List<CategoryProperty<?>> props = (List<CategoryProperty<?>>) getProperties(tablePerspective
							.getDataDomain());
					for (CategoryProperty<?> prop : props) {
						final String mutExclusiveLabel = prop.getCategoryName() + " " + label;
						s.add(createMutualExclusive(mutExclusiveLabel,
								tablePerspective.getRecordPerspective(), g, prop));
					}
					s.add(createJaccard(label, tablePerspective.getRecordPerspective(), g));
				}
				scores = new IScore[] { me, s };
			} else {
				String label = String.format("Sim. to %s %s", tablePerspective.getDataDomain().getLabel(),
						tablePerspective.getRecordPerspective().getLabel());
				MultiScore s = new MultiScore(label, color, bgColor);
				for (Group g : tablePerspective.getRecordPerspective().getVirtualArray().getGroupList()) {
					label = String.format("Sim. to %s %s %s", tablePerspective.getDataDomain().getLabel(),
							tablePerspective.getRecordPerspective().getLabel(), g);
					s.add(createJaccard(label, tablePerspective.getRecordPerspective(), g));
				}
				scores = new IScore[] { s };
			}
		} else {
			String label = String.format("Sim. to %s %s %s", tablePerspective.getDataDomain().getLabel(),
					tablePerspective.getRecordPerspective().getLabel(), group.getLabel());

			if (canHaveMutualExclusiveScore(tablePerspective
					.getDataDomain())) {
				@SuppressWarnings("unchecked")
				List<CategoryProperty<?>> props = (List<CategoryProperty<?>>) getProperties(tablePerspective
						.getDataDomain());
				List<IScore> scoresList = new ArrayList<IScore>();
				for (CategoryProperty<?> prop : props) {
					scoresList.add(createMutualExclusive(prop.getCategoryName()+" " + label, tablePerspective.getRecordPerspective(), group, prop));
				}
				scoresList.add(createJaccard(label, tablePerspective.getRecordPerspective(), group));
				// binary categorical -> add mutual exclusive score
				scores = scoresList.toArray(new IScore[0]);
			} else {
				scores = new IScore[] { createJaccard(label, tablePerspective.getRecordPerspective(), group) };
			}
		}
		reactions.addScoreToTourGuide(scores);
	}



	private class CreateJaccardScoreState extends SimpleState implements ISelectGroupState {
		private final IState target;

		public CreateJaccardScoreState(IState target) {
			super("Select query group by clicking on a framed block in one of the displayed columns\n"
					+ "Change query by clicking on other block at any time");
			this.target = target;
		}

		@Override
		public boolean apply(Pair<TablePerspective, Group> pair) {
			return true;
		}

		@Override
		public boolean isSelectAllSupported() {
			return true;
		}

		@Override
		public void select(TablePerspective tablePerspective, Group group, IReactions reactions) {
			createJaccardScore(tablePerspective, group, reactions);
			reactions.switchTo(target);
		}
	}

	private class UpdateAndBrowseJaccardIndex extends ABrowseState implements ISelectGroupState {
		public UpdateAndBrowseJaccardIndex(ITourGuideAdapter adapter) {
			super(adapter, "Select a stratification in the LineUp to preview.\n"
					+ "Then confirm or cancel your selection"
					+ "Change query by clicking on other block at any time");
		}

		@Override
		public boolean apply(Pair<TablePerspective, Group> pair) {
			return true;
		}

		@Override
		public boolean isSelectAllSupported() {
			return true;
		}

		@Override
		public void select(TablePerspective tablePerspective, Group group, IReactions reactions) {
			createJaccardScore(tablePerspective, group, reactions);
		}

		@Override
		public void onUpdateStratification(TablePerspective tablePerspective, IReactions adapter) {
			adapter.replaceTemplate(tablePerspective, false);
		}
	}

	@Override
	public Iterable<ScoreEntry> createGroupEntries(TablePerspective strat, Group group) {
		Collection<ScoreEntry> col = new ArrayList<>();
		col.add(new ScoreEntry("Score group", (IScore) createJaccard(null, strat.getRecordPerspective(), group)));
		return col;
	}

	@Override
	public Iterable<ScoreEntry> createStratEntries(TablePerspective strat) {
		Collection<ScoreEntry> col = new ArrayList<>();
		final Perspective rs = strat.getRecordPerspective();
		MultiScore composite = new MultiScore(rs.getLabel(), color, bgColor);
		for (Group group : rs.getVirtualArray().getGroupList()) {
			composite.add(createJaccard(null, rs, group));
		}
		col.add(new ScoreEntry("Score all groups in column", (IScore) composite));
		return col;
	}

	@Override
	public boolean supports(ITourGuideDataMode mode) {
		return DataDomainModes.areStratificatins(mode);
	}
}

