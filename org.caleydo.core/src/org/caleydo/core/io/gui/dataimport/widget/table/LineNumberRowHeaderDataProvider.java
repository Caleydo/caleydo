/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.table;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * Simple data provider for header rows of a {@link NatTable} that displays the line number.
 *
 * @author Christian Partl
 *
 */
public class LineNumberRowHeaderDataProvider implements IDataProvider {

	private int numRows;

	public LineNumberRowHeaderDataProvider(int numHeaders) {
		this.numRows = numHeaders;
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		return "" + (rowIndex + 1);

	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		// not supported
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return numRows;
	}

	/**
	 * @param numRows
	 *            setter, see {@link numRows}
	 */
	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

}
