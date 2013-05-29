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

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.io.gui.dataimport.widget.table.ColumnConfigTableWidget;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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

	protected Button setColumnPropertiesButton;

	/**
	 * Determines whether this page should init its widgets from the {@link DataDescription} .
	 */
	protected boolean initColumnDescriptions = true;

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

		setColumnPropertiesButton = new Button(parentComposite, SWT.PUSH);
		setColumnPropertiesButton.setText("Set Column Properties");
		setColumnPropertiesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int columnIndex = table.getSelectedColumn();
				if (columnIndex != -1)
					defineColumnProperties(columnIndex);
			}
		});

		table = new ColumnConfigTableWidget(parentComposite);
		setControl(parentComposite);
	}

	@SuppressWarnings("unchecked")
	private void defineColumnProperties(int columnIndex) {
		List<ColumnDescription> columnDescriptions = dataSetDescription.getOrCreateParsingPattern();
		ColumnDescription columnDescription = columnDescriptions.get(columnIndex);
		DataImportWizard wizard = getWizard();
		ColumnDataPropertiesDialog dialog;
		if (columnDescription.getDataDescription().getCategoricalClassDescription() != null) {
			dialog = new ColumnDataPropertiesDialog(getShell(), (CategoricalClassDescription<String>) columnDescription
					.getDataDescription().getCategoricalClassDescription(), wizard.getFilteredDataMatrix(), columnIndex);
		} else {
			dialog = new ColumnDataPropertiesDialog(getShell(), columnDescription.getDataDescription()
					.getNumericalProperties(), wizard.getFilteredDataMatrix(), columnIndex);
		}
		int status = dialog.open();

		if (status == Window.OK) {
			if (dialog.isNumericalData()) {
				NumericalProperties numericalProperties = dialog.getNumericalProperties();
				columnDescription.setDataDescription(new DataDescription(EDataClass.REAL_NUMBER, EDataType.FLOAT,
						numericalProperties));
			} else {
				CategoricalClassDescription<String> categoricalClassDescription = dialog
						.getCategoricalClassDescription();
				columnDescription.setDataDescription(new DataDescription(EDataClass.CATEGORICAL, EDataType.STRING,
						categoricalClassDescription));
			}

			table.update();
		}

	}

	@Override
	public void fillDataSetDescription() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pageActivated() {
		DataImportWizard wizard = getWizard();

		if (initColumnDescriptions) {
			// use default column description for each column
			ArrayList<ColumnDescription> inputPattern = new ArrayList<ColumnDescription>();
			DataDescription dataDescription = new DataDescription(EDataClass.REAL_NUMBER, EDataType.FLOAT,
					new NumericalProperties());

			for (Integer selected : wizard.getSelectedColumns()) {
				int columnIndex = selected.intValue();
				if (columnIndex == dataSetDescription.getColumnOfRowIds())
					continue;
				inputPattern.add(new ColumnDescription(columnIndex, dataDescription));
			}

			dataSetDescription.setParsingPattern(inputPattern);
		}

		table.createTableFromMatrix(wizard.getFilteredDataMatrix(), wizard.getFilteredRowOfColumnIDs(),
				wizard.getColumnOfRowIDs(), dataSetDescription.getOrCreateParsingPattern());

		wizard.setChosenDataTypePage(this);
		wizard.getContainer().updateButtons();
		initColumnDescriptions = false;
	}

	@Override
	public IWizardPage getPreviousPage() {
		return getWizard().getDataSetTypePage();
	}

	@Override
	public IWizardPage getNextPage() {
		return getWizard().getAddGroupingsPage();
	}

	/**
	 * @param initColumnDescriptions
	 *            setter, see {@link initColumnDescriptions}
	 */
	public void setInitColumnDescriptions(boolean initColumnDescriptions) {
		this.initColumnDescriptions = initColumnDescriptions;
	}

	/**
	 * @return the initColumnDescriptions, see {@link #initColumnDescriptions}
	 */
	public boolean isInitColumnDescriptions() {
		return initColumnDescriptions;
	}

}
