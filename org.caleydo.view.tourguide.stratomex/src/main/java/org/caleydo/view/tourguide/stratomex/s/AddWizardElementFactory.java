/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.s;

import java.util.Collection;
import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.tourguide.api.adapter.DataDomainModes;
import org.caleydo.view.tourguide.api.score.ScoreFactories;
import org.caleydo.view.tourguide.api.state.BrowseOtherState;
import org.caleydo.view.tourguide.api.state.BrowsePathwayState;
import org.caleydo.view.tourguide.api.state.BrowseStratificationState;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.RootState;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.api.util.PathwayOracle;
import org.caleydo.view.tourguide.internal.mode.VariableDataMode;
import org.caleydo.view.tourguide.spi.IScoreFactory2;
import org.caleydo.view.tourguide.stratomex.state.AlonePathwayState;
import org.caleydo.view.tourguide.stratomex.state.BrowseNumericalAndStratificationState;
import org.caleydo.view.tourguide.stratomex.state.BrowsePathwayAndStratificationState;
import org.caleydo.view.tourguide.stratomex.t.PathwayStratomexAdapter;
import org.caleydo.view.tourguide.stratomex.t.StratificationStratomexAdapter;
import org.caleydo.view.tourguide.stratomex.t.VariableStratomexAdapter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class AddWizardElementFactory {
	private static final String BROWSE_AND_SELECT_OTHER = "browseAndSelectNumerical";
	private static final String BROWSE_AND_SELECT_PATHWAY = "browseAndSelectPathway";
	private static final String ALONE_PATHWAY = "alonePathway";
	private static final String BROWSE_STRATIFICATIONS = "browseStratifications";
	private static final String BROWSE_OTHER = "browseOther";
	private static final String BROWSE_PATHWAY = "browsePathway";
	private static final String ADD_STRATIFICATIONS = "addStrafications";
	private static final String ADD_OTHER = "addOther";
	private static final String ADD_PATHWAY = "addPathway";

	public static AddWizardElement create(TourGuideAddin adapter, AGLView view) {
		return new AddWizardElement(view, adapter, createStateMachine(adapter.getVisibleTablePerspectives(),
				EWizardMode.GLOBAL, null));
	}

	public static AddWizardElement createDependent(TourGuideAddin adapter, AGLView view,
			TablePerspective tablePerspective) {
		return new AddWizardElement(view, adapter, createStateMachine(adapter.getVisibleTablePerspectives(),
				EWizardMode.DEPENDENT, tablePerspective));
	}

	public static AddWizardElement createIndepenent(TourGuideAddin adapter, AGLView view,
			TablePerspective tablePerspective) {
		return new AddWizardElement(view, adapter, createStateMachine(adapter.getVisibleTablePerspectives(),
				EWizardMode.INDEPENDENT, tablePerspective));
	}



	private static StateMachineImpl createStateMachine(List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source) {
		StateMachineImpl state = new StateMachineImpl();

		addDefaultStates(state);

		IState browseStratification = state.get(BROWSE_STRATIFICATIONS);
		BrowseOtherState browseNumerical = (BrowseOtherState) state.get(BROWSE_OTHER);
		BrowsePathwayState browsePathway = (BrowsePathwayState) state.get(BROWSE_PATHWAY);
		IState addStratification = state.get(ADD_STRATIFICATIONS);
		IState addNumerical = state.get(ADD_OTHER);
		IState addPathway = state.get(ADD_PATHWAY);

		switch (mode) {
		case GLOBAL:
			state.addTransition(addStratification, new SimpleTransition(browseStratification, "From list", null));
			state.addTransition(addNumerical, new SimpleTransition(browseNumerical,
					"From list and display unstratified", null));
			state.addState(ALONE_PATHWAY, new AlonePathwayState(PathwayStratomexAdapter.SECONDARY_ID));

			// select pathway -> show preview -> select stratification -> show both
			IState browseIntermediate = state.addState(BROWSE_AND_SELECT_PATHWAY,
					new BrowsePathwayAndStratificationState(PathwayStratomexAdapter.SECONDARY_ID));
			state.addTransition(addPathway, new SimpleTransition(browseIntermediate,
					"From list and stratify with a displayed stratification", !existing.isEmpty() ? null
							: "At least one mappable stratification must be already visible"));
			// select pathway -> show preview -> select stratification -> show both
			IState browseOtherIntermediate = state.addState(BROWSE_AND_SELECT_OTHER,
					new BrowseNumericalAndStratificationState(VariableStratomexAdapter.SECONARDY_ID));
			state.addTransition(addNumerical, new SimpleTransition(browseOtherIntermediate,
					"From list and stratify with a displayed stratification", !existing.isEmpty() ? null
							: "At least one mappable stratification must already be visible"));
			break;
		case DEPENDENT:
			browsePathway.setUnderlying(source.getRecordPerspective());
			browseNumerical.setUnderlying(source.getRecordPerspective());

			state.addTransition(addPathway,
					new SimpleTransition(browsePathway, "From list", PathwayOracle.canBeUnderlying(source) ? null
							: "The selected stratification can't be mapped to a pathway"));
			state.addTransition(addNumerical, new SimpleTransition(browseNumerical, "From list", null));
			break;
		case INDEPENDENT:
			state.addTransition(addStratification, new SimpleTransition(browseStratification, "From list", null));
			break;
		}

		fillStateMachine(state, existing, mode, source);

		boolean hideEmpty = mode != EWizardMode.GLOBAL;
		addStartTransition(state, ADD_STRATIFICATIONS, hideEmpty);
		addStartTransition(state, ADD_PATHWAY, hideEmpty);
		addStartTransition(state, ADD_OTHER, hideEmpty);

		skipSingleOnes(state);

		return state;
	}

	/**
	 * @param state
	 * @param existing
	 * @param mode
	 * @param source
	 */
	private static void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source) {
		for (IScoreFactory2 f : Iterables.filter(ScoreFactories.getFactories().values(), IScoreFactory2.class)) {
			f.fillStateMachine(stateMachine, existing, mode, source);
		}
	}

	/**
	 * @param state
	 */
	private static void skipSingleOnes(StateMachineImpl state) {
		final List<ITransition> start = state.getTransitions(state.getCurrent());

		if (start.size() == 1) {
			ITransition t = start.get(0);
			assert t instanceof SimpleTransition;
			state.move(((SimpleTransition) t).getTarget());
		}
	}

	private static void addDefaultStates(StateMachineImpl state) {
		state.addState(ADD_STRATIFICATIONS,
 new RootState(StratificationStratomexAdapter.SECONDARY_ID,
				DataDomainModes.STRATIFICATIONS, "Select stratification"));

		state.addState(BROWSE_STRATIFICATIONS, new BrowseStratificationState(
				StratificationStratomexAdapter.SECONDARY_ID,
				"Select a stratification in the LineUp to preview.\nThen confirm or cancel your selection."));

		state.addState(ADD_PATHWAY, new RootState(PathwayStratomexAdapter.SECONDARY_ID,
				DataDomainModes.PATHWAYS, "Select pathway"));

		state.addState(BROWSE_PATHWAY, new BrowsePathwayState(PathwayStratomexAdapter.SECONDARY_ID,
				"Select a pathway in the LineUp to preview.\n Then confirm or cancel your selection."));

		state.addState(ADD_OTHER,
 new RootState(VariableStratomexAdapter.SECONARDY_ID, VariableDataMode.INSTANCE,
				"Select other data " + toString(VariableDataMode.INSTANCE.getAllDataDomains())));

		state.addState(BROWSE_OTHER, new BrowseOtherState(VariableStratomexAdapter.SECONARDY_ID,
				"Select a entry in the LineUp\nto preview.\n\nThen confirm or cancel your selection."));
	}

	/**
	 * @param allDataDomains
	 * @return
	 */
	private static String toString(Collection<? extends IDataDomain> dataDomains) {
		if (dataDomains.isEmpty())
			return "";
		StringBuilder b = new StringBuilder("(");
		for (IDataDomain d : dataDomains)
			b.append(d.getLabel()).append(", ");
		b.setLength(b.length() - 2);
		b.append(')');
		return b.toString();
	}

	/**
	 * @param state
	 * @param hideEmpty
	 * @param addStratifications
	 */
	private static void addStartTransition(StateMachineImpl state, String stateID, boolean hideEmpty) {
		IState target = state.get(stateID);
		List<ITransition> transitions = state.getTransitions(target);
		if (transitions.isEmpty())
			return;
		String reason = "Unknown";
		for (ITransition t : Lists.reverse(transitions)) {
			if (t.isEnabled()) {
				reason = null;
				break;
			}
			reason = t.getDisabledReason();
		}
		if (reason == null || !hideEmpty)
			state.addTransition(state.getCurrent(), new SimpleTransition(target, target.getLabel(), reason));
	}

	public static AddWizardElement createForStratification(TourGuideAddin adapter, AGLView view) {
		StateMachineImpl stateMachine = createStateMachine(adapter.getVisibleTablePerspectives(), EWizardMode.GLOBAL,
				null);
		stateMachine.move(stateMachine.get(BROWSE_STRATIFICATIONS));
		return new AddWizardElement(view, adapter, stateMachine);
	}


	public static AddWizardElement createForOther(TourGuideAddin adapter, AGLView view) {
		StateMachineImpl stateMachine;
		stateMachine = createStateMachine(adapter.getVisibleTablePerspectives(), EWizardMode.GLOBAL, null);
		if (!adapter.getVisibleTablePerspectives().isEmpty()) {
			stateMachine.move(stateMachine.get(BROWSE_AND_SELECT_OTHER));
		} else {
			stateMachine.move(stateMachine.get(BROWSE_OTHER));
		}
		return new AddWizardElement(view, adapter, stateMachine);
	}


	public static AddWizardElement createForPathway(TourGuideAddin adapter, AGLView view) {
		StateMachineImpl stateMachine;
		stateMachine = createStateMachine(adapter.getVisibleTablePerspectives(), EWizardMode.GLOBAL, null);
		if (!adapter.getVisibleTablePerspectives().isEmpty()) {
			stateMachine.move(stateMachine.get(BROWSE_AND_SELECT_PATHWAY));
		} else {
			stateMachine.move(stateMachine.get(ALONE_PATHWAY));
		}
		return new AddWizardElement(view, adapter, stateMachine);
	}


}
