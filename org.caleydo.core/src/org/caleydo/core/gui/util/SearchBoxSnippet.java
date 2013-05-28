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