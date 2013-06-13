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

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CellPainterWrapper;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Creates a grid outline at the left, right, and bottom cell borders.
 * 
 * @author Christian
 *
 */
public class GridLineCellPainterDecorator extends CellPainterWrapper {

	public GridLineCellPainterDecorator() {
	}

	public GridLineCellPainterDecorator(ICellPainter painter) {
		super(painter);
	}

	private Color borderColor = GUIHelper.getColor(192, 192, 192);

	@Override
	public void paintCell(ILayerCell cell, GC gc, Rectangle rectangle, IConfigRegistry configRegistry) {

		super.paintCell(cell, gc, rectangle, configRegistry);

		Color foreground = gc.getForeground();

		gc.setForeground(borderColor);

		gc.drawLine(rectangle.x - 1, rectangle.y, rectangle.x - 1, rectangle.y + rectangle.height);
		gc.drawLine(rectangle.x - 1 + rectangle.width, rectangle.y, rectangle.x - 1 + rectangle.width, rectangle.y
				+ rectangle.height);
		// gc.setLineWidth(5);
		gc.drawLine(rectangle.x - 1, rectangle.y + rectangle.height - 1, rectangle.x - 1 + rectangle.width, rectangle.y
				+ rectangle.height - 1);

		gc.setForeground(foreground);

	}

}
