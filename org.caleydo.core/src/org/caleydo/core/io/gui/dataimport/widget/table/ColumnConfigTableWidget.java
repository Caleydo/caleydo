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
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
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
public class ColumnConfigTableWidget extends AMatrixBasedTableWidget {

	private class ColumnHeaderDataProvider implements IDataProvider {

		private int numColumns;

		public ColumnHeaderDataProvider(int numColumns) {
			this.numColumns = numColumns;
		}

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			return columnIndex + 1;
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		}

		@Override
		public int getColumnCount() {
			return numColumns;
		}

		@Override
		public int getRowCount() {
			return 1;
		}

	}

	public ColumnConfigTableWidget(Composite parent) {
		super(parent);
		bodyDataProvider = new MatrixBasedBodyDataProvider(null, 1);
		buildTable(bodyDataProvider, new ColumnHeaderDataProvider(1), new LineNumberRowHeaderDataProvider(1));
	}

	private void buildTable(MatrixBasedBodyDataProvider bodyDataProvider, ColumnHeaderDataProvider columnDataProvider,
			LineNumberRowHeaderDataProvider rowDataProvider) {

		if (table != null) { // cleanup old
			this.table.dispose();
			this.table = null;
		}

		final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
		ViewportLayer bodyLayer = new ViewportLayer(selectionLayer);

		final DataLayer columnDataLayer = new DataLayer(columnDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnDataLayer, bodyLayer, selectionLayer);

		DataLayer rowDataLayer = new DataLayer(rowDataProvider, 50, 20);
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

		// IConfigLabelAccumulator cellLabelAccumulator = new IConfigLabelAccumulator() {
		// @Override
		// public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
		// if (columnPosition == idColumnIndex || rowPosition == idRowIndex) {
		// configLabels.addLabel(ID_CELL);
		// }
		// if (rowPosition < numberOfHeaderRows) {
		// configLabels.addLabel(HEADER_LINE_CELL);
		// }
		// }
		// };

		// bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);
		ColumnOverrideLabelAccumulator acc = new ColumnOverrideLabelAccumulator(columnHeaderLayer);
		columnHeaderLayer.setConfigLabelAccumulator(acc);
		acc.registerColumnOverrides(9, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 9);
		table.addConfiguration(new DefaultNatTableStyleConfiguration());
		// table.addConfiguration(new AbstractRegistryConfiguration() {
		// @Override
		// public void configureRegistry(IConfigRegistry configRegistry) {
		// Style cellStyle = new Style();
		//
		// cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_GREEN);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
		// ID_CELL);
		//
		// cellStyle = new Style();
		// cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
		// HEADER_LINE_CELL);
		// }
		// });

		// final ColumnHeaderCheckBoxPainter columnHeaderCheckBoxPainter = new
		// ColumnHeaderCheckBoxPainter(columnDataLayer);
		// final ICellPainter column9HeaderPainter = new BeveledBorderDecorator(columnHeaderCheckBoxPainter);
		// final ICellPainter column9HeaderPainter = new BeveledBorderDecorator(new CellPainterDecorator(
		// new TextPainter(), CellEdgeEnum.RIGHT, columnHeaderCheckBoxPainter));
		// table.addConfiguration(new AbstractRegistryConfiguration() {
		// @Override
		// public void configureRegistry(IConfigRegistry configRegistry) {
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, column9HeaderPainter,
		// DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);
		// }
		//
		// @Override
		// public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		// uiBindingRegistry.registerFirstSingleClickBinding(new CellPainterMouseEventMatcher(
		// GridRegion.COLUMN_HEADER, MouseEventMatcher.LEFT_BUTTON, columnHeaderCheckBoxPainter),
		// new ToggleCheckBoxColumnAction(columnHeaderCheckBoxPainter, columnDataLayer));
		// }
		// });

		table.configure();

	}

	@Override
	public void createTableFromMatrix(List<List<String>> dataMatrix, int numColumns) {
		if (dataMatrix == null || dataMatrix.isEmpty())
			return;

		bodyDataProvider = new MatrixBasedBodyDataProvider(dataMatrix, numColumns);
		buildTable(bodyDataProvider, new ColumnHeaderDataProvider(numColumns), new LineNumberRowHeaderDataProvider(
				dataMatrix.size()));

	}

}
