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
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;

/**
 * @author Samuel Gratzl
 *
 */
public class FreezedRankColumnModel extends ACompositeRankColumnModel implements ICollapseableColumnMixin,
		IHideableColumnMixin {
	private boolean collapsed = false;
	private final int[] order;
	private final BitSet filter;

	public FreezedRankColumnModel(int[] order, BitSet filter) {
		super(Color.GRAY, new Color(0.95f, 0.95f, 0.95f));
		this.order = order;
		this.filter = filter;
	}

	public FreezedRankColumnModel(FreezedRankColumnModel copy) {
		super(copy);
		this.order = copy.order;
		this.filter = copy.filter;
		this.collapsed = copy.collapsed;
	}

	@Override
	public FreezedRankColumnModel clone() {
		return new FreezedRankColumnModel(this);
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
