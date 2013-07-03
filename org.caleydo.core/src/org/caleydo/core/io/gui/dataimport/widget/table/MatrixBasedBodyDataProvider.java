/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.table;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * Simple matrix based data provider for the body of a {@link NatTable}.
 *
 * @author Christian Partl
 *
 */
public class MatrixBasedBodyDataProvider implements IDataProvider {

	private List<List<String>> dataMatrix;
	private int numColumns;

	public MatrixBasedBodyDataProvider(List<List<String>> dataMatrix, int numDataTableColumns) {
		if (dataMatrix != null) {
			this.dataMatrix = dataMatrix;
			this.numColumns = numDataTableColumns;
		}
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		return dataMatrix == null ? "" : dataMatrix.get(rowIndex).get(columnIndex);
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		dataMatrix.get(rowIndex).set(columnIndex, (String) newValue);
	}

	@Override
	public int getColumnCount() {
		return numColumns;
	}

	@Override
	public int getRowCount() {
		return dataMatrix == null ? 1 : dataMatrix.size();
	}

	/**
	 * @return the dataMatrix, see {@link #dataMatrix}
	 */
	public List<List<String>> getDataMatrix() {
		return dataMatrix;
	}

	/**
	 * @param dataMatrix
	 *            setter, see {@link dataMatrix}
	 */
	public void setDataMatrix(List<List<String>> dataMatrix) {
		this.dataMatrix = dataMatrix;
	}

	/**
	 * @param numColumns
	 *            setter, see {@link numColumns}
	 */
	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}
}
