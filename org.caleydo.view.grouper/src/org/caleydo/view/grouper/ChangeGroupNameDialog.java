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
package org.caleydo.view.grouper;

import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Alexander Lex
 * 
 */
public class ChangeGroupNameDialog {
	static boolean deleteFlag = false;

	public static void run(Display display, final GroupRepresentation groupRep) {

		// Shell shell = new Shell(display);
		// shell.pack();
		// shell.open();
		final Shell dialog = new Shell(display, SWT.DIALOG_TRIM);
		final Text text = new Text(dialog, SWT.SHADOW_IN);
		text.setBounds(140, 40, 100, 25);
		text.setText(groupRep.getName());
		final Button okButton = new Button(dialog, SWT.PUSH);
		okButton.setText("&OK");
		Button cancelButton = new Button(dialog, SWT.PUSH);
		cancelButton.setText("&Cancel");

		FormLayout form = new FormLayout();
		form.marginWidth = form.marginHeight = 8;
		dialog.setLayout(form);
		FormData okData = new FormData();
		okData.top = new FormAttachment(text, 8);
		okButton.setLayoutData(okData);
		FormData cancelData = new FormData();
		cancelData.left = new FormAttachment(okButton, 8);
		cancelData.top = new FormAttachment(okButton, 0, SWT.TOP);
		cancelButton.setLayoutData(cancelData);

		dialog.setDefaultButton(okButton);
		dialog.pack();
		dialog.open();

		Listener okListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.widget == okButton) {
					groupRep.getClusterNode().setLabel(text.getText(), false);
				}
				dialog.dispose();

			}
		};

		okButton.addListener(SWT.Selection, okListener);

		cancelButton.addListener(SWT.Selection, okListener);

		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}

}
