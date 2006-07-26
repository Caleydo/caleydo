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
 * Program example snippet: find the icon of the program that edits .bmp files
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.program.*;

public class Snippet32 {

public static void main (String [] args) {
	Display display = new Display ();
	Shell shell = new Shell (display);
	Label label = new Label (shell, SWT.NONE);
	label.setText ("Can't find icon for .bmp");
	Image image = null;
	Program p = Program.findProgram (".bmp");
	if (p != null) {
		ImageData data = p.getImageData ();
		if (data != null) {
			image = new Image (display, data);
			label.setImage (image);
		}
	}
	label.pack ();
	shell.pack ();
	shell.open ();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	if (image != null) image.dispose ();
	display.dispose ();
}

} 
