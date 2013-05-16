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
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * Simple matrix based data provider for the body of a {@link NatTable}.
 *
 * @author Christian Partl
 *
 */
public class MatrixBasedBodyDataProvider implements IDataProvider {

	private String[][] dataMatrix;
	private int numColumns;

	public MatrixBasedBodyDataProvider(List<? extends List<String>> dataMatrix, int numDataTableColumns) {
		if (dataMatrix != null) {
			this.dataMatrix = new String[dataMatrix.size()][numDataTableColumns];
			for (int i = 0; i < dataMatrix.size(); i++) {
				for (int j = 0; j < numDataTableColumns; j++) {
					this.dataMatrix[i][j] = dataMatrix.get(i).get(j);
				}
			}
			this.numColumns = numDataTableColumns;
		}
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		return dataMatrix == null ? "" : dataMatrix[rowIndex][columnIndex];
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		// do not allow to change values
	}

	@Override
	public int getColumnCount() {
		return numColumns;
	}

	@Override
	public int getRowCount() {
		return dataMatrix == null ? 1 : dataMatrix.length;
	}

}
