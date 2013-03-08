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
import java.util.Objects;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.internal.ui.MultiLineInputDialog;
import org.caleydo.vis.rank.internal.ui.TextRenderer;
import org.caleydo.vis.rank.model.mixin.IAnnotatedColumnMixin;
import org.caleydo.vis.rank.model.mixin.ICompressColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.detail.ScoreBarRenderer;
import org.caleydo.vis.rank.ui.detail.ScoreSummary;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * the stacked column
 *
 * @author Samuel Gratzl
 *
 */
public class StackedRankColumnModel extends AMultiRankColumnModel implements IHideableColumnMixin,
		IAnnotatedColumnMixin, ISnapshotableColumnMixin, ICompressColumnMixin {
	public static final String PROP_ALIGNMENT = "alignment";

	private final PropertyChangeListener weightChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onWeightChanged((float) evt.getNewValue() - (float) evt.getOldValue());
		}
	};
	/**
	 * which is the current aligned column index or -1 for all
	 */
	private int alignment = 0;
	private boolean isCompressed = false;
	private float compressedWidth = 100;

	private String annotation = "";

	public StackedRankColumnModel() {
		super(Color.GRAY, new Color(0.95f, .95f, .95f));
		setHeaderRenderer(new TextRenderer("SUM", this));
		setWeight(0);
	}

	public StackedRankColumnModel(StackedRankColumnModel copy) {
		super(copy);
		this.alignment = copy.alignment;
		this.annotation = copy.annotation;
		this.compressedWidth = copy.compressedWidth;
		this.isCompressed = copy.isCompressed;
		setHeaderRenderer(new TextRenderer("SUM", this));
		setWeight(0);
		cloneInitChildren();
	}

	@Override
	public StackedRankColumnModel clone() {
		return new StackedRankColumnModel(this);
	}

	protected void onWeightChanged(float delta) {
		addDirectWeight(delta);
	}

	@Override
	public float getPreferredWidth() {
		if (isCompressed)
			return compressedWidth;
		return getWeight() + RenderStyle.COLUMN_SPACE * size() + 6;
	}

	/**
	 * @return the annotation, see {@link #annotation}
	 */
	@Override
	public String getAnnotation() {
		return annotation;
	}

	protected void setAnnotation(String annotation) {
		propertySupport.firePropertyChange(PROP_ANNOTATION, this.annotation, this.annotation = annotation);
	}

	@Override
	public void editAnnotation(final GLElement summary) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				InputDialog d = new MultiLineInputDialog(null, "Edit Annotation of: " + getTooltip(),
						"Edit Annotation",
						annotation, null);
				if (d.open() == Window.OK) {
					String v = d.getValue().trim();
					if (v.length() == 0)
						v = null;
					EventPublisher.publishEvent(new FilterEvent(v).to(summary));
				}
			}
		});
	}

	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		model.addPropertyChangeListener(PROP_WEIGHT, weightChanged);
		addDirectWeight(model.getWeight());
	}

	private void addDirectWeight(float delta) {
		setWeight(getWeight() + delta);
	}

	@Override
	public ARankColumnModel addWeight(float delta) {
		if (isCompressed) {
			propertySupport.firePropertyChange(PROP_WEIGHT, this.compressedWidth, this.compressedWidth += delta);
			return this;
		}
		// uniformly distribute the weight to my children
		if (children.isEmpty())
			addDirectWeight(delta);
		else {
			float sum = getWeight();
			for (ARankColumnModel r : this) {
				float w = r.getWeight();
				float f = w / sum;
				if ((w + f * delta) <= 1) // abort invalid weight
					return this;
			}
			for (ARankColumnModel r : this) {
				float w = r.getWeight();
				float f = w / sum;
				r.addWeight(f * delta);
			}
		}
		return this;
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(PROP_WEIGHT, weightChanged);
		addDirectWeight(-model.getWeight());
		if (alignment > size() - 2) {
			setAlignment(alignment - 1);
		}
	}

	@Override
	public boolean canAdd(ARankColumnModel model) {
		return model instanceof IRankableColumnMixin && super.canAdd(model);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new MyElement(this, interactive);
	}

	@Override
	public GLElement createValue() {
		return new GLElement(new ScoreBarRenderer(this));
	}

	@Override
	public float applyPrimitive(IRow row) {
		float s = 0;
		for (ARankColumnModel col : this) {
			s += ((IRankableColumnMixin) col).applyPrimitive(row) * col.getWeight();
		}
		return s / getWeight();
	}

	@Override
	public MultiFloat getSplittedValue(IRow row) {
		float[] s = new float[this.size()];
		int i = 0;
		for (ARankColumnModel col : this) {
			s[i++] = ((IRankableColumnMixin) col).applyPrimitive(row) * col.getWeight();
		}
		return new MultiFloat(-1, s);
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

	/**
	 * returns the distributions how much a individual column contributes to the overall scores, i.e. the normalized
	 * weights
	 *
	 * @return
	 */
	public float[] getDistributions() {
		float[] r = new float[this.size()];
		float sum = 0;
		int i = 0;
		for (ARankColumnModel col : this) {
			sum += col.getWeight();
			r[i++] = col.getWeight();
		}
		for (i = 0; i < r.length; ++i)
			r[i] /= sum;
		return r;
	}

	public boolean isAlignAll() {
		return alignment < 0;
	}

	public void setAlignAll(boolean alignAll) {
		if (isAlignAll() == alignAll)
			return;
		this.setAlignment(-alignment - 1);
	}

	static class MyElement extends ScoreSummary {
		public MyElement(StackedRankColumnModel model, boolean interactive) {
			super(model, interactive);
		}

		@ListenTo(sendToMe = true)
		private void onSetAnnotation(FilterEvent event) {
			((StackedRankColumnModel) model).setAnnotation(Objects.toString(event.getFilter(), null));
		}
	}

	/**
	 * @return the isCompressed, see {@link #isCompressed}
	 */
	@Override
	public boolean isCompressed() {
		return isCompressed;
	}

	@Override
	public void setCompressed(boolean compressed) {
		this.propertySupport.firePropertyChange(PROP_COMPRESSED, this.isCompressed, this.isCompressed = compressed);
	}
}
