/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.data.perspective.table;

import java.util.Iterator;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.function.AFloatList;
import org.caleydo.core.util.function.IFloatIterator;
import org.caleydo.core.util.function.IFloatList;

/**
 * the normalized table perspective data as an {@link IFloatList}
 *
 * @author Samuel Gratzl
 *
 */
public class TablePerspectiveFloatList extends AFloatList {
	private final Table table;
	private final VirtualArray dim;
	private final VirtualArray rec;
	private final int size;

	public TablePerspectiveFloatList(TablePerspective tablePerspective) {
		this.table = tablePerspective.getDataDomain().getTable();
		this.dim = tablePerspective.getDimensionPerspective().getVirtualArray();
		this.rec = tablePerspective.getRecordPerspective().getVirtualArray();
		this.size = this.dim.size() * this.rec.size();
	}

	@Override
	public float getPrimitive(int index) {
		// split in row / col
		int nrCols = dim.size();
		int recordIndex = index / nrCols;
		int dimIndex = index % nrCols;
		Integer recordID = rec.get(recordIndex);
		Integer dimensionID = dim.get(dimIndex);
		float v = table.getNormalizedValue(dimensionID, recordID);
		return v;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public IFloatIterator iterator() {
		return new Itr();
	}

	private class Itr implements IFloatIterator {
		private final Iterator<Integer> recItr = rec.iterator();
		private Integer recID;
		private Iterator<Integer> dimItr = dim.iterator();

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Float next() {
			return nextPrimitive();
		}

		@Override
		public boolean hasNext() {
			return recItr.hasNext() && dimItr != null && dimItr.hasNext();
		}

		@Override
		public float nextPrimitive() {
			if (recID == null && recItr.hasNext())
				recID = recItr.next();
			float v = table.getNormalizedValue(dimItr.next(), recID);
			if (!dimItr.hasNext()) {
				// shift
				if (!recItr.hasNext()) {
					dimItr = null;
					recID = null;
				} else {
					recID = recItr.next();
					dimItr = dim.iterator();
				}
			}
			return v;
		}
	}

}
