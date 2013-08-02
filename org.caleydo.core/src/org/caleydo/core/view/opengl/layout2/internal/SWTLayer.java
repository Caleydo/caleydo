/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.ISWTLayer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Samuel Gratzl
 *
 */
public class SWTLayer implements ISWTLayer {
	private final IGLCanvas canvas;
	/**
	 * cache current cursor to avoid setting to the same value
	 */
	private int cursor = -1;

	/**
	 * @param asComposite
	 */
	public SWTLayer(IGLCanvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public final IPickingListener createTooltip(ILabeled label) {
		return canvas.createTooltip(label);
	}

	@Override
	public final void showContextMenu(Iterable<? extends AContextMenuItem> items) {
		canvas.showPopupMenu(items);
	}

	@Override
	public final void setCursor(final int swtCursorConst) {
		if (cursor == swtCursorConst)
			return;
		cursor = swtCursorConst;
		run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				canvas.setCursor(swtCursorConst < 0 ? null : display.getSystemCursor(swtCursorConst));
			}
		});
	}

	@Override
	public void resetCursor() {
		setCursor(-1);
	}

	@Override
	public void run(final ISWTLayerRunnable runnable) {
		final Composite c = canvas.asComposite();
		final Display d = c.getDisplay();
		d.asyncExec(new Runnable() {
			@Override
			public void run() {
				runnable.run(d, c);
			}
		});
	}
}
