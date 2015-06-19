/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.lineup.layout.IRowLayoutInstance.IRowSetter;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel.Alignment;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMultiColumnMixin.MultiDouble;
import org.caleydo.vis.lineup.ui.RenderStyle;

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

		{// render the 0 line hint
			double zero = this.model.zeroValue();
			if (!Double.isNaN(zero) && isSimpleStacking()) {
				float wf = (w - getLeftPadding() * 2) * (float) zero;
				g.drawLine(wf, 1, wf, h - 2);
			}
		}

		g.lineWidth(1);

		super.renderImpl(g, w, h);
	}

	/**
	 * whether the stacking is a classical stacked bar
	 *
	 * @return
	 */
	private boolean isSimpleStacking() {
		final Alignment alignment = this.model.getAlignment();
		final int combinedAlign = this.model.getSingleAlignment();
		return alignment == Alignment.ORDERED || combinedAlign == 0;
	}

	@Override
	public void layoutRows(ARankColumnModel model, final IRowSetter setter, final float w, float h) {
		final Alignment alignment = this.model.getAlignment();
		final int combinedAlign = this.model.getSingleAlignment();
		final int index = this.model.indexOf(model);

		if (alignment == Alignment.SINGLE && combinedAlign >= 0 && index != combinedAlign && index >= 0) {
			// moving around
			final float[] weights = new float[this.model.size()];
			for (int i = 0; i < weights.length; ++i)
				weights[i] = this.model.getChildWidth(i);

			final RankTableModel table = this.model.getTable();
			IRowSetter wrappedSetter = new IRowSetter() {
				@Override
				public void set(int rowIndex, float x, float y, float w, float h, boolean pickable) {
					IRow data = table.getDataItem(rowIndex);
					x = getStackedX(combinedAlign, weights, index, data);
					setter.set(rowIndex, x, y, w, h, pickable);
				}
			};
			getRanker(model).layoutRows(wrappedSetter, 0, w);
		} else if (alignment == Alignment.ORDERED) { // order by value
			// moving around
			final float[] weights = new float[this.model.size()];
			for (int i = 0; i < weights.length; ++i)
				weights[i] = this.model.getChildWidth(i);
			final RankTableModel table = this.model.getTable();
			IRowSetter wrappedSetter = new IRowSetter() {
				@Override
				public void set(int rowIndex, float x, float y, float w, float h, boolean pickable) {
					IRow data = table.getDataItem(rowIndex);
					x = getOrderedX(weights, index, data);
					setter.set(rowIndex, x, y, w, h, pickable);
				}
			};
			getRanker(model).layoutRows(wrappedSetter, 0, w);
		} else
			// simple
			getColumnModelParent().layoutRows(model, setter, w, h);
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

	private float getStackedX(int combinedAlign, float[] weights, int index, IRow data) {
		if (index < 0)
			return 0;
		float x = 0;
		MultiDouble vs = model.getSplittedValue(data);
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

	private float getOrderedX(float[] weights, int index, IRow data) {
		if (index < 0)
			return 0;
		float x = 0;
		MultiDouble vs = model.getSplittedValue(data);
		double me = vs.values[index] * weights[index];
		BitSet larger = new BitSet(vs.values.length);
		for(int i = 0; i < vs.values.length; ++i) {
			double o = vs.values[i] * weights[i];
			larger.set(i, o > me || (o == me && i > index));
		}
		for (int i = 0; i < index; ++i) {
			x -= weights[i] + RenderStyle.COLUMN_SPACE;
		}
		// now x is at the left corner
		for (int i = larger.nextSetBit(0); i >= 0; i = larger.nextSetBit(i + 1))
			x += vs.values[i] * weights[i] + RenderStyle.COLUMN_SPACE;
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
		Alignment combinedAlign = this.model.getAlignment();
		if (combinedAlign != Alignment.SINGLE)
			return VAlign.LEFT;
		int index = this.model.indexOf(model.getModel());
		return (index >= this.model.getSingleAlignment() || index < 0) ? VAlign.LEFT : VAlign.RIGHT;
	}

	@Override
	public boolean hasFreeSpace(ITableColumnUI model) {
		int index = this.model.indexOf(model.getModel());
		if (index < 0)
			return true;
		switch(this.model.getAlignment()) {
		case ALL:
			return true;
		case SINGLE:
			return index >= this.model.getSingleAlignment() ? (index == (this.model.size() - 1)) : (index == 0);
		case ORDERED:
			return false; // FIXME not implemented
		}
		throw new IllegalStateException();
	}

	@Override
	public Color getBarOutlineColor() {
		return getColumnModelParent().getBarOutlineColor();
	}
}

