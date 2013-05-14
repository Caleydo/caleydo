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
package org.caleydo.view.tourguide.internal.stratomex;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.GLElementSelector;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.view.tourguide.api.state.IDefaultTransition;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.IUserTransition;
import org.caleydo.view.tourguide.internal.score.ScoreFactories;

/**
 * @author Samuel Gratzl
 *
 */
public class AddWizardElement extends GLElementSelector implements ICallback<IState> {
	private final IGLLayout stateLayout;

	private final Map<IState, Integer> stateMap = new HashMap<>();
	@DeepScan
	private StateMachineImpl stateMachine;


	public AddWizardElement(Object receiver, List<TablePerspective> existing) {
		this.stateLayout = new GLFlowLayout(false, 20, new GLPadding(2, 10, 2, 10));

		this.stateMachine = createStateMachine(receiver, existing);
		this.stateMachine.getCurrent().onEnter();
		this.add(convert(this.stateMachine.getCurrent()));
		stateMap.put(this.stateMachine.getCurrent(), 0);
	}

	private StateMachineImpl createStateMachine(Object receiver, List<TablePerspective> existing) {
		StateMachineImpl state = new StateMachineImpl();
		ScoreFactories.fillStateMachine(state, receiver, existing);
		return state;
	}

	private GLElement convert(final IState state) {
		GLElementContainer container = new GLElementContainer(stateLayout);
		final GLElement header = new GLElement(new MultiLineTextRenderer(state.getLabel())).setSize(-1, 100);
		container.add(header);
		for (ITransition t : stateMachine.getTransitions(state)) {
			if (!(t instanceof IUserTransition))
				continue;
			GLElement elem = convert((IUserTransition) t);
			if (elem != null)
				container.add(elem);
		}
		if (container.size() == 1) {
			header.setSize(-1, -1); // center if no elements are there
		}
		container.setLayoutData(state);
		return container;
	}

	/**
	 * @param t
	 * @return
	 */
	private GLElement convert(final IUserTransition t) {
		GLButton b = new GLButton();
		b.setCallback(new ISelectionCallback() {
			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				t.apply(AddWizardElement.this);
			}
		});
		b.setRenderer(new MultiLineTextRenderer(t.getLabel()));
		return b;
	}

	@Override
	protected int select(float w, float h) {
		return stateMap.get(stateMachine.getCurrent());
	}

	@Override
	public void on(IState target) {
		stateMachine.move(target);
		Collection<ITransition> transitions = stateMachine.getTransitions(target);
		for (ITransition t : transitions) {
			t.onSourceEnter(this);
		}
		// automatically switch default single transitions
		if (transitions.size() == 1 && transitions.iterator().next() instanceof IDefaultTransition) {
			((IDefaultTransition) transitions.iterator().next()).apply(this);
			return;
		}

		if (!stateMap.containsKey(target)) {
			this.add(convert(target));
			stateMap.put(target, size() - 1);
		} else {
			relayout();
		}
	}


	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(0.95f).fillRect(0, 0, w, h);
		g.color(Color.DARK_GRAY).drawRect(0, 0, w, h);
		super.renderImpl(g, w, h);
	}
}



