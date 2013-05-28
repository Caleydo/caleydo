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
	public Composite create(Composite parent, WizardPage page) {
		page.setPageComplete(true);

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

		return composite;
	}

	@Override
    public IStartupProcedure create() {
		return new GenericGUIStartupProcedure();
    }

}
