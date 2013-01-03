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
package org.caleydo.core.data.collection.table;

import java.util.HashMap;
import java.util.ListIterator;

import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.VAIterator;

/**
 * Implementation of the iterator for sets considering a virtual array.
 * 
 * @author Alexander Lex
 */
public class DimensionIterator
	implements ListIterator<AColumn> {
	private VAIterator vaIterator;
	private HashMap<Integer, AColumn> dimensions;

	/**
	 * Constructor
	 * 
	 * @param set
	 * @param virtualArray
	 */
	public DimensionIterator(HashMap<Integer, AColumn> dimension, DimensionVirtualArray virtualArray) {
		this.vaIterator = virtualArray.iterator();
		this.dimensions = dimension;
	}

	@Override
	public void add(AColumn dimension) {
		vaIterator.add(dimension.getID());
	}

	@Override
	public boolean hasNext() {
		return vaIterator.hasNext();
	}

	@Override
	public boolean hasPrevious() {
		return vaIterator.hasPrevious();
	}

	@Override
	public AColumn next() {
		return dimensions.get(vaIterator.next());
	}

	@Override
	public int nextIndex() {
		return vaIterator.nextIndex();
	}

	@Override
	public AColumn previous() {
		return dimensions.get(vaIterator.previous());
	}

	@Override
	public int previousIndex() {
		return vaIterator.previousIndex();
	}

	@Override
	public void remove() {
		vaIterator.remove();
	}

	@Override
	public void set(AColumn dimension) {
		vaIterator.set(dimension.getID());
	}

}
