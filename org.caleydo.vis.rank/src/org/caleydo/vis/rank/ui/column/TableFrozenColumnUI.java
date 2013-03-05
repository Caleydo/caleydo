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

import java.util.Iterator;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.FrozenRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.TableBodyUI;

import com.jogamp.common.util.IntIntHashMap;

/**
 * @author Samuel Gratzl
 *
 */
public class TableFrozenColumnUI extends ACompositeTableColumnUI<FrozenRankColumnModel> {
	private float[] rowPositions;
	private IntIntHashMap indexToRank;

	public TableFrozenColumnUI(FrozenRankColumnModel model) {
		super(model);

		indexToRank = new IntIntHashMap();
		int i = 0;
		for (Iterator<IRow> it = model.getCurrentOrder(); it.hasNext(); i++) {
			indexToRank.put(it.next().getIndex(), i);
		}
	}

	@Override
	protected GLElement wrap(ARankColumnModel model) {
		ITableColumnUI ui = TableColumnUIs.createBody(model, false);
		return ui.setData(model.getTable().getData(), this);
	}

	@Override
	public GLElement setData(Iterable<IRow> data, IColumModelLayout parent) {
		// nothing to do
		return this;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (!model.isCollapsed()) {
			// render the lines between the orders
			IRow selectedRow = model.getTable().getSelectedRow();
			renderOrderLines(g, selectedRow == null ? -1 : selectedRow.getIndex());

			// highlight selected row
			if (selectedRow != null && indexToRank.containsKey(selectedRow.getIndex())) {
				int rank = indexToRank.get(selectedRow.getIndex());
				if (rank < rowPositions.length && rank >= 0) {
					float prev = rank == 0 ? 0 : rowPositions[rank - 1];
					float next = rowPositions[rank];
					g.color(RenderStyle.COLOR_SELECTED_ROW);
					g.fillRect(RenderStyle.FROZEN_BAND_WIDTH, prev, w - RenderStyle.FROZEN_BAND_WIDTH, next - prev);
				}
			}
		}
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (!model.isCollapsed()) {
			int[] pickingIDs = ((TableBodyUI) getParent()).getPickingIDs();

			// my positions
			float[] right = getRowPositions();

			// the left positions
			float[] left = ((TableBodyUI) getParent()).getPreviousRowPositions(this);
			Iterator<IRow> leftOrder = ((TableBodyUI) getParent()).getPreviousOrder(this);

			ConnectionBandRenderer renderer = new ConnectionBandRenderer();
			renderer.init(g.gl);
			// align simple all the same x
			float y = 0;
			int ri = 0;
			for (float hl : left) {
				if (!leftOrder.hasNext())
					break;
				int index = leftOrder.next().getIndex();
				if (indexToRank.containsKey(index)) {
					int rightRank = indexToRank.get(index);
					if (rightRank < right.length) {
						// render the right picking id according to the left rank
						g.pushName(pickingIDs[ri]);
						float yr = rightRank == 0 ? 0 : right[rightRank - 1];
						float hr = right[rightRank];
						renderBand(g, renderer, y, hl, yr, hr, RenderStyle.FROZEN_BAND_WIDTH, false);
						g.fillRect(RenderStyle.FROZEN_BAND_WIDTH, yr,
								w - RenderStyle.FROZEN_BAND_WIDTH, hr - yr);
						g.popName();
					}
				}
				y = hl;
				ri++;
			}
		}
		super.renderPickImpl(g, w, h);
	}

	private void renderOrderLines(GLGraphics g, int selectedIndex) {
		// my positions
		float[] right = getRowPositions();

		// the left positions
		float[] left = ((TableBodyUI) getParent()).getPreviousRowPositions(this);
		Iterator<IRow> leftOrder = ((TableBodyUI) getParent()).getPreviousOrder(this);

		ConnectionBandRenderer renderer = new ConnectionBandRenderer();
		renderer.init(g.gl);
		// align simple all the same x
		g.save();
		g.gl.glTranslatef(0, 0, g.z());
		float y = 0;
		for (float hr : left) {
			if (!leftOrder.hasNext())
				break;
			int index = leftOrder.next().getIndex();
			if (indexToRank.containsKey(index)) {
				int rightRank = indexToRank.get(index);
				if (rightRank < right.length) {
					renderBand(g, renderer, y, hr, rightRank == 0 ? 0 : right[rightRank - 1], right[rightRank],
							RenderStyle.FROZEN_BAND_WIDTH - 3,
							selectedIndex == index);
				}
			}
			y = hr;
		}
		g.restore();
	}

	private void renderBand(GLGraphics g, ConnectionBandRenderer renderer, float yl1, float yl2, float yr1, float yr2,
			float w, boolean isSelected) {
		float deltal = Math.min(3, (yl2 - yl1 - 1) * .5f);
		float deltar = Math.min(3, (yr2 - yr1 - 1) * .5f);
		float[] leftTopPos = new float[] { 0, yl1 + deltal };
		float[] leftBottomPos = new float[] { 0, yl2 - deltal };
		float[] rightTopPos = new float[] { w, yr1 + deltar };
		float[] rightBottomPos = new float[] { w, yr2 - deltar };
		float[] col = (isSelected ? RenderStyle.COLOR_SELECTED_ROW : RenderStyle.COLOR_BAND).getRGBComponents(null);
		if (isSelected)
			g.gl.glTranslatef(0, 0, 0.1f);
		renderer.renderSingleBand(g.gl, leftTopPos, leftBottomPos, rightTopPos, rightBottomPos, false, 20, -1, col,
				false);
		if (isSelected)
			g.gl.glTranslatef(0, 0, -0.1f);
	}

	@Override
	protected float getLeftPadding() {
		return RenderStyle.FROZEN_BAND_WIDTH;
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IRow selected = model.getTable().getSelectedRow();
		int selectedRank;
		if (selected != null && indexToRank.containsKey(selected.getIndex()))
			selectedRank = indexToRank.get(selected.getIndex());
		else
			selectedRank = -1;
		rowPositions = ((TableBodyUI) getParent()).computeRowPositions(h, model.getCurrentSize(), selectedRank);
		super.doLayout(children, w, h);
	}

	@Override
	public void layoutRows(ARankColumnModel model, List<? extends IGLLayoutElement> children, float w, float h,
			float[] rowPositions) {
		// simple
		((IColumModelLayout) getParent()).layoutRows(model, children, w, h, rowPositions);
	}

	@Override
	public boolean hasOwnOrder() {
		return true;
	}

	@Override
	public float[] getRowPositions() {
		return rowPositions;
	}

	@Override
	public int getRankDelta(IRow row) {
		return 0; // there are no deltas currently as the order is static
	}

	@Override
	public VAlign getAlignment(TableColumnUI model) {
		return VAlign.LEFT;
	}

	@Override
	public boolean hasFreeSpace(TableColumnUI model) {
		return true;
	}

}
