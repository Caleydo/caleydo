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

import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;

import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.GroupRankColumnModel;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.TableHeaderUI;
/**
 * @author Samuel Gratzl
 *
 */
public class GroupColumnHeaderUI extends ACompositeHeaderUI implements IThickHeader, IColumnRenderInfo {
	protected final GroupRankColumnModel model;

	public GroupColumnHeaderUI(GroupRankColumnModel model, IRankTableUIConfig config) {
		super(config, 1);
		this.model = model;
		setLayoutData(model);
		this.add(0, new GroupSummaryHeaderUI(model, config));
		model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		init(model);
	}
	@Override
	protected GLElement wrapImpl(ARankColumnModel model) {
		GLElement g = ColumnUIs.createHeader(model, config, false);
		return g;
	}

	@Override
	protected void takeDown() {
		model.removePropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, childrenChanged);
		super.takeDown();
	}

	@Override
	protected boolean isSmallHeader() {
		if (getParent() == null)
			return false;
		IGLElementParent p = getParent();
		while (!(p instanceof TableHeaderUI))
			p = p.getParent();
		return ((TableHeaderUI) p).isSmallHeader();
	}

	@Override
	protected float getLeftPadding() {
		return +RenderStyle.STACKED_COLUMN_PADDING;
	}

	@Override
	public float getTopPadding(boolean smallHeader) {
		return LABEL_HEIGHT;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement summary = children.get(0);
		summary.setBounds(2, 0, w - 4, LABEL_HEIGHT);
		super.layoutColumns(children, w, h);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (true) {
			config.renderHeaderBackground(g, w, h, LABEL_HEIGHT, model);
			g.decZ().decZ();
			// RoundedRectRenderer.render(g, 0, 0, w, h, RenderStyle.HEADER_ROUNDED_RADIUS, 3,
			// RoundedRectRenderer.FLAG_FILL | RoundedRectRenderer.FLAG_TOP);

			g.lineWidth(RenderStyle.COLOR_STACKED_BORDER_WIDTH);
			g.color(RenderStyle.COLOR_STACKED_BORDER);

			// gl.glBegin(GL.GL_LINE_STRIP);
			// {
			// gl.glVertex3f(0, h, z);
			// renderRoundedCorner(g, 0, 0, RenderStyle.HEADER_ROUNDED_RADIUS, 0, RoundedRectRenderer.FLAG_TOP_LEFT);
			// renderRoundedCorner(g, w - RenderStyle.HEADER_ROUNDED_RADIUS, 0, RenderStyle.HEADER_ROUNDED_RADIUS, 0,
			// RoundedRectRenderer.FLAG_TOP_RIGHT);
			// gl.glVertex3f(w, h, z);
			// }
			// gl.glEnd();
			g.drawRect(0, 0, w, h);
			g.lineWidth(1);
			g.incZ().incZ();

			// g.drawLine(x, yi, x, yi + hi + 2);
		}
		super.renderImpl(g, w, h);
	}

	@Override
	protected float getChildWidth(int i, ARankColumnModel model) {
		return model.getWidth();
	}

	@Override
	public boolean canMoveHere(int index, ARankColumnModel model, boolean clone) {
		return this.model.isMoveAble(model, index, clone);
	}

	@Override
	public void moveHere(int index, ARankColumnModel model, boolean clone) {
		assert canMoveHere(index, model, clone);
		this.model.move(model, index, clone);
	}

	@Override
	public boolean isCollapsed() {
		return model.isCollapsed();
	}

	@Override
	public VAlign getAlignment() {
		return VAlign.LEFT;
	}

	@Override
	public boolean hasFreeSpace() {
		return true;
	}

}

