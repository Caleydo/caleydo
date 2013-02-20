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
package org.caleydo.view.tourguide.v3.model;

import gleem.linalg.Vec2f;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.List;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.v2.r.model.DataUtils;
import org.caleydo.view.tourguide.v3.model.mixin.IFilterColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IMappedColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IRankableColumnMixin;
import org.caleydo.view.tourguide.v3.ui.RenderUtils;
import org.caleydo.view.tourguide.v3.ui.detail.ScoreBarRenderer;
import org.eclipse.swt.SWT;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class FloatRankColumnModel extends ABasicRankColumnModel implements IFilterColumnMixin, IMappedColumnMixin,
		IRankableColumnMixin {
	private float selectionMin = 0;
	private float selectionMax = 1;

	private final FloatFunction<? super IRow> data;

	public FloatRankColumnModel(FloatFunction<? super IRow> data, IGLRenderer header, Color color, Color bgColor) {
		super(color, bgColor);
		this.data = data;
		setHeaderRenderer(header);
		setValueRenderer(new ScoreBarRenderer(this));
	}

	public void addSelection(boolean isMin, float delta) {
		if (delta == 0)
			return;
		Pair<Float, Float> bak = Pair.make(selectionMin, selectionMax);
		if (isMin) {
			this.selectionMin += delta;
		} else {
			this.selectionMax += delta;
		}
		propertySupport.firePropertyChange(PROP_FILTER, bak, Pair.make(selectionMin, selectionMax));
	}

	@Override
	public GLElement createSummary() {
		return new FloatSummary();
	}


	@Override
	public void editMapping() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFiltered() {
		return selectionMin > 0 || selectionMax < 1;
	}

	@Override
	public void editFilter() {
		// inline
	}

	@Override
	public float getValue(IRow row) {
		return data.applyPrimitive(row);
	}

	@Override
	public Histogram getHist(int bins) {
		Histogram hist = new Histogram(bins);
		for (IRow row : getTable()) {
			float value = data.applyPrimitive(row);
			if (Float.isNaN(value))
				hist.addNAN(0);
			else
				hist.add(Math.round(value * (bins - 1)), 0);
		}
		return hist;
	}

	@Override
	public BitSet getSelectedRows(List<IRow> rows) {
		BitSet b = new BitSet(rows.size());
		if (selectionMin <= 0 && selectionMax >= 1) {
			b.set(0, rows.size());
		} else {
			int i = 0;
			for(IRow row : rows) {
				Float v = data.applyPrimitive(row);
				b.set(i++, (v != null && !Float.isNaN(v) && v >= selectionMin && v <= selectionMax));
			}
		}
		return b;
	}

	class FloatSummary extends PickableGLElement {
		private int cursorMinPickingID = -1, cursorMaxPickingID = -1;
		private boolean cursorMinHovered = false, cursorMaxHovered = true;

		private final PropertyChangeListener repaintOnEvent = RenderUtils.repaintOnEvent(this);
		private final PropertyChangeListener selectRowListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onSelectRow((IRow) evt.getNewValue());
			}
		};
		private IRow selectedRow = null;

		@Override
		protected void init(IGLElementContext context) {
			setzDelta(.2f);
			super.init(context);

			RankTableModel table = getTable();
			table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, selectRowListener);
			this.selectedRow = table.getSelectedRow();

			addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, repaintOnEvent);

			cursorMinPickingID = context.registerPickingListener(new IPickingListener() {
				@Override
				public void pick(Pick pick) {
					onCursorPick(true, pick);
				}
			});
			cursorMaxPickingID = context.registerPickingListener(new IPickingListener() {
				@Override
				public void pick(Pick pick) {
					onCursorPick(false, pick);
				}
			});
		}

		@Override
		protected void takeDown() {
			context.unregisterPickingListener(cursorMinPickingID);
			context.unregisterPickingListener(cursorMaxPickingID);
			RankTableModel table = getTable();
			table.removePropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, selectRowListener);
			removePropertyChangeListener(PROP_FILTER, repaintOnEvent);
			super.takeDown();
		}

		@Override
		protected void onClicked(Pick pick) {
			if (pick.isAnyDragging())
				return;
			Vec2f p = toRelative(pick.getPickedPoint());
			float at = p.x() / this.getSize().x();
			float min = selectionMin;
			float max = selectionMax;
			if (at < min)
				addSelection(true, at - min);
			else if (at > max)
				addSelection(false, at - max);
			else if ((at - min) < (max - min) * .5f)
				addSelection(true, at - min);
			else
				addSelection(false, at - max);
			repaint();
		}

		protected void onCursorPick(boolean isMin, Pick pick) {
			switch (pick.getPickingMode()) {
			case CLICKED:
				pick.setDoDragging(true);
				break;
			case MOUSE_OVER:
				context.setCursor(SWT.CURSOR_HAND);
				if (isMin)
					cursorMinHovered = true;
				else
					cursorMaxHovered = true;
				repaintAll();
				break;
			case MOUSE_OUT:
				if (!pick.isDoDragging()) {
					if (isMin)
						cursorMinHovered = false;
					else
						cursorMaxHovered = false;
					repaintAll();
				}
				break;
			case DRAGGED:
				if (!pick.isDoDragging())
					return;
				int dx = pick.getDx();
				if (dx != 0) {
					float delta = dx / this.getSize().x();
					addSelection(isMin, delta);
					repaintAll();
				}
				break;
			case MOUSE_RELEASED:
				if (pick.isDoDragging()) {
					context.setCursor(-1);
					if (isMin)
						cursorMinHovered = false;
					else
						cursorMaxHovered = false;
					repaintAll();
				}
				break;
			default:
				break;
			}
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			// background
			g.color(bgColor).fillRect(0, 0, w, h);
			// hist
			int bins = Math.round(w);
			int selectedBin = selectedRow == null ? -1 : DataUtils.getHistBin(bins, data.applyPrimitive(selectedRow));
			RenderUtils.renderHist(g, getHist(bins), w, h, selectedBin, color, color.darker());
			// selection
			if (w > 20)
				renderSelection(g, selectionMin, selectionMax, w, h);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			super.renderPickImpl(g, w, h);
			if (w <= 20)
				return;
			g.incZ().incZ();
			float from = selectionMin;
			if (from > 0) {
				g.pushName(cursorMinPickingID);
				if (cursorMinHovered)
					g.fillRect(from * w - 8, 0, 16, h);
				else
					g.fillRect(from * w, 0, 1, h);
				g.popName();
			}
			float to = selectionMax;
			if (to < 1) {
				g.pushName(cursorMaxPickingID);
				if (cursorMaxHovered)
					g.fillRect(to * w - 8, 0, 16, h);
				else
					g.fillRect(to * w, 0, 1, h);
				g.popName();
			}
			g.decZ().decZ();
		}

		private void renderSelection(GLGraphics g, float from, float to, float w, float h) {
			assert from < to;
			if (from > 0 && from < 1) {
				g.color(0, 0, 0, 0.25f).fillRect(0, 0, from * w, h);
				if (cursorMinHovered)
					g.color(Color.BLACK).fillRect(from * w - 2, 0, 4, h);
				else
					g.color(Color.BLACK).fillRect(from * w, 0, 1, h);
			}
			if (to > 0 && to < 1) {
				g.color(0, 0, 0, 0.25f).fillRect(to * w, 0, (1 - to) * w, h);
				if (cursorMaxHovered)
					g.color(Color.BLACK).fillRect(to * w - 2, 0, 4, h);
				else
					g.color(Color.BLACK).fillRect(to * w, 0, 1, h);
			}

		}

		protected void onSelectRow(IRow selectedRow) {
			if (this.selectedRow == selectedRow)
				return;
			this.selectedRow = selectedRow;
			repaint();
		}
	}

	public interface FloatFunction<F> extends Function<F, Float> {
		float applyPrimitive(F in);
	}

	public static abstract class AFloatFunction<F> implements FloatFunction<F> {
		@Override
		public final Float apply(F in) {
			return applyPrimitive(in);
		}
	}
}
