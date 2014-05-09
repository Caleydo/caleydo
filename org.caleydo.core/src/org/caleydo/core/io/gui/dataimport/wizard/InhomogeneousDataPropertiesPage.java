/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataDescriptionUtil;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.io.gui.dataimport.widget.table.ColumnConfigTable;
import org.caleydo.core.util.base.IntegerCallback;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Page for defining properties of inhomogeneous datasets.
 *
 * @author Christian Partl
 *
 */
public class InhomogeneousDataPropertiesPage extends AImportDataPage<DataImportWizard> {

	public static final String PAGE_NAME = "Inhomogeneous Dataset Properties";

	public static final String PAGE_DESCRIPTION = "Specify data properties for individual columns.";

	protected ColumnConfigTable table;

	// protected Button setColumnPropertiesButton;

	/**
	 * Determines whether this page should init its widgets from the {@link DataDescription} .
	 */
	protected boolean initColumnDescriptions = true;

	protected Composite parentComposite;

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
	protected InhomogeneousDataPropertiesPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	protected void createGuiElements(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, true));

		Label descriptionLabel = new Label(parentComposite, SWT.WRAP);
		descriptionLabel
				.setText("In an inhomogeneous dataset the properties of each column need to be configured individually. \nYou can do so by clicking the \'Set Properties\' button at the top of each column.");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.widthHint = 400;
		// setColumnPropertiesButton = new Button(parentComposite, SWT.PUSH);
		// setColumnPropertiesButton.setText("Set Column Properties");
		// setColumnPropertiesButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// int columnIndex = table.getSelectedColumn();
		// if (columnIndex != -1)
		// defineColumnProperties(columnIndex);
		// }
		// });

		table = new ColumnConfigTable(parentComposite, new IntegerCallback() {

			@Override
			public void on(int data) {
				defineColumnProperties(data);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void defineColumnProperties(int columnIndex) {
		List<ColumnDescription> columnDescriptions = dataSetDescription.getOrCreateParsingPattern();
		ColumnDescription columnDescription = columnDescriptions.get(columnIndex);
		DataImportWizard wizard = getWizard();
		String columnCaption = wizard.getFilteredRowOfColumnIDs().get(columnIndex);
		ColumnDataPropertiesDialog dialog;
		if (columnDescription.getDataDescription().getCategoricalClassDescription() != null) {
			dialog = new ColumnDataPropertiesDialog(getShell(), (CategoricalClassDescription<String>) columnDescription
					.getDataDescription().getCategoricalClassDescription(), wizard.getFilteredDataMatrix(),
					columnIndex, columnCaption);
		} else {
			dialog = new ColumnDataPropertiesDialog(getShell(), columnDescription.getDataDescription()
					.getNumericalProperties(), columnDescription.getDataDescription().getRawDataType(),
					wizard.getFilteredDataMatrix(), columnIndex, columnCaption);
		}
		int status = dialog.open();

		if (status == Window.OK) {
			switch (dialog.getDataClass()) {
			case NATURAL_NUMBER:
			case REAL_NUMBER:
				NumericalProperties numericalProperties = dialog.getNumericalProperties();
				EDataType dataType = dialog.getDataType();
				columnDescription.setDataDescription(new DataDescription(dialog.getDataClass(), dataType,
						numericalProperties));
				break;
			case CATEGORICAL:
				CategoricalClassDescription<String> categoricalClassDescription = dialog
						.getCategoricalClassDescription();
				columnDescription.setDataDescription(new DataDescription(EDataClass.CATEGORICAL, EDataType.STRING,
						categoricalClassDescription));
				break;
			case UNIQUE_OBJECT:
				columnDescription.setDataDescription(new DataDescription(EDataClass.UNIQUE_OBJECT, EDataType.STRING));
			}
			table.update();
		}

	}

	@Override
	public void fillDataSetDescription() {
		// nothing to do, as column changes are directly saved in dataset description after the change

	}

	@Override
	public void setDataSetDescription(DataSetDescription dataSetDescription) {
		super.setDataSetDescription(dataSetDescription);
		initColumnDescriptions = false;
	}

	@Override
	public void pageActivated() {
		DataImportWizard wizard = getWizard();

		if (initColumnDescriptions) {
			// use default column description for each column
			ArrayList<ColumnDescription> inputPattern = new ArrayList<ColumnDescription>();
			List<List<String>> dataMatrix = getWizard().getFilteredDataMatrix();
			int matrixColumnIndex = 0;
			for (Integer selected : wizard.getSelectedColumns()) {
				int columnIndex = selected.intValue();
				if (columnIndex == dataSetDescription.getColumnOfRowIds())
					continue;
				DataDescription dataDescription = DataDescriptionUtil.createDataDescription(dataMatrix,
						matrixColumnIndex);
				inputPattern.add(new ColumnDescription(columnIndex, dataDescription));
				matrixColumnIndex++;
			}

			dataSetDescription.setParsingPattern(inputPattern);
		}

		table.createTableFromMatrix(wizard.getFilteredDataMatrix(), wizard.getFilteredRowOfColumnIDs(),
				wizard.getColumnOfRowIDs(), dataSetDescription.getOrCreateParsingPattern());
		parentComposite.layout(true);

		wizard.setChosenDataTypePage(this);
		wizard.getContainer().updateButtons();
		initColumnDescriptions = false;
	}

	@Override
	public IWizardPage getPreviousPage() {
		return getWizard().getLoadDataSetPage();
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
