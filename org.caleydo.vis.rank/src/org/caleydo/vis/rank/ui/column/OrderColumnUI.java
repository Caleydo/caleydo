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

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ColumnRanker;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.TableBodyUI;

/**
 * @author Samuel Gratzl
 *
 */
public class OrderColumnUI extends GLElement implements PropertyChangeListener, ITableColumnUI {
	private final ColumnRanker ranker;
	private final OrderColumn model;
	private float[] rowPositions;
	private int[] rankDeltas;

	public OrderColumnUI(OrderColumn model, ColumnRanker ranker) {
		this.ranker = ranker;
		this.model = model;
		ranker.addPropertyChangeListener(ColumnRanker.PROP_ORDER, this);
		ranker.addPropertyChangeListener(ColumnRanker.PROP_INVALID, this);
		setLayoutData(model);
	}

	private TableBodyUI getTableBodyUI() {
		return (TableBodyUI) getParent();
	}
	/**
	 * @return the ranker, see {@link #ranker}
	 */
	public ColumnRanker getRanker() {
		return ranker;
	}

	@Override
	public ARankColumnModel getModel() {
		return model;
	}

	@Override
	public GLElement asGLElement() {
		return this;
	}

	@Override
	public GLElement get(int index) {
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case ColumnRanker.PROP_ORDER:
			rankDeltas = (int[]) evt.getOldValue();
			updateMeAndMyChildren();
			break;
		case ColumnRanker.PROP_INVALID:
			updateMeAndMyChildren();
		}
	}

	@Override
	protected void takeDown() {
		ranker.removePropertyChangeListener(ColumnRanker.PROP_ORDER, this);
		ranker.removePropertyChangeListener(ColumnRanker.PROP_INVALID, this);
		super.takeDown();
	}

	private void updateMeAndMyChildren() {
		getTableBodyUI().updateMyChildren(this);
	}

	@Override
	protected void layout() {
		super.layout();
		float h = getSize().y();
		rowPositions = computeRowPositions(h, ranker.size(), ranker.getSelectedRank());
	}

	public int getRankDelta(IRow row) {
		if (rankDeltas == null)
			return 0;
		int r = ranker.getRank(row);
		if (r < 0 || rankDeltas.length <= r)
			return Integer.MAX_VALUE;
		int result = rankDeltas[r];
		return result;
	}

	public float[] getRowPositions() {
		return rowPositions;
	}

	private float[] computeRowPositions(float h, int numRows, int selectedRank) {
		float[] hs = getTableBodyUI().getRowLayout().compute(numRows, selectedRank, h - 5);
		float acc = 0;
		for (int i = 0; i < hs.length; ++i) {
			hs[i] += acc;
			acc = hs[i];
		}
		return hs;
	}

	@Override
	public GLElement setData(Iterable<IRow> rows, IColumModelLayout parent) {
		return this;
	}

	@Override
	public void update() {
		relayout();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (rankDeltas != null) {
			getTableBodyUI().triggerRankAnimations(this, rankDeltas);
			rankDeltas = null; // a single run with the rank deltas, not used anymore
		}
		// render the bands
		if (dontneedToRender(w))
			return;

		TableBodyUI body = getTableBodyUI();
		OrderColumnUI previousRanker = body.getRanker(model);
		ITableColumnUI previous = body.getLastCorrespondingColumn(previousRanker, true);
		ITableColumnUI self = body.getLastCorrespondingColumn(this, true);
		if (self == null || self == this || previous == null || previous instanceof OrderColumnUI)
			return;
		int selectedRank = model.getRanker().getSelectedRank();

		g.save();
		g.gl.glTranslatef(0, 0, g.z());
		int i = -1;
		for(IRow row : model.getRanker()) {
			i++;
			Vec4f left = previous.get(row.getIndex()).getBounds();
			Vec4f right = self.get(row.getIndex()).getBounds();
			if (!areValidBounds(left) || !areValidBounds(right))
				continue;
			boolean isEvenRight = i % 2 == 0;
			boolean isEvenLeft = previousRanker.getRanker().getRank(row) % 2 == 0;
			renderBand(g, left, right, w, selectedRank == i, isEvenLeft, isEvenRight);
		}
		g.restore();
		super.renderImpl(g, w, h);

	}

	private boolean dontneedToRender(float w) {
		return model == null || model.isCollapsed() || w < 20;
	}

	private void renderBand(GLGraphics g, Vec4f left, Vec4f right, float w,
			boolean isSelected, boolean isEvenLeft, boolean isEvenRight) {
		if (isSelected) {
			g.incZ();
			g.color(RenderStyle.COLOR_SELECTED_ROW);
			g.fillPolygon(new Vec2f(-1, left.y()), new Vec2f(w, right.y()), new Vec2f(w, right.y() + right.w()),
					new Vec2f(-1, left.y() + left.w()));

			g.color(RenderStyle.COLOR_SELECTED_BORDER);
			g.drawLine(0, left.y(), w, right.y());
			g.drawLine(0, left.y() + left.w(), w, right.y() + right.w());
			g.decZ();
			return;
		}
		g.color(Color.GRAY);
		g.drawLine(0, left.y() + left.w() * 0.5f, w - 0, right.y() + right.w() * 0.5f);
	}
}
