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
 * Text example snippet: stop CR from going to the default button
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Snippet147  {
public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell(display);
	shell.setLayout(new GridLayout());
	Combo combo = new Combo(shell, SWT.NONE);
	combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	combo.setText("Here is some text");
	combo.addSelectionListener(new SelectionAdapter() {
		public void widgetDefaultSelected(SelectionEvent e) {
			System.out.println("Combo default selected (overrides default button)");
		}
	});
	combo.addTraverseListener(new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			if (e.detail == SWT.TRAVERSE_RETURN) {
				e.doit = false;
				e.detail = SWT.TRAVERSE_NONE;
			}
		}
	});
	Button button = new Button(shell, SWT.PUSH);
	button.setText("Ok");
	button.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			System.out.println("Button selected");
		}
	});
	shell.setDefaultButton(button);
	shell.pack();
	shell.open();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch())
			display.sleep();
	}
	display.dispose();
}
}
