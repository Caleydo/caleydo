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
import java.util.BitSet;
import java.util.List;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.AFloatList;
import org.caleydo.core.util.function.FloatStatistics;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.data.IFloatInferrer;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.rank.ui.detail.ScoreBarElement;
import org.caleydo.vis.rank.ui.detail.ScoreSummary;
import org.caleydo.vis.rank.ui.detail.ValueElement;
import org.caleydo.vis.rank.ui.mapping.MappingFunctionUIs;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class FloatRankColumnModel extends ABasicFilterableRankColumnModel implements IMappedColumnMixin,
		IFloatRankableColumnMixin, ISnapshotableColumnMixin, IFilterColumnMixin {

	private SimpleHistogram cacheHist = null;
	private boolean dirtyDataStats = true;
	private float missingValue;
	private boolean filterNotMappedEntries = false;

	private final IMappingFunction mapping;
	private final IFloatInferrer missingValueInferer;

	private final IFloatFunction<IRow> data;
	private final ICallback<IMappingFunction> callback = new ICallback<IMappingFunction>() {
		@Override
		public void on(IMappingFunction data) {
			cacheHist = null;
			invalidAllFilter();
			propertySupport.firePropertyChange(PROP_MAPPING, null, data);
		}
	};
	private final PropertyChangeListener dataListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			dirtyDataStats = true;
			missingValue = Float.NaN;
		}
	};

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
		this.dirtyDataStats = copy.dirtyDataStats;
		this.cacheHist = copy.cacheHist;
	}

	@Override
	public FloatRankColumnModel clone() {
		return new FloatRankColumnModel(this);
	}

	@Override
	public void onRankingInvalid() {
		cacheHist = null;
		super.onRankingInvalid();
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyScoreSummary(this, interactive);
	}

	@Override
	public ValueElement createValue() {
		return new ScoreBarElement(this);
	}

	@Override
	public void editMapping(GLElement summary, IGLElementContext context) {
		GLElement m = MappingFunctionUIs.create(mapping, asRawData(), getColor(), getBgColor(), callback);
		m.setzDelta(0.5f);
		Vec2f location = summary.getAbsoluteLocation();
		Vec2f size = summary.getSize();
		context.getPopupLayer().show(m, new Vec4f(location.x(), location.y() + size.y(), 260, 300));
	}

	@Override
	protected void init(IRankColumnParent parent) {
		super.init(parent);
		getTable().addPropertyChangeListener(RankTableModel.PROP_DATA, dataListener);
		getTable().addPropertyChangeListener(RankTableModel.PROP_DATA_MASK, dataListener);
	}

	@Override
	protected void takeDown() {
		getTable().addPropertyChangeListener(RankTableModel.PROP_DATA, dataListener);
		getTable().addPropertyChangeListener(RankTableModel.PROP_DATA_MASK, dataListener);
		super.takeDown();
	}

	private IFloatList asRawData() {
		final List<IRow> data2 = getTable().getMaskedData();
		return new AFloatList() {
			@Override
			public float getPrimitive(int index) {
				return data.applyPrimitive(data2.get(index));
			}

			@Override
			public int size() {
				return data2.size();
			}
		};
	}

	private float computeMissingValue() {
		if (Float.isNaN(missingValue)) {
			IFloatList list = asRawData();
			missingValue = missingValueInferer.infer(list.iterator(), list.size());
		}
		return missingValue;
	}

	private void checkMapping() {
		if (dirtyDataStats) {
			FloatStatistics stats = FloatStatistics.compute(asRawData().iterator());
			mapping.setActStatistics(stats);
			dirtyDataStats = false;
			invalidAllFilter();
		}
	}

	@Override
	public Float apply(IRow row) {
		return applyPrimitive(row);
	}

	@Override
	public float applyPrimitive(IRow row) {
		float value = data.applyPrimitive(row);
		if (Float.isNaN(value))
			value = computeMissingValue();
		checkMapping();
		return mapping.apply(value);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return Float.compare(applyPrimitive(o1), applyPrimitive(o2));
	}

	@Override
	public void orderByMe() {
		parent.orderBy(this);
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
		return cacheHist = DataUtils.getHist(bins, getMyRanker().iterator(), this);
	}

	@Override
	public boolean isFiltered() {
		return filterNotMappedEntries;
	}

	/**
	 * @param filterNotMappedEntries
	 *            setter, see {@link filterNotMappedEntries}
	 */
	public void setFilterNotMappedEntries(boolean filterNotMappedEntries) {
		if (this.filterNotMappedEntries == filterNotMappedEntries)
			return;
		if (filterNotMappedEntries) {
			invalidAllFilter();
		}
		propertySupport.firePropertyChange(PROP_FILTER, this.filterNotMappedEntries,
				this.filterNotMappedEntries = filterNotMappedEntries);
	}

	@Override
	public void editFilter(final GLElement summary, IGLElementContext context) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				boolean f = MessageDialog.openQuestion(new Shell(), "Filter Not Mapped Entries?", "Do you want to filter entries that are not mapped?");
				EventPublisher.publishEvent(new FilterEvent(f).to(summary));
			}
		});
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> rows, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			float f = applyPrimitive(rows.get(i));
			mask.set(i, !Float.isNaN(f));
		}
	}

	static class MyScoreSummary extends ScoreSummary {
		public MyScoreSummary(IFloatRankableColumnMixin model, boolean interactive) {
			super(model, interactive);
		}

		@ListenTo(sendToMe = true)
		private void onFilterChanged(FilterEvent event) {
			boolean f = (Boolean) event.getFilter();
			((FloatRankColumnModel) model).setFilterNotMappedEntries(f);
		}

	}
}
