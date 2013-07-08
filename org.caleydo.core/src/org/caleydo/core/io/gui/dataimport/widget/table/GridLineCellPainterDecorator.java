/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
