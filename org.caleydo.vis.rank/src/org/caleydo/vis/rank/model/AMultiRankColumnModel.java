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
import java.util.BitSet;
import java.util.Iterator;

import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AMultiRankColumnModel extends ACompositeRankColumnModel implements IMultiColumnMixin {
	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			propertySupport.firePropertyChange(evt);
		}
	};
	public AMultiRankColumnModel(Color color, Color bgColor) {
		super(color, bgColor);
	}

	public AMultiRankColumnModel(ACompositeRankColumnModel copy) {
		super(copy);
	}

	@Override
	protected void init(IRankColumnParent parent) {
		parent.addPropertyChangeListener(PROP_DATA, listener);
		parent.addPropertyChangeListener(PROP_INVALID, listener);
		super.init(parent);
	}

	@Override
	protected void takeDown() {
		parent.removePropertyChangeListener(PROP_DATA, listener);
		parent.removePropertyChangeListener(PROP_INVALID, listener);
		super.takeDown();
	}

	@Override
	public final Float apply(IRow row) {
		return applyPrimitive(row);
	}

	@Override
	public final SimpleHistogram getHist(int bins) {
		return DataUtils.getHist(bins, parent.getCurrentOrder(), this);
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
	public boolean isValueInferred(IRow row) {
		for(IRankableColumnMixin child : Iterables.filter(this, IRankableColumnMixin.class))
			if (child.isValueInferred(row))
				return true;
		return false;
	}

	@Override
	public final boolean[] isValueInferreds(IRow row) {
		boolean[] r = new boolean[size()];
		int i = 0;
		for(IRankableColumnMixin child : Iterables.filter(this, IRankableColumnMixin.class))
			r[i++] = child.isValueInferred(row);
		return r;
	}

	@Override
	public final SimpleHistogram[] getHists(int bins) {
		SimpleHistogram[] hists = new SimpleHistogram[size()];
		int i = 0;
		for (IRankableColumnMixin child : Iterables.filter(this, IRankableColumnMixin.class))
			hists[i++] = child.getHist(bins);
		return hists;
	}

	@Override
	public Iterator<IRow> getCurrentOrder() {
		return parent.getCurrentOrder();
	}

	@Override
	public BitSet getCurrentFilter() {
		return parent.getCurrentFilter();
	}

	@Override
	public int getCurrentSize() {
		return parent.getCurrentSize();
	}

	@Override
	public IRow getCurrent(int index) {
		return parent.getCurrent(index);
	}
}