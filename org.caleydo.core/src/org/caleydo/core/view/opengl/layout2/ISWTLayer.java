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
