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
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.GLElementSelector;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * @author Samuel Gratzl
 *
 */
public class AddWizardElement extends GLElementSelector implements ISelectionCallback {
	private static int LINE_HEIGHT = 15;
	private int state = 0;

	public AddWizardElement(float archHeight) {
		int act = 1;
		GLFlowLayout layout = new GLFlowLayout(false, 10, new GLPadding(2, 10, 2, 10));
		GLElementContainer start = new GLElementContainer(layout);
		this.add(start);
		start.add(createState(archHeight, ""));

		{
			start.add(createTransition(act++, "Add", "Stratification"));
			GLElementContainer stratification = new GLElementContainer(layout);
			this.add(stratification);
			stratification.add(createState(archHeight, "Add", "Stratification"));

			int browseId;
			stratification.add(createTransition(browseId = act++, "Browse List"));
			{
				GLElementContainer browse = new GLElementContainer(layout);
				this.add(browse);
				browse.add(createState(archHeight, "Add", "Stratification"));
				browse.add(createFinal("Select", "a stratification in", "the Tour Guide", "to preview.", "",
						"Then confirm", "or cancel your", "selection."));
			}
			stratification.add(createTransition(act++, "Find similar to displayed stratification"));
			{
				GLElementContainer adjustedRand = new GLElementContainer(layout);
				this.add(adjustedRand);
				adjustedRand.add(createState(archHeight, "Add", "Stratification"));
				adjustedRand
						.add(createAction(
								browseId,
								"Select query stratification by clicking on the header brick of one of the displayed columns\nChange query by cvlicking on other header brick at any time"));
			}
			stratification.add(createTransition(act++, "Find large overlap with displayed clusters"));
			{
				GLElementContainer adjustedRand = new GLElementContainer(layout);
				this.add(adjustedRand);
				adjustedRand.add(createState(archHeight, "Add", "Stratification"));
				adjustedRand
						.add(createAction(
								browseId,
								"Select query stratification by clicking on a brick in one of the displayed columns\nChange query by cvlicking on other brick at any time"));
			}

		}

		{
			start.add(createTransition(act++, "Add", "Pathway"));
			GLElementContainer pathway = new GLElementContainer(layout);
			this.add(pathway);
			pathway.add(createState(archHeight, "Add", "Pathway"));
			pathway.add(createTransition(act++, "Browse List and stratify with displayed stratification"));
			{
				GLElementContainer list = new GLElementContainer(layout);
				this.add(list);
				list.add(createState(archHeight, "Add", "Pathway"));
				list.add(createAction(act++, "Select a pathway in the Tour Guide."));

				{
					GLElementContainer s = new GLElementContainer(layout);
					this.add(s);
					s.add(createState(archHeight, "Add", "Pathway"));
					// dynamic preview
					s.add(createFinal("Change pathway in Tour Guide at any time\nSelect stratification by clicking on the header brick of one of the displayed columns"));
				}
				list.add(createTransition(act++, "Find with GSEA based on displayed stratification"));
				list.add(createTransition(act++, "Find with GSEA based on strat. not displayed"));

			}
		}

		{
			start.add(createTransition(act++, "Add", "Numerical Data"));
			GLElementContainer numericalData = new GLElementContainer(layout);
			this.add(numericalData);
			numericalData.add(createState(archHeight, "Add", "Numerical Data"));
			numericalData.add(createFinal("Select", "a numerical data in", "the Tour Guide", "to preview.", "",
					"Then confirm", "or cancel your", "selection."));
		}
	}

	private GLElement createState(float height, String... lines) {
		return new GLElement(multiLine(lines)).setSize(-1, height);
	}

	private GLElement createAction(int objectId, String... lines) {
		return new ActionElement().setCallback(this).setPickingObjectId(objectId).setRenderer(multiLine(lines));
	}

	private GLElement createTransition(int objectId, String... lines) {
		return new GLButton().setCallback(this).setPickingObjectId(objectId).setRenderer(multiLine(lines));
	}

	private GLElement createFinal(String... lines) {
		return new GLElement(multiLine(lines));
	}

	/**
	 * @param lines
	 * @return
	 */
	private IGLRenderer multiLine(String[] lines) {
		final List<String> l = Arrays.asList(lines);
		return new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.drawText(l, 0, (h - LINE_HEIGHT*l.size()) * 0.5f, w, LINE_HEIGHT*l.size() ,0,VAlign.CENTER);
			}
		};
	}

	@Override
	protected int select(float w, float h) {
		return state;
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		state = button.getPickingObjectId();
		relayout();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(0.95f).fillRect(0, 0, w, h);
		g.color(Color.DARK_GRAY).drawRect(0, 0, w, h);
		super.renderImpl(g, w, h);
	}

	static class ActionElement extends GLButton {
		public ActionElement() {
			setVisibility(EVisibility.VISIBLE); // just manually trigger
		}
	}
}



