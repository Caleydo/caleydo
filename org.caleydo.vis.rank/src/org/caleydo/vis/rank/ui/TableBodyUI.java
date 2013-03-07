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
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.ACustomAnimation;
import org.caleydo.core.view.opengl.layout2.animation.Durations;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.vis.rank.layout.RowHeightLayouts.IRowHeightLayout;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ColumnRanker;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.ui.column.ACompositeTableColumnUI;
import org.caleydo.vis.rank.ui.column.ColumnUIs;
import org.caleydo.vis.rank.ui.column.IColumModelLayout;
import org.caleydo.vis.rank.ui.column.ITableColumnUI;
import org.caleydo.vis.rank.ui.column.OrderColumnUI;

import com.google.common.collect.Iterables;

/**
 * a visualization of the body of the {@link RankTableModel}, in HTML it would be the tbody
 *
 * @author Samuel Gratzl
 *
 */
public final class TableBodyUI extends AnimatedGLElementContainer implements IGLLayout, IColumModelLayout {
	private final static int FIRST_COLUMN = 1;
	private final RankTableModel table;

	private IRowHeightLayout rowLayout;
	private boolean isSelectedRowChanged = false;

	private final PropertyChangeListener listener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case RankTableModel.PROP_DATA:
				updateData();
				break;
			case RankTableModel.PROP_COLUMNS:
				onColumsChanged((IndexedPropertyChangeEvent) evt);
				break;
			case ARankColumnModel.PROP_WEIGHT:
			case ICollapseableColumnMixin.PROP_COLLAPSED:
				onRankerChanged((ARankColumnModel) evt.getSource());
				break;
			case RankTableModel.PROP_SELECTED_ROW:
				isSelectedRowChanged = true;
				update();
				break;
			}
		}
	};
	/**
	 * ids for row selection
	 */
	private int[] pickingIDs = new int[0];
	private PickingListenerComposite selectRowListener = new PickingListenerComposite();

	public TableBodyUI(final RankTableModel table, IRowHeightLayout rowLayout) {
		setAnimateByDefault(false);
		this.table = table;
		this.rowLayout = rowLayout;
		this.add(new OrderColumnUI(null, table.getMyRanker(null)));
		this.table.addPropertyChangeListener(RankTableModel.PROP_DATA, listener);
		this.table.addPropertyChangeListener(RankTableModel.PROP_COLUMNS, listener);
		this.table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, listener);
		for (ARankColumnModel col : table.getColumns()) {
			this.add(wrap(col));
		}
		selectRowListener.add(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED) {
					setSelectedRank(pick.getPickedPoint().x, pick.getObjectID());
				}
			}
		});
		setLayout(this);
	}

	private OrderColumnUI getDefaultrankerUI() {
		return (OrderColumnUI) get(0);
	}

	protected void setSelectedRank(int x, int objectID) {
		IRow row = getRanker(x).getRanker().get(objectID);
		table.setSelectedRow(row);
	}

	private OrderColumnUI getRanker(int mouseX) {
		OrderColumnUI r = getDefaultrankerUI();
		for (OrderColumnUI other : Iterables.filter(this, OrderColumnUI.class)) {
			Vec2f loc = other.getLocation();
			if (loc.x() > mouseX) // last one is the correct one
				break;
			r = other;
		}
		return r;
	}

	private OrderColumnUI getRanker(ColumnRanker ranker) {
		if (ranker == getDefaultrankerUI().getRanker())
			return getDefaultrankerUI();
		for (OrderColumnUI r : Iterables.filter(this, OrderColumnUI.class))
			if (r.getRanker() == ranker)
				return r;
		return null;
	}

	/**
	 * @param source
	 */
	protected void onRankerChanged(ARankColumnModel model) {
		if (model instanceof OrderColumn) {
			updateChildrenOf(((OrderColumn) model).getRanker());
		} else
			updateChildrenOf(model.getMyRanker());
	}

	private void updateChildrenOf(ColumnRanker ranker) {
		boolean started = false;
		for (ITableColumnUI r : Iterables.filter(this, ITableColumnUI.class)) {
			if (r instanceof OrderColumnUI) {
				OrderColumnUI cr = (OrderColumnUI) r;
				if (ranker != cr.getRanker())
					continue;
				if (started)
					break;
				started = true;
			}
			if (started)
				r.update();
		}
		relayout();
	}

	@Override
	public boolean causesReorderingLayouting() {
		return !isSelectedRowChanged;
	}

	public void updateMyChildren(OrderColumnUI ranker) {
		updateChildrenOf(ranker.getRanker());
	}

	@Override
	public OrderColumnUI getRanker(ARankColumnModel model) {
		return getRanker(model.getMyRanker());
	}

	/**
	 * @param rowLayout
	 *            setter, see {@link rowLayout}
	 */
	public void setRowLayout(IRowHeightLayout rowLayout) {
		if (this.rowLayout == rowLayout)
			return;
		this.rowLayout = rowLayout;
		update();
	}

	/**
	 * @return the rowLayout, see {@link #rowLayout}
	 */
	public IRowHeightLayout getRowLayout() {
		return rowLayout;
	}

	public void addOnRowPick(IPickingListener l) {
		this.selectRowListener.add(l);
	}

	public void removeOnRowPick(IPickingListener l) {
		this.selectRowListener.remove(l);
	}

	@SuppressWarnings("unchecked")
	protected void onColumsChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) {// moved
			int movedFrom = (Integer) evt.getOldValue();
			add(FIRST_COLUMN + index, get(FIRST_COLUMN + movedFrom));
		} else if (evt.getOldValue() == null) { // new
			Collection<GLElement> news = null;
			if (evt.getNewValue() instanceof ARankColumnModel) {
				news = Collections.singleton(wrap((ARankColumnModel) evt.getNewValue()));
			} else {
				news = new ArrayList<>();
				for (ARankColumnModel c : (Collection<ARankColumnModel>) evt.getNewValue())
					news.add(wrap(c));
			}
			for (GLElement new_ : news)
				add(FIRST_COLUMN + index++, new_);
		} else if (evt.getNewValue() == null) { // removed
			takeDown(get(FIRST_COLUMN + index).getLayoutDataAs(ARankColumnModel.class, null));
			remove(FIRST_COLUMN + index);
		} else { // replaced
			ARankColumnModel old = (ARankColumnModel) evt.getOldValue();
			takeDown(old);
			set(FIRST_COLUMN + index, wrap((ARankColumnModel) evt.getNewValue()));
		}
	}

	private void init(ARankColumnModel col) {
		col.addPropertyChangeListener(ARankColumnModel.PROP_WEIGHT, listener);
		col.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
	}

	private void takeDown(ARankColumnModel col) {
		col.removePropertyChangeListener(ARankColumnModel.PROP_WEIGHT, listener);
		col.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
	}

	private GLElement wrap(ARankColumnModel new_) {
		init(new_);
		if (new_ instanceof OrderColumn) {
			OrderColumn c = (OrderColumn) new_;
			return new OrderColumnUI(c, c.getRanker());
		}
		return ColumnUIs.createBody(new_, true).setData(table.getData(), this);
	}

	protected void updateData() {
		Collection<IRow> data = table.getData();
		for (ITableColumnUI col : Iterables.filter(this, ITableColumnUI.class)) {
			col.setData(data, this);
		}
	}

	protected void update() {
		relayout();
		repaint();
		for (ITableColumnUI g : Iterables.filter(this, ITableColumnUI.class)) {
			g.update();
		}
	}

	@Override
	protected void takeDown() {
		this.table.removePropertyChangeListener(RankTableModel.PROP_DATA, listener);
		this.table.removePropertyChangeListener(RankTableModel.PROP_COLUMNS, listener);
		this.table.removePropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, listener);
		for (GLElement col : this) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			if (model != null) {
				model.removePropertyChangeListener(ARankColumnModel.PROP_WEIGHT, listener);
				model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, listener);
			}
		}
		for (int pickingID : this.pickingIDs)
			context.unregisterPickingListener(pickingID);
		this.pickingIDs = new int[0];
		super.takeDown();
	}

	public int getRankPickingId(int rank) {
		enlargeRankPickers(rank);
		return pickingIDs[rank];
	}

	void enlargeRankPickers(int lastVisibleRank) {
		if (lastVisibleRank > pickingIDs.length) {
			int bak = this.pickingIDs.length;
			this.pickingIDs = Arrays.copyOf(this.pickingIDs, lastVisibleRank);
			for (int i = bak; i < this.pickingIDs.length; ++i)
				this.pickingIDs[i] = context.registerPickingListener(selectRowListener, i);
		}
	}

	/**
	 * layout cols
	 */
	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		// align the columns normally
		float x = RenderStyle.COLUMN_SPACE;
		for (IGLLayoutElement col : children) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			if (model == null) {
				col.setBounds(x, 3, 1, h - 3);
				x += 1;
			} else {
				col.setBounds(x, 3, model.getPreferredWidth(), h - 3);
				x += model.getPreferredWidth() + RenderStyle.COLUMN_SPACE;
			}
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		// push my resource locator to find the icons
		g.pushResourceLocator(ResourceLocators.classLoader(this.getClass().getClassLoader()));

		renderBackgroundLines(g, false);

		super.renderImpl(g, w, h);

		g.popResourceLocator();

		isSelectedRowChanged = false;
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		renderBackgroundLines(g, true);
		super.renderPickImpl(g, w, h);
	}

	private void renderBackgroundLines(GLGraphics g, boolean pick) {
		IRow selected = table.getSelectedRow();

		float x = 0;
		OrderColumnUI act = null;
		ITableColumnUI last = null;

		g.decZ();

		for (ITableColumnUI col : Iterables.filter(this, ITableColumnUI.class)) {
			if (col instanceof OrderColumnUI) {
				if (last != null) {
					renderSubArea(g, x, act, last, selected, pick);
				}
				act = (OrderColumnUI) col;
				x = act.getLocation().x() + act.getSize().x();
			}
			last = col;
		}
		if (last != null && size() > 1) {
			renderSubArea(g, x, act, last, selected, pick);
		}
		g.incZ();
	}

	public ITableColumnUI getLastCorrespondingColumn(OrderColumnUI target, boolean unwrapCombined) {
		ITableColumnUI last = null;
		boolean found = false;
		for (ITableColumnUI col : Iterables.filter(this, ITableColumnUI.class)) {
			if (col instanceof OrderColumnUI && found) {
				if (last != null) {
					if (unwrapCombined && last instanceof ACompositeTableColumnUI<?>)
						return getLast((ACompositeTableColumnUI<?>) last);
					return last;
				}
			}
			found = found || col == target;
			last = col;
		}
		if (unwrapCombined && last instanceof ACompositeTableColumnUI<?>)
			return getLast((ACompositeTableColumnUI<?>) last);
		return last;
	}

	private ITableColumnUI getLast(ACompositeTableColumnUI<?> col) {
		return col.getLastChild();
	}

	private void renderSubArea(GLGraphics g, float x, OrderColumnUI act, ITableColumnUI last, IRow selected,
			boolean pick) {
		Vec4f bounds2 = last.asGLElement().getBounds(); // get w
		float w = bounds2.x() + bounds2.z() - x;
		if (w <= 0)
			return;

		boolean even = true;
		enlargeRankPickers(act.getRanker().size());
		int i = -1;

		for (IRow rankedRow : act.getRanker()) {
			Vec4f bounds = getRowBounds(last, rankedRow.getIndex());
			i++;
			if (bounds.z() <= 0 || bounds.w() <= 0)
				continue;
			if (pick)
				g.pushName(pickingIDs[i]);
			else {
				if (rankedRow == selected)
					g.color(RenderStyle.COLOR_SELECTED_ROW);
				else
					g.color(RenderStyle.COLOR_BACKGROUND_EVEN);
			}
			if (pick || even || rankedRow == selected)
				g.fillRect(x, bounds.y() + 3, w, bounds.w());
			if (pick)
				g.popName();
			even = !even;
		}
	}

	void renderLineHighlight(GLGraphics g, int rowIndex, float alpha, int delta, OrderColumnUI ranker) {
		ITableColumnUI column = getLastCorrespondingColumn(ranker, false);
		if (column == null)
			return;
		Vec4f bounds = getRowBounds(column, rowIndex);
		if (bounds.w() < 0 || bounds.z() < 0)
			return;
		float calpha = RenderStyle.computeHighlightAlpha(alpha, delta);
		Color base = delta < 0 ? Color.GREEN : Color.RED;
		Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (calpha * 255));
		g.decZ();
		g.color(c);
		float x = ranker.getLocation().x();
		float w = column.asGLElement().getLocation().x() + column.asGLElement().getSize().x() - x;
		g.fillRect(x, bounds.y(), w, bounds.w());
		g.incZ();
	}

	/**
	 * @param column
	 * @param rowIndex
	 * @return
	 */
	private Vec4f getRowBounds(ITableColumnUI column, int rowIndex) {
		if (column instanceof ACompositeTableColumnUI<?>) {
			column = getLast((ACompositeTableColumnUI<?>) column);
		}
		return column.get(rowIndex).getBounds();
	}

	public void triggerRankAnimations(OrderColumnUI ranker, int[] rankDeltas) {
		assert rankDeltas != null;
		ITableColumnUI col = getLastCorrespondingColumn(ranker, false);
		if (col == null)
			return;
		for (int i = 0; i < rankDeltas.length; ++i) {
			int delta = rankDeltas[i];
			if (delta == 0 || delta == Integer.MAX_VALUE)
				continue;
			IRow r = ranker.getRanker().get(i);
			if (r == null)
				continue;
			this.animate(new LineHighlightAnimation(delta, r.getIndex(), ranker));
		}
	}

	@Override
	public void layoutRows(ARankColumnModel model, List<? extends IGLLayoutElement> children, float w, float h) {
		Iterator<IRow> ranks = model.getMyRanker().iterator();
		// align simple all the same x
		BitSet used = new BitSet(children.size());
		used.set(0, children.size());
		float y = 0;
		for (float hr : getRanker(model).getRowPositions()) {
			if (!ranks.hasNext())
				break;
			int r = ranks.next().getIndex();
			IGLLayoutElement row = children.get(r);
			used.clear(r);
			row.setBounds(RenderStyle.COLUMN_SPACE, y, w, hr - y);
			y = hr;
		}
		hideUnusedColumns(children, w, h, used);
	}

	public static void hideUnusedColumns(List<? extends IGLLayoutElement> children, float w, float h, BitSet used) {
		for (int unused = used.nextSetBit(0); unused >= 0; unused = used.nextSetBit(unused + 1)) {
			children.get(unused).setBounds(RenderStyle.COLUMN_SPACE, h, w, 0);
		}
	}

	@Override
	public VAlign getAlignment(ITableColumnUI tableColumnUI) {
		return VAlign.LEFT;
	}

	@Override
	public boolean hasFreeSpace(ITableColumnUI tableColumnUI) {
		return true;
	}

	class LineHighlightAnimation extends ACustomAnimation {
		private final int rowIndex;
		private final OrderColumnUI ranker;
		private final int delta;

		public LineHighlightAnimation(int delta, int rowIndex, OrderColumnUI ranker) {
			super(0, Durations.fix(RenderStyle.hightlightAnimationDuration(delta)));
			this.delta = delta;
			this.ranker = ranker;
			this.rowIndex = rowIndex;
		}

		@Override
		protected void firstTime(GLGraphics g, float w, float h) {
			animate(g, 0, w, h);
		}

		@Override
		protected void animate(GLGraphics g, float alpha, float w, float h) {
			renderLineHighlight(g, rowIndex, alpha, delta, ranker);
		}

		@Override
		protected void lastTime(GLGraphics g, float w, float h) {
			animate(g, 1, w, h);
		}
	}
}
