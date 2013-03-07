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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ColumnRanker;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.ui.column.IColumModelLayout;
import org.caleydo.vis.rank.ui.column.ITableColumnUI;

/**
 * @author Samuel Gratzl
 *
 */
public class ColumnRankerUI extends GLElement implements PropertyChangeListener, ITableColumnUI {
	private final ColumnRanker ranker;
	private final OrderColumn model;
	private float[] rowPositions;
	private int[] rankDeltas;

	public ColumnRankerUI(OrderColumn model, ColumnRanker ranker) {
		this.ranker = ranker;
		this.model = model;
		ranker.addPropertyChangeListener(ColumnRanker.PROP_ORDER, this);
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
		}
	}

	@Override
	protected void takeDown() {
		ranker.removePropertyChangeListener(ColumnRanker.PROP_ORDER, this);
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

	//
	// if (rankDeltas != null)
	// triggerRankAnimations(w, h);
	// rankDeltas = null; // a single run with the rank deltas, not used anymore
	//
	// reason = null;
	// }
	//
	// private void triggerRankAnimations(float w, float h) {
	// assert rankDeltas != null;
	// ITableColumnUI col = findFirstSimpleCol();
	// if (col == null)
	// return;
	// for (int i = 0; i < rankDeltas.length; ++i) {
	// int delta = rankDeltas[i];
	// if (delta == 0 || delta == Integer.MAX_VALUE)
	// continue;
	// IRow r = table.getMyRanker(null).get(i);
	// if (r == null)
	// continue;
	// this.animate(new LineHighlightAnimation(delta, r));
	// }
	// }
	//
	// protected static void renderLineHighlight(GLGraphics g, IRow row, float alpha, int delta, float w) {
	// Vec4f bounds = getRowBounds(row);
	// if (bounds == null || delta == Integer.MIN_VALUE)
	// return;
	// float calpha = RenderStyle.computeHighlightAlpha(alpha, delta);
	// Color base = delta < 0 ? Color.GREEN : Color.RED; // TODO alpha
	// Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (calpha * 255));
	// g.decZ();
	// g.color(c);
	// renderSubLine(g, w, bounds.y() + 1, bounds.w() - 2);
	// g.incZ();
	// }
	//
	//
	// private ITableColumnUI findFirstSimpleCol() {
	// for (GLElement elem : this) {
	// if (elem instanceof ITableColumnUI && (!(elem instanceof ACompositeTableColumnUI)))
	// return (ITableColumnUI) elem;
	// }
	// return null;
	// }
	//
	// private static void renderSubLine(GLGraphics g, float w, float y, float hi) {
	// // just for elements that haven't an own order
	// float x = 0;
	// for (GLElement child : this) {
	// if (child instanceof ACompositeTableColumnUI && ((ACompositeTableColumnUI<?>) child).hasOwnOrder()) {
	// Vec4f l = child.getBounds();
	// g.fillRect(x, y + 3, l.x() - x, hi);
	// x = l.x() + l.z();
	// }
	// }
	// Vec4f last = get(size() - 1).getBounds();
	// if (x < (last.x() + last.z()))
	// g.fillRect(x, y + 3, last.x() + last.z() - x, hi);
	// }
	//
	// @Override
	// protected void renderPickImpl(GLGraphics g, float w, float h) {
	// float y = 0;
	// for (int i = 0; i < rowPositions.length; ++i) {
	// g.pushName(pickingIDs[i]);
	// renderSubLine(g, w, y, rowPositions[i] - y);
	// y = rowPositions[i];
	// g.popName();
	// }
	// super.renderPickImpl(g, w, h);
	// }
	//
	// /**
	// * @return the rowPositions, see {@link #rowPositions}
	// */
	//
	// static class LineHighlightAnimation extends ACustomAnimation {
	// private final IRow row;
	// private final int delta;
	//
	// public LineHighlightAnimation(int delta, IRow row) {
	// super(0, Durations.fix(RenderStyle.hightlightAnimationDuration(delta)));
	// this.delta = delta;
	// this.row = row;
	// }
	//
	// @Override
	// protected void firstTime(GLGraphics g, float w, float h) {
	// animate(g, 0, w, h);
	// }
	//
	// @Override
	// protected void animate(GLGraphics g, float alpha, float w, float h) {
	// renderLineHighlight(g, row, alpha, delta, w);
	// }
	//
	// @Override
	// protected void lastTime(GLGraphics g, float w, float h) {
	// animate(g, 1, w, h);
	// }
	// }
	//
	//
	//
	//

	// @Override
	// protected void renderImpl(GLGraphics g, float w, float h) {
	// if (!model.isCollapsed()) {
	// // render the lines between the orders
	// IRow selectedRow = model.getTable().getSelectedRow();
	// renderOrderLines(g, selectedRow == null ? -1 : selectedRow.getIndex());
	//
	// // highlight selected row
	// if (selectedRow != null && indexToRank.containsKey(selectedRow.getIndex())) {
	// int rank = indexToRank.get(selectedRow.getIndex());
	// if (rank < rowPositions.length && rank >= 0) {
	// float prev = rank == 0 ? 0 : rowPositions[rank - 1];
	// float next = rowPositions[rank];
	// g.color(RenderStyle.COLOR_SELECTED_ROW);
	// g.fillRect(RenderStyle.FROZEN_BAND_WIDTH, prev, w - RenderStyle.FROZEN_BAND_WIDTH, next - prev);
	// }
	// }
	// }
	// super.renderImpl(g, w, h);
	// }
	//
	// @Override
	// protected void renderPickImpl(GLGraphics g, float w, float h) {
	// if (!model.isCollapsed()) {
	// int[] pickingIDs = ((TableBodyUI) getParent()).getPickingIDs();
	//
	// // my positions
	// float[] right = getRowPositions();
	//
	// // the left positions
	// float[] left = ((TableBodyUI) getParent()).getPreviousRowPositions(this);
	// Iterator<IRow> leftOrder = ((TableBodyUI) getParent()).getPreviousOrder(this);
	//
	// ConnectionBandRenderer renderer = new ConnectionBandRenderer();
	// renderer.init(g.gl);
	// // align simple all the same x
	// float y = 0;
	// int ri = 0;
	// for (float hl : left) {
	// if (!leftOrder.hasNext())
	// break;
	// int index = leftOrder.next().getIndex();
	// if (indexToRank.containsKey(index)) {
	// int rightRank = indexToRank.get(index);
	// if (rightRank < right.length) {
	// // render the right picking id according to the left rank
	// g.pushName(pickingIDs[ri]);
	// float yr = rightRank == 0 ? 0 : right[rightRank - 1];
	// float hr = right[rightRank];
	// renderBand(g, renderer, y, hl, yr, hr, RenderStyle.FROZEN_BAND_WIDTH, false);
	// g.fillRect(RenderStyle.FROZEN_BAND_WIDTH, yr, w - RenderStyle.FROZEN_BAND_WIDTH, hr - yr);
	// g.popName();
	// }
	// }
	// y = hl;
	// ri++;
	// }
	// }
	// super.renderPickImpl(g, w, h);
	// }
	//
	// private void renderOrderLines(GLGraphics g, int selectedIndex) {
	// // my positions
	// float[] right = getRowPositions();
	//
	// // the left positions
	// float[] left = ((TableBodyUI) getParent()).getPreviousRowPositions(this);
	// Iterator<IRow> leftOrder = ((TableBodyUI) getParent()).getPreviousOrder(this);
	//
	// ConnectionBandRenderer renderer = new ConnectionBandRenderer();
	// renderer.init(g.gl);
	// // align simple all the same x
	// g.save();
	// g.gl.glTranslatef(0, 0, g.z());
	// float y = 0;
	// for (float hr : left) {
	// if (!leftOrder.hasNext())
	// break;
	// int index = leftOrder.next().getIndex();
	// if (indexToRank.containsKey(index)) {
	// int rightRank = indexToRank.get(index);
	// if (rightRank < right.length) {
	// renderBand(g, renderer, y, hr, rightRank == 0 ? 0 : right[rightRank - 1], right[rightRank],
	// RenderStyle.FROZEN_BAND_WIDTH - 3, selectedIndex == index);
	// }
	// }
	// y = hr;
	// }
	// g.restore();
	// }
	//
	// private void renderBand(GLGraphics g, ConnectionBandRenderer renderer, float yl1, float yl2, float yr1, float
	// yr2,
	// float w, boolean isSelected) {
	// float deltal = Math.min(3, (yl2 - yl1 - 1) * .5f);
	// float deltar = Math.min(3, (yr2 - yr1 - 1) * .5f);
	// float[] leftTopPos = new float[] { 0, yl1 + deltal };
	// float[] leftBottomPos = new float[] { 0, yl2 - deltal };
	// float[] rightTopPos = new float[] { w, yr1 + deltar };
	// float[] rightBottomPos = new float[] { w, yr2 - deltar };
	// float[] col = (isSelected ? RenderStyle.COLOR_SELECTED_ROW : RenderStyle.COLOR_BAND).getRGBComponents(null);
	// if (isSelected)
	// g.gl.glTranslatef(0, 0, 0.1f);
	// renderer.renderSingleBand(g.gl, leftTopPos, leftBottomPos, rightTopPos, rightBottomPos, false, 20, -1, col,
	// false);
	// if (isSelected)
	// g.gl.glTranslatef(0, 0, -0.1f);
	// }

}
