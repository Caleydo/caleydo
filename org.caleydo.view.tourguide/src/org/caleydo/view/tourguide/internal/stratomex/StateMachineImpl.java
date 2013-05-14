package org.caleydo.view.tourguide.internal.stratomex;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.state.ButtonTransition;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.OpenTourGuideState;
import org.caleydo.view.tourguide.api.state.SimpleState;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;

class StateMachineImpl implements IStateMachine {
	@DeepScan
	private final BiMap<String, IState> states = HashBiMap.create();
	@DeepScan
	private final Multimap<IState, ITransition> transitions = ArrayListMultimap.create();
	private IState current = null;

	/**
	 *
	 */
	public StateMachineImpl() {
		createDefaultStateMachine();
	}

	/**
	 *
	 */
	private void createDefaultStateMachine() {
		this.current = addState("root", new SimpleState(""));
		IState addStratification = addState(ADD_STRATIFICATIONS, new SimpleState("Add Stratification"));
		addTransition(current, new ButtonTransition(addStratification, "Add Stratification"));
		IState browseStratification = addState(BROWSE_STRATIFICATIONS, new OpenTourGuideState(
				EDataDomainQueryMode.STRATIFICATIONS,
				"Select\na stratification in\nthe Tour Guide\nto preview.\n\nThen confirm\nor cancel your\nselection."));
		addTransition(addStratification, new ButtonTransition(browseStratification, "Browse List"));

		IState addPathway = addState(ADD_PATHWAY, new SimpleState("Add Pathway"));
		addTransition(current, new ButtonTransition(addPathway, "Add Pathway"));
		addState(BROWSE_PATHWAY, new OpenTourGuideState(EDataDomainQueryMode.PATHWAYS,
				"Select\na pathway in\nthe Tour Guide\nto preview.\n\nThen confirm\nor cancel your\nselection."));
		// addTransition(addPathway, new ButtonTransition(browsePathway, "Browse List"));

		IState addNumerical = addState(ADD_NUMERICAL, new SimpleState("Add Numerical Data"));
		addTransition(current, new ButtonTransition(addNumerical, "Add Numerical Data"));
		addState(BROWSE_NUMERICAL, new OpenTourGuideState(EDataDomainQueryMode.NUMERICAL,
				"Select\na stratification in\nthe Tour Guide\nto preview.\n\nThen confirm\nor cancel your\nselection."));
		// addTransition(addStratification, new ButtonTransition(browseStratification, "Browse List"));

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
	public Collection<ITransition> getTransitions(IState state) {
		return Collections.unmodifiableCollection(transitions.get(state));
	}

}