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
