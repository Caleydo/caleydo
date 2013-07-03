/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.column;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import org.caleydo.core.util.color.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

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
import org.caleydo.vis.rank.model.RankTableModel;
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
	private boolean becauseOfSelectedRow;

	public OrderColumnUI(OrderColumn model, ColumnRanker ranker, IRowHeightLayout rowLayout, IRankTableUIConfig config) {
		this.ranker = ranker;
		this.model = model;
		this.rowLayout = rowLayout;
		this.scrollBar = config.createScrollBar(false);
		this.scrollBar.setCallback(this);
		this.scrollBar.setWidth(RenderStyle.SCROLLBAR_WIDTH);
		ranker.addPropertyChangeListener(ColumnRanker.PROP_ORDER, this);
		ranker.addPropertyChangeListener(ColumnRanker.PROP_INVALID, this);
		ranker.getTable().addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, this);
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
			break;
		case RankTableModel.PROP_SELECTED_ROW:
			becauseOfSelectedRow = true;
			break;
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
		ranker.getTable().addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, this);
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
		if (!becauseOfSelectedRow)
			rowLayoutInstance = null;
		becauseOfSelectedRow = false;
		rowLayoutInstance = rowLayout.layout(ranker, getSize().y(), ranker.getTable().getDataSize(), scrollOffset,
				isScrollingUpdate, rowLayoutInstance);
		isScrollingUpdate = false;
		scrollOffset = rowLayoutInstance.getOffset();
		scrollBar.setBounds(rowLayoutInstance.getOffset(), rowLayoutInstance.getNumVisibles(),
				rowLayoutInstance.getSize());
		super.layoutImpl();
	}

	/**
	 * @return the scrollBar, see {@link #scrollBar}
	 */
	public IScrollBar getScrollBar() {
		return scrollBar;
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
		if (rowLayoutInstance == null)
			return;
		newOffset = Math.max(0, Math.min(rowLayoutInstance.getSize() - rowLayoutInstance.getNumVisibles(), newOffset));
		if (scrollOffset == newOffset)
			return;
		this.scrollOffset = newOffset;
		isScrollingUpdate = true;
		updateMeAndMyChildren();
	}

	public void layoutRows(IRowSetter setter, float x, float w) {
		rowLayoutInstance.layout(setter, x, w);
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
		boolean hasLeftScrollBar = previousRanker.needsScrollBar();
		float x1 = -1 + (hasLeftScrollBar ? RenderStyle.SCROLLBAR_WIDTH : 0);
		boolean hasRightScrollBar = this.needsScrollBar();
		float x2 = w + 1 - (hasRightScrollBar ? RenderStyle.SCROLLBAR_WIDTH : 0);

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
				boolean isLeftValid = areValidBounds(left);
				boolean isRightValid = areValidBounds(right);
				if (!isLeftValid && !isRightValid)
					continue;
				int pRank = previousRanker.getRanker().getRank(row);
				int delta = 0;
				if (pRank < 0)
					delta = Integer.MAX_VALUE;
				else
					delta = i - pRank;
				// if (isLeftValid && !isRightValid) {
				// } else if (left.y() <= 0) {
				// delta = (int) pScroll.getOffset() - pRank;
				// } else {
				// delta = pRank - (int) (pScroll.getOffset() - pScroll.getWindow());
				// }
				// } else if (!isLeftValid && isRightValid) {
				// } else if (left.y() <= 0) {
				// delta = (int) aScroll.getOffset() - i;
				// } else {
				// delta = i - (int) (aScroll.getOffset() - aScroll.getWindow());
				// }
				// }
				renderBand(g, left, right, x1, x2, selectedRank == i, delta);
			}
		}

		previousRanker.renderScrollBar(g, w, h, true); // render left
		this.renderScrollBar(g, w, h, false); // render right

		super.renderImpl(g, w, h);
	}

	@Override
	protected boolean hasPickAbles() {
		return true;
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
		float x1 = -1 + (hasLeftScrollBar ? RenderStyle.SCROLLBAR_WIDTH : 0);
		boolean hasRightScrollBar = this.needsScrollBar();
		float x2 = w + 1 - (hasRightScrollBar ? RenderStyle.SCROLLBAR_WIDTH : 0);

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
			renderBand(g, left, right, x1, x2, false, 0);
			g.popName();
		}
		g.move(hasLeftScrollBar ? -RenderStyle.SCROLLBAR_WIDTH : 0, 0);
	}


	private boolean dontneedToRenderBands(float w) {
		return model == null || model.isCollapsed() || w < 10;
	}

	private void renderBand(GLGraphics g, Vec4f left, Vec4f right, float x1, float x2, boolean isSelected, int delta) {
		boolean isLeftValid = areValidBounds(left);
		boolean isRightValid = areValidBounds(right);
		if (isSelected) {
			g.incZ();
			// g.color(deltaToColor(delta, RenderStyle.COLOR_SELECTED_ROW));
			g.color(RenderStyle.COLOR_SELECTED_ROW);
			if (isLeftValid && isRightValid) {
				g.fillPolygon(new Vec2f(x1, left.y()), new Vec2f(x2, right.y()), new Vec2f(x2, right.y() + right.w()),
						new Vec2f(x1, left.y() + left.w()));
				// g.color(deltaToColor(delta, RenderStyle.COLOR_SELECTED_BORDER));
				g.color(RenderStyle.COLOR_SELECTED_BORDER);
				g.drawLine(x1, left.y(), x2, right.y());
				g.drawLine(x1, left.y() + left.w(), x2, right.y() + right.w());
			} else if (isLeftValid && delta != Integer.MAX_VALUE) {
				g.fillPolygon(new Vec2f(x1, left.y()), new Vec2f(x2, right.y()), new Vec2f(x1, left.y() + left.w()));
				// g.color(deltaToColor(delta, RenderStyle.COLOR_SELECTED_BORDER));
				g.color(RenderStyle.COLOR_SELECTED_BORDER);
				g.drawLine(x1, left.y(), x2, right.y());
				g.drawLine(x1, left.y() + left.w(), x2, right.y());
			} else if (isRightValid && delta != Integer.MAX_VALUE) {
				g.fillPolygon(new Vec2f(x1, left.y()), new Vec2f(x2, right.y()), new Vec2f(x2, right.y() + right.w()));
				// g.color(deltaToColor(delta, RenderStyle.COLOR_SELECTED_BORDER));
				g.color(RenderStyle.COLOR_SELECTED_BORDER);
				g.drawLine(x1, left.y(), x2, right.y());
				g.drawLine(x1, left.y(), x2, right.y() + right.w());
			}
			g.decZ();
		} else if (delta != Integer.MAX_VALUE) {
			if (isLeftValid && isRightValid) {
				// g.color(deltaToColor(delta, Color.GRAY));
				g.color(Color.GRAY);
				g.drawLine(x1, left.y() + left.w() * 0.5f, x2, right.y() + right.w() * 0.5f);
			} else if (isLeftValid) {
				float v = getAlphaFromDelta(delta);
				// Color c = deltaToColor(delta, Color.BLACK);
				// g.color(c.getRed(), c.getGreen(), c.getBlue(), 1 - v);
				g.color(v, v, v, 1);
				arrow(g, x1, left.y(), left.w(), right.y() <= 0);
			} else if (isRightValid) {
				float v = getAlphaFromDelta(delta);
				// Color c = deltaToColor(delta, Color.BLACK);
				// g.color(c.getRed(), c.getGreen(), c.getBlue(), 1 - v);
				g.color(v, v, v, 1);
				arrow(g, x2 - 7, right.y(), right.w(), left.y() <= 0);
			}
		}
	}

	protected float getAlphaFromDelta(int delta) {
		return Math.max(1 - Math.min(Math.abs(delta) / 50.f, .8f), 0.2f);
	}

	private static void arrow(GLGraphics g, float x, float y, float h, boolean up) {
		if (h < 6)
			return;
		if (h > 10) {
			y += (h - 10) * 0.5f;
			h = 10;
		}
		GL2 gl = g.gl;
		gl.glBegin(GL.GL_LINES);
		if (h > 6) {
			gl.glVertex3f(x + 3, y + 1, g.z());
			gl.glVertex3f(x + 3, y + h - 1, g.z());
		}
		if (up) {
			gl.glVertex3f(x + 1, y + 4, g.z());
			gl.glVertex3f(x + 3, y + 1, g.z());
			gl.glVertex3f(x + 5, y + 4, g.z());
			gl.glVertex3f(x + 3, y + 1, g.z());
		} else {
			gl.glVertex3f(x + 1, y + h - 4, g.z());
			gl.glVertex3f(x + 3, y + h, g.z());
			gl.glVertex3f(x + 5, y + h - 4, g.z());
			gl.glVertex3f(x + 3, y + h, g.z());
		}
		gl.glEnd();
		// g.drawLine(-1, left.y() + left.w() * 0.5f, w, right.y() + right.w() * 0.5f);
	}

	@Override
	public float getHeight(IScrollBar scrollBar) {
		return getSize().y();
	}
}
