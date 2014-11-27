/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import org.caleydo.core.io.gui.dataimport.widget.table.GridLineCellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.cell.AlternatingRowConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * @author Christian
 *
 */
public class ContingencyTableConfiguration extends AbstractRegistryConfiguration {

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		ICellPainter cellpainter = new TextPainter(false, true, false);
		ICellPainter headerPainter = new GridLineCellPainterDecorator(cellpainter);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, cellpainter);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, DisplayMode.NORMAL,
				GridRegion.COLUMN_HEADER);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, DisplayMode.NORMAL,
				GridRegion.CORNER);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, DisplayMode.SELECT,
				GridRegion.COLUMN_HEADER);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, DisplayMode.SELECT,
				GridRegion.CORNER);

		// Body style
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
		cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
		cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
		cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, GUIHelper.COLOR_WHITE);
		cellStyle.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
		cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.MIDDLE);

		// cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, null);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle);

		// Header style
		cellStyle = createDefaultHeaderStyle();

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				GridRegion.COLUMN_HEADER);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				GridRegion.CORNER);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.SELECT,
				GridRegion.COLUMN_HEADER);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.SELECT,
				GridRegion.CORNER);

		// cellStyle = createDefaultHeaderStyle();
		// cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				GridRegion.ROW_HEADER);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.SELECT,
				GridRegion.ROW_HEADER);

		cellStyle = createDefaultHeaderStyle();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				GridRegion.ROW_GROUP_HEADER);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.SELECT,
				GridRegion.ROW_GROUP_HEADER);

		// FontData templateFontData = GUIHelper.DEFAULT_FONT.getFontData()[0];
		// Font font = new Font(Display.getDefault(), new FontData(templateFontData.getName(), 8, SWT.NONE));
		// cellStyle.setAttributeValue(CellStyleAttributes.FONT, font);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
		// GridRegion.COLUMN_HEADER);

		cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				AlternatingRowConfigLabelAccumulator.ODD_ROW_CONFIG_TYPE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.SELECT,
				AlternatingRowConfigLabelAccumulator.ODD_ROW_CONFIG_TYPE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				AlternatingRowConfigLabelAccumulator.EVEN_ROW_CONFIG_TYPE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.SELECT,
				AlternatingRowConfigLabelAccumulator.EVEN_ROW_CONFIG_TYPE);
		//
		// cellStyle = new Style();
		// cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
		// AlternatingRowConfigLabelAccumulator.EVEN_ROW_CONFIG_TYPE);
	}

	protected Style createDefaultHeaderStyle() {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_BACKGROUND);
		cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_FOREGROUND);
		cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
		cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, GUIHelper.getColor(136, 212, 215));
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
		cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.MIDDLE);
		cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, null);
		cellStyle
				.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.getFont(new FontData("Verdana", 9, SWT.NORMAL)));
		return cellStyle;
	}


}
