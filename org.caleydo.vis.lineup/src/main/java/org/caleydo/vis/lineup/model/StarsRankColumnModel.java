/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;


import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.data.IDoubleFunction;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ISetableColumnMixin;
import org.caleydo.vis.lineup.ui.detail.StarsSummary;
import org.caleydo.vis.lineup.ui.detail.StarsValueElement;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

import com.jogamp.common.util.IntObjectHashMap;

/**
 * @author Samuel Gratzl
 *
 */
public final class StarsRankColumnModel extends ARankColumnModel implements IDoubleRankableColumnMixin,
		IHideableColumnMixin, ICollapseableColumnMixin, Cloneable,
		ISetableColumnMixin {
	private final int stars;
	private final IDoubleFunction<IRow> data;
	private final IntObjectHashMap valueOverrides = new IntObjectHashMap(3);

	private final HistCache cacheHist = new HistCache();

	public StarsRankColumnModel(IDoubleFunction<IRow> data, IGLRenderer header, Color color, Color bgColor, int stars) {
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
	public Double apply(IRow row) {
		return applyPrimitive(row);
	}

	protected double map(double value, boolean handleNaNs) {
		if (Double.isNaN(value) && handleNaNs)
			return 0;
		return value;
	}

	public double getRaw(IRow in) {
		if (valueOverrides.containsKey(in.getIndex())) {
			return (Double) valueOverrides.get(in.getIndex());
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

	private String format(double raw) {
		return Formatter.formatNumber(raw);
	}

	@Override
	public double applyPrimitive(IRow in) {
		double v = map(getRaw(in), true);
		v /= stars;
		return DoubleFunctions.CLAMP01.apply(v);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return Double.compare(applyPrimitive(o1), applyPrimitive(o2));
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
		double v = getRaw(row);
		return Double.isNaN(v);
	}

	@Override
	public SimpleHistogram getHist(float width) {
		return cacheHist.get(width, getMyRanker(), new ADoubleFunction<IRow>() {
			@Override
			public double applyPrimitive(IRow in) {
				return map(getRaw(in), false);
			}
		});
	}
}
