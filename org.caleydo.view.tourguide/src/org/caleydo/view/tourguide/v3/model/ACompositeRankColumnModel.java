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
package org.caleydo.view.tourguide.v3.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.view.tourguide.v3.model.mixin.IRankableColumnMixin;

import com.google.common.collect.Iterators;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ACompositeRankColumnModel extends ARankColumnModel implements Iterable<ARankColumnModel>,
		IRankableColumnMixin, IRankColumnParent {
	public static final String PROP_CHILDREN = "children";

	protected final List<ARankColumnModel> children = new ArrayList<>(2);

	public ACompositeRankColumnModel(Color color,
			Color bgColor) {
		super(color, bgColor);
	}

	protected boolean canAdd(ARankColumnModel model) {
		return true;
	}

	public final boolean add(ARankColumnModel model) {
		return add(size(), model);
	}

	public final boolean add(int index, ARankColumnModel model) {
		if (!canAdd(model))
			return false;
		init(model);
		this.children.add(index, model);
		propertySupport.fireIndexedPropertyChange(PROP_CHILDREN, index, null, model);
		return true;
	}

	public int indexOf(ARankColumnModel model) {
		return children.indexOf(model);
	}

	protected void init(ARankColumnModel model) {
		model.setParent(this);
		ARankColumnModel.uncollapse(model);
	}

	protected void takeDown(ARankColumnModel model) {
		model.setParent(null);
	}

	public ARankColumnModel get(int index) {
		return children.get(index);
	}

	public final void move(ARankColumnModel model, int to) {
		int from = this.children.indexOf(model);
		if (from == to)
			return;
		children.add(to, model);
		children.remove(from < to ? from : from + 1);
		propertySupport.fireIndexedPropertyChange(PROP_CHILDREN, to, from, model);
	}

	@Override
	public void replace(ARankColumnModel from, ARankColumnModel to) {
		int i = this.children.indexOf(from);
		children.set(i, to);
		init(to);
		propertySupport.fireIndexedPropertyChange(PROP_CHILDREN, i, from, to);
		takeDown(from);
	}

	public final void remove(ARankColumnModel model) {
		int i = this.children.indexOf(model);
		if (i < 0)
			return;
		children.remove(i);
		propertySupport.fireIndexedPropertyChange(PROP_CHILDREN, i, model, null);
		takeDown(model);
	}

	/**
	 * @return the children, see {@link #children}
	 */
	public final List<ARankColumnModel> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public final Iterator<ARankColumnModel> iterator() {
		return Iterators.unmodifiableIterator(children.iterator());
	}

	public final int size() {
		return children.size();
	}

	@Override
	public final boolean hide(ARankColumnModel model) {
		this.remove(model);
		getTable().addToPool(model);
		return true;
	}

	@Override
	public boolean isHideAble(ARankColumnModel model) {
		return children.size() > 1;
	}

	@Override
	public final boolean isDestroyAble(ARankColumnModel model) {
		return false;
	}

	@Override
	public final boolean isCollapseAble(ARankColumnModel model) {
		return false;
	}

	@Override
	public void detach(ARankColumnModel model) {
		remove(model);
	}


	@Override
	public void explode(ACompositeRankColumnModel model) {
		int index = this.children.indexOf(model);
		List<ARankColumnModel> children = model.getChildren();
		getTable().destroy(model);
		this.children.set(index, children.get(0));
		propertySupport.fireIndexedPropertyChange(PROP_CHILDREN, index, model, children.get(0));
		if (children.size() > 1) {
			this.children.addAll(index + 1, children.subList(1, children.size()));
			propertySupport.fireIndexedPropertyChange(PROP_CHILDREN, index + 1, null,
					children.subList(1, children.size()));
		}
	}

	public final Color[] getColors() {
		Color[] colors = new Color[size()];
		int i = 0;
		for (ARankColumnModel child : this)
			colors[i++] = child.getColor();
		return colors;
	}

	public final Histogram[] getHists(int bins) {
		Histogram[] hists = new Histogram[size()];
		int i = 0;
		for (ARankColumnModel child : this)
			hists[i++] = ((IRankableColumnMixin) child).getHist(bins);
		return hists;
	}

	@Override
	public final Histogram getHist(int bins) {
		Histogram hist = new Histogram(bins);
		for (IRow row : getTable()) {
			float value = getValue(row);
			if (value > 1)
				System.err.println();
			if (Float.isNaN(value))
				hist.addNAN(0);
			else
				hist.add(Math.round(value * (bins - 1)), 0);
		}
		return hist;
	}

}