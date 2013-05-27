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
import org.caleydo.core.io.gui.dataimport.widget.table.ColumnConfigTableWidget;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Page for defining properties of inhomogeneous datasets.
 *
 * @author Christian Partl
 *
 */
public class InhomogeneousDataPropertiesPage extends AImportDataPage {

	public static final String PAGE_NAME = "Inhomogeneous Dataset Properties";

	public static final String PAGE_DESCRIPTION = "Specify data properties for individual columns.";

	protected ColumnConfigTableWidget table;

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
	protected InhomogeneousDataPropertiesPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, true));

		table = new ColumnConfigTableWidget(parentComposite);
		setControl(parentComposite);
	}

	@Override
	public void fillDataSetDescription() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pageActivated() {
		DataImportWizard wizard = (DataImportWizard) getWizard();
		table.createTableFromMatrix(wizard.getFilteredDataMatrix(), wizard.getFilteredRowOfColumnIDs(),
				wizard.getColumnOfRowIDs());

		wizard.setChosenDataTypePage(this);
		wizard.getContainer().updateButtons();
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
