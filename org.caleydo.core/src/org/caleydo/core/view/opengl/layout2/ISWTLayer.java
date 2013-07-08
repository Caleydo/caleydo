/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Samuel Gratzl
 *
 */
public interface ISWTLayer {
	void setCursor(int swtCursorConst);

	void resetCursor();

	void run(ISWTLayerRunnable runnable);

	/**
	 * shows the context menu, defined by the given items
	 * 
	 * @param items
	 */
	void showContextMenu(Iterable<? extends AContextMenuItem> items);

	/**
	 * creates a gl canvas specific picking listener that shows the given label
	 * 
	 * @param label
	 * @return
	 */
	IPickingListener createTooltip(ILabeled label);

	public interface ISWTLayerRunnable {
		void run(Display display, Composite canvas);
	}
}
