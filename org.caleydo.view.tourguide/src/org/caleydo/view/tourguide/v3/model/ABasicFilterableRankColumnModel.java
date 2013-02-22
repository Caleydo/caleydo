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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.caleydo.view.tourguide.v3.model.mixin.IFilterColumnMixin;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ABasicFilterableRankColumnModel extends ABasicRankColumnModel implements IFilterColumnMixin {

	private final BitSet mask = new BitSet();
	private final BitSet maskInvalid = new BitSet();

	private final PropertyChangeListener listerner = new PropertyChangeListener() {
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

	@Override
	protected void init(RankTableModel table) {
		table.addPropertyChangeListener(RankTableModel.PROP_DATA, listerner);
		super.init(table);
	}

	@Override
	protected void takeDown(RankTableModel table) {
		table.removePropertyChangeListener(RankTableModel.PROP_DATA, listerner);
		super.takeDown(table);
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

	protected abstract void updateMask(BitSet todo, List<IRow> data, BitSet mask);
}
