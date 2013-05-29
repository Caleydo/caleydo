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

import org.caleydo.core.io.ColumnDescription;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Table used to configure different data types (Numerical, categorical) for individual columns of an inhomogeneous
 * dataset.
 *
 * @author Christian Partl
 *
 */
public class ColumnConfigTableWidget {

	protected NatTable table;

	protected Composite parent;

	protected MatrixBasedBodyDataProvider bodyDataProvider;

	protected SelectionLayer selectionLayer;

	private class ColumnHeaderDataProvider implements IDataProvider {

		private List<String> rowOfColumnIDs;
		private List<ColumnDescription> columnDescriptions;

		public ColumnHeaderDataProvider(List<String> rowOfColumnIDs, List<ColumnDescription> columnDescriptions) {
			this.rowOfColumnIDs = rowOfColumnIDs;
			this.columnDescriptions = columnDescriptions;
		}

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {

			if (columnDescriptions == null || rowOfColumnIDs == null)
				return columnIndex + 1;

			if (rowIndex == 0) {
				ColumnDescription columnDescription = columnDescriptions.get(columnIndex);
				if (columnDescription.getDataDescription().getCategoricalClassDescription() != null) {
					return "Categorical";
				} else {
					return "Numerical";
				}
			} else {
				return rowOfColumnIDs.get(columnIndex);
			}
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		}

		@Override
		public int getColumnCount() {
			if (rowOfColumnIDs == null)
				return 1;
			return rowOfColumnIDs.size();
		}

		@Override
		public int getRowCount() {
			return 2;
		}

	}

	private class RowHeaderDataProvider implements IDataProvider {

		private List<String> columnOfRowIDs;

		public RowHeaderDataProvider(List<String> columnOfRowIDs) {
			this.columnOfRowIDs = columnOfRowIDs;
		}

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			if (columnOfRowIDs == null)
				return rowIndex + 1;
			return columnOfRowIDs.get(rowIndex);
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getRowCount() {
			if (columnOfRowIDs == null)
				return 1;
			return columnOfRowIDs.size();
		}

	}

	public ColumnConfigTableWidget(Composite parent) {
		this.parent = parent;
		bodyDataProvider = new MatrixBasedBodyDataProvider(null, 1);
		buildTable(bodyDataProvider, new ColumnHeaderDataProvider(null, null), new RowHeaderDataProvider(null));
	}

	private void buildTable(MatrixBasedBodyDataProvider bodyDataProvider, ColumnHeaderDataProvider columnDataProvider,
			RowHeaderDataProvider rowDataProvider) {

		if (table != null) { // cleanup old
			this.table.dispose();
			this.table = null;
		}

		final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		selectionLayer = new SelectionLayer(bodyDataLayer);
		ViewportLayer bodyLayer = new ViewportLayer(selectionLayer);

		final DataLayer columnDataLayer = new DataLayer(columnDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnDataLayer, bodyLayer, selectionLayer);

		DataLayer rowDataLayer = new DataLayer(rowDataProvider);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowDataLayer, bodyLayer, selectionLayer);

		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnDataProvider,
				rowDataProvider);
		CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

		GridLayer gridLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
		table = new NatTable(parent, gridLayer, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.heightHint = 300;
		gridData.widthHint = 800;
		table.setLayoutData(gridData);

		ColumnOverrideLabelAccumulator acc = new ColumnOverrideLabelAccumulator(columnHeaderLayer);
		columnHeaderLayer.setConfigLabelAccumulator(acc);
		acc.registerColumnOverrides(9, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 9);
		table.addConfiguration(new DefaultNatTableStyleConfiguration());

		table.configure();
	}

	public void createTableFromMatrix(List<List<String>> dataMatrix, List<String> rowOfColumnIDs,
			List<String> columnOfRowIDs, List<ColumnDescription> columnDescriptions) {
		if (dataMatrix == null || dataMatrix.isEmpty())
			return;

		bodyDataProvider = new MatrixBasedBodyDataProvider(dataMatrix, rowOfColumnIDs.size());
		buildTable(bodyDataProvider, new ColumnHeaderDataProvider(rowOfColumnIDs, columnDescriptions),
				new RowHeaderDataProvider(columnOfRowIDs));

	}

	/**
	 * @return index of the currently selected row, -1 if no row is selected.
	 */
	public int getSelectedRow() {
		PositionCoordinate[] positions = selectionLayer.getSelectedCellPositions();
		if (positions.length >= 1)
			return positions[0].rowPosition;
		return -1;
	}

	/**
	 * @return index of the currently selected column, -1 if no row is selected.
	 */
	public int getSelectedColumn() {
		PositionCoordinate[] positions = selectionLayer.getSelectedCellPositions();
		if (positions.length >= 1)
			return positions[0].columnPosition;
		return -1;
	}

	public void update() {
		table.refresh();
	}

}
