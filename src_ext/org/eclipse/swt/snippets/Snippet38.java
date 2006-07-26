/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.snippets;

/*
 * Table example snippet: create a table (columns, headers, lines)
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class Snippet38 {

public static void main (String [] args) {
	Display display = new Display ();
	Shell shell = new Shell (display);
	Table table = new Table (shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
	table.setLinesVisible (true);
	table.setHeaderVisible (true);
	String[] titles = {" ", "C", "!", "Description", "Resource", "In Folder", "Location"};
	for (int i=0; i<titles.length; i++) {
		TableColumn column = new TableColumn (table, SWT.NONE);
		column.setText (titles [i]);
	}	
	int count = 128;
	for (int i=0; i<count; i++) {
		TableItem item = new TableItem (table, SWT.NONE);
		item.setText (0, "x");
		item.setText (1, "y");
		item.setText (2, "!");
		item.setText (3, "this stuff behaves the way I expect");
		item.setText (4, "almost everywhere");
		item.setText (5, "some.folder");
		item.setText (6, "line " + i + " in nowhere");
	}
	for (int i=0; i<titles.length; i++) {
		table.getColumn (i).pack ();
	}	
	table.setSize (table.computeSize (SWT.DEFAULT, 200));
	shell.pack ();
	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
} 
