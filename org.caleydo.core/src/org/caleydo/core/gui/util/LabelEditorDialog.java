/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LabelEditorDialog
	extends Dialog {
	String sLabel;

	/**
	 * @param parent
	 */
	public LabelEditorDialog(Shell parent) {
		super(parent);
	}

	public String open(String sInitialLabel) {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		shell.setText("Change Label");
		shell.setLayout(new GridLayout(2, false));

		Label label = new Label(shell, SWT.NULL);
		label.setText("Enter caption:");

		final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(150, 20);
		text.setLayoutData(data);
		text.setText(sInitialLabel);
		text.selectAll();

		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
					case SWT.CR:
						sLabel = text.getText();
					case SWT.ESC:
						shell.dispose();
						break;
				}
			}
		});

		final Button buttonOK = new Button(shell, SWT.PUSH);
		buttonOK.setText("Ok");
		buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText("Cancel");

		text.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					sLabel = text.getText();
					buttonOK.setEnabled(true);
				}
				catch (Exception e) {
					buttonOK.setEnabled(false);
				}
			}
		});

		buttonOK.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});

		buttonCancel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				sLabel = null;
				shell.dispose();
			}
		});

		shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE) {
					event.doit = false;
				}
			}
		});

		shell.pack();

		// Set shell to open in the center of the screen
		Monitor primary = Display.getCurrent().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);

		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return sLabel;
	}

	public static void main(String[] args) {
		Shell shell = new Shell();
		LabelEditorDialog dialog = new LabelEditorDialog(shell);
		System.out.println(dialog.open("old"));
	}
}
