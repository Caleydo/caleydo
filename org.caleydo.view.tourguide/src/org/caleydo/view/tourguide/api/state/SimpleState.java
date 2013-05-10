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
package org.caleydo.view.tourguide.api.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleState implements IState {
	private final Collection<ITransition> transitions = new ArrayList<>();
	private final String label;

	public SimpleState(String label) {
		this.label = label;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void onEnter() {

	}

	public SimpleState addTransition(ITransition transition) {
		this.transitions.add(transition);
		return this;
	}

	@Override
	public Collection<ITransition> getTransitions() {
		return Collections.unmodifiableCollection(transitions);
	}

	@Override
	public void onLeave() {

	}

}
