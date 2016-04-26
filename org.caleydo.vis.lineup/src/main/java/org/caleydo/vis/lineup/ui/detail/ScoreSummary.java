/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.detail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.SimpleHistogram;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.INegativeColumnMixin;
import org.caleydo.vis.lineup.ui.IColumnRenderInfo;
import org.caleydo.vis.lineup.ui.RenderUtils;


/**
 * @author Samuel Gratzl
 *
 */
public class ScoreSummary extends GLElement {
	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case RankTableModel.PROP_SELECTED_ROW:
				onSelectRow((IRow) evt.getNewValue());
				break;
			default:
				repaint();
				break;
			}

		}
	};
	private IRow selectedRow = null;
	protected final IDoubleRankableColumnMixin model;

	public ScoreSummary(IDoubleRankableColumnMixin model, boolean interactive) {
		this.model = model;
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		RankTableModel table = model.getTable();
		table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, listener);
		this.selectedRow = table.getSelectedRow();
	}

	@Override
	protected void takeDown() {
		RankTableModel table = model.getTable();
		table.removePropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, listener);
		super.takeDown();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if ((model instanceof ICollapseableColumnMixin) && ((ICollapseableColumnMixin) model).isCollapsed() || w < 5)
			return;
		SimpleHistogram hist = model.getHist(w);
		int selectedBin = selectedRow == null ? -1 : hist.getBinOf(model.applyPrimitive(selectedRow));
		if (model instanceof INegativeColumnMixin) {
			double zero = ((INegativeColumnMixin) model).zeroValue();
			if (!Double.isNaN(zero)) {
				RenderUtils.renderNegativeAwareHist(g, hist, w, h, selectedBin, model.getColor(), model.getColor()
						.darker(), findRenderInfo().getBarOutlineColor(), zero);
				return;
			}
		}
		RenderUtils.renderHist(g, hist, w, h, selectedBin, model.getColor(), model.getColor().darker(),
				findRenderInfo().getBarOutlineColor());
	}

	/**
	 * @return
	 */
	private IColumnRenderInfo findRenderInfo() {
		IGLElementParent p = getParent();
		while (!(p instanceof IColumnRenderInfo) && p != null)
			p = p.getParent();
		return (IColumnRenderInfo) p;
	}

	protected void onSelectRow(IRow selectedRow) {
		if (this.selectedRow == selectedRow)
			return;
		this.selectedRow = selectedRow;
		repaint();
	}
}
