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
 * Control example snippet: print mouse state and button (down, move, up)
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.1
 */
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class Snippet62 {

public static void main (String [] args) {
	Display display = new Display ();
	final Shell shell = new Shell (display);
	Listener listener = new Listener () {
		public void handleEvent (Event e) {
			String string = "Unknown";
			switch (e.type) {
				case SWT.MouseDown: string = "DOWN"; break;
				case SWT.MouseMove: string = "MOVE"; break;
				case SWT.MouseUp: string = "UP"; break;
			}
			string +=": button: " + e.button + ", ";
			string += "stateMask=0x" + Integer.toHexString (e.stateMask);
			if ((e.stateMask & SWT.BUTTON1) != 0) string += " BUTTON1";
			if ((e.stateMask & SWT.BUTTON2) != 0) string += " BUTTON2";
			if ((e.stateMask & SWT.BUTTON3) != 0) string += " BUTTON3";
			if ((e.stateMask & SWT.BUTTON4) != 0) string += " BUTTON4";
			if ((e.stateMask & SWT.BUTTON5) != 0) string += " BUTTON5";
			System.out.println (string);
		}
	};
	shell.addListener (SWT.MouseDown, listener);
	shell.addListener (SWT.MouseMove, listener);
	shell.addListener (SWT.MouseUp, listener);
	shell.pack ();
	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
} 
