/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.ArrayList;
import java.util.EnumSet;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.widget.DataTranspositionWidget;
import org.caleydo.core.io.gui.dataimport.widget.numerical.NumericalDataPropertiesCollectionWidget;
import org.caleydo.core.io.gui.dataimport.widget.numerical.NumericalDataPropertiesCollectionWidget.ENumericalDataProperties;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Page that offers the possibility to transform the dataset, such as data logarithmation or table transpose.
 *
 * @author Christian Partl
 *
 */
public class NumericalDataPropertiesPage extends AImportDataPage implements Listener {

	public static final String PAGE_NAME = "Numerical Dataset Properties";

	public static final String PAGE_DESCRIPTION = "Specify properties for the numerical dataset.";
	/**
	 * Parent composite of all widgets in this page.
	 */
	protected Composite parentComposite;

	protected NumericalDataPropertiesCollectionWidget numericalDataPropertiesWidget;

	protected DataTranspositionWidget dataTranspositionWidget;

	/**
	 * Determines whether this page should init its widgets from the {@link DataDescription} .
	 */
	protected boolean initFromDataDescription = true;

	public NumericalDataPropertiesPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	protected void createGuiElements(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, true));

		numericalDataPropertiesWidget = new NumericalDataPropertiesCollectionWidget(parentComposite, this, EnumSet.of(
				ENumericalDataProperties.CLIPPING, ENumericalDataProperties.DATA_CENTER,
				ENumericalDataProperties.SCALING, ENumericalDataProperties.IMPUTATION));

		dataTranspositionWidget = new DataTranspositionWidget(parentComposite, getWizard(),
				dataSetDescription.isTransposeMatrix());
	}

	@Override
	public boolean isPageComplete() {

		if (numericalDataPropertiesWidget.isDataValid()) {
			return super.isPageComplete();
		}
		return false;
	}

	@Override
	public void fillDataSetDescription() {

		EDataType dataType = numericalDataPropertiesWidget.getDataType();

		DataDescription dataDescription = new DataDescription(dataType == EDataType.FLOAT ? EDataClass.REAL_NUMBER
				: EDataClass.NATURAL_NUMBER, dataType, numericalDataPropertiesWidget.getNumericalProperties());
		dataSetDescription.setDataDescription(dataDescription);
		dataSetDescription.setTransposeMatrix(dataTranspositionWidget.isTransposition());

		ArrayList<ColumnDescription> inputPattern = new ArrayList<ColumnDescription>();
		DataImportWizard wizard = getWizard();

		for (Integer selected : wizard.getSelectedColumns()) {
			int columnIndex = selected.intValue();
			if (columnIndex == dataSetDescription.getColumnOfRowIds())
				continue;
			inputPattern.add(new ColumnDescription(columnIndex, dataDescription));

		}

		dataSetDescription.setParsingPattern(inputPattern);
	}

	@Override
	public void pageActivated() {
		if (initFromDataDescription) {
			numericalDataPropertiesWidget.updateNumericalProperties(dataSetDescription.getDataDescription()
					.getNumericalProperties());
			numericalDataPropertiesWidget.setDataType(dataSetDescription.getDataDescription().getRawDataType());
			initFromDataDescription = false;
		}
		getWizard().setChosenDataTypePage(this);
		getWizard().getContainer().updateButtons();
		dataTranspositionWidget.update();

	}

	@Override
	public void handleEvent(Event event) {
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
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
