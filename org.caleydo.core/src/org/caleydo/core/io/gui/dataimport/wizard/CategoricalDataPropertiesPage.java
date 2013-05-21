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
package org.caleydo.core.io.gui.dataimport.wizard;

import org.caleydo.core.io.DataSetDescription;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Christian
 *
 */
public class CategoricalDataPropertiesPage extends AImportDataPage {

	public static final String PAGE_NAME = "Categorical Dataset Properties";

	public static final String PAGE_DESCRIPTION = "Specify properties for the categorical dataset.";

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
	protected CategoricalDataPropertiesPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		Composite parentComposite = new Composite(parent, SWT.NONE);
		setControl(parentComposite);
	}

	@Override
	public void fillDataSetDescription() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pageActivated() {
		((DataImportWizard) getWizard()).setChosenDataTypePage(this);
		((DataImportWizard) getWizard()).getContainer().updateButtons();

	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((DataImportWizard) getWizard()).getDataSetTypePage();
	}

	@Override
	public IWizardPage getNextPage() {
		return ((DataImportWizard) getWizard()).getAddGroupingsPage();
	}

}
