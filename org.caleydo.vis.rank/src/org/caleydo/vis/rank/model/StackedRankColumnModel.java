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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.util.function.FloatFunctions;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.detail.ScoreBarElement;
import org.caleydo.vis.rank.ui.detail.ScoreFilter2;
import org.caleydo.vis.rank.ui.detail.StackedScoreSummary;
import org.caleydo.vis.rank.ui.detail.ValueElement;

import com.google.common.collect.Iterables;
import com.jogamp.common.util.IntObjectHashMap;

/**
 * the stacked column
 *
 * @author Samuel Gratzl
 *
 */
public class StackedRankColumnModel extends AMultiRankColumnModel implements ISnapshotableColumnMixin,
		ICompressColumnMixin {
	public static final String PROP_ALIGNMENT = "alignment";
	public static final String PROP_WEIGHTS = "weights";

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
	 * which is the current aligned column index or -1 for all
	 */
	private int alignment = 0;
	private boolean isCompressed = false;
	private float compressedWidth = 100;

	/**
	 * if more than x percent of the the score is created by inferred values, filter it out
	 */
	private float filterInferredPercentage = 0.99f;
	private IntObjectHashMap cacheMulti = new IntObjectHashMap();

	public StackedRankColumnModel() {
		this(Color.GRAY, new Color(0.95f, .95f, .95f));
	}

	public StackedRankColumnModel(Color color, Color bgColor) {
		super(color, bgColor, "SUM");
		width = RenderStyle.COLUMN_SPACE;
	}

	public StackedRankColumnModel(StackedRankColumnModel copy) {
		super(copy);
		this.alignment = copy.alignment;
		this.isCompressed = copy.isCompressed;
		this.filterInferredPercentage = copy.filterInferredPercentage;
		setHeaderRenderer(this);
		width = RenderStyle.COLUMN_SPACE;
		cloneInitChildren();
	}

	@Override
	public StackedRankColumnModel clone() {
		return new StackedRankColumnModel(this);
	}

	@Override
	public boolean canAdd(ARankColumnModel model) {
		return !(model instanceof StackedRankColumnModel) && super.canAdd(model);
	}
	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		model.addPropertyChangeListener(PROP_WIDTH, listener);
		model.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.addPropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
		// addDirectWeight(model.getWeight());
		cacheMulti.clear();
		super.setWidth(width + model.getWidth() + RenderStyle.COLUMN_SPACE);
		model.setParentData(model.getWidth());
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(PROP_WIDTH, listener);
		model.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
		// addDirectWeight(-model.getWeight());
		if (alignment > size() - 2) {
			setAlignment(alignment - 1);
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

	@Override
	public ARankColumnModel setWidth(float width) {
		if (isCompressed) {
			this.propertySupport.firePropertyChange(PROP_WIDTH, compressedWidth, this.compressedWidth = width);
			return this;
		}
		float shift = (this.size() + 1) * RenderStyle.COLUMN_SPACE;
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
		return new StackedScoreSummary(this, interactive);
	}

	@Override
	public ValueElement createValue() {
		return new ScoreBarElement(this);
	}

	@Override
	public float applyPrimitive(IRow row) {
		float s = 0;
		final int size = children.size();
		MultiFloat f = getSplittedValue(row);
		float[] ws = this.getWeights();
		for (int i = 0; i < size; ++i) {
			float fi = f.values[i];
			if (Float.isNaN(fi))
				return Float.NaN;
			s += fi * ws[i];
		}
		return s;
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return Float.compare(applyPrimitive(o1), applyPrimitive(o2));
	}

	@Override
	public boolean isValueInferred(IRow row) {
		for (IFloatRankableColumnMixin child : Iterables.filter(this, IFloatRankableColumnMixin.class))
			if (child.isValueInferred(row))
				return true;
		return false;
	}

	@Override
	public MultiFloat getSplittedValue(IRow row) {
		if (cacheMulti.containsKey(row.getIndex()))
			return (MultiFloat) cacheMulti.get(row.getIndex());
		float[] s = new float[this.size()];
		for (int i = 0; i < s.length; ++i) {
			s[i] = ((IFloatRankableColumnMixin) get(i)).applyPrimitive(row);
		}
		MultiFloat f = new MultiFloat(-1, s);
		cacheMulti.put(row.getIndex(), f);
		return f;
	}

	/**
	 * @return the alignment, see {@link #alignment}
	 */
	public int getAlignment() {
		return alignment;
	}

	/**
	 * @param alignment
	 *            setter, see {@link alignment}
	 */
	public void setAlignment(int alignment) {
		if (alignment > this.children.size())
			alignment = this.children.size();
		if (alignment == this.alignment)
			return;
		propertySupport.firePropertyChange(PROP_ALIGNMENT, this.alignment, this.alignment = alignment);
	}

	@Override
	public void orderBy(IRankableColumnMixin child) {
		int index = indexOf((ARankColumnModel)child);
		if (alignment == index)
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
		float base = width - RenderStyle.COLUMN_SPACE * (size() + 1);
		int i = 0;
		for (ARankColumnModel col : this) {
			r[i++] = (float) col.getParentData() / base;
		}
		return r;
	}

	public void setWeights(float[] weights) {
		assert this.size() == weights.length;
		float sum = 0;
		for (float v : weights)
			sum += v;
		float factor = (width - RenderStyle.COLUMN_SPACE * (size() + 1)) / sum;
		int i = 0;
		for (ARankColumnModel col : this) {
			float w = weights[i++] * factor;
			col.setParentData(w);
		}
		propertySupport.firePropertyChange(PROP_WEIGHTS, null, weights);
	}

	public float getChildWidth(int i) {
		return (float) get(i).getParentData();
	}

	public boolean isAlignAll() {
		return alignment < 0;
	}

	public void setAlignAll(boolean alignAll) {
		if (isAlignAll() == alignAll)
			return;
		this.setAlignment(-alignment - 1);
	}

	@Override
	protected GLElement createEditFilterPopup(IFloatList data, GLElement summary) {
		return new ScoreFilter2(this, data, summary);
	}
	/**
	 * @return the filterInferredPercentage, see {@link #filterInferredPercentage}
	 */
	public float getFilterInferredPercentage() {
		return filterInferredPercentage;
	}

	/**
	 * @param filterInferredPercentage
	 *            setter, see {@link filterInferredPercentage}
	 */
	public void setFilterInferredPercentage(float filterInferredPercentage) {
		filterInferredPercentage = FloatFunctions.CLAMP01.apply(filterInferredPercentage);
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
		if (!super.filterEntry(row))
			return false;
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
	public void setCompressed(boolean compressed) {
		this.propertySupport.firePropertyChange(PROP_COMPRESSED, this.isCompressed, this.isCompressed = compressed);
	}

	@Override
	public boolean isFlatAdding(ACompositeRankColumnModel model) {
		return model instanceof StackedRankColumnModel;
	}

	@Override
	public void explode() {
		parent.explode(this);
	}
}
