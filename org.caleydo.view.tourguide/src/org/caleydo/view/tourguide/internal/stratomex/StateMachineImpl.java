package org.caleydo.view.tourguide.internal.stratomex;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.view.tourguide.api.state.BrowseNumericalState;
import org.caleydo.view.tourguide.api.state.BrowsePathwayState;
import org.caleydo.view.tourguide.api.state.BrowseStratificationState;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.SimpleState;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.internal.stratomex.state.BrowsePathwayAndStratificationState;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;

class StateMachineImpl implements IStateMachine {
	@DeepScan
	private final BiMap<String, IState> states = HashBiMap.create();
	@DeepScan
	private final ListMultimap<IState, ITransition> transitions = ArrayListMultimap.create();
	private IState current = null;

	/**
	 *
	 */
	private StateMachineImpl() {

	}

	public static StateMachineImpl create(Object receiver, List<TablePerspective> existing) {
		StateMachineImpl impl = new StateMachineImpl();

		impl.current = impl.addState("root", new SimpleState(""));
		IState current = impl.getCurrent();

		IState addStratification = impl.addState(ADD_STRATIFICATIONS, new SimpleState("Add Stratification"));
		IState browseStratification = impl.addState(BROWSE_STRATIFICATIONS, new BrowseStratificationState(
				"Select a stratification in the Tour Guide to preview.\nThen confirm or cancel your selection."));
		impl.addTransition(addStratification, new SimpleTransition(browseStratification, "Browse List"));

		IState addPathway = impl.addState(ADD_PATHWAY, new SimpleState("Add Pathway"));
		impl.addState(BROWSE_PATHWAY, new BrowsePathwayState(
				"Select a pathway in the Tour Guide to preview.\n Then confirm or cancel your selection."));

		if (!existing.isEmpty()) {
			// select pathway -> show preview -> select stratification -> show both
			IState browseIntermediate = impl.addState("browseAndSelectPathway",
					new BrowsePathwayAndStratificationState());
			impl.addTransition(addPathway, new SimpleTransition(browseIntermediate,
					"Browse list and stratify with a displayed stratification"));
		}

		IState addNumerical = impl.addState(ADD_NUMERICAL, new SimpleState("Add Numerical Data"));
		impl.addState(BROWSE_NUMERICAL, new BrowseNumericalState(
				"Select a stratification in the Tour Guide\nto preview.\n\nThen confirm or cancel your selection."));
		// addTransition(addStratification, new ButtonTransition(browseStratification, "Browse List"));

		return impl;
	}

	@Override
	public IState addState(String id, IState state) {
		states.put(id, state);
		return state;
	}

	/**
	 * @param target
	 * @return
	 */
	public void move(IState target) {
		this.current.onLeave();
		this.current = target;
		this.current.onEnter();

	}

	@Override
	public void addTransition(IState source, ITransition transition) {
		if (!states.inverse().containsKey(source)) // add if not already part of
			addState(source.getLabel(), source);
		transitions.put(source, transition);
	}

	@Override
	public IState get(String id) {
		return states.get(id);
	}

	@Override
	public Set<String> getStates() {
		return Collections.unmodifiableSet(states.keySet());
	}

	@Override
	public IState getCurrent() {
		return current;
	}

	@Override
	public List<ITransition> getTransitions(IState state) {
		return Collections.unmodifiableList(transitions.get(state));
	}

}