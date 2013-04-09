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

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * TODO
 *
 * @author Marc Streit
 *
 */
public class DataProvider implements IDataProvider {

	TablePerspective tablePerspective;

	public DataProvider(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public int getColumnCount() {
		return tablePerspective.getNrDimensions();
	}

	@Override
	public Object getDataValue(int row, int col) {

		final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		final VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		Integer recordID = recordVA.get(row);
		Integer dimensionID = dimensionVA.get(col);

		return tablePerspective.getDataDomain().getTable().getNormalizedValue(dimensionID, recordID);
	}

	@Override
	public int getRowCount() {
		return tablePerspective.getNrRecords();
	}

	@Override
	public void setDataValue(int arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
	}

	public String[] getColumnLabels() {

		String[] columnLabels = new String[(tablePerspective.getNrDimensions())];
		for (int colIndex = 0; colIndex < columnLabels.length; colIndex++)
			columnLabels[colIndex] = tablePerspective.getDimensionPerspective().getLabel();
		return columnLabels;
	}
}
