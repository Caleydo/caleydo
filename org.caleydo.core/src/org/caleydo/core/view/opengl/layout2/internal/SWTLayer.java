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
