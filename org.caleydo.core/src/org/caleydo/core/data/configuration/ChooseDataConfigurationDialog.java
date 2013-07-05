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
package org.caleydo.core.data.configuration;

import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.io.gui.IDataOKListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ChooseDataConfigurationDialog extends Dialog implements IDataOKListener {

	private DataConfiguration dataConfiguration;

	private DataChooserComposite dataChooserComposite;
	private String windowTitle;
	/**
	 * All dataDomains that shall be available to choose from.
	 */
	private List<ATableBasedDataDomain> supportedDataDomains;

	public ChooseDataConfigurationDialog(Shell parent, String windowTitle) {
		super(parent);
		this.windowTitle = windowTitle;

	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText(windowTitle);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		createGUI(parent);
		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		if (!dataChooserComposite.isOK())
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		return control;
	}

	private void createGUI(Composite shell) {
		shell.setLayout(new GridLayout(1, false));
		dataChooserComposite = new DataChooserComposite(this, shell,
				supportedDataDomains, SWT.NONE);

	}

	@Override
	protected void okPressed() {
		super.okPressed();
		dataConfiguration = dataChooserComposite.getDataConfiguration();
	}

	/**
	 * @return the dataConfiguration, see {@link #dataConfiguration}
	 */
	public DataConfiguration getDataConfiguration() {
		return dataConfiguration;
	}

	public static void main(String[] args) {
		ChooseDataConfigurationDialog dialog = new ChooseDataConfigurationDialog(
				new Shell(), "Bla");
		dialog.open();
	}

	@Override
	public void dataOK() {
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	/**
	 * @param supportedDataDomains
	 *            setter, see {@link #supportedDataDomains}
	 */
	public void setSupportedDataDomains(List<ATableBasedDataDomain> supportedDataDomains) {
		this.supportedDataDomains = supportedDataDomains;
	}
}
