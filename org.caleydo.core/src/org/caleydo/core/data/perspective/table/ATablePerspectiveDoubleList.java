/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.data.perspective.table;

import java.util.Iterator;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.virtualarray.VAIterator;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.function.ADoubleList;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.util.function.IDoubleSizedIterator;
import org.caleydo.core.util.function.Utils;

/**
 * the normalized table perspective data as an {@link IDoubleList}
 *
 * @author Samuel Gratzl
 *
 */
abstract class ATablePerspectiveDoubleList extends ADoubleList {
	protected final Table table;
	private final VirtualArray dim;
	private final VirtualArray rec;
	private final int size;

	public ATablePerspectiveDoubleList(TablePerspective tablePerspective) {
		this.table = tablePerspective.getDataDomain().getTable();
		this.dim = tablePerspective.getDimensionPerspective().getVirtualArray();
		this.rec = tablePerspective.getRecordPerspective().getVirtualArray();
		this.size = this.dim.size() * this.rec.size();
	}

	@Override
	public final double getPrimitive(int index) {
		// split in row / col
		int nrCols = dim.size();
		int recordIndex = index / nrCols;
		int dimIndex = index % nrCols;
		Integer recordID = rec.get(recordIndex);
		Integer dimensionID = dim.get(dimIndex);
		double v = getValue(dimensionID, recordID);
		return v;
	}

	protected abstract double getValue(Integer dimensionID, Integer recordID);

	@Override
	public final int size() {
		return size;
	}

	@Override
	public final IDoubleSizedIterator iterator() {
		if (isEmpty())
			return Utils.EMPTY;
		if (this.dim.size() == 1) // single col
			return new SingleItr(this.rec, this.dim.get(0));
		if (this.rec.size() == 1) // single row
			return new SingleItr(this.rec.get(0), this.dim);
		return new Itr();
	}

	private class SingleItr implements IDoubleSizedIterator {
		private final VAIterator va;
		private final Integer id;
		private final boolean singleIsDim;

		public SingleItr(VirtualArray rec, Integer dim) {
			this(rec, dim, true);
		}

		public SingleItr(Integer rec, VirtualArray dim) {
			this(dim, rec, false);
		}

		public SingleItr(VirtualArray va, Integer id, boolean singleIsDim) {
			this.va = va.iterator();
			this.id = id;
			this.singleIsDim = singleIsDim;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean hasNext() {
			return va.hasNext();
		}

		@Override
		public Double next() {
			return nextPrimitive();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public double nextPrimitive() {
			if (singleIsDim)
				return getValue(id, va.next());
			else
				return getValue(va.next(), id);
		}

	}

	private class Itr implements IDoubleSizedIterator {
		private final Iterator<Integer> recItr = rec.iterator();
		private Integer recID;
		private Iterator<Integer> dimItr = dim.iterator();

		@Override
		public int size() {
			return size;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Double next() {
			return nextPrimitive();
		}

		@Override
		public boolean hasNext() {
			// has more rows or last row isn't done
			return recItr.hasNext() || (dimItr != null && dimItr.hasNext());
		}

		@Override
		public double nextPrimitive() {
			if (recID == null && recItr.hasNext()) // init
				recID = recItr.next();
			double v = getValue(dimItr.next(), recID);
			if (!dimItr.hasNext()) {
				// shift
				if (!recItr.hasNext()) { // no more rows
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
