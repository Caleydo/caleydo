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
package org.caleydo.core.view.contextmenu;

import java.awt.MouseInfo;
import java.util.ArrayList;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.widgets.Composite;

public class ContextMenuCreator {

	private ArrayList<AContextMenuItem> menuItems = new ArrayList<AContextMenuItem>();

	private Composite parent;

	private AGLView view;

	/**
	 * 
	 */
	public ContextMenuCreator() {
	}

	public void open(final AGLView view) {

		if (view.isRenderedRemote())
			this.view = (AGLView) view.getRemoteRenderingGLView();
		else
			this.view = view;

		this.parent = view.getParentComposite();

		final ContextMenuCreator menuCreator = this;
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				final JPopupMenu popup = menuCreator.create();

				int cursorLocationX = MouseInfo.getPointerInfo().getLocation().x
						- view.getParentGLCanvas().getLocationOnScreen().x;
				int cursorLocationY = MouseInfo.getPointerInfo().getLocation().y
						- view.getParentGLCanvas().getLocationOnScreen().y;
				popup.show(view.getParentGLCanvas().getParent(), cursorLocationX, cursorLocationY);
				
			}
		});
		
	}

	public Composite getParent() {
		return parent;
	}

	public synchronized void clear() {
		menuItems.clear();
	}

	public synchronized void addContextMenuItem(AContextMenuItem menuItem) {
		menuItems.add(menuItem);
	}

	public synchronized void addContextMenuItemContainer(AContextMenuItemContainer menuItemContainer) {
		menuItems.addAll(menuItemContainer.getContextMenuItems());
	}

	public synchronized boolean hasMenuItems() {
		if (menuItems.size() > 0)
			return true;

		return false;
	}

	public synchronized JPopupMenu create() {

		JPopupMenu menu = new JPopupMenu();

		for (AContextMenuItem menuItem : menuItems) {
			menuItem.create(menu);
		}

		return menu;
	}

	public AGLView getView() {
		return view;
	}
}
