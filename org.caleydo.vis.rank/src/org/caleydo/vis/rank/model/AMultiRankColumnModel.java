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
import java.util.Collection;
import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.AFloatList;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.internal.event.AnnotationEditEvent;
import org.caleydo.vis.rank.internal.ui.TitleDescriptionDialog;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IExplodeableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankColumnModel;
import org.caleydo.vis.rank.ui.detail.ScoreFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Iterables;
import com.google.common.primitives.Floats;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AMultiRankColumnModel extends ACompositeRankColumnModel implements IMultiColumnMixin,
		IExplodeableColumnMixin, IHideableColumnMixin, ICollapseableColumnMixin, IFilterColumnMixin, IGLRenderer {
	private final BitSet mask = new BitSet();
	private final BitSet maskInvalid = new BitSet();
	private float filterMin = 0;
	private float filterMax = 1;
	private boolean isGlobalFilter = false;
	private final HistCache cacheHist = new HistCache();

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case RankTableModel.PROP_DATA:
				@SuppressWarnings("unchecked")
				Collection<IRow> news = (Collection<IRow>) evt.getNewValue();
				maskInvalid.set(getTable().getDataSize() - news.size(), getTable().getDataSize());
				break;
			}
		}
	};
	private final String prefix;
	private String title = null;
	private String description = "";

	public AMultiRankColumnModel(Color color, Color bgColor, String prefix) {
		super(color, bgColor);
		this.prefix = prefix;
		setHeaderRenderer(this);
	}

	public AMultiRankColumnModel(AMultiRankColumnModel copy) {
		super(copy);
		this.prefix = copy.prefix;
		setHeaderRenderer(this);
		this.title = copy.title;
		this.description = copy.description;
		this.mask.or(copy.mask);
		this.maskInvalid.or(copy.maskInvalid);
		this.filterMin = copy.filterMin;
		this.filterMax = copy.filterMax;
		this.isGlobalFilter = copy.isGlobalFilter;
	}

	@Override
	protected void init(IRankColumnParent table) {
		super.init(table);
		RankTableModel t = getTable();
		t.addPropertyChangeListener(RankTableModel.PROP_DATA, listener);
		maskInvalid.set(0, t.getDataSize());
		cacheHist.invalidate();
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		cacheHist.invalidate();
		super.takeDown(model);
	}

	@Override
	protected void takeDown() {
		getTable().removePropertyChangeListener(RankTableModel.PROP_DATA, listener);
		super.takeDown();
	}

	/**
	 * @return the isGlobalFilter, see {@link #isGlobalFilter}
	 */
	@Override
	public boolean isGlobalFilter() {
		return isGlobalFilter;
	}

	/**
	 * @param isGlobalFilter
	 *            setter, see {@link isGlobalFilter}
	 */
	public void setGlobalFilter(boolean isGlobalFilter) {
		if (this.isGlobalFilter == isGlobalFilter)
			return;
		this.propertySupport.firePropertyChange(IFilterColumnMixin.PROP_FILTER, this.isGlobalFilter,
				this.isGlobalFilter = isGlobalFilter);
	}

	protected final void invalidAllFilter() {
		maskInvalid.set(0, getTable().getDataSize());
	}

	@Override
	public final void filter(List<IRow> data, BitSet mask) {
		if (!isFiltered())
			return;
		if (!maskInvalid.isEmpty()) {
			BitSet todo = (BitSet) maskInvalid.clone();
			todo.and(mask);
			updateMask(todo, data, this.mask);
			maskInvalid.andNot(todo);
		}
		mask.and(this.mask);
	}

	@Override
	public boolean isFiltered() {
		return filterMin > 0 || filterMax < 1;
	}

	/**
	 * @param filterNotMappedEntries
	 *            setter, see {@link filterNotMappedEntries}
	 */
	public void setFilter(float min, float max) {
		min = Math.max(0, min);
		max = Math.min(1, max);
		if (this.filterMin == min && this.filterMax == max)
			return;
		invalidAllFilter();
		float bak1 = filterMin;
		float bak2 = filterMax;
		this.filterMin = min;
		this.filterMax = max;
		propertySupport.firePropertyChange(PROP_FILTER, Pair.make(bak1, bak2), Pair.make(min, max));
	}

	/**
	 * @return the filterMin, see {@link #filterMin}
	 */
	public float getFilterMin() {
		return filterMin;
	}

	/**
	 * @return the filterMax, see {@link #filterMax}
	 */
	public float getFilterMax() {
		return filterMax;
	}

	@Override
	public void editFilter(final GLElement summary, IGLElementContext context) {
		// FIXME
		GLElement m = createEditFilterPopup(asRawData(), summary);
		m.setzDelta(0.5f);
		Vec2f location = summary.getAbsoluteLocation();
		Vec2f size = summary.getSize();
		context.getPopupLayer().show(m, new Vec4f(location.x(), location.y() + size.y(), 200, m.getSize().y()));
	}

	protected GLElement createEditFilterPopup(IFloatList data, GLElement summary) {
		return new ScoreFilter(this, data, summary);
	}

	private IFloatList asRawData() {
		final List<IRow> data2 = getTable().getFilteredData();
		return new AFloatList() {
			@Override
			public float getPrimitive(int index) {
				return applyPrimitive(data2.get(index));
			}

			@Override
			public int size() {
				return data2.size();
			}

			@Override
			public float[] toPrimitiveArray() {
				return Floats.toArray(this);
			}
		};
	}

	private void updateMask(BitSet todo, List<IRow> rows, BitSet mask) {
		for (int i = todo.nextSetBit(0); i >= 0; i = todo.nextSetBit(i + 1)) {
			mask.set(i, filterEntry(rows.get(i)));
		}
	}

	protected boolean filterEntry(IRow row) {
		float f = applyPrimitive(row);
		return !Float.isNaN(f) && f >= filterMin && f <= filterMax;
	}

	@Override
	public boolean canAdd(ARankColumnModel model) {
		return model instanceof IFloatRankableColumnMixin && super.canAdd(model);
	}

	@Override
	public final Float apply(IRow row) {
		return applyPrimitive(row);
	}

	@Override
	public String getValue(IRow row) {
		return Formatter.formatNumber(applyPrimitive(row));
	}

	@Override
	public void orderByMe() {
		parent.orderBy(this);
	}

	@Override
	public void explode() {
		parent.explode(this);
	}

	@Override
	public final SimpleHistogram getHist(float width) {
		return cacheHist.get(width, getMyRanker(), this);
	}

	@Override
	public final Color[] getColors() {
		Color[] colors = new Color[size()];
		int i = 0;
		for (ARankColumnModel child : this)
			colors[i++] = child.getColor();
		return colors;
	}

	@Override
	public void onRankingInvalid() {
		cacheHist.invalidate();
		super.onRankingInvalid();
	}

	@Override
	public final boolean[] isValueInferreds(IRow row) {
		boolean[] r = new boolean[size()];
		int i = 0;
		for(IFloatRankableColumnMixin child : Iterables.filter(this, IFloatRankableColumnMixin.class))
			r[i++] = child.isValueInferred(row);
		return r;
	}

	@Override
	public ColumnRanker getMyRanker(IRankColumnModel model) {
		return getMyRanker();
	}

	/**
	 * @return the description, see {@link #description}
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @param title
	 *            setter, see {@link title}
	 */
	@Override
	public void setTitle(String title) {
		if (title == null || title.isEmpty())
			title = "";
		propertySupport.firePropertyChange(PROP_TITLE, this.title, this.title = title);
	}

	/**
	 * @param description
	 *            setter, see {@link description}
	 */
	@Override
	public void setDescription(String description) {
		propertySupport.firePropertyChange(PROP_DESCRIPTION, this.description, this.description = description);
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.drawText(getTitle(), 0, 0, w, h, VAlign.CENTER);
	}

	@Override
	public String getTitle() {
		if (title == null || title.isEmpty()) {
			StringBuilder b = new StringBuilder(prefix).append(" (");
			for (ARankColumnModel r : this) {
				b.append(r.getTitle()).append(", ");
			}
			if (size() > 0)
				b.setLength(b.length() - 2);
			b.append(")");
			return b.toString();
		}
		return title;
	}

	@Override
	public void editAnnotation(final GLElement summary) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				String tori = getTitle();
				TitleDescriptionDialog d = new TitleDescriptionDialog(null, "Edit Label of: " + tori, tori, description);
				if (d.open() == Window.OK) {
					String t = d.getTitle().trim();
					String desc = d.getDescription().trim();
					EventPublisher.trigger(new AnnotationEditEvent(t, desc).to(summary));
				}
			}
		});
	}

}