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


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.layout.IRowLayoutInstance.IRowSetter;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin.MultiFloat;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class StackedColumnUI extends ACompositeTableColumnUI<StackedRankColumnModel> {

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case StackedRankColumnModel.PROP_WEIGHTS:
			case StackedRankColumnModel.PROP_ALIGNMENT:
				onAlignmentChanged();
				break;
			case ICompressColumnMixin.PROP_COMPRESSED:
			case ICollapseableColumnMixin.PROP_COLLAPSED:
				onCompressedChanged();
				break;
			}
		}
	};

	public StackedColumnUI(StackedRankColumnModel model) {
		super(model, 1);
		model.addPropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, listener);
		model.addPropertyChangeListener(StackedRankColumnModel.PROP_WEIGHTS, listener);
		model.addPropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
		model.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		this.add(0, wrap(model));
	}

	protected void onCompressedChanged() {
		relayout();
		relayoutParent();
	}

	protected void onAlignmentChanged() {
		relayoutChildren();
		relayout();
		repaint();
	}

	@Override
	protected GLElement wrap(ARankColumnModel model) {
		ITableColumnUI ui = ColumnUIs.createBody(model, false);
		return ui.asGLElement();
	}

	@Override
	protected void takeDown() {
		model.removePropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, listener);
		model.removePropertyChangeListener(StackedRankColumnModel.PROP_WEIGHTS, listener);
		model.removePropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
		model.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		super.takeDown();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement elem = children.get(0);
		if (model.isCompressed()) {
			elem.setBounds(0, 0, w, h);
			for (IGLLayoutElement child : children.subList(1, children.size()))
				child.hide();
		} else {
			elem.hide();
			super.doLayout(children, w, h);
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.decZ().decZ();
		g.color(RenderStyle.COLOR_STACKED_BORDER).lineWidth(RenderStyle.COLOR_STACKED_BORDER_WIDTH);
		g.drawLine(-1, 0, -1, h).drawLine(w - 1, 0, w - 1, h);
		g.incZ().incZ();
		g.lineWidth(1);
		super.renderImpl(g, w, h);
	}

	@Override
	public void layoutRows(ARankColumnModel model, final IRowSetter setter, final float w, float h) {
		final int combinedAlign = this.model.getAlignment();
		final int index = this.model.indexOf(model);
		if (combinedAlign >= 0 && index != combinedAlign && index >= 0) {
			// moving around
			final float[] weights = new float[this.model.size()];
			for (int i = 0; i < weights.length; ++i)
				weights[i] = this.model.getChildWidth(i);

			final RankTableModel table = this.model.getTable();
			IRow selected = table.getSelectedRow();

			IRowSetter wrappedSetter = new IRowSetter() {
				@Override
				public void set(int rowIndex, float x, float y, float w, float h, boolean pickable) {
					IRow data = table.getDataItem(rowIndex);
					x = getX(combinedAlign, weights, index, data);
					setter.set(rowIndex, x, y, w, h, pickable);
				}
			};
			getRanker(model).layoutRows(wrappedSetter, 0, w);
		} else {
			// simple
			getColumnModelParent().layoutRows(model, setter, w, h);
		}
	}

	@Override
	protected float getLeftPadding() {
		return RenderStyle.STACKED_COLUMN_PADDING;
	}

	@Override
	protected float getChildWidth(int i, ARankColumnModel model) {
		return this.model.getChildWidth(i);
	}

	@Override
	public int getNumVisibleRows(ARankColumnModel model) {
		return getRanker(model).getNumVisibleRows();
	}

	private float getX(int combinedAlign, float[] weights, int index, IRow data) {
		float x = 0;
		MultiFloat vs = model.getSplittedValue(data);
		if (index < combinedAlign) {
			for (int i = index; i < combinedAlign; ++i)
				x += (1 - vs.values[i]) * weights[i];
			// x += RenderStyle.COLUMN_SPACE;
		} else {
			for (int i = combinedAlign; i < index; ++i)
				x += (vs.values[i] - 1) * weights[i];
			// x -= RenderStyle.COLUMN_SPACE;
		}
		return x;
	}

	@Override
	public OrderColumnUI getRanker(ARankColumnModel model) {
		return getColumnModelParent().getRanker(model);
	}

	@Override
	public boolean causesReorderingLayouting() {
		return getColumnModelParent().causesReorderingLayouting();
	}

	@Override
	public VAlign getAlignment(ITableColumnUI model) {
		int combinedAlign = this.model.getAlignment();
		if (combinedAlign < 0)
			return VAlign.LEFT;
		int index = this.model.indexOf(model.getModel());
		return (index >= combinedAlign || index < 0) ? VAlign.LEFT : VAlign.RIGHT;
	}

	@Override
	public boolean hasFreeSpace(ITableColumnUI model) {
		int combinedAlign = this.model.getAlignment();
		if (combinedAlign < 0)
			return true;
		int index = this.model.indexOf(model.getModel());
		if (index < 0)
			return true;
		return index >= combinedAlign ? (index == (this.model.size() - 1)) : (index == 0);
	}
}

