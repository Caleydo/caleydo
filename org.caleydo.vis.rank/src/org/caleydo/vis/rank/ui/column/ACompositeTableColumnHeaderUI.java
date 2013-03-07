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
package org.caleydo.vis.rank.ui.column;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.SeparatorUI;
/**
 * @author Samuel Gratzl
 *
 */
public abstract class ACompositeTableColumnHeaderUI<T extends ACompositeRankColumnModel> extends ACompositeHeaderUI {
	private static final int SUMMARY = 0;

	protected final T model;

	public ACompositeTableColumnHeaderUI(T model, boolean interactive) {
		super(interactive, 1);
		this.model = model;
		setLayoutData(model);
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		init(model);
	}

	@Override
	protected SeparatorUI createSeparator(int index) {
		return new SeparatorUI(this, 0);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (model.isCollapsed()) { // just the summary
			g.incZ();
			get(SUMMARY).render(g);
			g.decZ();
		} else
			super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (model.isCollapsed()) { // just the summary
			g.incZ();
			get(SUMMARY).renderPick(g);
			g.decZ();
		} else
			super.renderPickImpl(g, w, h);
	}

	@Override
	protected void takeDown() {
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		super.takeDown();
	}

	@Override
	protected float getLeftPadding() {
		return RenderStyle.COLUMN_SPACE;
	}

	@Override
	public boolean canMoveHere(int index, ARankColumnModel model) {
		return this.model.isMoveAble(model, index);
	}

	@Override
	public void moveHere(int index, ARankColumnModel model) {
		assert canMoveHere(index, model);
		this.model.move(model, index);
	}
}

