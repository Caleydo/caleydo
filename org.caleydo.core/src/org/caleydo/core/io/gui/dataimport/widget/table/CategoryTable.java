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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.io.gui.dataimport.widget.IntegerCallback;
import org.caleydo.core.io.gui.dataimport.widget.table.ITableDataChangeListener.EChangeType;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Table for displaying and modifying properties of categories.
 *
 * @author Christian Partl
 *
 */
public class CategoryTable extends AMatrixBasedTableWidget implements ILayerListener, DisposeListener {

	private static final String EDITABLE = "EDITABLE";
	private static final String NON_EDITABLE = "NON_EDITABLE";

	public static final String[] COLUMN_HEADERS = { "Value", "Count", "Name", "Color" };

	private Object layoutData;

	private SelectionLayer selectionLayer;

	private IntegerCallback rowSelectionCallback;

	private Map<String, Color> colorRegistry = new HashMap<>();

	private LineNumberRowHeaderDataProvider rowHeaderDataProvider;

	private Set<ITableDataChangeListener> listeners = new HashSet<>();

	private class ColumnHeaderDataProvider implements IDataProvider {

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {

			return COLUMN_HEADERS[columnIndex];
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			// not possible
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			return 1;
		}

	}

	private class ColorCellPainter implements ICellPainter {

		@Override
		public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
			// gc.setForeground(GUIHelper.COLOR_BLACK);
			gc.setBackground(colorRegistry.get(cell.getDataValue()));
			gc.fillRectangle(bounds);
		}

