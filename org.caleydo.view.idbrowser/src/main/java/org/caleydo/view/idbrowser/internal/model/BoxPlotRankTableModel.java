/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import gleem.linalg.Vec2f;

import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TableDoubleLists;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.ADoubleList;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.core.util.function.ArrayDoubleList;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementAccessor;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.view.histogram.v2.ListBoxAndWhiskersElement;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class BoxPlotRankTableModel extends ADataDomainRankTableModel implements IRankableColumnMixin {
	private double min;
	private double max;

	/**
	 * @param d
	 * @param dim
	 */
	public BoxPlotRankTableModel(ATableBasedDataDomain d, EDimension dim) {
		super(d, dim);
		assert DataSupportDefinitions.numericalTables.apply(d);
		final NumericalTable table = (NumericalTable) d.getTable();
		this.min = table.getMin();
		this.max = table.getMax();
	}

	public BoxPlotRankTableModel(TablePerspective t, EDimension dim) {
		super(t, dim);
		// compute local stats if we just have a table perspective
		DoubleStatistics stats = DoubleStatistics.of(TableDoubleLists.asRawList(t));
		this.min = stats.getMin();
		this.max = stats.getMax();
	}

	/**
	 * @param distributionRankTableModel
	 */
	public BoxPlotRankTableModel(BoxPlotRankTableModel clone) {
		super(clone);
		this.min = clone.min;
		this.max = clone.max;
	}

	@Override
	public ARankColumnModel clone() {
		return new BoxPlotRankTableModel(this);
	}

	@Override
	public String getValue(IRow row) {
		AdvancedDoubleStatistics stats = getStats(row);
		if (stats == null)
			return "";
		StringBuilder b = new StringBuilder();
		b.append("min: ").append(stats.getMin()).append(" max: ").append(stats.getMax());
		return b.toString();
	}

	AdvancedDoubleStatistics getStats(IRow row) {
		if (cache.containsKey(row.getIndex()))
			return (AdvancedDoubleStatistics) cache.get(row.getIndex());
		AdvancedDoubleStatistics c = computeStats((IIDRow) row);
		cache.put(row.getIndex(), c);
		return c;
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		AdvancedDoubleStatistics s1 = getStats(o1);
		AdvancedDoubleStatistics s2 = getStats(o2);
		if (s1 == s2)
			return 0;
		if (s1 == null)
			return 1;
		if (s2 == null)
			return -1;
		double sd1 = Math.abs(s1.getSd());
		double sd2 = Math.abs(s2.getSd());
		return Double.compare(sd1, sd2);
	}

	@Override
	public void orderByMe() {
		getParent().orderBy(this);
	}


	/**
	 * @param row
	 * @return
	 */
	private AdvancedDoubleStatistics computeStats(IIDRow row) {
		Set<Object> ids = row.get(getIDType());
		if (ids == null || ids.isEmpty())
			return null;
		final Table table = d.getTable();
		final int size = others.size() * ids.size();
		final List<Object> ids_l = Lists.newArrayList(ids);
		return AdvancedDoubleStatistics.of(new ADoubleList() {
			@Override
			public double getPrimitive(int index) {
				Integer oIndex = others.get(index / ids_l.size());
				Object iIndex = ids_l.get(index % ids_l.size());

				Object r;
				if (dim.isHorizontal()) {
					r = table.getRaw((Integer) iIndex, oIndex);
				} else {
					r = table.getRaw(oIndex, (Integer) iIndex);
				}
				if (r instanceof Number)
					return ((Number) r).doubleValue();
				return Double.NaN;
			}

			@Override
			public int size() {
				return size;
			}
		});
	}

	@Override
	public ValueElement createValue() {
		return new MyValueElement();
	}

	private class MyValueElement extends ValueElement implements IGLElementParent {
		// render as box and whiskers element
		private final ListBoxAndWhiskersElement content;
		private AdvancedDoubleStatistics old = null;

		/**
		 *
		 */
		public MyValueElement() {
			IDoubleList l = new ArrayDoubleList(new double[0]);
			content = new ListBoxAndWhiskersElement(l, EDetailLevel.LOW, EDimension.RECORD, false, false,
					d.getLabel(), Color.LIGHT_GRAY);
		}

		@Override
		public String getTooltip() {
			AdvancedDoubleStatistics stats = updateStats();
			if (stats == null)
				return null;
			return content.getTooltip();
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h, IRow row) {
			if (h < 1)
				return;
			AdvancedDoubleStatistics stats = updateStats();
			if (stats == null)
				return;
			content.render(g);
		}

		private AdvancedDoubleStatistics updateStats() {
			AdvancedDoubleStatistics stats = getStats(getRow());
			if (stats != old) {
				content.setData(stats, min, max);
				this.old = stats;
			}
			return stats;
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
			if (content != null) {
				GLElementAccessor.setParent(content, this);
				GLElementAccessor.init(content, context);
			}
		}

		@Override
		protected void takeDown() {
			if (content != null)
				GLElementAccessor.takeDown(content);
			super.takeDown();
		}

		@Override
		protected boolean hasPickAbles() {
			return true;
		}

		@Override
		public void layout(int deltaTimeMs) {
			super.layout(deltaTimeMs);
			if (content != null)
				content.layout(deltaTimeMs);
		}

		@Override
		protected void layoutImpl(int deltaTimeMs) {
			if (content != null) {
				Vec2f size = getSize();
				content.setBounds(0, 0, size.x(), size.y());
			}
			super.layoutImpl(deltaTimeMs);
		}

		@Override
		public boolean moved(GLElement child) {
			return false;
		}
	}
}
