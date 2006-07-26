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
 * UI Automation (for testing tools) snippet: post key events
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.0
 */
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class Snippet146 {

public static void main(String[] args) {
	final Display display = new Display();
	final Shell shell = new Shell(display);
	final Text text = new Text(shell, SWT.BORDER);
	text.setSize(text.computeSize(150, SWT.DEFAULT));
	shell.pack();
	shell.open();
	new Thread(){
		public void run(){
			String string = "Love the method.";
			for (int i = 0; i < string.length(); i++) {
				char ch = string.charAt(i);
				boolean shift = Character.isUpperCase(ch);
				ch = Character.toLowerCase(ch);
				if (shift) {
					Event event = new Event();
					event.type = SWT.KeyDown;
					event.keyCode = SWT.SHIFT;
					display.post(event);	
				}
				Event event = new Event();
				event.type = SWT.KeyDown;
				event.character = ch;
				display.post(event);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {}
				event.type = SWT.KeyUp;
				display.post(event);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				if (shift) {
					event = new Event();
					event.type = SWT.KeyUp;
					event.keyCode = SWT.SHIFT;
					display.post(event);	
				}
			}
		}	
	}.start();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	display.dispose();
}
}
