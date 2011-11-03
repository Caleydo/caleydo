package org.caleydo.core.view.contextmenu;

import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class ContextMenuCreator {

	private ArrayList<AContextMenuItem> menuItems = new ArrayList<AContextMenuItem>();

	private Composite parent;

	private AGLView view;

	public void open(final AGLView view) {

		if (view.isRenderedRemote())
			this.view = (AGLView) view.getRemoteRenderingGLView();
		else
			this.view = view;

		this.parent = view.getParentComposite();

		final ContextMenuCreator menuCreator = this;

		Runnable runnable = new Runnable() {
			public void run() {

				// Menu popupMenu = menuCreator.create(parent);
				// PopupOverAwtHelper popupMenuOverAwtHelper = new PopupOverAwtHelper(popupMenu);
				// Point cursorLocation = menuCreator.getParent().getDisplay().getCursorLocation();
				//
				// popupMenuOverAwtHelper.swtDirectShowMenu(cursorLocation.x, cursorLocation.y);

				// new PopupDemo(view.getParentGLCanvas()).setVisible(true);

				final JPopupMenu popup = new JPopupMenu();
				JMenuItem menuItem1 = new JMenuItem("Option 1");
				popup.add(menuItem1);

				JMenuItem menuItem2 = new JMenuItem("Option 2");
				popup.add(menuItem2);

				view.getParentGLCanvas().addMouseListener(new MouseAdapter() {

					@Override
					public void mouseReleased(MouseEvent e) {

						int cursorLocationX =
							MouseInfo.getPointerInfo().getLocation().x
								- view.getParentGLCanvas().getLocationOnScreen().x;
						int cursorLocationY =
							MouseInfo.getPointerInfo().getLocation().y
								- view.getParentGLCanvas().getLocationOnScreen().y;
						popup.show(view.getParentGLCanvas().getParent(), cursorLocationX, cursorLocationY);

					}
				});

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
