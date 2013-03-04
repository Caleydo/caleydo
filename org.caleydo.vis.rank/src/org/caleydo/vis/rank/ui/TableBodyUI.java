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


import gleem.linalg.Vec4f;

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
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
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
import org.caleydo.vis.rank.model.IRankColumnParent;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.ui.column.ACompositeTableColumnUI;
import org.caleydo.vis.rank.ui.column.IColumModelLayout;
import org.caleydo.vis.rank.ui.column.ITableColumnUI;
import org.caleydo.vis.rank.ui.column.TableColumnUI;
import org.caleydo.vis.rank.ui.column.TableColumnUIs;

/**
 * a visualization of the body of the {@link RankTableModel}, in HTML it would be the tbody
 *
 * @author Samuel Gratzl
 *
 */
public final class TableBodyUI extends GLElementContainer implements IGLLayout,
		IColumModelLayout {
	private final RankTableModel table;
	private final IRowHeightLayout rowLayout;
	private final PropertyChangeListener layoutOnChange = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			update();
		}
	};
	private final PropertyChangeListener updateData = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateData();
		}
	};
	private final PropertyChangeListener columnsChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onColumsChanged((IndexedPropertyChangeEvent) evt);
		}
	};
	/**
	 * ids for row selection
	 */
	private int[] pickingIDs = null;
	private PickingListenerComposite selectRowListener = new PickingListenerComposite();
	private float[] rowPositions;


	public TableBodyUI(final RankTableModel table, IRowHeightLayout rowLayout) {
		this.table = table;
		this.rowLayout = rowLayout;
		this.table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, layoutOnChange);
		this.table.addPropertyChangeListener(RankTableModel.PROP_ORDER, layoutOnChange);
		this.table.addPropertyChangeListener(IRankColumnParent.PROP_INVALID, layoutOnChange);
		this.table.addPropertyChangeListener(IRankColumnParent.PROP_DATA, updateData);
		this.table.addPropertyChangeListener(RankTableModel.PROP_COLUMNS, columnsChanged);
		for (ARankColumnModel col : table.getColumns()) {
			this.add(wrap(col));
		}
		selectRowListener.add(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					table.setSelectedRow(pick.getObjectID());
			}
		});
		setLayout(this);
	}

	public void addOnRowPick(IPickingListener l) {
		this.selectRowListener.add(l);
	}

	public void removeOnRowPick(IPickingListener l) {
		this.selectRowListener.remove(l);
	}

	private void init(ARankColumnModel col) {
		col.addPropertyChangeListener(ARankColumnModel.PROP_WEIGHT, layoutOnChange);
		col.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

	private void takeDown(ARankColumnModel col) {
		col.removePropertyChangeListener(ARankColumnModel.PROP_WEIGHT, layoutOnChange);
		col.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

	@SuppressWarnings("unchecked")
	protected void onColumsChanged(IndexedPropertyChangeEvent evt) {
		int index = evt.getIndex();
		if (evt.getOldValue() instanceof Integer) {// moved
			int movedFrom = (Integer) evt.getOldValue();
			add(index, get(movedFrom));
		} else if (evt.getOldValue() == null) { // new
			Collection<GLElement> news = null;
			if (evt.getNewValue() instanceof ARankColumnModel) {
				news = Collections.singleton(wrap((ARankColumnModel) evt.getNewValue()));
			} else {
				news = new ArrayList<>();
				for (ARankColumnModel c : (Collection<ARankColumnModel>) evt.getNewValue())
					news.add(wrap(c));
			}
			asList().addAll(index, news);
		} else if (evt.getNewValue() == null) { // removed
			takeDown(get(index).getLayoutDataAs(ARankColumnModel.class, null));
			remove(index);
		} else { // replaced
			ARankColumnModel old = (ARankColumnModel) evt.getOldValue();
			takeDown(old);
			set(index, wrap((ARankColumnModel) evt.getNewValue()));
		}
	}

	private GLElement wrap(ARankColumnModel new_) {
		init(new_);
		return TableColumnUIs.createBody(new_, true).setData(table.getData(), this);
	}

	protected void updateData() {
		Collection<IRow> data = table.getData();
		for (GLElement col : this) {
			if (col instanceof ITableColumnUI)
				((ITableColumnUI) col).setData(data, this);
		}
	}

	protected void update() {
		relayout();
		repaint();
		for (GLElement g : this) {
			if (g instanceof ITableColumnUI) {
				((ITableColumnUI) g).update();
			}
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		this.pickingIDs = new int[this.table.size()];
		for (int i = 0; i < this.pickingIDs.length; ++i)
			this.pickingIDs[i] = context.registerPickingListener(selectRowListener, i);
	}

	/**
	 * @return the pickingIDs, see {@link #pickingIDs}
	 */
	public int[] getPickingIDs() {
		return pickingIDs;
	}

	@Override
	protected void takeDown() {
		this.table.removePropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, layoutOnChange);
		this.table.removePropertyChangeListener(RankTableModel.PROP_ORDER, layoutOnChange);
		this.table.removePropertyChangeListener(IRankColumnParent.PROP_INVALID, layoutOnChange);
		this.table.removePropertyChangeListener(IRankColumnParent.PROP_DATA, updateData);
		this.table.removePropertyChangeListener(RankTableModel.PROP_COLUMNS, columnsChanged);
		for (GLElement col : this) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			model.removePropertyChangeListener(ARankColumnModel.PROP_WEIGHT, layoutOnChange);
			model.addPropertyChangeListener(ACompositeRankColumnModel.PROP_CHILDREN, columnsChanged);
		}

		for (int pickingID : this.pickingIDs)
			context.unregisterPickingListener(pickingID);
		this.pickingIDs = null;
		super.takeDown();
	}

	/**
	 * layout cols
	 */
	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		rowPositions = computeRowPositions(h, table.size(),table.getSelectedRow() == null ? -1 : table
				.getSelectedRow().getRank());
		if (rowPositions.length > pickingIDs.length) {
			int bak = this.pickingIDs.length;
			this.pickingIDs = Arrays.copyOf(this.pickingIDs, rowPositions.length);
			for (int i = bak; i < this.pickingIDs.length; ++i)
				this.pickingIDs[i] = context.registerPickingListener(selectRowListener, i);
		}
		//align the columns normally
		float x = RenderStyle.COLUMN_SPACE;
		for (IGLLayoutElement col : children) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			col.setBounds(x, 3, model.getPreferredWidth(), h - 3);
			x += model.getPreferredWidth() + RenderStyle.COLUMN_SPACE;
		}
	}

	public float[] computeRowPositions(float h, int numRows, int selectedRank) {
		float[] hs = rowLayout.compute(numRows, selectedRank, h - 5);
		float acc = 0;
		for (int i = 0; i < hs.length; ++i) {
			hs[i] += acc;
			acc = hs[i];
		}
		return hs;
	}

	@Override
	public VAlign getAlignment(TableColumnUI tableColumnUI) {
		return VAlign.LEFT;
	}

	@Override
	public boolean hasFreeSpace(TableColumnUI tableColumnUI) {
		return true;
	}

	@Override
	public void layoutRows(ARankColumnModel model, List<? extends IGLLayoutElement> children, float w, float h,
			float[] rowPositions) {
		Iterator<IRow> ranks = model.getParent().getCurrentOrder();
		// align simple all the same x
		BitSet used = new BitSet(children.size());
		used.set(0, children.size());
		float y = 0;
		for (float hr : rowPositions) {
			if (!ranks.hasNext())
				break;
			int r = ranks.next().getIndex();
			IGLLayoutElement row = children.get(r);
			used.clear(r);
			row.setBounds(RenderStyle.COLUMN_SPACE, y, w, hr - y);
			y = hr;
		}
		hideUnused(children, w, h, used);
	}

	public static void hideUnused(List<? extends IGLLayoutElement> children, float w, float h, BitSet used) {
		for (int unused = used.nextSetBit(0); unused >= 0; unused = used.nextSetBit(unused + 1)) {
			children.get(unused).setBounds(RenderStyle.COLUMN_SPACE, h, w, 0);
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(ResourceLocators.classLoader(this.getClass().getClassLoader()));

		// highlight selected row
		IRow selectedRow = table.getSelectedRow();
		if (selectedRow != null) {
			int rank = selectedRow.getRank();
			if (rank < rowPositions.length && rank >= 0) {
				float prev = rank == 0 ? 0 : rowPositions[rank - 1];
				float next = rowPositions[rank];
				g.color(RenderStyle.COLOR_SELECTED_ROW);
				float hi = next - prev;
				renderSubLine(g, w, prev, hi);
			}
		}
		super.renderImpl(g, w, h);
		g.popResourceLocator();
	}

	private void renderSubLine(GLGraphics g, float w, float y, float hi) {
		// just for elements that haven't an own order
		float x = 0;
		for (GLElement child : this) {
			if (child instanceof ACompositeTableColumnUI && ((ACompositeTableColumnUI<?>) child).hasOwnOrder()) {
				Vec4f l = child.getBounds();
				g.fillRect(x, y + 3, l.x() - x, hi);
				x = l.x() + l.z();
			}
		}
		Vec4f last = get(size() - 1).getBounds();
		if (x < (last.x() + last.z()))
			g.fillRect(x, y + 3, last.x() + last.z() - x, hi);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		float y = 0;
		for (int i = 0; i < rowPositions.length; ++i) {
			g.pushName(pickingIDs[i]);
			renderSubLine(g, w, y, rowPositions[i] - y);
			y = rowPositions[i];
			g.popName();
		}
		super.renderPickImpl(g, w, h);
	}

	/**
	 * @return the rowPositions, see {@link #rowPositions}
	 */
	@Override
	public float[] getRowPositions() {
		return rowPositions;
	}

	public float[] getPreviousRowPositions(ACompositeTableColumnUI<?> col) {
		ACompositeTableColumnUI<?> prev = findPrevious(col);
		if (prev == null)
			return getRowPositions();
		return prev.getRowPositions();
	}

	private ACompositeTableColumnUI<?> findPrevious(ACompositeTableColumnUI<?> col) {
		int index = indexOf(col);
		if (index <= 0)
			return null;
		for (int i = index - 1; i >= 0; --i) {
			GLElement g = get(i);
			if (g instanceof ACompositeTableColumnUI)
				return (ACompositeTableColumnUI<?>) g;
		}
		return null;
	}

	public Iterator<IRow> getPreviousOrder(ACompositeTableColumnUI<?> col) {
		ACompositeTableColumnUI<?> prev = findPrevious(col);
		if (prev == null)
			return table.getCurrentOrder();
		return prev.getModel().getCurrentOrder();
	}
}


