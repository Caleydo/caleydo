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
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.IScrollBar;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.layout.IRowHeightLayout;
import org.caleydo.vis.rank.layout.IRowLayoutInstance;
import org.caleydo.vis.rank.layout.IRowLayoutInstance.IRowSetter;
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
public class OrderColumnUI extends GLElement implements PropertyChangeListener, ITableColumnUI,
		IScrollBar.IScrollBarCallback {
	private final ColumnRanker ranker;
	private final OrderColumn model;
	private int scrollBarPickingId = -1;
	private IRowHeightLayout rowLayout;
	private IRowLayoutInstance rowLayoutInstance;

	private int[] rankDeltas;
	private final IScrollBar scrollBar;
	private int scrollOffset;
	private boolean isScrollingUpdate;
	private boolean becauseOfReordering;

	public OrderColumnUI(OrderColumn model, ColumnRanker ranker, IRowHeightLayout rowLayout, IRankTableUIConfig config) {
		this.ranker = ranker;
		this.model = model;
		this.rowLayout = rowLayout;
		this.scrollBar = config.createScrollBar(false);
		this.scrollBar.setCallback(this);
		ranker.addPropertyChangeListener(ColumnRanker.PROP_ORDER, this);
		ranker.addPropertyChangeListener(ColumnRanker.PROP_INVALID, this);
		setLayoutData(model);
	}

	private TableBodyUI getTableBodyUI() {
		return (TableBodyUI) getParent();
	}

	/**
	 * @return the rowLayout, see {@link #rowLayout}
	 */
	public IRowHeightLayout getRowLayout() {
		return rowLayout;
	}

	/**
	 * @param rowLayout
	 *            setter, see {@link rowLayout}
	 */
	public void setRowLayout(IRowHeightLayout rowLayout) {
		this.rowLayout = rowLayout;
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
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case ColumnRanker.PROP_ORDER:
			rankDeltas = (int[]) evt.getOldValue();
			becauseOfReordering = true;
			updateMeAndMyChildren();
			break;
		case ColumnRanker.PROP_INVALID:
			becauseOfReordering = true;
			updateMeAndMyChildren();
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		scrollBarPickingId = context.registerPickingListener(scrollBar);
		super.init(context);
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(scrollBarPickingId);
		ranker.removePropertyChangeListener(ColumnRanker.PROP_ORDER, this);
		ranker.removePropertyChangeListener(ColumnRanker.PROP_INVALID, this);
		super.takeDown();
	}

	public boolean haveRankDeltas() {
		return becauseOfReordering && rankDeltas != null;
	}

	public boolean isInternalReLayout() {
		if (becauseOfReordering)
			return false;
		return isScrollingUpdate;
	}

	public void layoutingDone() {
		becauseOfReordering = false;
		isScrollingUpdate = false;
		rankDeltas = null; // a single run with the rank deltas, not used anymore
	}

	@Override
	public Vec4f getBounds(int rowIndex) {
		return null;
	}

	@Override
	protected void layoutImpl() {
		rowLayoutInstance = rowLayout.layout(ranker, getSize().y(), ranker.getTable().getDataSize(), scrollOffset,
				isScrollingUpdate);
		isScrollingUpdate = false;
		scrollOffset = rowLayoutInstance.getOffset();
		scrollBar.setBounds(rowLayoutInstance.getOffset(), rowLayoutInstance.getNumVisibles(),
				rowLayoutInstance.getSize());
		super.layoutImpl();
	}

	private void updateMeAndMyChildren() {
		getTableBodyUI().updateMyChildren(this);
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

	public boolean needsScrollBar() {
		return rowLayoutInstance.needsScrollBar();
	}

	public boolean renderScrollBar(GLGraphics g, float w, float h, boolean left) {
		if (!rowLayoutInstance.needsScrollBar())
			return false;
		g.incZ().incZ();
		g.move((left ? -1 : w - RenderStyle.SCROLLBAR_WIDTH + 1), 0);
		scrollBar.render(g, RenderStyle.SCROLLBAR_WIDTH, h, this);
		g.move(-(left ? -1 : w - RenderStyle.SCROLLBAR_WIDTH + 1), 0);
		g.decZ().decZ();
		return true;
	}

	public void renderPickScrollBar(GLGraphics g, float w, float h, boolean left) {
		if (!rowLayoutInstance.needsScrollBar())
			return;
		g.incZ().incZ();
		g.pushName(scrollBarPickingId);
		g.move((left ? -1 : w - RenderStyle.SCROLLBAR_WIDTH + 1), 0);
		scrollBar.renderPick(g, RenderStyle.SCROLLBAR_WIDTH, h, this);
		g.move(-(left ? -1 : w - RenderStyle.SCROLLBAR_WIDTH + 1), 0);
		g.popName();
		g.decZ().decZ();
	}

	@Override
	public void onScrollBarMoved(IScrollBar scrollBar, float offset) {
		setScrollOffset(Math.round(offset));
	}

	/**
	 * @param newOffset
	 */
	private void setScrollOffset(int newOffset) {
		newOffset = Math.max(0, Math.min(rowLayoutInstance.getSize() - rowLayoutInstance.getNumVisibles(), newOffset));
		if (scrollOffset == newOffset)
			return;
		this.scrollOffset = newOffset;
		isScrollingUpdate = true;
		updateMeAndMyChildren();
	}

	public void layoutRows(IRowSetter setter, float x, float w, int selectedIndex) {
		rowLayoutInstance.layout(setter, x, w, selectedIndex);
	}

	public int getNumVisibleRows() {
		return rowLayoutInstance.getNumVisibles();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		TableBodyUI body = getTableBodyUI();

		OrderColumnUI previousRanker = model == null ? null : body.getRanker(model);
		if (previousRanker == null)
			return;

		// render the bands
		if (!dontneedToRenderBands(w)) {
			ITableColumnUI previous = body.getLastCorrespondingColumn(previousRanker, true);
			ITableColumnUI self = body.getLastCorrespondingColumn(this, true);
			if (self == null || self == this || previous == null || previous instanceof OrderColumnUI)
				return;
			int selectedRank = model.getRanker().getSelectedRank();

			int i = -1;
			for (IRow row : model.getRanker()) {
				i++;
				Vec4f left = previous.getBounds(row.getIndex());
				Vec4f right = self.getBounds(row.getIndex());
				if (!areValidBounds(left) && !areValidBounds(right))
					continue;
				renderBand(g, left, right, w, selectedRank == i);
			}
		}
		previousRanker.renderScrollBar(g, w, h, true); // render left
		this.renderScrollBar(g, w, h, false); // render right

		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		TableBodyUI body = getTableBodyUI();
		OrderColumnUI previousRanker = model == null ? null : body.getRanker(model);
		if (previousRanker == null)
			return;

		previousRanker.renderPickScrollBar(g, w, h, true); // render left
		this.renderPickScrollBar(g, w, h, false); // render right

		if (dontneedToRenderBands(w))
			return;

		boolean hasLeftScrollBar = previousRanker.needsScrollBar();
		boolean hasRightScrollBar = needsScrollBar();

		ITableColumnUI previous = body.getLastCorrespondingColumn(previousRanker, true);
		ITableColumnUI self = body.getLastCorrespondingColumn(this, true);
		if (self == null || self == this || previous == null || previous instanceof OrderColumnUI)
			return;

		g.move(hasLeftScrollBar ? RenderStyle.SCROLLBAR_WIDTH : 0, 0);
		if (hasLeftScrollBar)
			w -= RenderStyle.SCROLLBAR_WIDTH;
		if (hasRightScrollBar)
			w -= RenderStyle.SCROLLBAR_WIDTH;
		int i = -1;
		for (IRow row : model.getRanker()) {
			i++;
			Vec4f left = previous.getBounds(row.getIndex());
			Vec4f right = self.getBounds(row.getIndex());
			if (!areValidBounds(left) && !areValidBounds(right))
				continue;
			g.pushName(body.getRankPickingId(i));
			renderBand(g, left, right, w, false);
			g.popName();
		}
		g.move(hasLeftScrollBar ? -RenderStyle.SCROLLBAR_WIDTH : 0, 0);
	}


	private boolean dontneedToRenderBands(float w) {
		return model == null || model.isCollapsed() || w < 10;
	}

	private void renderBand(GLGraphics g, Vec4f left, Vec4f right, float w, boolean isSelected) {
		boolean isLeftValid = areValidBounds(left);
		boolean isRightValid = areValidBounds(right);
		if (isSelected) {
			g.incZ();
			if (isLeftValid && isRightValid) {
				g.color(RenderStyle.COLOR_SELECTED_ROW);
				g.fillPolygon(new Vec2f(-1, left.y()), new Vec2f(w, right.y()), new Vec2f(w, right.y() + right.w()),
						new Vec2f(-1, left.y() + left.w()));
				g.color(RenderStyle.COLOR_SELECTED_BORDER);
				g.drawLine(0, left.y(), w, right.y());
				g.drawLine(0, left.y() + left.w(), w, right.y() + right.w());
			} else if (isLeftValid) {
				g.color(RenderStyle.COLOR_SELECTED_ROW);
				g.fillPolygon(new Vec2f(-1, left.y()), new Vec2f(w, right.y()), new Vec2f(-1, left.y() + left.w()));
				g.color(RenderStyle.COLOR_SELECTED_BORDER);
				g.drawLine(0, left.y(), w, right.y());
				g.drawLine(0, left.y() + left.w(), w, right.y());
			} else if (isRightValid) {
				g.color(RenderStyle.COLOR_SELECTED_ROW);
				g.fillPolygon(new Vec2f(-1, left.y()), new Vec2f(w, right.y()), new Vec2f(w, right.y() + right.w()));
				g.color(RenderStyle.COLOR_SELECTED_BORDER);
				g.drawLine(0, left.y(), w, right.y());
				g.drawLine(0, left.y(), w, right.y() + right.w());
			}
			g.decZ();
		} else {
			g.color(Color.GRAY);
			g.drawLine(-1, left.y() + left.w() * 0.5f, w, right.y() + right.w() * 0.5f);
		}
	}

	@Override
	public float getTotal(IScrollBar scrollBar) {
		return getSize().y();
	}
}
