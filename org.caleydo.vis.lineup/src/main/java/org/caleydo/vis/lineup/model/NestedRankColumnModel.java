/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IManualComparatorMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.detail.MultiRankScoreSummary;
import org.caleydo.vis.lineup.ui.detail.ScoreBarElement;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

import com.google.common.collect.Iterables;
import com.jogamp.common.util.IntObjectHashMap;

/**
 * the stacked column
 *
 * @author Samuel Gratzl
 *
 */
public class NestedRankColumnModel extends AMultiRankColumnModel implements ISnapshotableColumnMixin,
		ICompressColumnMixin, ICollapseableColumnMixin, IManualComparatorMixin {
	public static final String PROP_WEIGHTS = "weights";

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch(evt.getPropertyName()) {
			case PROP_WIDTH:
				onWeightChanged((ARankColumnModel) evt.getSource(), (float) evt.getOldValue(),
						(float) evt.getNewValue());
				break;
			case IFilterColumnMixin.PROP_FILTER:
			case IMappedColumnMixin.PROP_MAPPING:
				cacheMulti.clear();
				invalidAllFilter();
				propertySupport.firePropertyChange(evt);
				break;
			}
		}
	};
	private IntObjectHashMap cacheMulti = new IntObjectHashMap();
	private boolean isCompressed = false;
	private float compressedWidth = 100;

	public NestedRankColumnModel() {
		this(Color.GRAY, new Color(0.95f, .95f, .95f));
	}

	public NestedRankColumnModel(Color color, Color bgColor) {
		super(color, bgColor, "NESTED");
		width = +RenderStyle.STACKED_COLUMN_PADDING * 2;
	}

	public NestedRankColumnModel(NestedRankColumnModel copy) {
		super(copy);
		setHeaderRenderer(this);
		this.isCompressed = copy.isCompressed;
		this.compressedWidth = copy.compressedWidth;
		width = RenderStyle.STACKED_COLUMN_PADDING * 2;
		cloneInitChildren();
	}

	@Override
	public NestedRankColumnModel clone() {
		return new NestedRankColumnModel(this);
	}

	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		model.addPropertyChangeListener(PROP_WIDTH, listener);
		model.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.addPropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
		// addDirectWeight(model.getWeight());
		cacheMulti.clear();
		float oldWidth = size() == 1 ? (getSpaces() - RenderStyle.COLUMN_SPACE) : width;
		super.setWidth(oldWidth + model.getWidth() + RenderStyle.COLUMN_SPACE);
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(PROP_WIDTH, listener);
		model.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
		super.setWidth(width - model.getWidth() - RenderStyle.COLUMN_SPACE);
		cacheMulti.clear();
	}

	@Override
	protected void moved(int from, int to) {
		cacheMulti.clear();
		super.moved(from, to);
	}

	protected void onWeightChanged(ARankColumnModel child, float oldValue, float newValue) {
		super.setWidth(width + (newValue - oldValue));
	}

	private boolean isRecursive(ARankColumnModel model) {
		return false; // (model instanceof MaxCompositeRankColumnModel);
	}

	@Override
	public boolean canAdd(ARankColumnModel model) {
		return !isRecursive(model) && super.canAdd(model);
	}

	@Override
	public boolean isFlatAdding(ACompositeRankColumnModel model) {
		return isRecursive(model);
	}

	@Override
	public ARankColumnModel setWidth(float width) {
		if (isCompressed) {
			this.propertySupport.firePropertyChange(PROP_WIDTH, compressedWidth, this.compressedWidth = width);
			return this;
		}
		float shift = getSpaces();
		float factor = (width - shift) / (this.width - shift); // new / old
		for (ARankColumnModel col : this) {
			float wi = col.getWidth() * factor;
			col.removePropertyChangeListener(PROP_WIDTH, listener);
			col.setWidth(wi);
			col.addPropertyChangeListener(PROP_WIDTH, listener);
		}
		return super.setWidth(width);
	}

	@Override
	public float getWidth() {
		if (isCollapsed())
			return COLLAPSED_WIDTH;
		if (isCompressed)
			return compressedWidth;
		return super.getWidth();
	}
	@Override
	public GLElement createSummary(boolean interactive) {
		return new MultiRankScoreSummary(this, interactive);
	}

	@Override
	public ValueElement createValue() {
		return new ScoreBarElement(this);
	}

	@Override
	public double applyPrimitive(IRow row) {
		final int size = children.size();
		MultiDouble f = getSplittedValue(row);

		// strategy up to now return the primary value
		if (size == 0)
			return Double.NaN;
		return f.values[0];
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		final int size = children.size();
		MultiDouble f1 = getSplittedValue(o1);
		MultiDouble f2 = getSplittedValue(o2);
		for (int i = 0; i < size; ++i) {
			double a = f1.values[i];
			double b = f2.values[i];
			int c = -ColumnRanker.nanCompare(a, b, false);
			if (c != 0)
				return c;
		}
		return 0;
	}

	@Override
	public boolean isValueInferred(IRow row) {
		for (IDoubleRankableColumnMixin child : Iterables.filter(this, IDoubleRankableColumnMixin.class))
			if (child.isValueInferred(row))
				return true;
		return false;
	}

	@Override
	public MultiDouble getSplittedValue(IRow row) {
		if (cacheMulti.containsKey(row.getIndex()))
			return (MultiDouble) cacheMulti.get(row.getIndex());
		double[] s = new double[this.size()];
		for (int i = 0; i < s.length; ++i) {
			s[i] = ((IDoubleRankableColumnMixin) get(i)).applyPrimitive(row);
		}
		MultiDouble f = new MultiDouble(0, s);
		cacheMulti.put(row.getIndex(), f);
		return f;
	}

	@Override
	public void orderBy(IRankableColumnMixin child) {
		parent.orderBy(child);
	}


	/**
	 * @return
	 */
	private float getSpaces() {
		return RenderStyle.STACKED_COLUMN_PADDING * 2 + RenderStyle.COLUMN_SPACE * size();
	}

	@Override
	public void explode() {
		parent.explode(this);
	}

	/**
	 * @return the isCompressed, see {@link #isCompressed}
	 */
	@Override
	public boolean isCompressed() {
		return isCompressed || isCollapsed();
	}

	@Override
	public ICompressColumnMixin setCompressed(boolean compressed) {
		this.propertySupport.firePropertyChange(PROP_COMPRESSED, this.isCompressed, this.isCompressed = compressed);
		return this;
	}
}
