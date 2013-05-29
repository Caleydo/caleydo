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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.Color;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author Christian
 *
 */
public class DataSetTypePage extends AImportDataPage {

	public static final String PAGE_NAME = "Select Dataset Type";

	public static final String PAGE_DESCRIPTION = "Specify the type of the dataset.";

	protected Button numericalDatasetButton;
	protected Button categoricalDatasetButton;
	protected Button inhomogeneousDatasetButton;

	/**
	 * determines whether a new dataset has been loaded prior visiting this page.
	 */
	protected boolean datasetChanged = true;

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
	protected DataSetTypePage(DataSetDescription dataSetDescription) {
		super("Data Type Selection", dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Dataset Type");
		group.setLayout(new GridLayout(1, true));

		numericalDatasetButton = new Button(group, SWT.RADIO);
		numericalDatasetButton.setText("Numerical");
		numericalDatasetButton.setSelection(true);
		boldifyButtonText(numericalDatasetButton);
		Label numericalDatasetLabel = new Label(group, SWT.NONE);
		numericalDatasetLabel.setText("Description...");

		categoricalDatasetButton = new Button(group, SWT.RADIO);
		categoricalDatasetButton.setText("Categorical");
		boldifyButtonText(categoricalDatasetButton);
		Label categoricalDatasetLabel = new Label(group, SWT.NONE);
		categoricalDatasetLabel.setText("Description...");

		inhomogeneousDatasetButton = new Button(group, SWT.RADIO);
		inhomogeneousDatasetButton.setText("Inhomogeneous");
		boldifyButtonText(inhomogeneousDatasetButton);
		Label inhomogeneousDatasetLabel = new Label(group, SWT.NONE);
		inhomogeneousDatasetLabel.setText("Description...");

		setControl(group);
	}

	private void boldifyButtonText(Button button) {
		FontData fontData = button.getFont().getFontData()[0];
		Font font = new Font(button.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		button.setFont(font);
	}

	@Override
	public void fillDataSetDescription() {
		if (numericalDatasetButton.getSelection()) {
			if (dataSetDescription.getDataDescription() == null
					|| dataSetDescription.getDataDescription().getNumericalProperties() == null || datasetChanged) {
				dataSetDescription.setDataDescription(new DataDescription(EDataClass.REAL_NUMBER, EDataType.FLOAT,
						new NumericalProperties()));
				// the page needs to init from the newly created data description
				getWizard().getNumericalDataPage().setInitFromDataDescription(true);
			}

		} else if (categoricalDatasetButton.getSelection()) {
			if (dataSetDescription.getDataDescription() == null
					|| dataSetDescription.getDataDescription().getCategoricalClassDescription() == null
					|| datasetChanged) {
				CategoricalClassDescription<String> categoricalClassDescription = createCategoricalClassDescription();
				dataSetDescription.setDataDescription(new DataDescription(EDataClass.CATEGORICAL, EDataType.STRING,
						categoricalClassDescription));
				// the page needs to init from the newly created data description
				getWizard().getCategoricalDataPage().setInitFromDataDescription(true);
			}
		} else {
			if (datasetChanged || dataSetDescription.getDataDescription() != null) {
				getWizard().getInhomogeneousDataPropertiesPage().setInitColumnDescriptions(true);
				// No global data description for inhomogeneous
				dataSetDescription.setDataDescription(null);
			}
		}
		datasetChanged = false;
	}

	private CategoricalClassDescription<String> createCategoricalClassDescription() {

		Set<String> categories = new HashSet<>();

		for (List<String> row : getWizard().getFilteredDataMatrix()) {
			for (String value : row) {
				categories.add(value);
			}
		}

		List<String> categoryValues = new ArrayList<>(categories);
		Collections.sort(categoryValues);

		CategoricalClassDescription<String> categoricalClassDescription = new CategoricalClassDescription<>();
		categoricalClassDescription.setCategoryType(ECategoryType.ORDINAL);
		categoricalClassDescription.setRawDataType(EDataType.STRING);

		for (String categoryValue : categoryValues) {
			categoricalClassDescription.addCategoryProperty(categoryValue, categoryValue, new Color("000000"));
		}

		return categoricalClassDescription;
	}

	@Override
	public void pageActivated() {
		// The user must always visit the following page before he can finish
		getWizard().setChosenDataTypePage(null);
		getWizard().getContainer().updateButtons();

	}

	@Override
	public IWizardPage getNextPage() {
		DataImportWizard wizard = getWizard();
		if (numericalDatasetButton.getSelection()) {
			return wizard.getNumericalDataPage();
		} else if (categoricalDatasetButton.getSelection()) {
			return wizard.getCategoricalDataPage();
		}
		return wizard.getInhomogeneousDataPropertiesPage();
	}

	/**
	 * @param datasetChanged
	 *            setter, see {@link datasetChanged}
	 */
	public void setDatasetChanged(boolean datasetChanged) {
		this.datasetChanged = datasetChanged;
	}

	/**
	 * @return the datasetChanged, see {@link #datasetChanged}
	 */
	public boolean isDatasetChanged() {
		return datasetChanged;
	}
}
