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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.basic.GLElementSelector;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.state.ASelectGroupState;
import org.caleydo.view.tourguide.api.state.ASelectStratificationState;
import org.caleydo.view.tourguide.api.state.ButtonTransition;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.MultiLineTextRenderer;
import org.caleydo.view.tourguide.api.state.OpenTourGuideState;
import org.caleydo.view.tourguide.api.state.SimpleState;

/**
 * @author Samuel Gratzl
 *
 */
public class AddWizardElement extends GLElementSelector implements ICallback<IState> {
	private final IGLLayout stateLayout;
	private final Object receiver;

	private IState current;
	private final Map<IState, Integer> stateMap = new HashMap<>();


	public AddWizardElement(Object receiver) {
		this.stateLayout = new GLFlowLayout(false, 20, new GLPadding(2, 10, 2, 10));
		this.receiver = receiver;

		this.current = createStateMachine();
		this.current.onEnter(this);
		this.add(convert(current));
		stateMap.put(current, 0);
	}

	private GLElement convert(final IState state) {
		GLElementContainer container = new GLElementContainer(stateLayout);
		container.add(new GLElement(multiLine(state.getLabel().split("\n"))).setSize(-1, 100));
		for (ITransition t : state.getTransitions()) {
			container.add(t.create(this));
		}
		container.setLayoutData(state);
		return container;
	}

	private IState createStateMachine() {
		SimpleState start = new SimpleState("");
		{
			SimpleState stratification = new SimpleState("Add\nStratification");
			start.addTransition(new ButtonTransition(stratification, "Add\nStratification"));

			IState browse = new OpenTourGuideState(EDataDomainQueryMode.STRATIFICATIONS,
					"Select\na stratification in\nthe Tour Guide\nto preview.\n\nThen confirm\nor cancel your\nselection.");
			stratification.addTransition(new ButtonTransition(browse, "Browse List"));

			IState selectStratification = new ASelectStratificationState(
					"Select query stratification by clicking on the header brick of one of the displayed columns\nChange query by cvlicking on other header brick at any time",
					browse, receiver) {
				@Override
				protected void handleSelection(TablePerspective tablePerspective) {
					//TODO
				}

				@Override
				public boolean apply(TablePerspective tablePerspective) {
					return true;
				}
			};
			stratification.addTransition(new ButtonTransition(selectStratification,
					"Find similar to displayed stratification"));

			IState selectGroup = new ASelectGroupState(
					"Select query stratification by clicking on a brick in one of the displayed columns\nChange query by cvlicking on other brick at any time",
					browse, receiver) {
				@Override
				protected void handleSelection(TablePerspective tablePerspective, Group group) {
					// TODO
				}

				@Override
				public boolean apply(Pair<TablePerspective, Group> selection) {
					return true;
				}
			};
			stratification
					.addTransition(new ButtonTransition(selectGroup, "Find large overlap with displayed clusters"));
		}

		{
			SimpleState pathway = new SimpleState("Add\nPathway");
			start.addTransition(new ButtonTransition(pathway, "Add\nPathway"));

			IState browse = new OpenTourGuideState(EDataDomainQueryMode.PATHWAYS,
					"Select\na stratification in\nthe Tour Guide\nto preview.\n\nThen confirm\nor cancel your\nselection.");
			pathway.addTransition(new ButtonTransition(browse, "Browse List"));

			// start.add(createTransition(act++, "Add", "Pathway"));
			// GLElementContainer pathway = new GLElementContainer(layout);
			// this.add(pathway);
			// pathway.add(createState("Add", "Pathway"));
			// pathway.add(createTransition(act++, "Browse List and stratify with displayed stratification"));
			// {
			// GLElementContainer list = new GLElementContainer(layout);
			// this.add(list);
			// list.add(createState("Add", "Pathway"));
			// list.add(createAction(act++, "Select a pathway in the Tour Guide."));
			//
			// {
			// GLElementContainer s = new GLElementContainer(layout);
			// this.add(s);
			// s.add(createState("Add", "Pathway"));
			// // dynamic preview
			// s.add(createFinal("Change pathway in Tour Guide at any time\nSelect stratification by clicking on the header brick of one of the displayed columns"));
			// }
			// list.add(createTransition(act++, "Find with GSEA based on displayed stratification"));
			// list.add(createTransition(act++, "Find with GSEA based on strat. not displayed"));
			//
			// }
		}

		{
			SimpleState numerical = new SimpleState("Add\nNumerical Data");
			start.addTransition(new ButtonTransition(numerical, "Add\nNumerical Data"));

			IState browse = new OpenTourGuideState(EDataDomainQueryMode.NUMERICAL,
					"Select\na numerical data in\nthe Tour Guide\nto preview.\n\nThen confirm\nor cancel your\nselection.");
			numerical.addTransition(new ButtonTransition(browse, "Browse List"));
		}

		return start;
	}

	/**
	 * @param lines
	 * @return
	 */
	private IGLRenderer multiLine(String[] lines) {
		final List<String> l = Arrays.asList(lines);
		return new MultiLineTextRenderer(l);
	}

	@Override
	protected int select(float w, float h) {
		return stateMap.get(current);
	}

	@Override
	public void on(IState target) {
		this.current.onLeave();
		this.current = target;
		this.current.onEnter(this);
		if (!stateMap.containsKey(target)) {
			this.add(convert(current));
			stateMap.put(current, size() - 1);
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

	public static void main(String[] args) {
		GLSandBox.main(args, new AddWizardElement(null));
	}
}



