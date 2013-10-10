/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.s;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.SimpleState;

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

	private final Deque<IState> history = new LinkedList<>();

	public StateMachineImpl() {
		current = addState("root", new SimpleState(""));
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
		this.history.add(this.current);
		this.current.onLeave();
		this.current = target;
		this.current.onEnter();
	}

	public void goBack() {
		IState previous = history.pollLast();
		this.current.onLeave();
		this.current = previous;
		this.current.onEnter();
	}

	public IState getPrevious() {
		return history.isEmpty() ? null : history.getLast();
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
