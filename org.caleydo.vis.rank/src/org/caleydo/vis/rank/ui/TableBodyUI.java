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

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.IScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.internal.event.SetValueEvent;
import org.caleydo.vis.rank.internal.event.TriggerEditValuesEvent;
import org.caleydo.vis.rank.internal.ui.EditValuesDialog;
import org.caleydo.vis.rank.layout.IRowHeightLayout;
import org.caleydo.vis.rank.layout.IRowLayoutInstance.IRowSetter;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ColumnRanker;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.rank.model.mixin.IGrabRemainingHorizontalSpace;
import org.caleydo.vis.rank.model.mixin.ISetableColumnMixin;
import org.caleydo.vis.rank.ui.column.ACompositeTableColumnUI;
import org.caleydo.vis.rank.ui.column.ColumnUIs;
import org.caleydo.vis.rank.ui.column.IColumModelLayout;
import org.caleydo.vis.rank.ui.column.ITableColumnUI;
import org.caleydo.vis.rank.ui.column.OrderColumnUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.Iterables;

/**
 * a visualization of the body of the {@link RankTableModel}, in HTML it would be the tbody
 *
 * @author Samuel Gratzl
 *
 */
public final class TableBodyUI extends AnimatedGLElementContainer implements IGLLayout, IColumModelLayout,
		IScrollBar.IScrollBarCallback {
	private final static int FIRST_COLUMN = 1;
	private final RankTableModel table;

	private boolean isSelectedRowChanged = false;

	private final PropertyChangeListener listener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case RankTableModel.PROP_COLUMNS:
				onColumsChanged((IndexedPropertyChangeEvent) evt);
				break;
			case ARankColumnModel.PROP_WIDTH:
			case ICollapseableColumnMixin.PROP_COLLAPSED:
				onRankerChanged((ARankColumnModel) evt.getSource());
				updateMyMinSize();
				break;
			case ICompressColumnMixin.PROP_COMPRESSED:
				updateMyMinSize();
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
	private final PickingListenerComposite selectRowListener = new PickingListenerComposite();
	private final IRankTableUIConfig config;

	/**
	 * overall scrollbar
	 */
	private final IScrollBar scrollBar;
	private int scrollBarPickingId = -1;
	private boolean needsScrollBar;

	private final IPickingListener selectedRowListener = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onSelectedRowPick(pick);
		}
	};

	public TableBodyUI(final RankTableModel table, IRowHeightLayout rowLayout, final IRankTableUIConfig config) {
		setAnimateByDefault(false);
		this.table = table;
		this.config = config;
		this.add(new OrderColumnUI(null, table.getMyRanker(null), rowLayout, config));
		this.table.addPropertyChangeListener(RankTableModel.PROP_COLUMNS, listener);
		this.table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, listener);
		for (ARankColumnModel col : table.getColumns()) {
			this.add(wrap(col));
		}

		updateMyMinSize();
		selectRowListener.add(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				config.onRowClick(table, pick.getPickingMode(), toRow(pick), false);
			}

			protected IRow toRow(Pick pick) {
				OrderColumnUI col = getRankerUI(pick);
				return col.getRanker().get(pick.getObjectID());
			}

			protected OrderColumnUI getRankerUI(Pick pick) {
				return getRanker(toRelative(pick.getPickedPoint()).x());
			}
		});
		setLayout(this);
		this.scrollBar = config.createScrollBar(false);
		this.scrollBar.setCallback(this);
		this.scrollBar.setWidth(RenderStyle.SCROLLBAR_WIDTH);
	}

	protected void editRow(final OrderColumnUI col, final IRow row) {
		final Iterator<ARankColumnModel> columns = table.getColumnsOf(col.getRanker());
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				new EditValuesDialog(new Shell(), row, columns, TableBodyUI.this).open();
			}
		});
	}

	/**
	 * returns the listener that handles the selected row
	 *
	 * @return the selectedRowListener, see {@link #selectedRowListener}
	 */
	public IPickingListener getSelectedRowListener() {
		return selectedRowListener;
	}

	/**
	 * @param pick
	 */
	protected void onSelectedRowPick(Pick pick) {
		switch (pick.getPickingMode()) {
		case DOUBLE_CLICKED:
			if (config.canEditValues()) {
				OrderColumnUI col = getRanker(toRelative(pick.getPickedPoint()).x());
				editRow(col, table.getSelectedRow());
			}
			break;
		case RIGHT_CLICKED:
			List<AContextMenuItem> items = new ArrayList<>(1);
			if (config.canEditValues()) {
				OrderColumnUI col2 = getRanker(toRelative(pick.getPickedPoint()).x());
				items.add(new GenericContextMenuItem("Edit Values", new TriggerEditValuesEvent(col2).to(this)));
			}
			context.getSWTLayer().showContextMenu(items);
			break;
		default:
			break;
		}
		config.onRowClick(table, pick.getPickingMode(), table.getSelectedRow(), true);
	}

	@ListenTo(sendToMe = true)
	private void onOpenEditDialog(TriggerEditValuesEvent event) {
		editRow(event.getRanker(), table.getSelectedRow());
	}

	@ListenTo(sendToMe = true)
	private void onEditValueEvent(SetValueEvent event) {
		ISetableColumnMixin col = event.getCol();
		String v = event.getValue();
		col.set(event.getRow(), v);
		repaintAll();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		scrollBarPickingId = context.registerPickingListener(scrollBar);
	}

	private OrderColumnUI getDefaultrankerUI() {
		return (OrderColumnUI) get(0);
	}

	private OrderColumnUI getRanker(float f) {
		OrderColumnUI r = getDefaultrankerUI();
		for (OrderColumnUI other : Iterables.filter(this, OrderColumnUI.class)) {
			Vec2f loc = other.getLocation();
			if (loc.x() > f) // last one is the correct one
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

	private OrderColumnUI getLastRanker() {
		OrderColumnUI last = null;
		for (OrderColumnUI r : Iterables.filter(this, OrderColumnUI.class))
			last = r;
		return last;
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
				if (started) {
					((OrderColumnUI) r).repaintAll(); // repaint the next one
					break;
				}
				if (ranker != cr.getRanker())
					continue;
				started = true;
			}
			if (started)
				r.relayout();
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
		for (OrderColumnUI g : Iterables.filter(this, OrderColumnUI.class)) {
			g.setRowLayout(rowLayout);
		}
		update();
	}

	/**
	 * @return the rowLayout, see {@link #rowLayout}
	 */
	public IRowHeightLayout getRowLayout() {
		return getDefaultrankerUI().getRowLayout();
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

		updateMyMinSize();
	}

	private void updateMyMinSize() {
		float x = computeNeededWidth();
		Vec2f old = getLayoutDataAs(Vec2f.class, new Vec2f(0, 0));
		if (old.x() == x)
			return;
		old.setX(x);
		setLayoutData(old);
		relayoutParent();
	}

	protected float computeNeededWidth() {
		float x = RenderStyle.COLUMN_SPACE;
		for (GLElement col : this) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			if (model == null)
				x += 1;
			else
				x += model.getWidth() + RenderStyle.COLUMN_SPACE;
		}
		return x + RenderStyle.SCROLLBAR_WIDTH;
	}

	private void init(ARankColumnModel col) {
		col.addPropertyChangeListener(ARankColumnModel.PROP_WIDTH, listener);
		col.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		col.addPropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
	}

	private void takeDown(ARankColumnModel col) {
		col.removePropertyChangeListener(ARankColumnModel.PROP_WIDTH, listener);
		col.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		col.removePropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
	}

	private GLElement wrap(ARankColumnModel new_) {
		init(new_);
		if (new_ instanceof OrderColumn) {
			OrderColumn c = (OrderColumn) new_;
			return new OrderColumnUI(c, c.getRanker(), getDefaultrankerUI().getRowLayout(), config);
		}
		return ColumnUIs.createBody(new_, true).asGLElement();
	}

	protected void update() {
		relayout();
		repaint();
		for (ITableColumnUI g : Iterables.filter(this, ITableColumnUI.class)) {
			g.relayout();
		}
	}

	@Override
	protected void takeDown() {
		this.table.removePropertyChangeListener(RankTableModel.PROP_COLUMNS, listener);
		this.table.removePropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, listener);
		for (GLElement col : this) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			if (model != null) {
				model.removePropertyChangeListener(ARankColumnModel.PROP_WIDTH, listener);
				model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, listener);
			}
		}
		for (int pickingID : this.pickingIDs)
			context.unregisterPickingListener(pickingID);
		this.pickingIDs = new int[0];

		context.unregisterPickingListener(scrollBarPickingId);
		scrollBarPickingId = -1;

		super.takeDown();
	}

	public int getRankPickingId(int rank) {
		enlargeRankPickers(rank);
		return pickingIDs[rank];
	}

	void enlargeRankPickers(int lastVisibleRank) {
		if (lastVisibleRank >= pickingIDs.length) {
			int bak = this.pickingIDs.length;
			this.pickingIDs = Arrays.copyOf(this.pickingIDs, lastVisibleRank+10);
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

		Iterator<? extends IGLLayoutElement> it = children.iterator();
		while (it.hasNext()) {
			IGLLayoutElement col = it.next();
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			if (model == null) {
				col.setBounds(x, 0, 1, h);
				x += 0;
			} else {
				float wi = model.getWidth();
				if (!it.hasNext() && model instanceof IGrabRemainingHorizontalSpace && (!model.isCollapsed())) {
					// catch all
					wi = w - x - RenderStyle.SCROLLBAR_WIDTH;
					col.setBounds(x, 0, wi, h);
					break;
				}
				col.setBounds(x, 0, wi, h);
				x += wi + RenderStyle.COLUMN_SPACE;
			}
		}
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);
		float offset = 0;
		float window = 0;
		float size = 0;
		boolean needed = false;
		for (OrderColumnUI child : Iterables.filter(this, OrderColumnUI.class)) {
			child.layoutingDone();
			IScrollBar sb = child.getScrollBar();
			offset = sb.getOffset();
			window = sb.getWindow();
			size = sb.getSize();
			needed = child.needsScrollBar();
		}
		this.needsScrollBar = needed;
		scrollBar.setBounds(offset, window, size);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		renderBackgroundLines(g, w, false);

		super.renderImpl(g, w, h);

		if (needsScrollBar) {
			g.incZ(1);
			Rect clippingArea = findClippingArea(w, h);
			float offset = clippingArea.x() + clippingArea.width() - RenderStyle.SCROLLBAR_WIDTH + 1;
			g.move(offset, 0);
			scrollBar.render(g, RenderStyle.SCROLLBAR_WIDTH, h, this);
			g.move(-offset, 0);
			g.incZ(-1);
		}

		isSelectedRowChanged = false;
	}

	private Rect findClippingArea(float w, float h) {
		IGLElementParent act = getParent();
		while (act != null && !(act instanceof ScrollingDecorator)) {
			act = act.getParent();
		}
		if (act == null)
			return new Rect(0, 0, w, h);
		return ((ScrollingDecorator) act).getClipingArea();
	}

	@Override
	public void onScrollBarMoved(IScrollBar scrollBar, float offset) {
		float delta = offset - getLastRanker().getScrollBar().getOffset();
		for (OrderColumnUI col : Iterables.filter(this, OrderColumnUI.class)) {
			float o1 = col.getScrollBar().getOffset() + delta;
			col.onScrollBarMoved(scrollBar, o1);
		}
	}

	/**
	 * manually scrolling
	 *
	 * @param rowDelta
	 */
	public void scroll(int rowDelta) {
		onScrollBarMoved(scrollBar, scrollBar.getOffset() + rowDelta);
	}

	public void scrollFirst() {
		onScrollBarMoved(scrollBar, 0);
	}

	public void scrollLast() {
		onScrollBarMoved(scrollBar, scrollBar.getSize() - scrollBar.getWindow());
	}

	@Override
	public float getHeight(IScrollBar scrollBar) {
		return getSize().y();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		renderBackgroundLines(g, w, true);

		super.renderPickImpl(g, w, h);

		if (needsScrollBar) {
			g.incZ(1);
			g.pushName(scrollBarPickingId);
			Rect clippingArea = findClippingArea(w, h);
			float offset = clippingArea.x() + clippingArea.width() - RenderStyle.SCROLLBAR_WIDTH + 1;
			g.move(offset, 0);
			scrollBar.renderPick(g, RenderStyle.SCROLLBAR_WIDTH, h, this);
			g.move(-offset, 0);
			g.popName();
			g.incZ(-1);
		}
	}

	private void renderBackgroundLines(GLGraphics g, float w, boolean pick) {
		IRow selected = table.getSelectedRow();

		float x = 0;
		OrderColumnUI act = null;
		ITableColumnUI last = null;

		g.decZ();

		for (ITableColumnUI col : Iterables.filter(this, ITableColumnUI.class)) {
			if (!GLElement.areValidBounds(col.asGLElement().getBounds()))
				break;
			if (col instanceof OrderColumnUI) {
				if (last != null) {
					Vec4f bounds2 = last.asGLElement().getBounds(); // get w
					float wi = bounds2.x() + bounds2.z() - x;
					renderSubArea(g, x, act, last, selected, pick, wi);
				}
				act = (OrderColumnUI) col;
				x = act.getLocation().x() + act.getSize().x();
			}
			last = col;
		}
		if (last != null && !(last instanceof OrderColumnUI) && size() > 1) {
			renderSubArea(g, x, act, last, selected, pick, w - x - RenderStyle.SCROLLBAR_WIDTH);
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
						last = ((ACompositeTableColumnUI<?>) last).getLastChild();
					return last;
				}
			}
			found = found || col == target;
			last = col;
		}
		if (unwrapCombined && last instanceof ACompositeTableColumnUI<?>)
			last = ((ACompositeTableColumnUI<?>) last).getLastChild();
		if (last == target)
			return null;
		return last;
	}

	private void renderSubArea(GLGraphics g, float x, OrderColumnUI act, ITableColumnUI last, IRow selected,
			boolean pick, float w) {
		if (w <= 0)
			return;

		boolean even = false;
		int i = -1;

		if (last instanceof ACompositeTableColumnUI<?>) {
			last = ((ACompositeTableColumnUI<?>) last).getLastChild();
		}
		if (last == null)
			return;

		int[] order = act.getRanker().getOrder();
		enlargeRankPickers(order.length);

		for (int index : order) {
			Vec4f bounds = last.getBounds(index);
			i++;
			even = !even;
			if (!GLElement.areValidBounds(bounds))
				continue;
			IRow rankedRow = table.getDataItem(index);
			if (pick) {
				if (selected != rankedRow) { // the selected row is not pickable again
					g.pushName(pickingIDs[i]);
					g.color(Color.LIGHT_GRAY).fillRect(x, bounds.y(), w, bounds.w());
					g.popName();
				}
			}
			config.renderRowBackground(g, x, bounds.y(), w, bounds.w(), even, rankedRow, selected);
		}
	}

	@Override
	public void layoutRows(ARankColumnModel model, final IRowSetter setter, final float w, float h) {
		// IRow selected = table.getSelectedRow();
		getRanker(model).layoutRows(setter, 0, w);
	}

	@Override
	public int getNumVisibleRows(ARankColumnModel model) {
		return getRanker(model).getNumVisibleRows();
	}

	@Override
	public VAlign getAlignment(ITableColumnUI tableColumnUI) {
		return VAlign.LEFT;
	}

	@Override
	public boolean hasFreeSpace(ITableColumnUI tableColumnUI) {
		return true;
	}

	@Override
	public Color getBarOutlineColor() {
		return config.getBarOutlineColor();
	}
}
