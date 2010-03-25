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
					groupRep.getClusterNode().setNodeName(text.getText());
					groupRep.getClusterNode().setLabel(text.getText());
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

// Shell shell = new Shell(display);
// shell.setText("Dialog Example");
// shell.setSize(300, 200);
// shell.open();
//
// final Button button = new Button(shell, SWT.PUSH);
// button.setText("Rename Group");
// button.setBounds(20, 40, 80, 25);
//
// final Text text = new Text(shell, SWT.SHADOW_IN);
// text.setBounds(140, 40, 100, 25);
//
// // final Shell dialog = new Shell(shell, SWT.APPLICATION_MODAL
// // | SWT.DIALOG_TRIM);
// // dialog.setText("Change group name:");
// // dialog.setSize(250, 150);
//
// // final Button buttonOK = new Button(dialog, SWT.PUSH);
// // buttonOK.setText("OK");
// // buttonOK.setBounds(20, 55, 80, 25);
//
// // Button buttonCancel = new Button(dialog, SWT.PUSH);
// // buttonCancel.setText("Cancel");
// // buttonCancel.setBounds(120, 55, 80, 25);
//
//	
//
// // Listener listener = new Listener() {
// // public void handleEvent(Event event) {
// // if (event.widget == buttonOK) {
// // deleteFlag = true;
// // } else {
// // deleteFlag = false;
// // }
// // dialog.close();
// // }
// // };
//
// // buttonOK.addListener(SWT.Selection, listener);
// // buttonCancel.addListener(SWT.Selection, listener);
//
// // Listener buttonListener = new Listener() {
// // public void handleEvent(Event event) {
// // dialog.open();
// // }
// // };
//
// // button.addListener(SWT.Selection, buttonListener);
//
// // while (!dialog.isDisposed()) {
// // if (!display.readAndDispatch())
// // display.sleep();
// // }
//
// if (deleteFlag) {
// text.setText("File deleted.");
// } else {
// text.setText("File not deleted.");
// }
//
// while (!shell.isDisposed()) {
// if (!display.readAndDispatch())
// display.sleep();
// }
// display.dispose();
// return "Bla";
// }
