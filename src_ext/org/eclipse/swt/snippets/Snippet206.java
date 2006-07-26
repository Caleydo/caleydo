/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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
 * A button with text and image
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.2
 */ 
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Snippet206 {

public static void main(String[] args) {
	Display display = new Display();
	Image image = display.getSystemImage(SWT.ICON_QUESTION);
	Shell shell = new Shell(display);
	shell.setLayout (new GridLayout());
	Button button = new Button(shell, SWT.PUSH);
	button.setImage(image);
	button.setText("Button");
	shell.setSize(300, 300);
	shell.open();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
}