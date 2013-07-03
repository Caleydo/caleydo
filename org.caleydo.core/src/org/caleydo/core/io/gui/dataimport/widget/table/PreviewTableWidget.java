/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.io.gui.dataimport.widget.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.parser.ascii.ATextParser;
import org.caleydo.core.util.base.IntegerCallback;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.action.ToggleCheckBoxColumnAction;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ButtonCellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ColumnHeaderCheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellLabelMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Manager for NAT table to create and maintain preview tables for tabular data that draws one row of buttons for
 * selecting/deselecting columns on top and one row enumeration column at the left.
 *
 * @author Christian Partl
 *
 */
public class PreviewTableWidget extends AMatrixBasedTableWidget {
	/**
	 * Maximum number of previewed rows in {@link #previewTable}.
	 */
	public static final int MAX_PREVIEW_TABLE_ROWS = 50;

	private static final String ID_CELL = "ID_CELL";
	private static final String HEADER_LINE_CELL = "HEADER_LINE_CELL";
	private static final String COLUMN_ID = "COLUMN_ID";
	private static final String ROW_ID = "ROW_ID";
	private static final String DISABLED_CELL = "DISABLED_CELL";

	private int numberOfHeaderRows = -1;
	private int idRowIndex = -1;
	private int idColumnIndex = -1;

	private List<Boolean> columnSelectionStatus = new ArrayList<>();
	private RegExIDConverter columnIDConverter;
	private RegExIDConverter rowIDConverter;
	private IntegerCallback onColumnSelection;
	private INoArgumentCallback onTranspose;

	private ColumnHeaderDataProvider columnHeaderDataProvider;
	private LineNumberRowHeaderDataProvider rowHeaderDataProvider;

	private DataLayer bodyDataLayer;

	private boolean isTransposeable = false;
	private boolean isTransposed = false;

	private class TransposeDataProvider implements IDataProvider {

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			return "Transpose";
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			// can not be set
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getRowCount() {
			return 1;
		}

	}

	private class ColumnSelectionAction extends ToggleCheckBoxColumnAction {

		/**
		 * @param columnHeaderCheckBoxPainter
		 * @param bodyDataLayer
		 */
		public ColumnSelectionAction(ColumnHeaderCheckBoxPainter columnHeaderCheckBoxPainter,
				IUniqueIndexLayer bodyDataLayer) {
			super(columnHeaderCheckBoxPainter, bodyDataLayer);
		}

		@Override
		public void run(NatTable natTable, MouseEvent event) {
			super.run(natTable, event);
			int columnIndex = natTable.getColumnPositionByX(event.x) - 1;

			if (columnIndex != -1 && onColumnSelection != null) {

				onColumnSelection.on(columnIndex);
			}
		}

	}

	private class RegExIDConverter implements IDisplayConverter {
		private IDTypeParsingRules idTypeParsingRules;

		public RegExIDConverter(IDTypeParsingRules idTypeParsingRules) {
			this.idTypeParsingRules = idTypeParsingRules;
		}

		/**
		 * @param idTypeParsingRules
		 *            setter, see {@link idTypeParsingRules}
		 */
		public void setIdTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
			this.idTypeParsingRules = idTypeParsingRules;
		}

		@Override
		public Object canonicalToDisplayValue(Object canonicalValue) {
			if (idTypeParsingRules == null || canonicalValue == null)
				return canonicalValue;
			return ATextParser.convertID((String) canonicalValue, idTypeParsingRules);
		}

		@Override
		public Object displayToCanonicalValue(Object displayValue) {
			// We can not get the canonical value without further information
			return displayValue;
		}

		@Override
		public Object canonicalToDisplayValue(ILayerCell cell, IConfigRegistry configRegistry, Object canonicalValue) {
			return canonicalToDisplayValue(canonicalValue);
		}

