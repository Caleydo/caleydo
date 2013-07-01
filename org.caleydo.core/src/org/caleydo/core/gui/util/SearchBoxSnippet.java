/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SearchBoxSnippet {

	public static void main(String[] args) {
		String items[] =
			{ "Lions", "Tigers", "Bears", "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf",
					"Hotel", "India", "Juliet", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa",
					"Quebec", "Romeo", "Sierra", "Tango", "Uniform", "Victor", "Whiskey", "X-Ray", "Yankee",
					"Zulu" };
		Display display = Display.getDefault();
		Shell shell1 = new Shell(display);
		shell1.setLayout(new GridLayout());
		shell1.setText("SearchBox");
		shell1.setLocation(400, 150);
		Label l = new Label(shell1, SWT.NORMAL);
		l.setText("Click any key to open list ...");
		SearchBox sb = new SearchBox(shell1, SWT.NONE);
		sb.setItems(items);
		shell1.pack();
		shell1.open();
		while (!shell1.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
