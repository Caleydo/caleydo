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
package org.caleydo.view.tourguide.v3.ui;

import java.awt.Color;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.tourguide.v3.layout.RowHeightLayouts.IRowHeightLayout;
import org.caleydo.view.tourguide.v3.model.ACompositeRankColumnModel;
import org.caleydo.view.tourguide.v3.model.ARankColumnModel;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.RankTableModel;
import org.caleydo.view.tourguide.v3.model.StackedRankColumnModel;
import org.caleydo.view.tourguide.v3.model.mixin.ICollapseableColumnMixin;

/**
 * FIXME: watch for changes: * weight changes -> relayout * reorder column -> sort columns * remove column -> remove
 * column * add column *
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
	private int[] pickingIDs = null;
	private IPickingListener selectRowListener = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			if (pick.getPickingMode() == PickingMode.CLICKED)
				table.setSelectedRow(pick.getObjectID());
		}
	};
	private float[] rowPositions;


	public TableBodyUI(RankTableModel table, IRowHeightLayout rowLayout) {
		this.table = table;
		this.rowLayout = rowLayout;
		this.table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, layoutOnChange);
		this.table.addPropertyChangeListener(RankTableModel.PROP_ORDER, layoutOnChange);
		this.table.addPropertyChangeListener(RankTableModel.PROP_DATA, updateData);
		this.table.addPropertyChangeListener(RankTableModel.PROP_COLUMNS, columnsChanged);
		for (ARankColumnModel col : table.getColumns()) {
			if (col instanceof StackedRankColumnModel) {
				StackedRankColumnModel s = (StackedRankColumnModel) col;
				TableStackedColumnUI ui = new TableStackedColumnUI(s);
				ui.setData(table.getData());
				init(col);
				this.add(ui);
			} else {
				this.add(wrap(col));
			}
		}
		setLayout(this);
	}

	private void init(ARankColumnModel col) {
		col.addPropertyChangeListener(ARankColumnModel.PROP_WEIGHT, layoutOnChange);
		col.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

	private void takeDown(ARankColumnModel col) {
		col.removePropertyChangeListener(ARankColumnModel.PROP_WEIGHT, layoutOnChange);
		col.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, layoutOnChange);
	}

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
		return new TableColumnUI(new_).setData(table.getData());
	}

	protected void updateData() {
		Collection<IRow> data = table.getData();
		for (GLElement col : this) {
			if (col instanceof TableStackedColumnUI) {
				((TableStackedColumnUI) col).setData(data);
			} else
				((TableColumnUI) col).setData(data);
		}
	}

	protected void update() {
		relayout();
		repaint();
		for (GLElement g : this) {
			if (g instanceof TableStackedColumnUI) {
				((TableStackedColumnUI) g).update();
			}
			g.relayout();
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		this.pickingIDs = new int[this.table.size()];
		for (int i = 0; i < this.pickingIDs.length; ++i)
			this.pickingIDs[i] = context.registerPickingListener(selectRowListener, i);
	}

	@Override
	protected void takeDown() {
		this.table.removePropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, layoutOnChange);
		this.table.removePropertyChangeListener(RankTableModel.PROP_ORDER, layoutOnChange);
		this.table.removePropertyChangeListener(RankTableModel.PROP_DATA, updateData);
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
		System.out.println("compute layout");
		rowPositions = preScan(rowLayout.compute(table.size(), table.getSelectedRow() == null ? -1 : table
				.getSelectedRow().getRank(), h));
		if (rowPositions.length > pickingIDs.length) {
			int bak = this.pickingIDs.length;
			this.pickingIDs = Arrays.copyOf(this.pickingIDs, rowPositions.length);
			for (int i = bak; i < this.pickingIDs.length; ++i)
				this.pickingIDs[bak] = context.registerPickingListener(selectRowListener, i);
		}
		//align the columns normally
		float x = TableHeaderUI.COLUMN_SPACE;
		for (IGLLayoutElement col : children) {
			ARankColumnModel model = col.getLayoutDataAs(ARankColumnModel.class, null);
			col.setBounds(x, 0, model.getPreferredWidth(), h);
			x += model.getPreferredWidth() + TableHeaderUI.COLUMN_SPACE;
		}
	}

	private float[] preScan(float[] compute) {
		float acc = 0;
		for (int i = 0; i < compute.length; ++i) {
			compute[i] += acc;
			acc = compute[i];
		}
		return compute;
	}

	@Override
	public void layoutRows(ARankColumnModel model, List<? extends IGLLayoutElement> children, float w, float h) {
		int[] ranks = table.getOrder();
		// align simple all the same x
		BitSet used = new BitSet(children.size());
		used.set(0, children.size());
		int i = 0;
		float y = 0;
		for (float hr : rowPositions) {
			int r = ranks[i++];
			IGLLayoutElement row = children.get(r);
			used.clear(r);
			row.setBounds(0, y, w, hr - y);
			y = hr;
		}
		for (int unused = used.nextSetBit(0); unused >= 0; unused = used.nextSetBit(unused + 1)) {
			children.get(unused).setBounds(0, h, w, 0);
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		// highlight selected row
		IRow selectedRow = table.getSelectedRow();
		if (selectedRow != null) {
			int rank = selectedRow.getRank();
			if (rank < rowPositions.length && rank >= 0) {
				float prev = rank == 0 ? 0 : rowPositions[rank - 1];
				float next = rowPositions[rank];
				g.color(Color.YELLOW).fillRect(0, prev, w, next - prev);
			}
		}
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		float y = 0;
		for (int i = 0; i < rowPositions.length; ++i) {
			g.pushName(pickingIDs[i]);
			g.fillRect(0, y, w, rowPositions[i] - y);
			y = rowPositions[i];
			g.popName();
		}
		super.renderPickImpl(g, w, h);
	}

	/**
	 * @return the rowPositions, see {@link #rowPositions}
	 */
	public float[] getRowPositions() {
		return rowPositions;
	}

}


