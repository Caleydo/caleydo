/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * abstraction of the SWT interface, which is thread safe
 *
 * @author Samuel Gratzl
 *
 */
public interface ISWTLayer {
	/**
	 * set the cursor to a specific SWT cursor instance
	 *
	 * @param swtCursorConst
	 */
	void setCursor(int swtCursorConst);

	/**
	 * reset to normal cursor
	 */
	void resetCursor();

	/**
	 * run the given {@link ISWTLayerRunnable} within the SWT thread
	 *
	 * @param runnable
	 */
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

	/**
	 * creates a gl canvas specific picking listener that computes the label to show
	 * 
	 * @param label
	 * @return
	 */
	IPickingListener createTooltip(IPickingLabelProvider label);

	public interface ISWTLayerRunnable {
		void run(Display display, Composite canvas);
	}
}
