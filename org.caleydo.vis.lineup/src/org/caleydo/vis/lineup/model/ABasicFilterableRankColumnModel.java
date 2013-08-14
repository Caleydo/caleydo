/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;

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
				RankTableModel table = getTable();
				if (table != null) {
					@SuppressWarnings("unchecked")
					Collection<IRow> news = (Collection<IRow>) evt.getNewValue();
					maskInvalid.set(table.getDataSize() - news.size(), table.getDataSize());
				} else {
					// System.err.println();
				}
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
