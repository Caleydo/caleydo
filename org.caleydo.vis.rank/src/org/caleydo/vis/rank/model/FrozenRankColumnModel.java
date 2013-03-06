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
import java.text.DateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
public class FrozenRankColumnModel extends ACompositeRankColumnModel implements ICollapseableColumnMixin,
		IHideableColumnMixin {
	private boolean collapsed = false;
	private final int[] order;
	private final BitSet filter;

	private final PropertyChangeListener weightChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onWeightChanged((float) evt.getNewValue() - (float) evt.getOldValue());
		}
	};

	public FrozenRankColumnModel(int[] order, BitSet filter) {
		super(Color.GRAY, new Color(0.95f, 0.95f, 0.95f));
		this.order = order;
		this.filter = filter;
		setHeaderRenderer(GLRenderers.drawText(getNow(), VAlign.CENTER));
		setWeight(0);
	}

	public FrozenRankColumnModel(FrozenRankColumnModel copy) {
		super(copy);
		this.order = copy.order;
		this.filter = copy.filter;
		this.collapsed = copy.collapsed;
		float w = 0; // recompute as added and set
		for (ARankColumnModel c : this)
			w += c.getWeight();
		setWeight(w);
	}

	@Override
	public FrozenRankColumnModel clone() {
		return new FrozenRankColumnModel(this);
	}

	protected void onWeightChanged(float delta) {
		addWeight(delta);
	}

	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		model.addPropertyChangeListener(PROP_WEIGHT, weightChanged);
		addWeight(model.getWeight());
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(PROP_WEIGHT, weightChanged);
		addWeight(-model.getWeight());
	}

	private static String getNow() {
		return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.ENGLISH).format(new Date());
	}

	@Override
	public float getPreferredWidth() {
		if (isCollapsed())
			return COLLAPSED_WIDTH;
		float w = RenderStyle.FROZEN_BAND_WIDTH; // for the offsets
		for (ARankColumnModel c : this)
			w += c.getPreferredWidth() + RenderStyle.COLUMN_SPACE;
		return w;
	}

	@Override
	public IRow getCurrent(int rank) {
		return getTable().getData().get(order[rank]);
	}

	@Override
	public BitSet getCurrentFilter() {
		return filter;
	}

	@Override
	public boolean isHideAble(ARankColumnModel model) {
		return getTable().isHideAble(model); // can remove the last one
	}

	@Override
	public Iterator<IRow> getCurrentOrder() {
		final List<IRow> data = getTable().getData();
		return new Iterator<IRow>() {
			int cursor = 0;

			@Override
			public boolean hasNext() {
				return cursor < order.length;
			}

			@Override
			public IRow next() {
				return data.get(order[cursor++]);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int getCurrentSize() {
		return order.length;
	}


	@Override
	public GLElement createSummary(boolean interactive) {
		return new GLElement();
	}

	@Override
	public GLElement createValue() {
		return new GLElement();
	}

}
