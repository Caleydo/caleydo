/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.lineup.layout.IRowLayoutInstance.IRowSetter;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.lineup.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleColumnUI extends ACompositeTableColumnUI<ACompositeRankColumnModel> {

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case ICompressColumnMixin.PROP_COMPRESSED:
			case ICollapseableColumnMixin.PROP_COLLAPSED:
				onCompressedChanged();
				break;
			}
		}
	};

	public SimpleColumnUI(ACompositeRankColumnModel model) {
		super(model, 1);
		model.addPropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
		model.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		this.add(0, wrap(model));
	}

	protected void onCompressedChanged() {
		relayout();
		relayoutParent();
	}

	@Override
	protected GLElement wrap(ARankColumnModel model) {
		ITableColumnUI ui = ColumnUIs.createBody(model, false);
		return ui.asGLElement();
	}

	@Override
	protected void takeDown() {
		model.removePropertyChangeListener(ICompressColumnMixin.PROP_COMPRESSED, listener);
		model.removePropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, listener);
		super.takeDown();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement elem = children.get(0);
		if (model instanceof ICompressColumnMixin && ((ICompressColumnMixin) model).isCompressed()) {
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
		g.drawLine(getLeftPadding() - 1, 0, getLeftPadding() - 1, h).drawLine(w, 0, w, h);
		g.incZ().incZ();
		g.lineWidth(1);
		super.renderImpl(g, w, h);
	}

	@Override
	public void layoutRows(ARankColumnModel model, final IRowSetter setter, final float w, float h) {
		getColumnModelParent().layoutRows(model, setter, w, h);
	}

	@Override
	protected float getLeftPadding() {
		return RenderStyle.GROUP_COLUMN_PADDING;
	}

	@Override
	protected float getChildWidth(int i, ARankColumnModel model) {
		return model.getWidth();
	}

	@Override
	public int getNumVisibleRows(ARankColumnModel model) {
		return getRanker(model).getNumVisibleRows();
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
	public Color getBarOutlineColor() {
		return getColumnModelParent().getBarOutlineColor();
	}

	@Override
	public VAlign getAlignment(ITableColumnUI model) {
		return VAlign.LEFT;
	}

	@Override
	public boolean hasFreeSpace(ITableColumnUI model) {
		return true;
	}
}

