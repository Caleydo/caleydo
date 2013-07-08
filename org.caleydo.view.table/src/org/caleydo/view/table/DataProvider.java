/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
