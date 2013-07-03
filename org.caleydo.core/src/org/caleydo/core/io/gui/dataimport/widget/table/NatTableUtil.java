/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
