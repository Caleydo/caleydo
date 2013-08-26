/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.detail.MultiRankScoreSummary;
import org.caleydo.vis.lineup.ui.detail.ScoreBarElement;
import org.caleydo.vis.lineup.ui.detail.ScoreFilter2;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

import com.google.common.collect.Iterables;
import com.jogamp.common.util.IntObjectHashMap;

/**
 * the stacked column
 *
 * @author Samuel Gratzl
 *
 */
public final class StackedRankColumnModel extends AMultiRankColumnModel implements ISnapshotableColumnMixin,
		ICompressColumnMixin, ICollapseableColumnMixin, IFilterColumnMixin, Cloneable {
	public static final String PROP_ALIGNMENT = "alignment";
	public static final String PROP_WEIGHTS = "weights";

	public enum Alignment {
		ALL, ORDERED, SINGLE
	}

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch(evt.getPropertyName()) {
			case PROP_WIDTH:
				cacheMulti.clear();
				invalidAllFilter();
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

	/**
	 * which is the current aligned column index or ALIGN_ALL for all
	 */
	private Alignment alignment = Alignment.SINGLE;
	private int alignmentDetails = 0;
	private boolean isCompressed = false;
	private float compressedWidth = 100;

	/**
	 * if more than x percent of the the score is created by inferred values, filter it out
	 */
	private double filterInferredPercentage = 0.99f;
	private IntObjectHashMap cacheMulti = new IntObjectHashMap();

	public StackedRankColumnModel() {
		this(Color.GRAY, new Color(0.95f, .95f, .95f));
	}

	public StackedRankColumnModel(Color color, Color bgColor) {
		super(color, bgColor, "SUM");
		width = +RenderStyle.STACKED_COLUMN_PADDING * 2;
	}

	public StackedRankColumnModel(StackedRankColumnModel copy) {
		super(copy);
		this.alignment = copy.alignment;
		this.compressedWidth = copy.compressedWidth;
		this.isCompressed = copy.isCompressed;
		this.filterInferredPercentage = copy.filterInferredPercentage;
		setHeaderRenderer(this);
		width = RenderStyle.STACKED_COLUMN_PADDING * 2;
		cloneInitChildren();
	}

	@Override
	public StackedRankColumnModel clone() {
		return new StackedRankColumnModel(this);
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
		model.setParentData(model.getWidth());
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(PROP_WIDTH, listener);
		model.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
		// addDirectWeight(-model.getWeight());
		if (alignmentDetails > size() - 2) {
			setAlignment(alignmentDetails - 1);
		}
		super.setWidth(width - model.getWidth() - RenderStyle.COLUMN_SPACE);
		model.setParentData(null);
		cacheMulti.clear();
	}

	@Override
	protected void moved(int from, int to) {
		cacheMulti.clear();
		super.moved(from, to);
	}

	protected void onWeightChanged(ARankColumnModel child, float oldValue, float newValue) {
		child.setParentData(newValue);
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
			float wi = ((float) col.getParentData()) * factor;
			col.setParentData(wi);
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
		float s = 0;
		final int size = children.size();
		MultiDouble f = getSplittedValue(row);
		float[] ws = this.getWeights();
		for (int i = 0; i < size; ++i) {
			double fi = f.values[i];
			if (Double.isNaN(fi))
				return Double.NaN;
			s += fi * ws[i];
		}
		return s;
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return Double.compare(applyPrimitive(o1), applyPrimitive(o2));
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
		MultiDouble f = new MultiDouble(-1, s);
		cacheMulti.put(row.getIndex(), f);
		return f;
	}

	/**
	 * @return the alignment, see {@link #alignment}
	 */
	/**
	 * @return the alignment, see {@link #alignment}
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	public int getSingleAlignment() {
		return alignment != Alignment.SINGLE ? -1 : alignmentDetails;
	}

	/**
	 * @param alignment
	 *            setter, see {@link alignment}
	 */
	public void setAlignment(int alignment) {
		if (alignment > this.children.size())
			alignment = this.children.size();
		if (alignmentDetails == alignment && this.alignment == Alignment.SINGLE)
			return;
		this.alignment = Alignment.SINGLE;
		propertySupport.firePropertyChange(PROP_ALIGNMENT, this.alignmentDetails, this.alignmentDetails = alignment);
	}

	/**
	 *
	 */
	public void switchToNextAlignment() {
		setAlignment(Alignment.values()[(alignment.ordinal() + 1) % (Alignment.values().length)]);
	}

	public void setAlignment(Alignment alignment) {
		if (this.alignment == alignment)
			return;
		propertySupport.firePropertyChange(PROP_ALIGNMENT, this.alignment, this.alignment = alignment);
	}

	@Override
	public void orderBy(IRankableColumnMixin child) {
		int index = indexOf((ARankColumnModel)child);
		if (alignmentDetails == index && alignment == Alignment.SINGLE)
			setAlignment(index + 1);
		else
			setAlignment(index);
	}

	/**
	 * returns the weights how much a individual column contributes to the overall scores, i.e. the normalized weights
	 *
	 * @return
	 */
	public float[] getWeights() {
		float[] r = new float[this.size()];
		float base = width - getSpaces();
		int i = 0;
		for (ARankColumnModel col : this) {
			r[i++] = (float) col.getParentData() / base;
		}
		return r;
	}

	/**
	 * @return
	 */
	private float getSpaces() {
		return RenderStyle.STACKED_COLUMN_PADDING * 2 + RenderStyle.COLUMN_SPACE * size();
	}

	public void setWeights(float[] weights) {
		assert this.size() == weights.length;
		float sum = 0;
		for (float v : weights)
			sum += v;
		float factor = (width - getSpaces()) / sum;
		int i = 0;
		for (ARankColumnModel col : this) {
			float w = weights[i++] * factor;
			col.setParentData(w);
			col.setWidthImpl(w);
		}
		propertySupport.firePropertyChange(PROP_WEIGHTS, null, weights);
	}

	/**
	 * sorts the children decreasing by their weight
	 */
	public void sortByWeights() {
		cacheMulti.clear();
		sortBy(new Comparator<ARankColumnModel>() {
			@Override
			public int compare(ARankColumnModel o1, ARankColumnModel o2) {
				// return -Float.compare((float) o1.getParentData(), (float) o2.getParentData());
				return Float.compare((float) o2.getParentData(), (float) o1.getParentData());
			}
		});
	}

	public float getChildWidth(int i) {
		return (float) get(i).getParentData();
	}

	@Override
	protected GLElement createEditFilterPopup(IDoubleList data, GLElement summary) {
		return new ScoreFilter2(this, data, summary);
	}
	/**
	 * @return the filterInferredPercentage, see {@link #filterInferredPercentage}
	 */
	public double getFilterInferredPercentage() {
		return filterInferredPercentage;
	}

	/**
	 * @param filterInferredPercentage
	 *            setter, see {@link filterInferredPercentage}
	 */
	public void setFilterInferredPercentage(double filterInferredPercentage) {
		filterInferredPercentage = DoubleFunctions.CLAMP01.apply(filterInferredPercentage);
		if (this.filterInferredPercentage == filterInferredPercentage)
			return;
		invalidAllFilter();
		propertySupport.firePropertyChange(PROP_FILTER, this.filterInferredPercentage,
				this.filterInferredPercentage = filterInferredPercentage);
	}

	@Override
	public boolean isFiltered() {
		return super.isFiltered() || filterInferredPercentage < 1;
	}

	@Override
	protected boolean filterEntry(IRow row) {
		if (filterInferredPercentage >= 1)
			return super.filterEntry(row);
		if (!super.filterEntry(row)) {
			return false;
		}
		boolean[] inferreds = isValueInferreds(row);
		boolean any = false;
		for (int i = 0; i < inferreds.length; ++i) {
			if (inferreds[i]) {
				any = true;
			}
		}
		if (!any)
			return true;
		float[] ws = getWeights();
		float inferPercentage = 0;
		for (int i = 0; i < inferreds.length; ++i) {
			if (inferreds[i])
				inferPercentage += ws[i];
		}
		return inferPercentage < filterInferredPercentage;
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


	@Override
	public void explode() {
		parent.explode(this);
	}
}
