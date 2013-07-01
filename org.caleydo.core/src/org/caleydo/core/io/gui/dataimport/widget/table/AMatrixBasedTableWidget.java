/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.table;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for tables using {@link NatTable}s to display matrix data.
 *
 * @author Christian Partl
 *
 */
public abstract class AMatrixBasedTableWidget {

	protected NatTable table;

	protected Composite parent;

	protected MatrixBasedBodyDataProvider bodyDataProvider;

	public AMatrixBasedTableWidget(Composite parent) {
		this.parent = parent;
	}

	public abstract void createTableFromMatrix(List<List<String>> dataMatrix, int numColumns);


	public String getValue(int rowIndex, int columnIndex) {
		return (String) bodyDataProvider.getDataValue(columnIndex, rowIndex);
	}

	public int getColumnCount() {
		return bodyDataProvider.getColumnCount();
	}

	public int getRowCount() {
		return bodyDataProvider.getRowCount();
	}
}
