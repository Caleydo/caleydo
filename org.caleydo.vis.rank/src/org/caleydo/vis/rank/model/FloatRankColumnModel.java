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
package org.caleydo.vis.rank.model;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.BitSet;
import java.util.List;

import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.AFloatList;
import org.caleydo.core.util.function.IFloatIterator;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.data.IFloatInferrer;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.RenderUtils;
import org.caleydo.vis.rank.ui.detail.ScoreBarRenderer;
import org.caleydo.vis.rank.ui.detail.ScoreSummary;
import org.caleydo.vis.rank.ui.mapping.MappingFunctionUIs;
import org.eclipse.swt.SWT;

/**
 * @author Samuel Gratzl
 *
 */
public class FloatRankColumnModel extends ABasicFilterableRankColumnModel implements IFilterColumnMixin,
		IMappedColumnMixin, IRankableColumnMixin, ISnapshotableColumnMixin {
	private float selectionMin = 0;
	private float selectionMax = 1;

	private SimpleHistogram cacheHist = null;
	private boolean dirtyMinMax = true;
	private final IMappingFunction mapping;
	private float missingValue;
	private final IFloatInferrer missingValueInferer;

	private final IFloatFunction<IRow> data;
	private final PropertyChangeListener listerner = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case IRankColumnParent.PROP_INVALID:
				if (!mapping.hasDefinedMappingBounds())
					invalidAllFilter();
				cacheHist = null;
				dirtyMinMax = true;
				missingValue = Float.NaN;
				break;
			}
		}
	};
	private final ICallback<IMappingFunction> callback = new ICallback<IMappingFunction>() {
		@Override
		public void on(IMappingFunction data) {
			cacheHist = null;
			invalidAllFilter();
			propertySupport.firePropertyChange(PROP_MAPPING, null, data);
		}
	};
	private final IGLRenderer valueRenderer = new ScoreBarRenderer(this);

	public FloatRankColumnModel(IFloatFunction<IRow> data, IGLRenderer header, Color color, Color bgColor,
			PiecewiseMapping mapping, IFloatInferrer missingValue) {
		super(color, bgColor);
		this.data = data;
		this.mapping = mapping;
		this.missingValueInferer = missingValue;

		setHeaderRenderer(header);
	}

	public FloatRankColumnModel(FloatRankColumnModel copy) {
		super(copy);
		this.data = copy.data;
		this.mapping = copy.mapping.clone();
		this.missingValueInferer = copy.missingValueInferer;
		setHeaderRenderer(copy.getHeaderRenderer());
		this.missingValue = copy.missingValue;
		this.dirtyMinMax = copy.dirtyMinMax;
		this.cacheHist = copy.cacheHist;
		this.selectionMin = copy.selectionMin;
		this.selectionMax = copy.selectionMax;
	}

	@Override
	public FloatRankColumnModel clone() {
		return new FloatRankColumnModel(this);
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
		invalidAllFilter();
	}

	@Override
	protected void init(IRankColumnParent parent) {
		parent.addPropertyChangeListener(IRankColumnParent.PROP_INVALID, listerner);
		super.init(parent);
	}

	@Override
	protected void takeDown() {
		parent.removePropertyChangeListener(IRankColumnParent.PROP_INVALID, listerner);
		super.takeDown();
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new ScoreSummary(this, interactive);
	}

	@Override
	public GLElement createValue() {
		return new GLElement(valueRenderer);
	}

	@Override
	public void editFilter(GLElement summary, IGLElementContext context) {
		// inline
	}

	@Override
	public void editMapping(GLElement summary, IGLElementContext context) {
		GLElement m = MappingFunctionUIs.create(mapping, asData(), getColor(), getBgColor(), callback);
		m.setzDelta(0.5f);
		Vec2f location = summary.getAbsoluteLocation();
		Vec2f size = summary.getSize();
		context.getPopupLayer().show(m, new Vec4f(location.x(), location.y() + size.y(), 260, 260));
	}

	private IFloatList asData() {
		final int size = parent.getCurrentSize();
		return new AFloatList() {
			@Override
			public float getPrimitive(int index) {
				return data.applyPrimitive(parent.getCurrent(index));
			}

			@Override
			public int size() {
				return size;
			}
		};
	}

	private IFloatIterator asDataIterator() {
		final List<IRow> data2 = getTable().getData();
		final BitSet filter = parent.getCurrentFilter();
		return new IFloatIterator() {
			int act = 0;
			@Override
			public boolean hasNext() {
				return act >= 0 && filter.nextSetBit(act + 1) >= 0;
			}

			@Override
			public Float next() {
				return nextPrimitive();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public float nextPrimitive() {
				int bak = act;
				act = filter.nextSetBit(act + 1);
				return data.applyPrimitive(data2.get(bak));
			}

		};
	}


	@Override
	public boolean isFiltered() {
		return selectionMin > 0 || selectionMax < 1;
	}

	protected float map(float value) {
		if (Float.isNaN(value))
			value = computeMissingValue();
		checkMapping();
		float r = mapping.apply(value);
		if (Float.isNaN(r))
			return 0;
		return r;
	}

	private float computeMissingValue() {
		if (Float.isNaN(missingValue)) {
			missingValue = missingValueInferer.infer(asDataIterator(), parent.getCurrentSize());
		}
		return missingValue;
	}

	private void checkMapping() {
		if (dirtyMinMax && mapping.isMappingDefault() && !mapping.hasDefinedMappingBounds()) {
			float[] minmax = AFloatList.computeStats(asDataIterator());
			mapping.setAct(minmax[0], minmax[1]);
			dirtyMinMax = false;
		}
	}

	@Override
	public Float apply(IRow row) {
		return applyPrimitive(row);
	}

	@Override
	public float applyPrimitive(IRow row) {
		return map(data.applyPrimitive(row));
	}

	@Override
	public boolean isValueInferred(IRow row) {
		return Float.isNaN(data.applyPrimitive(row));
	}

	@Override
	public String getRawValue(IRow row) {
		float r = data.applyPrimitive(row);
		if (Float.isNaN(r))
			return "";
		return Formatter.formatNumber(r);
	}

	@Override
	public SimpleHistogram getHist(int bins) {
		if (cacheHist != null && cacheHist.size() == bins)
			return cacheHist;
		return cacheHist = DataUtils.getHist(bins, parent.getCurrentOrder(), this);
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			float v = applyPrimitive(data.get(i));
			mask.set(i++, (!Float.isNaN(v) && v >= selectionMin && v <= selectionMax));
		}
	}

	class FloatSummary extends PickableGLElement {
		private int cursorMinPickingID = -1, cursorMaxPickingID = -1;
		private boolean cursorMinHovered = false, cursorMaxHovered = true;

		private final PropertyChangeListener repaintOnEvent = GLPropertyChangeListeners.repaintOnEvent(this);
		private final PropertyChangeListener selectRowListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onSelectRow((IRow) evt.getNewValue());
			}
		};
		private IRow selectedRow = null;
		private final boolean interactive;

		/**
		 * @param interactive
		 */
		public FloatSummary(boolean interactive) {
			this.interactive = interactive;
			if (interactive) {
				setzDelta(.2f);
			} else {
				setVisibility(EVisibility.VISIBLE); // disable picking
			}
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);

			RankTableModel table = getTable();
			table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, selectRowListener);
			this.selectedRow = table.getSelectedRow();

			addPropertyChangeListener(PROP_FILTER, repaintOnEvent);
			addPropertyChangeListener(PROP_MAPPING, repaintOnEvent);

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
			removePropertyChangeListener(PROP_MAPPING, repaintOnEvent);
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
			SimpleHistogram hist = getHist(RenderStyle.binsForWidth(w));
			int selectedBin = selectedRow == null ? -1 : hist.getBinOf(applyPrimitive(selectedRow));
			RenderUtils.renderHist(g, hist, w, h, selectedBin, color, color.darker());
			// selection
			if (w > 20)
				renderSelection(g, selectionMin, selectionMax, w, h);
			checkMapping();
			DecimalFormat d = new DecimalFormat("#####.##");

			float[] m = mapping.getMappedMin();
			g.drawText(d.format(m[0]), 1, h - 23, w * 0.4f, 10);
			g.drawText(d.format(m[1]), 1, h - 12, w * 0.4f, 10);
			m = mapping.getMappedMax();
			g.drawText(d.format(m[0]), w * 0.6f, h - 23, w * 0.4f - 1, 10, VAlign.RIGHT);
			g.drawText(d.format(m[1]), w * 0.6f, h - 12, w * 0.4f - 1, 10, VAlign.RIGHT);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			super.renderPickImpl(g, w, h);
			if (w <= 20 || !interactive)
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

	@Override
	public void takeSnapshot() {
		parent.takeSnapshot(this);
	}

}
