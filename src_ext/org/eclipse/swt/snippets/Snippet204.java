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
 * TextLayout example snippet: using the rise field of a TextStyle.
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.2
 */ 
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;

public class Snippet204 {
	
public static void main(String[] args) {
	Display display = new Display();
	Shell shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
	shell.setText("Modify Rise");
	FontData data = display.getSystemFont().getFontData()[0];
	Font font = new Font(display, data.getName(), 24, SWT.NORMAL);
	Font smallFont = new Font(display, data.getName(), 8, SWT.NORMAL);
	GC gc = new GC(shell);
	gc.setFont(smallFont);
	FontMetrics smallMetrics = gc.getFontMetrics();
	final int smallBaseline = smallMetrics.getAscent() + smallMetrics.getLeading();
	gc.setFont(font);
	FontMetrics metrics = gc.getFontMetrics();
	final int baseline = metrics.getAscent() + metrics.getLeading();
	gc.dispose();
	
	final TextLayout layout0 = new TextLayout(display);
	layout0.setText("SubscriptScriptSuperscript");
	layout0.setFont(font);
	TextStyle subscript0 = new TextStyle(smallFont, null, null);
	TextStyle superscript0 = new TextStyle(smallFont, null, null);
	superscript0.rise = baseline - smallBaseline;
	layout0.setStyle(subscript0, 0, 8);
	layout0.setStyle(superscript0, 15, 25);
		
	final TextLayout layout1 = new TextLayout(display);
	layout1.setText("SubscriptScriptSuperscript");
	layout1.setFont(font);
	TextStyle subscript1 = new TextStyle(smallFont, null, null);
	subscript1.rise = -smallBaseline;
	TextStyle superscript1 = new TextStyle(smallFont, null, null);
	superscript1.rise = baseline;
	layout1.setStyle(subscript1, 0, 8);
	layout1.setStyle(superscript1, 15, 25);
	
	shell.addListener(SWT.Paint, new Listener() {
		public void handleEvent(Event event) {
			Display display = event.display;
			GC gc = event.gc;
			
			Rectangle rect0 = layout0.getBounds();
			rect0.x += 10;
			rect0.y += 10;
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.fillRectangle(rect0);
			layout0.draw(gc, rect0.x, rect0.y);
			gc.setForeground(display.getSystemColor(SWT.COLOR_MAGENTA));
			gc.drawLine(rect0.x, rect0.y, rect0.x + rect0.width, rect0.y);
			gc.drawLine(rect0.x, rect0.y + baseline, rect0.x + rect0.width, rect0.y + baseline);
			gc.drawLine(rect0.x + rect0.width / 2, rect0.y, rect0.x + rect0.width / 2, rect0.y + rect0.height);
			
			Rectangle rect1 = layout1.getBounds();
			rect1.x += 10;
			rect1.y += 20 + rect0.height;
			gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc.fillRectangle(rect1);			
			layout1.draw(gc, rect1.x, rect1.y);
			
			gc.setForeground(display.getSystemColor(SWT.COLOR_MAGENTA));
			gc.drawLine(rect1.x, rect1.y + smallBaseline, rect1.x + rect1.width, rect1.y + smallBaseline);
			gc.drawLine(rect1.x, rect1.y + baseline + smallBaseline, rect1.x + rect1.width, rect1.y + baseline + smallBaseline);
			gc.drawLine(rect1.x + rect1.width / 2, rect1.y, rect1.x + rect1.width / 2, rect1.y + rect1.height);
		}
	});
	shell.open();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	layout0.dispose();
	layout1.dispose();
	smallFont.dispose();
	font.dispose();
	display.dispose();
}
}
