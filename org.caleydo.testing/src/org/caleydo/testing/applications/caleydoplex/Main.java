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
package org.caleydo.testing.applications.caleydoplex;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("Caleydoplex Tester"); 

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
		
		Button linkButton = new Button(shell, SWT.PUSH);
		linkButton.setText("show links"); 

		Label label = new Label(shell, SWT.NULL);
		label.setText("<no message>");

		shell.setSize(300, 300);
		shell.open();
		shell.setText("Deskotheque Tester"); 

		DeskothequeManager deskothequeManager = new DeskothequeManager();
		connectButton.addSelectionListener(new ConnectListener(shell,
				deskothequeManager));
		linkButton.addSelectionListener(new LinkListener(shell, 
				deskothequeManager)); 

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		deskothequeManager.destroy();

		display.dispose();

	}
}