		@Override
		public Object displayToCanonicalValue(ILayerCell cell, IConfigRegistry configRegistry, Object displayValue) {
			return bodyDataProvider.getDataValue(cell.getColumnIndex(), cell.getRowIndex());
		}

	}

	private class ColumnNumberCellPainter extends TextPainter {
		@Override
		protected String convertDataType(ILayerCell cell, IConfigRegistry configRegistry) {
			return new Integer(cell.getColumnIndex() + 1).toString();
		}
	}

	private class ColumnHeaderDataProvider implements IDataProvider {

		private int numColumns;

		public ColumnHeaderDataProvider(int numColumns) {
			this.numColumns = numColumns;
		}

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			// return "" + (isColumnHeader ? columnIndex + 1 : rowIndex + 1);
			if (columnSelectionStatus == null || columnSelectionStatus.size() == 0)
				return false;
			// return null;
			return columnSelectionStatus.get(columnIndex);
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			// prevent to disable the id column
			if (columnSelectionStatus == null || columnSelectionStatus.size() == 0 || columnIndex == idColumnIndex)
				return;
			columnSelectionStatus.set(columnIndex, (Boolean) newValue);
			table.refresh();
		}

		@Override
		public int getColumnCount() {
			return numColumns;
		}

		@Override
		public int getRowCount() {
			return 1;
		}

		/**
		 * @param numColumns
		 *            setter, see {@link numColumns}
		 */
		public void setNumColumns(int numColumns) {
			this.numColumns = numColumns;
		}

	}

	public PreviewTableWidget(Composite parent, IntegerCallback onColumnSelection, boolean isTransposeable,
			INoArgumentCallback onTranspose) {
		super(parent);
		this.onColumnSelection = onColumnSelection;
		this.isTransposeable = isTransposeable;
		this.onTranspose = onTranspose;
		List<List<String>> emptyMatrix = createEmptyDataMatrix(15, 10);

		// emptyMatrix.get(0).add("1");

		bodyDataProvider = new MatrixBasedBodyDataProvider(emptyMatrix, 10);
		columnHeaderDataProvider = new ColumnHeaderDataProvider(10);
		rowHeaderDataProvider = new LineNumberRowHeaderDataProvider(emptyMatrix.size());
		buildTable(bodyDataProvider, columnHeaderDataProvider, rowHeaderDataProvider);
	}

	private List<List<String>> createEmptyDataMatrix(int numRows, int numColumns) {
		List<List<String>> dataMatrix = new ArrayList<>(numRows);
		for (int i = 0; i < numRows; i++) {
			List<String> row = new ArrayList<>(numColumns);
			for (int j = 0; j < numColumns; j++) {
				row.add("");
			}
			dataMatrix.add(row);
		}
		return dataMatrix;
	}

	private void buildTable(MatrixBasedBodyDataProvider bodyDataProvider,
			final ColumnHeaderDataProvider columnDataProvider, LineNumberRowHeaderDataProvider rowDataProvider) {

		if (table != null) { // cleanup old
			this.table.dispose();
			this.table = null;
		}

		bodyDataLayer = new DataLayer(bodyDataProvider);
		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
		ViewportLayer bodyLayer = new ViewportLayer(selectionLayer);

		final DataLayer columnDataLayer = new DataLayer(columnDataProvider, 120, 25);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnDataLayer, bodyLayer, selectionLayer);

		DataLayer rowDataLayer = new DataLayer(rowDataProvider, isTransposeable ? 100 : 50, 20);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowDataLayer, bodyLayer, selectionLayer);

		IDataProvider cornerDataProvider = isTransposeable ? new TransposeDataProvider()
				: new DefaultCornerDataProvider(columnDataProvider, rowDataProvider);
		CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

		GridLayer gridLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
		table = new NatTable(parent, gridLayer, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);

		// gridData.heightHint = 100;
		// gridData.widthHint = 800;
		table.setLayoutData(gridData);

		IConfigLabelAccumulator cellLabelAccumulator = new IConfigLabelAccumulator() {
			@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
				if (columnPosition == idColumnIndex || rowPosition == idRowIndex) {
					configLabels.addLabel(ID_CELL);
				}
				if (rowPosition < numberOfHeaderRows) {
					configLabels.addLabel(HEADER_LINE_CELL);
				}
				if (columnPosition == idColumnIndex && rowPosition >= numberOfHeaderRows && rowPosition != idRowIndex) {
					configLabels.addLabel(ROW_ID);
				}
				if (rowPosition == idRowIndex && columnPosition != idColumnIndex) {
					configLabels.addLabel(COLUMN_ID);
				}

				if ((Boolean) columnDataProvider.getDataValue(columnPosition, 0) == false) {
					configLabels.addLabel(DISABLED_CELL);
				}
			}
		};

		bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);
		// ColumnOverrideLabelAccumulator acc = new ColumnOverrideLabelAccumulator(columnHeaderLayer);
		// columnHeaderLayer.setConfigLabelAccumulator(acc);
		// acc.registerColumnOverrides(9, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 9);

		NatTableUtil.applyDefaultNatTableStyling(table);
		if (columnIDConverter == null)
			columnIDConverter = new RegExIDConverter(null);
		if (rowIDConverter == null)
			rowIDConverter = new RegExIDConverter(null);

		final ColumnHeaderCheckBoxPainter columnHeaderCheckBoxPainter = new ColumnHeaderCheckBoxPainter(columnDataLayer);
		final ICellPainter columnHeaderPainter = new GridLineCellPainterDecorator(new CellPainterDecorator(
				new ColumnNumberCellPainter(), CellEdgeEnum.LEFT, columnHeaderCheckBoxPainter));

		final ButtonCellPainter transposeButton = new ButtonCellPainter(new TextPainter());
		transposeButton.addClickListener(new IMouseAction() {

			@Override
			public void run(NatTable natTable, MouseEvent event) {
				if (onTranspose != null)
					onTranspose.on();

			}
		});

		table.addConfiguration(new AbstractRegistryConfiguration() {
			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {

				Style cellStyle = new Style();

				cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_GREEN);
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
						ID_CELL);

				cellStyle = new Style();
				cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
						HEADER_LINE_CELL);
				configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, columnIDConverter,
						DisplayMode.NORMAL, COLUMN_ID);
				configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, rowIDConverter,
						DisplayMode.NORMAL, ROW_ID);

				cellStyle = new Style();

				cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_NORMAL_SHADOW);
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
						DISABLED_CELL);

				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, columnHeaderPainter,
						DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);

				if (isTransposeable) {
					configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, transposeButton,
							DisplayMode.NORMAL, GridRegion.CORNER);
				}
			}

			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				uiBindingRegistry.registerFirstSingleClickBinding(new CellPainterMouseEventMatcher(
						GridRegion.COLUMN_HEADER, MouseEventMatcher.LEFT_BUTTON, columnHeaderCheckBoxPainter),
						new ColumnSelectionAction(columnHeaderCheckBoxPainter, columnDataLayer));

				if (isTransposeable) {
					CellLabelMouseEventMatcher mouseEventMatcher = new CellLabelMouseEventMatcher(GridRegion.CORNER,
							MouseEventMatcher.LEFT_BUTTON, GridRegion.CORNER);

					// Inform the button painter of the click.
					uiBindingRegistry.registerMouseDownBinding(mouseEventMatcher, transposeButton);
				}
			}
		});

		table.configure();

	}

	/**
	 * Creates the {@link #previewTable} according to the {@link #dataMatrix}.
	 */
	@Override
	public void createTableFromMatrix(List<List<String>> dataMatrix, int numColumns) {
		if (dataMatrix == null || dataMatrix.isEmpty())
			return;

		columnSelectionStatus = new ArrayList<>(numColumns);
		for (int i = 0; i < numColumns; i++) {
			columnSelectionStatus.add(true);
		}
		bodyDataProvider.setDataMatrix(dataMatrix);
		bodyDataProvider.setNumColumns(numColumns);
		columnHeaderDataProvider.setNumColumns(numColumns);
		rowHeaderDataProvider.setNumRows(dataMatrix.size());

		table.addConfiguration(new AbstractRegistryConfiguration() {

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				ICellPainter bodyPainter = null;
				for (int i = 0; i < Math.min(5, columnHeaderDataProvider.getColumnCount()); i++) {
					bodyDataLayer.setColumnWidthByPosition(i, 120);
				}
				if (columnHeaderDataProvider.getColumnCount() < 5) {
					bodyPainter = new TextPainter(false, true, true);
				} else {
					bodyPainter = new TextPainter();
				}
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, bodyPainter,
						DisplayMode.NORMAL, GridRegion.BODY);
			}
		});
		table.configure();

		// buildTable(bodyDataProvider, new ColumnHeaderDataProvider(numColumns), new LineNumberRowHeaderDataProvider(
		// dataMatrix.size()));
		table.refresh();
	}

	public void setColumnIDTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
		this.columnIDConverter.setIdTypeParsingRules(idTypeParsingRules);
		table.refresh();
	}

	public void setRowIDTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
		this.rowIDConverter.setIdTypeParsingRules(idTypeParsingRules);
		table.refresh();
	}

	/**
	 * Colors the header rows gray and the id row and id column green.
	 *
	 * @param numberOfHeaderRows
	 *            Number of rows that should be treated as headers.
	 * @param idRowIndex
	 *            Index of the row that contains IDs. If no row shall be colored, set -1.
	 * @param idColumnIndex
	 *            Index of the column that contains IDs. If no column shall be colored, set -1.
	 */
	public void updateTableColors(int numberOfHeaderRows, int idRowIndex, int idColumnIndex) {

		if (columnSelectionStatus != null && !columnSelectionStatus.isEmpty())
			columnSelectionStatus.set(idColumnIndex, true);

		this.idRowIndex = idRowIndex;
		this.idColumnIndex = idColumnIndex;
		this.numberOfHeaderRows = numberOfHeaderRows;

		// table.setSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		// table.pack();
		// table.layout(true);
		table.refresh();

		// table.redraw();
	}

	public void selectColumns(boolean selectAll, int columnOfRowId) {

		int numColumns = columnSelectionStatus.size();
		columnSelectionStatus = new ArrayList<>(numColumns);
		for (int i = 0; i < numColumns; i++) {
			if (i == columnOfRowId) {
				columnSelectionStatus.add(true);
			} else {
				columnSelectionStatus.add(selectAll);
			}
		}
		table.refresh();
	}

	/**
	 * @param selectedColumns
	 */
	public void setSelectedColumns(Collection<Integer> selectedColumns) {

		for (int i = 0; i < columnSelectionStatus.size(); i++) {
			columnSelectionStatus.set(i, selectedColumns.contains(i));
		}
	}

	/**
	 * returns the current selected column indices + optional a -1 as wildcard for all unseen
	 *
	 * @return
	 */
	public List<Integer> getSelectedColumns() {
		List<Integer> result = new ArrayList<>();

		for (int i = 0; i < columnSelectionStatus.size(); i++) {
			if (columnSelectionStatus.get(i)) {
				result.add(i);
			}
		}
		return result;
	}

	public void setEnabled(boolean isEnabled) {
		table.setEnabled(isEnabled);
	}

	/**
	 * @return
	 */
	public Composite getTable() {
		return this.table;
	}

}
