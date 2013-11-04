/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.ArrayList;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.widget.CategoricalDataPropertiesWidget;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Page to specify categorical properties.
 *
 * @author Christian Partl
 *
 */
public class CategoricalDataPropertiesPage extends AImportDataPage {

	public static final String PAGE_NAME = "Categorical Dataset Properties";

	public static final String PAGE_DESCRIPTION = "Specify properties for the categorical dataset.";

	// protected DataTranspositionWidget dataTranspositionWidget;

	protected Composite parentComposite;

	protected CategoricalDataPropertiesWidget categoricalDataWidget;

	/**
	 * Determines whether this page should init its widgets from the {@link DataDescription} .
	 */
	protected boolean initFromDataDescription = true;

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
	protected CategoricalDataPropertiesPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	protected void createGuiElements(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, true));

		categoricalDataWidget = new CategoricalDataPropertiesWidget(parentComposite);

		// dataTranspositionWidget = new DataTranspositionWidget(parentComposite, getWizard(),
		// dataSetDescription.isTransposeMatrix());

	}

	@Override
	public void fillDataSetDescription() {

		// dataSetDescription.setTransposeMatrix(dataTranspositionWidget.isTransposition());

		dataSetDescription.getDataDescription().setCategoricalClassDescription(
				categoricalDataWidget.getCategoricalClassDescription());

		ArrayList<ColumnDescription> inputPattern = new ArrayList<ColumnDescription>();
		DataImportWizard wizard = getWizard();

		for (Integer selected : wizard.getSelectedColumns()) {
			int columnIndex = selected.intValue();
			if (columnIndex == dataSetDescription.getColumnOfRowIds())
				continue;
			inputPattern.add(new ColumnDescription(columnIndex, dataSetDescription.getDataDescription()));
		}

		dataSetDescription.setParsingPattern(inputPattern);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void pageActivated() {

		DataImportWizard wizard = getWizard();

		// categoricalDataWidget.updateCategories(wizard.getFilteredDataMatrix(), -1);
		if (initFromDataDescription) {
			categoricalDataWidget.updateCategories(wizard.getFilteredDataMatrix(), -1,
					(CategoricalClassDescription<String>) dataSetDescription.getDataDescription()
							.getCategoricalClassDescription());
		}
		// dataTranspositionWidget.update();

		// categoryTable.update();
		parentComposite.layout(true);

		wizard.setChosenDataTypePage(this);
		wizard.getContainer().updateButtons();
		initFromDataDescription = false;

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
	 * @param initFromDataDescription
	 *            setter, see {@link initFromDataDescription}
	 */
	public void setInitFromDataDescription(boolean initFromDataDescription) {
		this.initFromDataDescription = initFromDataDescription;
	}

	/**
	 * @return the initFromDataDescription, see {@link #initFromDataDescription}
	 */
	public boolean isInitFromDataDescription() {
		return initFromDataDescription;
	}

}
