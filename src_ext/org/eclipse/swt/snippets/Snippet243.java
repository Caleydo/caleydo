/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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
 * Text snippet: type in one text, output to another
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;

public class Snippet243 {

public static void main(String [] args) {
	final Display display = new Display();
	Shell shell = new Shell(display);
	shell.setLayout(new FillLayout ());
	final Text text0 = new Text (shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	final Text text1 = new Text (shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	text0.addVerifyListener (new VerifyListener () {
		public void verifyText (VerifyEvent event) {
			text1.setTopIndex (text0.getTopIndex ());
			text1.setSelection (event.start, event.end);
			text1.insert (event.text);
		}
	});
	shell.setBounds(10, 10, 200, 200);
	shell.open ();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	display.dispose();
}
}
