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
