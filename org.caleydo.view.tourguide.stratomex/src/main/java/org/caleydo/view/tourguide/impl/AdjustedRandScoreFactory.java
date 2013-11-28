/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl;

import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.adapter.TourGuideDataModes;
import org.caleydo.view.tourguide.api.score.DefaultComputedReferenceStratificationScore;
import org.caleydo.view.tourguide.api.state.ABrowseState;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.ISelectStratificationState;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.RootState;
import org.caleydo.view.tourguide.api.state.SimpleState;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.spi.IScoreFactory2;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class AdjustedRandScoreFactory implements IScoreFactory2 {
	private final static Color color = new Color("#5fd3bc");
	private final static Color bgColor = new Color("#d5fff6");

	private IRegisteredScore create(String label, Perspective reference) {
		return new DefaultComputedReferenceStratificationScore(label, reference, AdjustedRandIndex.get(), null, color,
				bgColor) {
			@Override
			public PiecewiseMapping createMapping() {
				PiecewiseMapping m = new PiecewiseMapping(-1, 1);
				m.put(-1, 0);
				m.put(1, 1);
				return m;
			}
		};
	}

	@Override
	public void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source) {
		if (mode != EWizardMode.GLOBAL) // nothing to compare
			return;

		for (RootState start : Iterables.filter(stateMachine.getRootStates(), RootState.ARE_STRATIFICATIONS)) {
			IState browse = stateMachine.addState("AdjustedRandBrowse",
					new UpdateAndBrowseAdjustedRand(start.getAdapter()));
			IState target = stateMachine.addState("AdjustedRand", new CreateAdjustedRandState(browse));

			stateMachine.addTransition(start, new SimpleTransition(target,
					"Based on similarity to displayed stratification",
					existing.isEmpty() ? "At least one stratification must already be visible" : null));
		}
	}

	@Override
	public Iterable<ScoreEntry> createGroupEntries(TablePerspective strat, Group group) {
		return Collections.emptyList();
	}

	@Override
	public Iterable<ScoreEntry> createStratEntries(TablePerspective strat) {
		return Collections
				.singleton(new ScoreEntry("Score column", (IScore) create(null, strat.getRecordPerspective())));
	}

	@Override
	public boolean supports(ITourGuideDataMode mode) {
		return TourGuideDataModes.areStratificatins(mode);
	}

	private void createAdjustedRandScore(TablePerspective tablePerspective, IReactions reactions) {
		String label = String.format("Sim. to %s %s", tablePerspective.getDataDomain().getLabel(), tablePerspective
				.getRecordPerspective().getLabel());
		reactions.addScoreToTourGuide(create(label, tablePerspective.getRecordPerspective()));
	}

	private class CreateAdjustedRandState extends SimpleState implements ISelectStratificationState {
		private final IState target;

		public CreateAdjustedRandState(IState target) {
			super("Select query stratification by clicking on the header block of one of the displayed columns\n"
					+ "Change query by clicking on other header block at any time");
			this.target = target;
		}

		@Override
		public boolean apply(TablePerspective tablePerspective) {
			return true;
		}

		@Override
		public void select(TablePerspective tablePerspective, IReactions reactions) {
			createAdjustedRandScore(tablePerspective, reactions);
			reactions.switchTo(target);
		}

		@Override
		public boolean isAutoSelect() {
			return false;
		}
	}

	private class UpdateAndBrowseAdjustedRand extends ABrowseState implements ISelectStratificationState {
		public UpdateAndBrowseAdjustedRand(String adapter) {
			super(adapter, "Select a stratification in the LineUp to preview.\n"
					+ "Then confirm or cancel your selection"
					+ "Change query by clicking on other block at any time");
		}

		@Override
		public boolean apply(TablePerspective tablePerspective) {
			return true;
		}

		@Override
		public void select(TablePerspective tablePerspective, IReactions reactions) {
			createAdjustedRandScore(tablePerspective, reactions);
		}

		@Override
		public boolean isAutoSelect() {
			return false;
		}
	}
}