		@Override
		public int getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
			return 20;
		}

		@Override
		public int getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
			return 20;
		}

		@Override
		public ICellPainter getCellPainterAt(int x, int y, ILayerCell cell, GC gc, Rectangle adjustedCellBounds,
				IConfigRegistry configRegistry) {
			if (cell.getColumnPosition() == 4)
				return this;
			return null;
		}
	}

	private class ChangeColorAction implements IMouseAction {

		@Override
		public void run(NatTable natTable, MouseEvent event) {
			int sourceRowPosition = natTable.getRowPositionByY(event.y);
			ColorDialog dialog = new ColorDialog(natTable.getShell());
			ILayerCell cell = natTable.getCellByPosition(4, sourceRowPosition);
			Color color = colorRegistry.get(cell.getDataValue());
			dialog.setRGB(color.getRGB());
			RGB newRGB = dialog.open();
			if (newRGB != null) {
				org.caleydo.core.util.color.Color newColor = new org.caleydo.core.util.color.Color(newRGB.red,
						newRGB.green, newRGB.blue);
				bodyDataProvider.setDataValue(3, sourceRowPosition - 1, newColor.getHEX());
				update();
			}
		}
	}

	/**
	 * @param parent
	 */
	public CategoryTable(Composite parent, Object layoutData, boolean areRowsMoveable) {
		super(parent);
		this.layoutData = layoutData;
		bodyDataProvider = new MatrixBasedBodyDataProvider(null, 1) {
			@Override
			public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
				super.setDataValue(columnIndex, rowIndex, newValue);
				notifyListeners(EChangeType.VALUE);
			}
		};
		rowHeaderDataProvider = new LineNumberRowHeaderDataProvider(1);
		buildTable(bodyDataProvider, new ColumnHeaderDataProvider(), rowHeaderDataProvider, areRowsMoveable);
	}

	@Override
	public void createTableFromMatrix(List<List<String>> dataMatrix, int numColumns) {
		bodyDataProvider.setDataMatrix(dataMatrix);
		bodyDataProvider.setNumColumns(numColumns);
		rowHeaderDataProvider.setNumRows(dataMatrix.size());
		update();
		// buildTable(bodyDataProvider, new ColumnHeaderDataProvider(),
		// new LineNumberRowHeaderDataProvider(dataMatrix.size()));
	}

	public void setRowsMoveable(boolean areRowsMoveable) {
		buildTable(bodyDataProvider, new ColumnHeaderDataProvider(), rowHeaderDataProvider, areRowsMoveable);
		table.refresh();
	}

	private void buildTable(MatrixBasedBodyDataProvider bodyDataProvider, ColumnHeaderDataProvider columnDataProvider,
			LineNumberRowHeaderDataProvider rowDataProvider, final boolean areRowsMoveable) {

		if (table != null) { // cleanup old
			this.table.dispose();
			this.table = null;
		}

		final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider, 120, 36);
		bodyDataLayer.addLayerListener(this);

		selectionLayer = new SelectionLayer(bodyDataLayer);
		selectionLayer.addLayerListener(this);
		ViewportLayer bodyLayer = new ViewportLayer(selectionLayer);

		final DataLayer columnDataLayer = new DataLayer(columnDataProvider, 120, 25);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnDataLayer, bodyLayer, selectionLayer);

		DataLayer rowDataLayer = new DataLayer(rowDataProvider, 80, 36);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowDataLayer, bodyLayer, selectionLayer);
		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnDataProvider,
				rowDataProvider);
		CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);
		GridLayer gridLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
		table = new NatTable(parent, gridLayer, false);
		table.setLayoutData(layoutData);

		table.addConfiguration(new DefaultNatTableStyleConfiguration());
		ColumnOverrideLabelAccumulator acc = new ColumnOverrideLabelAccumulator(bodyDataLayer);
		bodyDataLayer.setConfigLabelAccumulator(acc);
		acc.registerColumnOverrides(0, NON_EDITABLE);
		acc.registerColumnOverrides(1, NON_EDITABLE);
		acc.registerColumnOverrides(2, EDITABLE);
		acc.registerColumnOverrides(3, "COLOR");
		final ColorCellPainter colorCellPainter = new ColorCellPainter();

		final ICellPainter upImagePainter = new ImagePainter(GeneralManager.get().getResourceLoader()
				.getImage(parent.getDisplay(), "resources/icons/general/arrow_up.png"));
		final ICellPainter downImagePainter = new ImagePainter(GeneralManager.get().getResourceLoader()
				.getImage(parent.getDisplay(), "resources/icons/general/arrow_down.png"));

		final ICellPainter upPainter = new BeveledBorderDecorator(upImagePainter);
		final ICellPainter downPainter = new BeveledBorderDecorator(downImagePainter);

		final ICellPainter rowHeaderPainter = new CellPainterDecorator(new TextPainter(), CellEdgeEnum.LEFT,
				new CellPainterDecorator(upPainter, CellEdgeEnum.BOTTOM, downPainter));

		table.addConfiguration(new AbstractRegistryConfiguration() {

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
						IEditableRule.ALWAYS_EDITABLE, DisplayMode.EDIT, EDITABLE);

				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, colorCellPainter,
						DisplayMode.NORMAL, "COLOR");

				if (areRowsMoveable) {
					configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, rowHeaderPainter,
							DisplayMode.NORMAL, GridRegion.ROW_HEADER);
				}

				Style cellStyle = new Style();

				cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_NORMAL_SHADOW);
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
						NON_EDITABLE);

			}

			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				uiBindingRegistry.registerDoubleClickBinding(new CellPainterMouseEventMatcher(GridRegion.BODY,
						MouseEventMatcher.LEFT_BUTTON, colorCellPainter), new ChangeColorAction());

				if (areRowsMoveable) {
					IMouseAction moveUp = new IMouseAction() {

						@Override
						public void run(NatTable natTable, MouseEvent event) {
							int rowIndex = natTable.getRowPositionByY(event.y) - 1;

							if (rowIndex > 0) {
								swapRows(rowIndex, rowIndex - 1);
								update();
								selectRow(rowIndex - 1);
							}
						}
					};

					IMouseAction moveDown = new IMouseAction() {

						@Override
						public void run(NatTable natTable, MouseEvent event) {
							int rowIndex = natTable.getRowPositionByY(event.y) - 1;

							if (rowIndex != -1 && rowIndex < getRowCount() - 1) {
								swapRows(rowIndex, rowIndex + 1);
								update();
								selectRow(rowIndex + 1);
							}
						}
					};

					uiBindingRegistry.registerFirstSingleClickBinding(new CellPainterMouseEventMatcher(
							GridRegion.ROW_HEADER, MouseEventMatcher.LEFT_BUTTON, upPainter), moveUp);

					uiBindingRegistry.registerFirstSingleClickBinding(new CellPainterMouseEventMatcher(
							GridRegion.ROW_HEADER, MouseEventMatcher.LEFT_BUTTON, downPainter), moveDown);

					uiBindingRegistry.registerFirstSingleClickBinding(new CellPainterMouseEventMatcher(
							GridRegion.ROW_HEADER, MouseEventMatcher.LEFT_BUTTON, upImagePainter), moveUp);

					uiBindingRegistry.registerFirstSingleClickBinding(new CellPainterMouseEventMatcher(
							GridRegion.ROW_HEADER, MouseEventMatcher.LEFT_BUTTON, downImagePainter), moveDown);
				}
			}
		});

		table.addDisposeListener(this);

		table.configure();
	}

	private void notifyListeners(EChangeType changeType) {
		for (ITableDataChangeListener l : listeners) {
			l.dataChanged(changeType);
		}
	}

	private void swapRows(int row1Index, int row2Index) {
		List<List<String>> categoryMatrix = getDataMatrix();
		List<String> copyRow1 = new ArrayList<>(categoryMatrix.get(row1Index));
		categoryMatrix.set(row1Index, categoryMatrix.get(row2Index));
		categoryMatrix.set(row2Index, copyRow1);
		notifyListeners(EChangeType.STRUCTURAL);
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

	public List<List<String>> getDataMatrix() {
		return bodyDataProvider.getDataMatrix();
	}

	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if (rowSelectionCallback != null && (event instanceof CellSelectionEvent || event instanceof RowSelectionEvent)) {
			PositionCoordinate[] positions = selectionLayer.getSelectedCellPositions();
			if (positions.length >= 1)
				rowSelectionCallback.on(positions[0].rowPosition);
		}
	}

	public void update() {
		updateColors();
		table.refresh();
	}

	private void updateColors() {
		disposeColors();
		for (List<String> row : bodyDataProvider.getDataMatrix()) {
			org.caleydo.core.util.color.Color c = new org.caleydo.core.util.color.Color(row.get(3));
			int[] rgba = c.getIntRGBA();
			colorRegistry.put(row.get(3), new Color(Display.getCurrent(), rgba[0], rgba[1], rgba[2]));
		}
	}

	public void selectRow(int rowIndex) {
		selectionLayer.selectRow(0, rowIndex, false, false);
	}

	public void disposeColors() {
		for (Color color : colorRegistry.values()) {
			color.dispose();
		}
		colorRegistry.clear();
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		listeners.clear();
		disposeColors();
	}

	/**
	 * @param rowSelectionCallback
	 *            setter, see {@link rowSelectionCallback}
	 */
	public void setRowSelectionCallback(IntegerCallback rowSelectionCallback) {
		this.rowSelectionCallback = rowSelectionCallback;
	}

	public void registerTableDataChangeListener(ITableDataChangeListener listener) {
		if (listener != null)
			listeners.add(listener);
	}

	public void unregisterTableDataChangeListener(ITableDataChangeListener listener) {
		if (listener != null)
			listeners.remove(listener);
	}
}
