package org.caleydo.core.view.opengl.util.overlay.contextmenu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MenuCreator {
	public Menu create(Control parent) {
		Menu menu = new Menu(parent);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				System.out.println("select");
			}
		});

		item.setText("push it");
		return menu;
	}
}
