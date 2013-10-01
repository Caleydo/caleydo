/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex;

import static org.caleydo.view.tourguide.api.state.IStateMachine.ADD_OTHER;
import static org.caleydo.view.tourguide.api.state.IStateMachine.ADD_PATHWAY;
import static org.caleydo.view.tourguide.api.state.IStateMachine.ADD_STRATIFICATIONS;
import static org.caleydo.view.tourguide.api.state.IStateMachine.BROWSE_OTHER;
import static org.caleydo.view.tourguide.api.state.IStateMachine.BROWSE_PATHWAY;
import static org.caleydo.view.tourguide.api.state.IStateMachine.BROWSE_STRATIFICATIONS;

import java.util.Collection;
import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.state.BrowseOtherState;
import org.caleydo.view.tourguide.api.state.BrowsePathwayState;
import org.caleydo.view.tourguide.api.state.BrowseStratificationState;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.api.util.PathwayOracle;
import org.caleydo.view.tourguide.internal.score.ScoreFactories;
import org.caleydo.view.tourguide.stratomex.state.AlonePathwayState;
import org.caleydo.view.tourguide.stratomex.state.BrowseNumericalAndStratificationState;
import org.caleydo.view.tourguide.stratomex.state.BrowsePathwayAndStratificationState;
import org.caleydo.view.tourguide.stratomex.state.SelectStateState;
import org.caleydo.view.tourguide.stratomex.wizard.AAddWizardElement;
import org.caleydo.view.tourguide.stratomex.wizard.IAddWizardElementFactory;
import org.caleydo.view.tourguide.stratomex.wizard.IStratomexAdapter;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class AddWizardElementFactory implements IAddWizardElementFactory {
	private static final String BROWSE_AND_SELECT_OTHER = "browseAndSelectNumerical";
	private static final String BROWSE_AND_SELECT_PATHWAY = "browseAndSelectPathway";
	private static final String ALONE_PATHWAY = "alonePathway";

	@Override
	public AAddWizardElement create(IStratomexAdapter adapter, AGLView view) {
		return new AddWizardElement(view, adapter, createStateMachine(adapter.getVisibleTablePerspectives(),
				EWizardMode.GLOBAL, null));
	}

	@Override
	public AAddWizardElement createDependent(IStratomexAdapter adapter, AGLView view, TablePerspective tablePerspective) {
		return new AddWizardElement(view, adapter, createStateMachine(adapter.getVisibleTablePerspectives(),
				EWizardMode.DEPENDENT, tablePerspective));
	}

	@Override
	public AAddWizardElement createIndepenent(IStratomexAdapter adapter, AGLView view, TablePerspective tablePerspective) {
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
			state.addState(ALONE_PATHWAY, new AlonePathwayState());

			// select pathway -> show preview -> select stratification -> show both
			IState browseIntermediate = state.addState(BROWSE_AND_SELECT_PATHWAY,
					new BrowsePathwayAndStratificationState());
			state.addTransition(addPathway, new SimpleTransition(browseIntermediate,
					"From list and stratify with a displayed stratification", !existing.isEmpty() ? null
							: "At least one mappable stratification must be already visible"));
			// select pathway -> show preview -> select stratification -> show both
			IState browseOtherIntermediate = state.addState(BROWSE_AND_SELECT_OTHER,
					new BrowseNumericalAndStratificationState());
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

		ScoreFactories.fillStateMachine(state, existing, mode, source);

		boolean hideEmpty = mode != EWizardMode.GLOBAL;
		addStartTransition(state, IStateMachine.ADD_STRATIFICATIONS, hideEmpty);
		addStartTransition(state, IStateMachine.ADD_PATHWAY, hideEmpty);
		addStartTransition(state, IStateMachine.ADD_OTHER, hideEmpty);

		skipSingleOnes(state);

		return state;
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
		state.addState(ADD_STRATIFICATIONS, new SelectStateState("Select stratification",
				EDataDomainQueryMode.STRATIFICATIONS));

		state.addState(BROWSE_STRATIFICATIONS, new BrowseStratificationState(
				"Select a stratification in the LineUp to preview.\nThen confirm or cancel your selection."));

		state.addState(ADD_PATHWAY, new SelectStateState("Select pathway", EDataDomainQueryMode.PATHWAYS));

		state.addState(BROWSE_PATHWAY, new BrowsePathwayState(
				"Select a pathway in the LineUp to preview.\n Then confirm or cancel your selection."));

		state.addState(ADD_OTHER,
				new SelectStateState("Select other data " + toString(EDataDomainQueryMode.OTHER.getAllDataDomains()),
						EDataDomainQueryMode.OTHER));

		state.addState(BROWSE_OTHER, new BrowseOtherState(
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
	@Override
	public AAddWizardElement createForStratification(IStratomexAdapter adapter, AGLView view) {
		StateMachineImpl stateMachine = createStateMachine(adapter.getVisibleTablePerspectives(), EWizardMode.GLOBAL,
				null);
		stateMachine.move(stateMachine.get(BROWSE_STRATIFICATIONS));
		return new AddWizardElement(view, adapter, stateMachine);
	}

	@Override
	public AAddWizardElement createForOther(IStratomexAdapter adapter, AGLView view) {
		StateMachineImpl stateMachine;
		stateMachine = createStateMachine(adapter.getVisibleTablePerspectives(), EWizardMode.GLOBAL, null);
		if (!adapter.getVisibleTablePerspectives().isEmpty()) {
			stateMachine.move(stateMachine.get(BROWSE_AND_SELECT_OTHER));
		} else {
			stateMachine.move(stateMachine.get(BROWSE_OTHER));
		}
		return new AddWizardElement(view, adapter, stateMachine);
	}

	@Override
	public AAddWizardElement createForPathway(IStratomexAdapter adapter, AGLView view) {
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
