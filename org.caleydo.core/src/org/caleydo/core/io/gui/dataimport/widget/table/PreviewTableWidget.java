/**
 *
 */
package org.caleydo.core.io.gui.dataimport.widget.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.action.ToggleCheckBoxColumnAction;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.ColumnHeaderCheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
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

	private int numberOfHeaderRows = -1;
	private int idRowIndex = -1;
	private int idColumnIndex = -1;

	private List<Boolean> columnSelectionStatus = new ArrayList<>();

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

	}

	public PreviewTableWidget(Composite parent) {
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

		IConfigLabelAccumulator cellLabelAccumulator = new IConfigLabelAccumulator() {
			@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
				if (columnPosition == idColumnIndex || rowPosition == idRowIndex) {
					configLabels.addLabel(ID_CELL);
				}
				if (rowPosition < numberOfHeaderRows) {
					configLabels.addLabel(HEADER_LINE_CELL);
				}
				// configLabels.addLabel(ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnPosition);
				// configLabels.addLabel(CHECK_BOX_CONFIG_LABEL);
				// configLabels.addLabel(CHECK_BOX_EDITOR_CONFIG_LABEL);
			}
		};

		bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);
		ColumnOverrideLabelAccumulator acc = new ColumnOverrideLabelAccumulator(columnHeaderLayer);
		columnHeaderLayer.setConfigLabelAccumulator(acc);
		acc.registerColumnOverrides(9, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 9);
		table.addConfiguration(new DefaultNatTableStyleConfiguration());
		// table.addConfiguration(new HeaderMenuConfiguration(table));
		// table.addConfiguration(new AbstractRegistryConfiguration() {
		//
		// @Override
		// public void configureRegistry(IConfigRegistry configRegistry) {
		//
		//
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(),
		// DisplayMode.NORMAL, CHECK_BOX_CONFIG_LABEL);
		// // configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new
		// // DefaultBooleanDisplayConverter(), DisplayMode.NORMAL, CHECK_BOX_CONFIG_LABEL);
		// configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new CheckBoxCellEditor(),
		// DisplayMode.NORMAL, CHECK_BOX_EDITOR_CONFIG_LABEL);
		//
		// }});
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
			}
		});

		final ColumnHeaderCheckBoxPainter columnHeaderCheckBoxPainter = new ColumnHeaderCheckBoxPainter(columnDataLayer);
		final ICellPainter columnHeaderPainter = new BeveledBorderDecorator(columnHeaderCheckBoxPainter);

		// final ICellPainter column9HeaderPainter = new BeveledBorderDecorator(new CellPainterDecorator(
		// new TextPainter(), CellEdgeEnum.RIGHT, columnHeaderCheckBoxPainter));
		table.addConfiguration(new AbstractRegistryConfiguration() {
			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, columnHeaderPainter,
						DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);
			}

			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				uiBindingRegistry.registerFirstSingleClickBinding(new CellPainterMouseEventMatcher(
						GridRegion.COLUMN_HEADER, MouseEventMatcher.LEFT_BUTTON, columnHeaderCheckBoxPainter),
						new ToggleCheckBoxColumnAction(columnHeaderCheckBoxPainter, columnDataLayer));
			}
		});

		table.configure();

		// =========================================

		// DefaultGridLayer gridLayer = new DefaultGridLayer(RowDataListFixture.getList(),
		// RowDataListFixture.getPropertyNames(), RowDataListFixture.getPropertyToLabelMap());
		//
		// DataLayer columnHeaderDataLayer = (DataLayer) gridLayer.getColumnHeaderDataLayer();
		// columnHeaderDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator());
		//
		// final DataLayer bodyDataLayer = (DataLayer) gridLayer.getBodyDataLayer();
		// IDataProvider dataProvider = bodyDataLayer.getDataProvider();
		//
		// // NOTE: Register the accumulator on the body data layer.
		// // This ensures that the labels are bound to the column index and are unaffected by column order.
		// final ColumnOverrideLabelAccumulator columnLabelAccumulator = new
		// ColumnOverrideLabelAccumulator(bodyDataLayer);
		// bodyDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);
		//
		// NatTable natTable = new NatTable(parent, gridLayer, false);
		//
		// natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		//
		//
		// final ColumnHeaderCheckBoxPainter columnHeaderCheckBoxPainter = new
		// ColumnHeaderCheckBoxPainter(bodyDataLayer);
		// final ICellPainter column9HeaderPainter = new BeveledBorderDecorator(new CellPainterDecorator(new
		// TextPainter(), CellEdgeEnum.RIGHT, columnHeaderCheckBoxPainter));
		// natTable.addConfiguration(new AbstractRegistryConfiguration() {
		// @Override
		// public void configureRegistry(IConfigRegistry configRegistry) {
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
		// column9HeaderPainter,
		// DisplayMode.NORMAL,
		// ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 9);
		// }
		//
		// @Override
		// public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		// uiBindingRegistry.registerFirstSingleClickBinding(
		// new CellPainterMouseEventMatcher(GridRegion.COLUMN_HEADER, MouseEventMatcher.LEFT_BUTTON,
		// columnHeaderCheckBoxPainter),
		// new ToggleCheckBoxColumnAction(columnHeaderCheckBoxPainter, bodyDataLayer)
		// );
		// }
		// });
		//
		// natTable.configure();
		//
		// return natTable;
		// }
		//
		// public static AbstractRegistryConfiguration editableGridConfiguration(
		// final ColumnOverrideLabelAccumulator columnLabelAccumulator,
		// final IDataProvider dataProvider) {
		//
		// return new AbstractRegistryConfiguration() {
		//
		// @Override
		// public void configureRegistry(IConfigRegistry configRegistry) {
		//
		//
		//
		// registerCheckBoxEditor(configRegistry, new CheckBoxPainter(), new CheckBoxCellEditor());
		//
		// }
		//
		// };
		// }

		// private static void registerConfigLabelsOnColumns(ColumnOverrideLabelAccumulator columnLabelAccumulator) {
		// columnLabelAccumulator.registerColumnOverrides(RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.SECURITY_ID_PROP_NAME),
		// SECURITY_ID_EDITOR, SECURITY_ID_CONFIG_LABEL);
		//
		// columnLabelAccumulator.registerColumnOverrides(RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.SECURITY_DESCRIPTION_PROP_NAME),
		// ALIGN_CELL_CONTENTS_LEFT_CONFIG_LABEL);
		//
		// columnLabelAccumulator.registerColumnOverrides(RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.ISSUE_DATE_PROP_NAME),
		// FORMAT_DATE_CONFIG_LABEL);
		//
		// columnLabelAccumulator.registerColumnOverrides(RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.PRICING_TYPE_PROP_NAME),
		// COMBO_BOX_CONFIG_LABEL, COMBO_BOX_EDITOR_CONFIG_LABEL, FORMAT_PRICING_TYPE_CONFIG_LABEL);
		//
		// columnLabelAccumulator.registerColumnOverrides(RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.BID_PRICE_PROP_NAME),
		// BID_PRICE_CONFIG_LABEL, FORMAT_DOUBLE_6_PLACES_CONFIG_LABEL, FORMAT_DOUBLE_2_PLACES_CONFIG_LABEL,
		// ALIGN_CELL_CONTENTS_RIGHT_CONFIG_LABEL);
		//
		// columnLabelAccumulator.registerColumnOverrides(RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.ASK_PRICE_PROP_NAME),
		// ASK_PRICE_CONFIG_LABEL, FORMAT_DOUBLE_6_PLACES_CONFIG_LABEL, FORMAT_DOUBLE_2_PLACES_CONFIG_LABEL,
		// ALIGN_CELL_CONTENTS_RIGHT_CONFIG_LABEL);
		//
		// columnLabelAccumulator.registerColumnOverrides(RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.SPREAD_PROP_NAME),
		// SPREAD_CONFIG_LABEL, FORMAT_DOUBLE_6_PLACES_CONFIG_LABEL, ALIGN_CELL_CONTENTS_RIGHT_CONFIG_LABEL);
		//
		// columnLabelAccumulator.registerColumnOverrides(RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.LOT_SIZE_PROP_NAME),
		// LOT_SIZE_CONFIG_LABEL, FORMAT_IN_MILLIONS_CONFIG_LABEL, ALIGN_CELL_CONTENTS_RIGHT_CONFIG_LABEL);
		//
		// columnLabelAccumulator.registerColumnOverrides(RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.PUBLISH_FLAG_PROP_NAME),
		// CHECK_BOX_EDITOR_CONFIG_LABEL, CHECK_BOX_CONFIG_LABEL);
		// }
		//
		// private static void registerSecurityDescriptionCellStyle(IConfigRegistry configRegistry) {
		// Style cellStyle = new Style();
		// cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
		// ALIGN_CELL_CONTENTS_LEFT_CONFIG_LABEL);
		// }
		//
		// private static void registerPricingCellStyle(IConfigRegistry configRegistry) {
		// Style cellStyle = new Style();
		// cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
		// ALIGN_CELL_CONTENTS_RIGHT_CONFIG_LABEL);
		// }

		// private static void registerCheckBoxEditor(IConfigRegistry configRegistry, ICellPainter checkBoxCellPainter,
		// ICellEditor checkBoxCellEditor) {
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, checkBoxCellPainter,
		// DisplayMode.NORMAL, CHECK_BOX_CONFIG_LABEL);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new
		// DefaultBooleanDisplayConverter(), DisplayMode.NORMAL, CHECK_BOX_CONFIG_LABEL);
		// configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditor,
		// DisplayMode.NORMAL, CHECK_BOX_EDITOR_CONFIG_LABEL);
		// }

		// ===================================================================
		//
		// List<Person> myList = new ArrayList<Person>();
		// for (int i = 0; i < 100; i++) {
		// myList.add(new Person(i, "Joe" + i, new Date()));
		// }
		//
		// String[] propertyNames = { "id", "name", "birthDate" };
		//
		// IColumnPropertyAccessor<Person> columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<Person>(
		// propertyNames);
		// ListDataProvider<Person> listDataProvider = new ListDataProvider<Person>(myList, columnPropertyAccessor);
		// DefaultGridLayer gridLayer = new DefaultGridLayer(listDataProvider, new DummyColumnHeaderDataProvider(
		// listDataProvider));
		// final DefaultBodyLayerStack bodyLayer = gridLayer.getBodyLayer();
		//
		// // Custom label "FOO" for cell at column, row index (1, 5)
		// IConfigLabelAccumulator cellLabelAccumulator = new IConfigLabelAccumulator() {
		// @Override
		// public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
		// int columnIndex = bodyLayer.getColumnIndexByPosition(columnPosition);
		// int rowIndex = bodyLayer.getRowIndexByPosition(rowPosition);
		// if (columnIndex == 1 && rowIndex == 5) {
		// configLabels.addLabel(FOO_LABEL);
		// }
		// }
		// };
		// bodyLayer.setConfigLabelAccumulator(cellLabelAccumulator);
		//
		// NatTable natTable = new NatTable(parent, gridLayer, false);
		//
		// natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		// // Custom style for label "FOO"
		// natTable.addConfiguration(new AbstractRegistryConfiguration() {
		// @Override
		// public void configureRegistry(IConfigRegistry configRegistry) {
		// Style cellStyle = new Style();
		// cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_GREEN);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
		// FOO_LABEL);
		// }
		// });
		// natTable.configure();

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
		bodyDataProvider = new MatrixBasedBodyDataProvider(dataMatrix, numColumns);
		buildTable(bodyDataProvider, new ColumnHeaderDataProvider(numColumns), new LineNumberRowHeaderDataProvider(
				dataMatrix.size()));

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

}
