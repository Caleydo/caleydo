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
import java.util.Collection;
import java.util.List;

import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ABasicFilterableRankColumnModel extends ARankColumnModel implements IHideableColumnMixin,
		ICollapseableColumnMixin, IFilterColumnMixin {

	private final BitSet mask = new BitSet();
	private final BitSet maskInvalid = new BitSet();
	protected boolean isGlobalFilter = false;

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

	public ABasicFilterableRankColumnModel(Color color, Color bgColor) {
		super(color, bgColor);
	}

	public ABasicFilterableRankColumnModel(ABasicFilterableRankColumnModel copy) {
		super(copy);
		this.mask.or(copy.mask);
		this.maskInvalid.or(copy.maskInvalid);
		this.isGlobalFilter = copy.isGlobalFilter;
	}

	@Override
	protected void init(IRankColumnParent table) {
		super.init(table);
		RankTableModel t = getTable();
		t.addPropertyChangeListener(RankTableModel.PROP_DATA, listener);
		maskInvalid.set(0, t.getDataSize());
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
		if (parent != null)
			maskInvalid.set(0, getTable().getDataSize());
	}

	@Override
	public abstract boolean isFiltered();

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

	protected abstract void updateMask(BitSet todo, List<IRow> data, BitSet mask);
}
