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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.io.gui.dataimport.widget.IntegerCallback;
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
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
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
public class CategoryTable extends AMatrixBasedTableWidget implements ILayerListener {

	private static final String EDITABLE = "EDITABLE";
	private static final String NON_EDITABLE = "NON_EDITABLE";

	public static final String[] COLUMN_HEADERS = { "Value", "Count", "Name", "Color" };

	private Object layoutData;

	private SelectionLayer selectionLayer;

	private IntegerCallback rowSelectionCallback;

	private Map<String, Color> colorRegistry = new HashMap<>();

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
	public CategoryTable(Composite parent, Object layoutData, IntegerCallback rowSelectionCallback) {
		super(parent);
		this.layoutData = layoutData;
		this.rowSelectionCallback = rowSelectionCallback;
		bodyDataProvider = new MatrixBasedBodyDataProvider(null, 1);
		buildTable(bodyDataProvider, new ColumnHeaderDataProvider(), new LineNumberRowHeaderDataProvider(1));
	}

	@Override
	public void createTableFromMatrix(List<List<String>> dataMatrix, int numColumns) {
		bodyDataProvider = new MatrixBasedBodyDataProvider(dataMatrix, numColumns);
		updateColors();

		buildTable(bodyDataProvider, new ColumnHeaderDataProvider(),
				new LineNumberRowHeaderDataProvider(dataMatrix.size()));
	}

	private void buildTable(MatrixBasedBodyDataProvider bodyDataProvider, ColumnHeaderDataProvider columnDataProvider,
			LineNumberRowHeaderDataProvider rowDataProvider) {

		if (table != null) { // cleanup old
			this.table.dispose();
			this.table = null;
		}

		final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);

		selectionLayer = new SelectionLayer(bodyDataLayer);
		selectionLayer.addLayerListener(this);
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
		// gridData.heightHint = 300;
		// gridData.widthHint = 800;
		table.setLayoutData(layoutData);

		table.addConfiguration(new DefaultNatTableStyleConfiguration());
		ColumnOverrideLabelAccumulator acc = new ColumnOverrideLabelAccumulator(bodyDataLayer);
		bodyDataLayer.setConfigLabelAccumulator(acc);
		acc.registerColumnOverrides(0, NON_EDITABLE);
		acc.registerColumnOverrides(1, NON_EDITABLE);
		acc.registerColumnOverrides(2, EDITABLE);
		acc.registerColumnOverrides(3, "COLOR");
		final ColorCellPainter colorCellPainter = new ColorCellPainter();

		table.addConfiguration(new AbstractRegistryConfiguration() {

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
						IEditableRule.ALWAYS_EDITABLE, DisplayMode.EDIT, EDITABLE);

				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, colorCellPainter,
						DisplayMode.NORMAL, "COLOR");

				Style cellStyle = new Style();

				cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_NORMAL_SHADOW);
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
						NON_EDITABLE);

				// cellStyle = new Style();
				// cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
				// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
				// DisplayMode.NORMAL,
				// "WHITE");

			}

			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				uiBindingRegistry.registerDoubleClickBinding(new CellPainterMouseEventMatcher(GridRegion.BODY,
						MouseEventMatcher.LEFT_BUTTON, colorCellPainter), new ChangeColorAction());
			}
		});

		table.configure();
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

	public List<List<String>> getDataMatrix() {
		return bodyDataProvider.getDataMatrix();
	}

	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if (event instanceof CellSelectionEvent || event instanceof RowSelectionEvent) {
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
		dispose();
		for (List<String> row : bodyDataProvider.getDataMatrix()) {
			org.caleydo.core.util.color.Color c = new org.caleydo.core.util.color.Color(row.get(3));
			int[] rgba = c.getIntRGBA();
			colorRegistry.put(row.get(3), new Color(Display.getCurrent(), rgba[0], rgba[1], rgba[2]));
		}
	}

	public void selectRow(int rowIndex) {
		selectionLayer.selectRow(0, rowIndex, false, false);
	}

	public void dispose() {
		for (Color color : colorRegistry.values()) {
			color.dispose();
		}
		colorRegistry.clear();
	}
}
