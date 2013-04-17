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
package org.caleydo.view.table;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;

/**
 * {@link IDataProvider} for accessing the data of a {@link TablePerspective}
 *
 * @author Marc Streit
 *
 */
public class DataProvider implements IRowDataProvider<Integer> {
	private final TablePerspective tablePerspective;
	private EDataRepresentation mode = EDataRepresentation.RAW;

	public DataProvider(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	/**
	 * @param mode
	 */
	public void setDataRepresentation(EDataRepresentation mode) {
		if (this.mode == mode)
			return;
		this.mode = mode;
	}

	/**
	 * @return the mode, see {@link #mode}
	 */
	public EDataRepresentation getDataRepresentation() {
		return mode;
	}

	@Override
	public int getColumnCount() {
		return tablePerspective.getNrDimensions();
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {

		final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		final VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		Integer recordID = recordVA.get(rowIndex);
		Integer dimensionID = dimensionVA.get(columnIndex);

		Table table = tablePerspective.getDataDomain().getTable();
		switch (mode) {
		case RAW:
			return table.getRaw(dimensionID, recordID);
		case NORMALIZED:
			return table.getNormalizedValue(dimensionID, recordID);
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return tablePerspective.getNrRecords();
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object value) {
		// not editing support
	}

	@Override
	public Integer getRowObject(int rowIndex) {
		return tablePerspective.getRecordPerspective().getVirtualArray().get(rowIndex);
	}

	public Integer getColumnObject(int columnIndex) {
		return tablePerspective.getDimensionPerspective().getVirtualArray().get(columnIndex);
	}

	@Override
	public int indexOfRowObject(Integer rowObject) {
		return tablePerspective.getRecordPerspective().getVirtualArray().indexOf(rowObject);
	}

	public int indexOfColumnObject(Integer columnObject) {
		return tablePerspective.getDimensionPerspective().getVirtualArray().indexOf(columnObject);
	}
}
