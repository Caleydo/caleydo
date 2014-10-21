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
import org.caleydo.core.util.collection.Pair;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.command.EditCellCommandHandler;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ButtonCellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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
	private static final String ID_ROW = "ID_ROW";
	private static final String ID_COLUMN = "ID_COLUMN";
	private static final String COLORED_COLUMN = "COLORED_COLUMN";
	private static final String COLORED_ROW = "COLORED_ROW";

	private static final String DISABLED_CELL = "DISABLED_CELL";
	private static final String EDITABLE = "EDITABLE";
	private static final String COLUMN_HEADER_CHECKBOX = "COLUMN_HEADER_CHECKBOX";

	private int numberOfHeaderRows = -1;

	private boolean headerRowsInFront = false;

	private List<RowColDesc> extRowDescriptions = new ArrayList<>();
	private List<RowColDesc> extColumnDescriptions = new ArrayList<>();

	private List<RowColDescInternal> rowDescriptions = new ArrayList<>();
	private List<RowColDescInternal> columnDescriptions = new ArrayList<>();
	// private int idRowIndex = -1;
	// private int idColumnIndex = -1;

	private List<Pair<IDataProvider, Boolean>> customHeaderDataProviders = new ArrayList<>();
	private List<Boolean> columnSelectionStatus = new ArrayList<>();
	// private RegExIDConverter columnIDConverter;
	// private RegExIDConverter rowIDConverter;
	private IntegerCallback onColumnSelection;
	private INoArgumentCallback onTranspose;

	private ColumnHeaderDataProvider columnHeaderDataProvider;
	private LineNumberRowHeaderDataProvider rowHeaderDataProvider;

	private DataLayer bodyDataLayer;

	private boolean isTransposeable = false;
	private boolean areColumnsSelectable = true;

	private Label tableDimensionsLabel;

	private class RowColDescInternal {
		public int index;
		public Color color;
		public RegExIDConverter converter;

		public RowColDescInternal(RowColDesc desc) {
			this.index = desc.index;
			this.color = GUIHelper.getColor(desc.color.asRGB());
			this.converter = new RegExIDConverter(desc.idTypeParsingRules);
		}

	}

	public static class RowColDesc {
		public int index;
		public org.caleydo.core.util.color.Color color;
		public IDTypeParsingRules idTypeParsingRules;

		public RowColDesc() {

		}

		/**
		 * @param index
		 * @param color
		 * @param converter
		 */
		public RowColDesc(int index, org.caleydo.core.util.color.Color color, IDTypeParsingRules idTypeParsingRules) {
			this.index = index;
			this.color = color;
			this.idTypeParsingRules = idTypeParsingRules;
		}

	}

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

	private class ColumnSelectionAction implements IMouseAction {

		/**
		 * @param columnHeaderCheckBoxPainter
		 * @param bodyDataLayer
		 */
		public ColumnSelectionAction() {
		}

		@Override
		public void run(NatTable natTable, MouseEvent event) {
			int columnIndex = natTable.getColumnIndexByPosition(natTable.getColumnPositionByX(event.x));
			int rowIndex = natTable.getRowIndexByPosition(natTable.getColumnPositionByX(event.y));
			columnHeaderDataProvider.setDataValue(columnIndex, rowIndex,
					!(Boolean) columnHeaderDataProvider.getDataValue(columnIndex, rowIndex));

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

		// /**
		// * @param idTypeParsingRules
		// * setter, see {@link idTypeParsingRules}
		// */
		// public void setIdTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
		// this.idTypeParsingRules = idTypeParsingRules;
		// }

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
			if (rowIndex > 0) {
				int pastRows = 1;
				for (Pair<IDataProvider, Boolean> pair : customHeaderDataProviders) {
					IDataProvider provider = pair.getFirst();
					if (rowIndex - pastRows < provider.getRowCount()) {
						return provider.getDataValue(columnIndex, rowIndex - pastRows);

					}
					pastRows += provider.getRowCount();
				}
				return null;
			} else {
				if (columnSelectionStatus == null || columnSelectionStatus.size() == 0)
					return false;
				// return null;
				return columnSelectionStatus.get(columnIndex);
			}

		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			// prevent to disable the id column
			if (rowIndex > 0) {
				if (newValue instanceof Boolean)
					return;
				int pastRows = 1;
				for (Pair<IDataProvider, Boolean> pair : customHeaderDataProviders) {
					IDataProvider provider = pair.getFirst();
					if (rowIndex - pastRows < provider.getRowCount()) {
						provider.setDataValue(columnIndex, rowIndex - pastRows, newValue);
						break;
					}
					pastRows += provider.getRowCount();
				}
			} else {
				if (columnSelectionStatus == null || columnSelectionStatus.size() == 0
						|| containsIndex(columnIndex, columnDescriptions))
					return;

				columnSelectionStatus.set(columnIndex, (Boolean) newValue);
			}
			table.refresh();
		}

		@Override
		public int getColumnCount() {
			return numColumns;
		}

		@Override
		public int getRowCount() {
			int numRows = 0;
			for (Pair<IDataProvider, Boolean> provider : customHeaderDataProviders) {
				numRows += provider.getFirst().getRowCount();
			}
			return numRows + 1;
		}

		/**
		 * @param numColumns
		 *            setter, see {@link numColumns}
		 */
		public void setNumColumns(int numColumns) {
			this.numColumns = numColumns;
		}

	}

	public PreviewTableWidget(Composite parent, boolean areColumnsSelectable, IntegerCallback onColumnSelection,
			boolean isTransposeable, INoArgumentCallback onTranspose) {
		super(parent);
		this.parent = new Composite(parent, SWT.NONE);
		this.parent.setLayout(new GridLayout(1, true));
		this.parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		this.onColumnSelection = onColumnSelection;
		this.isTransposeable = isTransposeable;
		this.areColumnsSelectable = areColumnsSelectable;
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
		columnHeaderLayer.registerCommandHandler(new EditCellCommandHandler());

		DataLayer rowDataLayer = new DataLayer(rowDataProvider, isTransposeable ? 100 : 50, 20);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowDataLayer, bodyLayer, selectionLayer);

		IDataProvider cornerDataProvider = isTransposeable ? new TransposeDataProvider()
				: new DefaultCornerDataProvider(columnDataProvider, rowDataProvider);
		CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

		GridLayer gridLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
		table = new NatTable(parent, gridLayer, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

		// gridData.heightHint = 100;
		// gridData.widthHint = 800;
		table.setLayoutData(gridData);

		IConfigLabelAccumulator cellLabelAccumulator = new IConfigLabelAccumulator() {
			@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {

				for (int i = 0; i < rowDescriptions.size(); i++) {
					RowColDescInternal rowDesc = rowDescriptions.get(i);
					if (rowPosition == rowDesc.index) {
						configLabels.addLabel(COLORED_ROW + i);
						if (!containsIndex(columnPosition, columnDescriptions))
							configLabels.addLabel(ID_ROW + i);
					}
				}

				if (headerRowsInFront && rowPosition < numberOfHeaderRows) {
					configLabels.addLabel(HEADER_LINE_CELL);
				}

				for (int i = 0; i < columnDescriptions.size(); i++) {
					RowColDescInternal columnDesc = columnDescriptions.get(i);
					if (columnPosition == columnDesc.index) {
						configLabels.addLabel(COLORED_COLUMN + i);
						if (rowPosition >= numberOfHeaderRows && !containsIndex(rowPosition, rowDescriptions)) {
							configLabels.addLabel(ID_COLUMN + i);
						}
					}
				}

				if (!headerRowsInFront && rowPosition < numberOfHeaderRows) {
					configLabels.addLabel(HEADER_LINE_CELL);
				}

				// if (columnPosition == idColumnIndex || rowPosition == idRowIndex) {
				// configLabels.addLabel(ID_CELL);
				// }
				// if (columnPosition == idColumnIndex && rowPosition >= numberOfHeaderRows && rowPosition !=
				// idRowIndex) {
				// configLabels.addLabel(ROW_ID);
				// }

				if ((Boolean) columnDataProvider.getDataValue(columnPosition, 0) == false) {
					configLabels.addLabel(DISABLED_CELL);
				}
			}
		};

		bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);
		columnDataLayer.setConfigLabelAccumulator(new IConfigLabelAccumulator() {

			@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
				if (rowPosition > 0) {
					int currentMaxRowPosition = 1;
					for (Pair<IDataProvider, Boolean> pair : customHeaderDataProviders) {
						currentMaxRowPosition += pair.getFirst().getRowCount();
						if (rowPosition <= currentMaxRowPosition) {
							if (pair.getSecond()) {
								configLabels.addLabel(EDITABLE);
							}
							return;
						}
					}
				} else {
					configLabels.addLabel(COLUMN_HEADER_CHECKBOX);
				}

			}
		});
		// ColumnOverrideLabelAccumulator acc = new ColumnOverrideLabelAccumulator(columnHeaderLayer);
		// columnHeaderLayer.setConfigLabelAccumulator(acc);
		// acc.registerColumnOverrides(9, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 9);

		NatTableUtil.applyDefaultNatTableStyling(table);
		// if (columnIDConverter == null)
		// columnIDConverter = new RegExIDConverter(null);
		// if (rowIDConverter == null)
		// rowIDConverter = new RegExIDConverter(null);

		final CheckBoxPainter columnHeaderCheckBoxPainter = new CheckBoxPainter();
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
				cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
						HEADER_LINE_CELL);

				for (int i = 0; i < rowDescriptions.size(); i++) {
					cellStyle = new Style();
					RowColDescInternal rowDesc = rowDescriptions.get(i);
					cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, rowDesc.color);
					configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
							DisplayMode.NORMAL, COLORED_ROW + i);

					configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, rowDesc.converter,
							DisplayMode.NORMAL, ID_ROW + i);
				}

				for (int i = 0; i < columnDescriptions.size(); i++) {
					cellStyle = new Style();
					RowColDescInternal columnDesc = columnDescriptions.get(i);
					cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, columnDesc.color);
					configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
							DisplayMode.NORMAL, COLORED_COLUMN + i);

					configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
							columnDesc.converter, DisplayMode.NORMAL, ID_COLUMN + i);
				}

				cellStyle = new Style();

				cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_NORMAL_SHADOW);
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
						DISABLED_CELL);

				if (areColumnsSelectable) {
					configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, columnHeaderPainter,
							DisplayMode.NORMAL, COLUMN_HEADER_CHECKBOX);
					configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, columnHeaderPainter,
							DisplayMode.EDIT, COLUMN_HEADER_CHECKBOX);
				} else {
					configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
							new GridLineCellPainterDecorator(new ColumnNumberCellPainter()), DisplayMode.NORMAL,
							COLUMN_HEADER_CHECKBOX);
					configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
							new GridLineCellPainterDecorator(new ColumnNumberCellPainter()), DisplayMode.EDIT,
							COLUMN_HEADER_CHECKBOX);
				}

				configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
						new DefaultBooleanDisplayConverter(), DisplayMode.NORMAL, COLUMN_HEADER_CHECKBOX);
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
						IEditableRule.ALWAYS_EDITABLE, DisplayMode.EDIT, COLUMN_HEADER_CHECKBOX);

				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
						IEditableRule.ALWAYS_EDITABLE, DisplayMode.EDIT, EDITABLE);

				if (isTransposeable) {
					configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, transposeButton,
							DisplayMode.NORMAL, GridRegion.CORNER);
				}
			}

			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				if (areColumnsSelectable) {
					uiBindingRegistry.registerFirstSingleClickBinding(new CellPainterMouseEventMatcher(
							GridRegion.COLUMN_HEADER, MouseEventMatcher.LEFT_BUTTON, columnHeaderCheckBoxPainter),
							new ColumnSelectionAction());

					uiBindingRegistry.registerFirstSingleClickBinding(new CellLabelMouseEventMatcher(
							GridRegion.COLUMN_HEADER, MouseEventMatcher.LEFT_BUTTON, EDITABLE), new MouseEditAction());
				}
				if (isTransposeable) {
					CellLabelMouseEventMatcher mouseEventMatcher = new CellLabelMouseEventMatcher(GridRegion.CORNER,
							MouseEventMatcher.LEFT_BUTTON, GridRegion.CORNER);

					// Inform the button painter of the click.
					uiBindingRegistry.registerMouseDownBinding(mouseEventMatcher, transposeButton);
				}
			}
		});

		table.configure();

		tableDimensionsLabel = new Label(parent, SWT.RIGHT);
		tableDimensionsLabel.setText("Rows: " + bodyDataProvider.getRowCount() + " Columns: "
				+ bodyDataProvider.getColumnCount());
		tableDimensionsLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}

	private boolean containsIndex(int index, List<RowColDescInternal> descriptions) {
		for (RowColDescInternal desc : descriptions) {
			if (desc.index == index)
				return true;
		}
		return false;
	}

	/**
	 * Creates the {@link #previewTable} according to the {@link #dataMatrix}.
	 */
	@Override
	public void createTableFromMatrix(List<List<String>> dataMatrix, int numColumns) {
		if (dataMatrix == null || dataMatrix.isEmpty())
			return;

		clearCustomHeaderRows();
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
		tableDimensionsLabel.setText("Rows: " + bodyDataProvider.getRowCount() + " Columns: "
				+ bodyDataProvider.getColumnCount());
	}

	// /**
	// * @param idTypeParsingRules
	// */
	// public void setColumnIDTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
	// this.columnIDConverter.setIdTypeParsingRules(idTypeParsingRules);
	// table.refresh();
	// }
	//
	// public void setRowIDTypeParsingRules(IDTypeParsingRules idTypeParsingRules) {
	// this.rowIDConverter.setIdTypeParsingRules(idTypeParsingRules);
	// table.refresh();
	// }

	// /**
	// * Colors the header rows gray and the id row and id column green.
	// *
	// * @param numberOfHeaderRows
	// * Number of rows that should be treated as headers.
	// * @param idRowIndex
	// * Index of the row that contains IDs. If no row shall be colored, set -1.
	// * @param idColumnIndex
	// * Index of the column that contains IDs. If no column shall be colored, set -1.
	// */
	// public void updateTableColors(int numberOfHeaderRows, int idRowIndex, int idColumnIndex) {
	//
	// if (columnSelectionStatus != null && !columnSelectionStatus.isEmpty() && idColumnIndex >= 0)
	// columnSelectionStatus.set(idColumnIndex, true);
	//
	// this.idRowIndex = idRowIndex;
	// this.idColumnIndex = idColumnIndex;
	// this.numberOfHeaderRows = numberOfHeaderRows;
	//
	// // table.setSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	// // table.pack();
	// // table.layout(true);
	// table.refresh();
	//
	// // table.redraw();
	// }

	private boolean needsReconfiguration(List<RowColDesc> rowDescriptions, List<RowColDesc> columnDescriptions) {
		if (needsReconfig(rowDescriptions, extRowDescriptions))
			return true;

		return needsReconfig(columnDescriptions, extColumnDescriptions);
	}

	private boolean needsReconfig(List<RowColDesc> descriptions, List<RowColDesc> presentDescriptions) {
		if (descriptions.size() != presentDescriptions.size())
			return true;

		for (RowColDesc desc : descriptions) {
			boolean newDesc = true;
			for (RowColDesc prevDesc : presentDescriptions) {
				if (desc.color == prevDesc.color && desc.idTypeParsingRules == prevDesc.idTypeParsingRules) {
					newDesc = false;
					break;
				}
			}
			if (newDesc)
				return true;
		}
		return false;
	}

	public void updateTable(int numberOfHeaderRows, List<RowColDesc> rowDescriptions,
			List<RowColDesc> columnDescriptions) {

		boolean needsReconfiguration = needsReconfiguration(rowDescriptions, columnDescriptions);

		this.extColumnDescriptions = columnDescriptions;
		this.extRowDescriptions = rowDescriptions;

		this.rowDescriptions.clear();
		this.columnDescriptions.clear();

		for (RowColDesc r : rowDescriptions) {
			this.rowDescriptions.add(new RowColDescInternal(r));
		}
		for (RowColDesc c : columnDescriptions) {
			this.columnDescriptions.add(new RowColDescInternal(c));
		}

		if (columnSelectionStatus != null && !columnSelectionStatus.isEmpty()) {
			for (RowColDescInternal desc : this.columnDescriptions) {
				columnSelectionStatus.set(desc.index, true);
			}
		}

		this.numberOfHeaderRows = numberOfHeaderRows;

		// table.setSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		// table.pack();
		// table.layout(true);
		if (needsReconfiguration)
			table.configure();
		table.refresh();

		// table.redraw();
	}

	public void reconfigure() {
		table.configure();
		table.refresh();
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

	public void addCustomHeaderRows(IDataProvider headerDataProvider, boolean isEditable) {
		if (headerDataProvider.getColumnCount() != this.columnHeaderDataProvider.getColumnCount())
			throw new IllegalStateException(
					"The number of columns of the specified dataprovider must be equal to the number of columns of this table.");

		customHeaderDataProviders.add(Pair.make(headerDataProvider, isEditable));

	}

	public void clearCustomHeaderRows() {
		customHeaderDataProviders.clear();
	}

	public void setHeaderRowsInFront(boolean isInFront) {
		this.headerRowsInFront = isInFront;
	}

}
