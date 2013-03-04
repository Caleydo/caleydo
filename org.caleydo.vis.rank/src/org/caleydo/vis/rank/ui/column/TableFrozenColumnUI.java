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

import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.FrozenRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.ui.TableBodyUI;

/**
 * @author Samuel Gratzl
 *
 */
public class TableFrozenColumnUI extends ACompositeTableColumnUI<FrozenRankColumnModel> {

	public TableFrozenColumnUI(FrozenRankColumnModel model) {
		super(model);
	}

	@Override
	protected GLElement wrap(ARankColumnModel model) {
		TableColumnUI ui = new TableColumnUI(model);
		ui.setData(model.getTable().getData(), this);
		return ui;
	}

	@Override
	public GLElement setData(Iterable<IRow> data, IColumModelLayout parent) {
		// nothing to do
		return this;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		// g.decZ().decZ();
		// g.color(stacked.getBgColor()).fillRect(0, 0, w, h);
		// g.incZ().incZ();
		// float x = get(stacked.getAlignment()).getLocation().x();
		// g.color(Color.BLUE).drawLine(x, 0, x, h);
		super.renderImpl(g, w, h);
	}

	@Override
	public void layoutRows(ARankColumnModel model, List<? extends IGLLayoutElement> children, float w, float h) {
		// simple
		((TableBodyUI) getParent()).layoutRows(model, children, w, h);
	}

	@Override
	public VAlign getAlignment(TableColumnUI model) {
		return VAlign.LEFT;
	}

	@Override
	public boolean hasFreeSpace(TableColumnUI model) {
		return true;
	}

}
