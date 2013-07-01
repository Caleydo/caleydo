/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.table;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

/**
 * @author Christian Partl
 *
 */
public class NatTableToolTip extends DefaultToolTip {

	private NatTable natTable;

	public NatTableToolTip(NatTable natTable) {
		super(natTable, ToolTip.NO_RECREATE, false);
		this.natTable = natTable;
	}

	@Override
	protected Object getToolTipArea(Event event) {
		int col = natTable.getColumnPositionByX(event.x);
		int row = natTable.getRowPositionByY(event.y);

		return new Point(col, row);
	}

	@Override
	protected String getText(Event event) {
		int col = natTable.getColumnPositionByX(event.x);
		int row = natTable.getRowPositionByY(event.y);
		ILayerCell cell = natTable.getCellByPosition(col, row);
		if (cell == null)
			return null;
		Object dataValue = cell.getDataValue();

		if (dataValue == null)
			return null;

		if (dataValue instanceof String)
			return cell.getDataValue().toString();
		if (row == 0)
			return new Integer(col).toString();
		return new Integer(row).toString();
	}

	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {
		// This is where you could get really creative with your tooltips...
		return super.createToolTipContentArea(event, parent);
	}

}
