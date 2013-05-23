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
