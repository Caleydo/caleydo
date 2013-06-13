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

/**
 * @author Christian
 *
 */
public class DefaultCaleydoNatTableConfiguration extends AbstractRegistryConfiguration {

	protected boolean calculateCellSizeFromContent = false;

	public DefaultCaleydoNatTableConfiguration() {
	}

	public DefaultCaleydoNatTableConfiguration(boolean calculateCellSizeFromContent) {
		this.calculateCellSizeFromContent = calculateCellSizeFromContent;
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {

		ICellPainter cellpainter = new TextPainter(false, true, calculateCellSizeFromContent);
		ICellPainter headerPainter = new GridLineCellPainterDecorator(cellpainter);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, cellpainter);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, DisplayMode.NORMAL,
				GridRegion.COLUMN_HEADER);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, DisplayMode.NORMAL,
				GridRegion.CORNER);

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

		cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(254, 251, 243));
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				AlternatingRowConfigLabelAccumulator.ODD_ROW_CONFIG_TYPE);

		cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				AlternatingRowConfigLabelAccumulator.EVEN_ROW_CONFIG_TYPE);
	}

}
