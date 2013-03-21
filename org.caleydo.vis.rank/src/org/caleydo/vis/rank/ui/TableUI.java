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
package org.caleydo.vis.rank.ui;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.layout.IRowHeightLayout;
import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.RankTableModel;

/**
 * basic ui widget for a {@link RankTableModel}
 *
 * @author Samuel Gratzl
 *
 */
public class TableUI extends GLElementContainer implements IGLLayout {
	public TableUI(RankTableModel table, IRankTableUIConfig config, IRowHeightLayout... layouts) {
		setLayout(this);
		this.add(new TableHeaderUI(table, config));
		this.add(new TableBodyUI(table, layouts.length == 0 ? RowHeightLayouts.UNIFORM : layouts[0], config));
	}

	public TableBodyUI getBody() {
		return (TableBodyUI) get(1);
	}

	public TableHeaderUI getHeader() {
		return (TableHeaderUI) get(0);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement header = children.get(0);
		IGLLayoutElement body = children.get(1);
		float hi = header.getSetHeight();
		header.setBounds(0, 0, w, hi);
		body.setBounds(0, hi, w, h - hi);
		Vec2f old = getLayoutDataAs(Vec2f.class, new Vec2f(0, 0));
		Vec2f new_ = body.getLayoutDataAs(Vec2f.class, new Vec2f(0, 0));
		if (!old.equals(new_)) {
			setLayoutData(new_.copy());
			relayoutParent();
		}
	}
}