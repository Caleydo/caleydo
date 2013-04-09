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
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * {@link IDataProvider} for accessing the data of a {@link TablePerspective}
 *
 * @author Marc Streit
 *
 */
public class DataProvider implements IDataProvider {
	private final TablePerspective tablePerspective;
	private boolean isReturnRaw = true;

	public DataProvider(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	/**
	 * @param isReturnRaw
	 *            setter, see {@link isReturnRaw}
	 */
	public void setReturnRaw(boolean isReturnRaw) {
		this.isReturnRaw = isReturnRaw;
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
		if (isReturnRaw)
			return table.getRaw(dimensionID, recordID);
		else
			return table.getNormalizedValue(dimensionID, recordID);
	}

	@Override
	public int getRowCount() {
		return tablePerspective.getNrRecords();
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object value) {
		// not editing support
	}

	public String[] getColumnLabels() {
		String[] columnLabels = new String[tablePerspective.getNrDimensions()];
		ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();
		VirtualArray arr = tablePerspective.getDimensionPerspective().getVirtualArray();
		for (int i = 0; i < columnLabels.length; i++)
			columnLabels[i] = dataDomain.getDimensionLabel(arr.get(i));
		return columnLabels;
	}
}
