package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class ContextMenuCreator {

	private ArrayList<ContextMenuItem> menuItems = new ArrayList<ContextMenuItem>();
	
	private Composite parent;
	
	public void open(Composite parent) {

		this.parent = parent;
		
		final ContextMenuCreator menuCreator = this;

		Runnable runnable = new Runnable() {
			public void run() {
				AWTBridgePopupFix.showMenu(menuCreator);
			}
		};
		parent.getDisplay().asyncExec(runnable);
	}

	public Composite getParent() {
		return parent;
	}
	
	public void clear() {
		menuItems.clear();
	}
	
	public void addContextMenuItem(ContextMenuItem menuItem) {
		menuItems.add(menuItem);
	}

	public boolean hasMenuItems() {
		if (menuItems.size() > 0)
			return true;

		return false;
	}

	public Menu create(Control parent) {

		Menu menu = new Menu(parent);

		for (ContextMenuItem menuItem : menuItems) {
			menuItem.create(menu);
		}

		return menu;
	}
}
