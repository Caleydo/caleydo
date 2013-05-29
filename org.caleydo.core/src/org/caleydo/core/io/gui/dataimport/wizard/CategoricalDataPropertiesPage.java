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

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.widget.CategoricalDataPropertiesWidget;
import org.caleydo.core.io.gui.dataimport.widget.DataTranspositionWidget;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
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

	protected DataTranspositionWidget dataTranspositionWidget;

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
	public void createControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, true));

		categoricalDataWidget = new CategoricalDataPropertiesWidget(parentComposite);

		dataTranspositionWidget = new DataTranspositionWidget(parentComposite, getWizard(),
				dataSetDescription.isTransposeMatrix());

		setControl(parentComposite);
	}

	@Override
	public void fillDataSetDescription() {

		dataSetDescription.setTransposeMatrix(dataTranspositionWidget.isTransposition());

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
		dataTranspositionWidget.update();

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
