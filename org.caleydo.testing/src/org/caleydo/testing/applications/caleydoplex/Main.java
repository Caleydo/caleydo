package org.caleydo.testing.applications.caleydoplex;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Main {

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());

		Button connectButton = new Button(shell, SWT.PUSH);
		connectButton.setText("connect to Deskotheque");

		Button publishButton = new Button(shell, SWT.PUSH);
		publishButton.setText("publish");
		publishButton.addSelectionListener(new PublishListener());

		Button obtainButton = new Button(shell, SWT.PUSH);
		obtainButton.setText("obtain");
		obtainButton.addSelectionListener(new ObtainListener());

		Label label = new Label(shell, SWT.NULL);
		label.setText("<no message>");

		shell.setSize(300, 300);
		shell.open();

		DeskothequeManager deskothequeManager = new DeskothequeManager();
		connectButton.addSelectionListener(new ConnectListener(shell,
				deskothequeManager));

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		deskothequeManager.destroy();

		display.dispose();

	}
}
