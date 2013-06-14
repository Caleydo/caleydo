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


import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.FloatFunctions;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISetableColumnMixin;
import org.caleydo.vis.rank.ui.detail.StarsSummary;
import org.caleydo.vis.rank.ui.detail.StarsValueElement;
import org.caleydo.vis.rank.ui.detail.ValueElement;

import com.jogamp.common.util.IntObjectHashMap;

/**
 * @author Samuel Gratzl
 *
 */
public class StarsRankColumnModel extends ARankColumnModel implements IFloatRankableColumnMixin, IHideableColumnMixin,
		ICollapseableColumnMixin,
		ISetableColumnMixin {
	private final int stars;
	private final IFloatFunction<IRow> data;
	private final IntObjectHashMap valueOverrides = new IntObjectHashMap(3);

	private final HistCache cacheHist = new HistCache();

	public StarsRankColumnModel(IFloatFunction<IRow> data, IGLRenderer header, Color color, Color bgColor, int stars) {
		super(color, bgColor);
		this.stars = stars;
		this.data = data;
		setHeaderRenderer(header);
	}

	public StarsRankColumnModel(StarsRankColumnModel copy) {
		super(copy);
		this.stars = copy.stars;
		this.data = copy.data;
		this.valueOverrides.putAll(copy.valueOverrides);
		setHeaderRenderer(copy.getHeaderRenderer());
	}

	@Override
	public StarsRankColumnModel clone() {
		return new StarsRankColumnModel(this);
	}

	/**
	 * @return the stars, see {@link #stars}
	 */
	public int getStars() {
		return stars;
	}

	@Override
	public void onRankingInvalid() {
		cacheHist.invalidate();
		super.onRankingInvalid();
	}

	@Override
	public Float apply(IRow row) {
		return applyPrimitive(row);
	}

	protected float map(float value, boolean handleNaNs) {
		if (Float.isNaN(value) && handleNaNs)
			return 0;
		return value;
	}

	public float getRaw(IRow in) {
		if (valueOverrides.containsKey(in.getIndex())) {
			return (Float) valueOverrides.get(in.getIndex());
		}
		return data.applyPrimitive(in);
	}

	@Override
	public boolean isOverriden(IRow row) {
		return valueOverrides.containsKey(row.getIndex());
	}

	@Override
	public String getOriginalValue(IRow row) {
		return format(data.applyPrimitive(row));
	}

	@Override
	public void set(IRow row, String value) {
		if (value == null) {
			valueOverrides.remove(row.getIndex());
		} else if (value.length() == 0) {
			valueOverrides.put(row.getIndex(), Float.NaN);
		} else {
			try {
				valueOverrides.put(row.getIndex(), Float.parseFloat(value));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		cacheHist.invalidate();
		propertySupport.firePropertyChange(IMappedColumnMixin.PROP_MAPPING, null, data);
	}

	@Override
	public String getValue(IRow row) {
		return format(getRaw(row));
	}

	private String format(float raw) {
		return Formatter.formatNumber(raw);
	}

	@Override
	public float applyPrimitive(IRow in) {
		float v = map(getRaw(in), true);
		v /= stars;
		return FloatFunctions.CLAMP01.apply(v);
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
	public GLElement createSummary(boolean interactive) {
		return new StarsSummary(this, interactive);
	}

	@Override
	public ValueElement createValue() {
		return new StarsValueElement(this);
	}


	@Override
	public boolean isValueInferred(IRow row) {
		float v = getRaw(row);
		return Float.isNaN(v);
	}

	@Override
	public SimpleHistogram getHist(float width) {
		return cacheHist.get(width, getMyRanker(), new AFloatFunction<IRow>() {
			@Override
			public float applyPrimitive(IRow in) {
				return map(getRaw(in), false);
			}
		});
	}
}
