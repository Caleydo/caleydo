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
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.painter.layer.NatGridLayerPainter;
import org.eclipse.swt.graphics.Point;

/**
 * Utility class for styling a {@link NatTable}.
 *
 * @author Christian Partl
 * 
 */
public final class NatTableUtil {

	private NatTableUtil() {
	}

	public static void applyDefaultNatTableStyling(NatTable table) {
		table.addConfiguration(new DefaultCaleydoNatTableConfiguration());
		DefaultToolTip toolTip = new NatTableToolTip(table);
		// toolTip.setBackgroundColor(natTable.getDisplay().getSystemColor(SWT.COLOR_RED));
		// toolTip.setPopupDelay(500);
		toolTip.activate();
		toolTip.setShift(new Point(10, 10));

		NatGridLayerPainter layerPainter = new NatGridLayerPainter(table);
		table.setLayerPainter(layerPainter);
	}

}
