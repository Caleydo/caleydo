/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.generic.internal;

import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.kohsuke.args4j.Option;

/**
 * @author Samuel Gratzl
 *
 */
public class LoadGenericStartupAddon implements IStartupAddon {
	private static final int WIDTH = 400;

	@Option(name = "-load")
	private boolean loadViaCmd;

	@Override
	public boolean init() {
		if (loadViaCmd)
			return true;
		return false;
	}

	@Override
	public Composite create(Composite parent, WizardPage page, Listener listener) {
		// page.setPageComplete(true);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label geneticDataDescription = new Label(composite, SWT.WRAP);
		geneticDataDescription.setText("Load tabular data wich does not fall under the genetic type, "
				+ "i.e., that does not contain common gene identifiers. \n");
		geneticDataDescription.setBackground(composite.getBackground());
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.widthHint = WIDTH;
		geneticDataDescription.setLayoutData(gridData);

		Button buttonNewProject = new Button(composite, SWT.RADIO);
		buttonNewProject.setText("Load data from file (CSV, TXT)");
		buttonNewProject.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		buttonNewProject.setSelection(true);
		buttonNewProject.addListener(SWT.Selection, listener);

		return composite;
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public IStartupProcedure create() {
		return new GenericGUIStartupProcedure();
	}

}
