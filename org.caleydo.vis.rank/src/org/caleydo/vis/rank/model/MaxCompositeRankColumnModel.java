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

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.detail.MaxScoreSummary;
import org.caleydo.vis.rank.ui.detail.MultiScoreBarElement;
import org.caleydo.vis.rank.ui.detail.ValueElement;

/**
 * @author Samuel Gratzl
 *
 */
public class MaxCompositeRankColumnModel extends AMultiRankColumnModel implements ICollapseableColumnMixin,
		IFilterColumnMixin {

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case IFilterColumnMixin.PROP_FILTER:
			case IMappedColumnMixin.PROP_MAPPING:
				invalidAllFilter();
				propertySupport.firePropertyChange(evt);
				break;
			}
		}
	};

	public MaxCompositeRankColumnModel() {
		this(Color.GRAY, new Color(0.95f, .95f, .95f));
	}

	public MaxCompositeRankColumnModel(Color color, Color bgColor) {
		super(color, bgColor, "MAX");
	}

	public MaxCompositeRankColumnModel(MaxCompositeRankColumnModel copy) {
		super(copy);
		cloneInitChildren();
	}

	@Override
	public MaxCompositeRankColumnModel clone() {
		return new MaxCompositeRankColumnModel(this);
	}

	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		model.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.addPropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MaxScoreSummary(this, interactive);
	}

	@Override
	public ValueElement createValue() {
		return new RepaintingGLElement();
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
	public MultiFloat getSplittedValue(IRow row) {
		if (children.isEmpty())
			return new MultiFloat(-1);
		float max = Float.NEGATIVE_INFINITY;
		int maxIndex = -1;
		float[] vs = new float[size()];
		int i = 0;
		for (ARankColumnModel col : this) {
			float v = ((IFloatRankableColumnMixin) col).applyPrimitive(row);
			vs[i++] = v;
			if (!Float.isNaN(v) && v > max) {
				maxIndex = i - 1;
				max = v;
			}
		}
		return new MultiFloat(maxIndex, vs);
	}

	@Override
	public float applyPrimitive(IRow row) {
		if (children.isEmpty())
			return 0;
		float max = Float.NEGATIVE_INFINITY;
		for (ARankColumnModel col : this) {
			float v = ((IFloatRankableColumnMixin) col).applyPrimitive(row);
			if (Float.isNaN(v))
				continue;
			max = Math.max(max, v);
		}
		if (max == Float.NEGATIVE_INFINITY) {
			max = Float.NaN;
		}
		return max;
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return Float.compare(applyPrimitive(o1), applyPrimitive(o2));
	}

	@Override
	public boolean isValueInferred(IRow row) {
		int repr = getSplittedValue(row).repr;
		if (repr < 0)
			return false;
		return (((IFloatRankableColumnMixin) get(repr)).isValueInferred(row));
	}

	private class RepaintingGLElement extends MultiScoreBarElement {
		private final PropertyChangeListener l = GLPropertyChangeListeners.repaintOnEvent(this);

		public RepaintingGLElement() {
			super(MaxCompositeRankColumnModel.this);
		}
		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
			addPropertyChangeListener(PROP_CHILDREN, l);
		}

		@Override
		protected void takeDown() {
			removePropertyChangeListener(PROP_CHILDREN, l);
			super.takeDown();
		}
	}

	@Override
	public void orderBy(IRankableColumnMixin model) {
		// nothing to do
	}
}
