package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class ContextMenuCreator {

	private ArrayList<AContextMenuItem> menuItems = new ArrayList<AContextMenuItem>();

	private Composite parent;

	private AGLView view;

	public void open(AGLView view) {

		if (view.isRenderedRemote())
			this.view = (AGLView) view.getRemoteRenderingGLView();
		else
			this.view = view;

		this.parent = view.getParentComposite();

		final ContextMenuCreator menuCreator = this;

		Runnable runnable = new Runnable() {
			public void run() {

				Menu popupMenu = menuCreator.create(parent);
				PopupOverAwtHelper popupMenuOverAwtHelper = new PopupOverAwtHelper(popupMenu);
				Point cursorLocation = menuCreator.getParent().getDisplay().getCursorLocation();

				popupMenuOverAwtHelper.swtDirectShowMenu(cursorLocation.x, cursorLocation.y);
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

	public void addContextMenuItem(AContextMenuItem menuItem) {
		menuItems.add(menuItem);
	}

	public void addContextMenuItemContainer(AContextMenuItemContainer menuItemContainer) {
		menuItems.addAll(menuItemContainer.getContextMenuItems());
	}

	public boolean hasMenuItems() {
		if (menuItems.size() > 0)
			return true;

		return false;
	}

	public Menu create(Control parent) {

		Menu menu = new Menu(parent);

		for (AContextMenuItem menuItem : menuItems) {
			menuItem.create(menu);
		}

		return menu;
	}

	public AGLView getView() {
		return view;
	}
}
