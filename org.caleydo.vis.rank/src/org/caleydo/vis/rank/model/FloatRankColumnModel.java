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
import java.util.BitSet;
import java.util.List;

import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.AFloatList;
import org.caleydo.core.util.function.IFloatIterator;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.data.IFloatInferrer;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.rank.ui.detail.ScoreBarElement;
import org.caleydo.vis.rank.ui.detail.ScoreSummary;
import org.caleydo.vis.rank.ui.mapping.MappingFunctionUIs;

/**
 * @author Samuel Gratzl
 *
 */
public class FloatRankColumnModel extends ARankColumnModel implements IMappedColumnMixin, IRankableColumnMixin,
		ICollapseableColumnMixin, IHideableColumnMixin, ISnapshotableColumnMixin {
	private SimpleHistogram cacheHist = null;
	private boolean dirtyMinMax = true;
	private final IMappingFunction mapping;
	private float missingValue;
	private final IFloatInferrer missingValueInferer;

	private final IFloatFunction<IRow> data;
	private final ICallback<IMappingFunction> callback = new ICallback<IMappingFunction>() {
		@Override
		public void on(IMappingFunction data) {
			cacheHist = null;
			propertySupport.firePropertyChange(PROP_MAPPING, null, data);
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
		this.dirtyMinMax = copy.dirtyMinMax;
		this.cacheHist = copy.cacheHist;
	}

	@Override
	public FloatRankColumnModel clone() {
		return new FloatRankColumnModel(this);
	}

	@Override
	public void onRankingInvalid() {
		cacheHist = null;
		dirtyMinMax = true;
		missingValue = Float.NaN;
		super.onRankingInvalid();
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new ScoreSummary(this, interactive);
	}

	@Override
	public GLElement createValue() {
		return new ScoreBarElement(this);
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
		final ColumnRanker ranker = getMyRanker();
		final int size = ranker.size();
		return new AFloatList() {
			@Override
			public float getPrimitive(int index) {
				return data.applyPrimitive(ranker.get(index));
			}

			@Override
			public int size() {
				return size;
			}
		};
	}

	private IFloatIterator asDataIterator() {
		final List<IRow> data2 = getTable().getData();
		final BitSet filter = getMyRanker().getFilter();
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
			missingValue = missingValueInferer.infer(asDataIterator(), getMyRanker().size());
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
		return cacheHist = DataUtils.getHist(bins, getMyRanker().iterator(), this);
	}
}
