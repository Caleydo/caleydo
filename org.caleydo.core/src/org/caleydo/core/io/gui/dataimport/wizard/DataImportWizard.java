/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.DataImportStatusDialog;
import org.caleydo.core.io.gui.dataimport.DataImportStatusDialogs;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;

/**
 * Wizard that guides the user through the different steps of importing a dataset: 1. Dataset Specification, 2. Dataset
 * Transformation, 3. Loading of groupings. The user may finish the import after completing the first step.
 *
 * @author Christian Partl
 *
 */
public class DataImportWizard extends AWizard<DataImportWizard> {

	/**
	 * The {@link DataSetDescription} specified by this wizard that is used to load the dataset.
	 */
	private DataSetDescription dataSetDescription;

	/**
	 * First page of the wizard that is used to specify the dataset.
	 */
	private LoadDataSetPage loadDataSetPage;

	/**
	 * Page of the wizard that is used to add groupings to the dataset.
	 */
	private AddGroupingsPage addGroupingsPage;

	/**
	 * Page to determine the type of the dataset to be loaded.
	 */
	private DataSetTypePage dataSetTypePage;

	/**
	 * Page of the wizard that is used to specify properties for a numerical dataset.
	 */
	private NumericalDataPropertiesPage numericalDataPage;

	/**
	 * Page of the wizard that is used to specify properties for a categorical dataset.
	 */
	private CategoricalDataPropertiesPage categoricalDataPage;

	/**
	 * Page of the wizard that is used to specify properties for an inhomogeneous dataset.
	 */
	private InhomogeneousDataPropertiesPage inhomogeneousDataPropertiesPage;

	/**
	 * The data page chosen by the user (numerical, categorical, or inhomogeneous).
	 */
	private AImportDataPage<DataImportWizard> chosenDataTypePage;

	/**
	 * Determines whether all required data has been specified and the dialog can be finished.
	 */
	private boolean requiredDataSpecified = false;

	/**
	 * Matrix that only contains the selected data columns (no id column) and data rows (no header rows).
	 */
	private List<List<String>> filteredDataMatrix;

	/**
	 * Indices of all columns of the dataset that shall be imported.
	 */
	private List<Integer> selectedColumns;

	/**
	 * List of selected columnIDs as they occur in the dataset.
	 */
	private List<String> filteredRowOfColumnIDs;

	/**
	 * List of rowIDs as they occur in the dataset.
	 */
	private List<String> columnOfRowIDs;

	private ATableBasedDataDomain dataDomain;

	private String error;

	/**
	 *
	 */
	public DataImportWizard() {
		dataSetDescription = new DataSetDescription();
		setWindowTitle("Data Import Wizard");
		// setHelpAvailable(true);
	}

	public DataImportWizard(DataSetDescription dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
		setWindowTitle("Data Import Wizard");
		// setHelpAvailable(true);
	}

	@Override
	public void addPages() {
		loadDataSetPage = new LoadDataSetPage(dataSetDescription);
		dataSetTypePage = new DataSetTypePage(dataSetDescription);
		numericalDataPage = new NumericalDataPropertiesPage(dataSetDescription);
		categoricalDataPage = new CategoricalDataPropertiesPage(dataSetDescription);
		inhomogeneousDataPropertiesPage = new InhomogeneousDataPropertiesPage(dataSetDescription);
		addGroupingsPage = new AddGroupingsPage(dataSetDescription);

		IWizardContainer wizardContainer = getContainer();
		if (wizardContainer instanceof IPageChangeProvider) {
			IPageChangeProvider pageChangeProvider = (IPageChangeProvider) wizardContainer;
			pageChangeProvider.addPageChangedListener(loadDataSetPage);
			pageChangeProvider.addPageChangedListener(dataSetTypePage);
			pageChangeProvider.addPageChangedListener(numericalDataPage);
			pageChangeProvider.addPageChangedListener(categoricalDataPage);
			pageChangeProvider.addPageChangedListener(inhomogeneousDataPropertiesPage);
			pageChangeProvider.addPageChangedListener(addGroupingsPage);
		}

		addPage(loadDataSetPage);
		addPage(dataSetTypePage);
		addPage(categoricalDataPage);
		addPage(numericalDataPage);
		addPage(inhomogeneousDataPropertiesPage);
		addPage(addGroupingsPage);
	}

	@Override
	public boolean performFinish() {

		// if (!visitedPages.contains(loadDataSetPage))
		// return false;

		if (visitedPages.contains(loadDataSetPage) || getContainer().getCurrentPage().equals(loadDataSetPage))
			loadDataSetPage.fillDataSetDescription();
		if (visitedPages.contains(dataSetTypePage) || getContainer().getCurrentPage().equals(dataSetTypePage))
			dataSetTypePage.fillDataSetDescription();
		if (visitedPages.contains(chosenDataTypePage) || getContainer().getCurrentPage().equals(chosenDataTypePage))
			chosenDataTypePage.fillDataSetDescription();
		if (visitedPages.contains(addGroupingsPage) || getContainer().getCurrentPage().equals(addGroupingsPage))
			addGroupingsPage.fillDataSetDescription();

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());

		try {

			dialog.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						dataDomain = DataLoader.loadData(dataSetDescription, monitor);
					} catch (Exception e) {
						error = e.getMessage();
					}
				}
			});
		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Dataset loading failed: " + e.getMessage()));
		}
		if (dataDomain == null) {
			MessageDialog.openError(getShell(), "Dataset Loading Failed", "An error has occurred during loading file "
					+ dataSetDescription.getDataSourcePath() + ". " + (error != null ? error : ""));
		} else {
			DataImportStatusDialog d = DataImportStatusDialogs.createDatasetImportStatusDialog(getShell(), dataDomain);
			d.open();
		}

		// DataLoader.loadData(dataSetDescription, null);

		// try {
		// new ProgressMonitorDialog(shell).run(true, true,
		// new LongRunningOperation(indeterminate.getSelection()));
		// } catch (InvocationTargetException e) {
		// MessageDialog.openError(shell, "Error", e.getMessage());
		// } catch (InterruptedException e) {
		// MessageDialog.openInformation(shell, "Cancelled", e.getMessage());
		// }

		// ATableBasedDataDomain dataDomain;
		// Job job = new Job("First Job") {
		// @Override
		// protected IStatus run(IProgressMonitor monitor) {
		//
		//
		// // Use this to open a Shell in the UI thread
		// return Status.OK_STATUS;
		// }
		//
		// };
		// job.setUser(true);
		// job.schedule();

		// todo handle failure

		// try {
		//
		// String secondaryID = UUID.randomUUID().toString();
		// RCPViewInitializationData rcpViewInitData = new
		// RCPViewInitializationData();
		// rcpViewInitData.setDataDomainID(dataDomain.getDataDomainID());
		// RCPViewManager.get().addRCPView(secondaryID, rcpViewInitData);
		//
		// if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
		// PlatformUI
		// .getWorkbench()
		// .getActiveWorkbenchWindow()
		// .getActivePage()
		// .showView(dataDomain.getDefaultStartViewType(), secondaryID,
		// IWorkbenchPage.VIEW_ACTIVATE);
		//
		// }
		// } catch (PartInitException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		return true;
	}

	@Override
	public boolean canFinish() {
		if (chosenDataTypePage == null)
			return false;
		return super.canFinish();
	}

	/**
	 * @param requiredDataSpecified
	 *            setter, see {@link #requiredDataSpecified}
	 */
	public void setRequiredDataSpecified(boolean requiredDataSpecified) {
		this.requiredDataSpecified = requiredDataSpecified;
	}

	/**
	 * @return the requiredDataSpecified, see {@link #requiredDataSpecified}
	 */
	public boolean isRequiredDataSpecified() {
		return requiredDataSpecified;
	}

	/**
	 * @param chosenDataTypePage
	 *            setter, see {@link chosenDataTypePage}
	 */
	public void setChosenDataTypePage(AImportDataPage<DataImportWizard> chosenDataTypePage) {
		this.chosenDataTypePage = chosenDataTypePage;
	}

	/**
	 * @return the chosenDataTypePage, see {@link #chosenDataTypePage}
	 */
	public AImportDataPage<DataImportWizard> getChosenDataTypePage() {
		return chosenDataTypePage;
	}

	/**
	 * @return the loadDataSetPage, see {@link #loadDataSetPage}
	 */
	public LoadDataSetPage getLoadDataSetPage() {
		return loadDataSetPage;
	}

	/**
	 * @return the numericalDataPage, see {@link #numericalDataPage}
	 */
	public NumericalDataPropertiesPage getNumericalDataPage() {
		return numericalDataPage;
	}

	/**
	 * @return the categoricalDataPage, see {@link #categoricalDataPage}
	 */
	public CategoricalDataPropertiesPage getCategoricalDataPage() {
		return categoricalDataPage;
	}

	/**
	 * @return the dataSetTypePage, see {@link #dataSetTypePage}
	 */
	public DataSetTypePage getDataSetTypePage() {
		return dataSetTypePage;
	}

	/**
	 * @return the addGroupingsPage, see {@link #addGroupingsPage}
	 */
	public AddGroupingsPage getAddGroupingsPage() {
		return addGroupingsPage;
	}

	/**
	 * @param filteredDataMatrix
	 *            setter, see {@link filteredDataMatrix}
	 */
	public void setFilteredDataMatrix(List<List<String>> filteredDataMatrix) {
		this.filteredDataMatrix = filteredDataMatrix;
	}

	/**
	 * @return the filteredDataMatrix, see {@link #filteredDataMatrix}
	 */
	public List<List<String>> getFilteredDataMatrix() {
		return filteredDataMatrix;
	}

	/**
	 * @param selectedColumns
	 *            setter, see {@link selectedColumns}
	 */
	public void setSelectedColumns(List<Integer> selectedColumns) {
		this.selectedColumns = selectedColumns;
	}

	/**
	 * @return the selectedColumns, see {@link #selectedColumns}
	 */
	public List<Integer> getSelectedColumns() {
		return selectedColumns;
	}

	/**
	 * @return the inhomogeneousDataPropertiesPage, see {@link #inhomogeneousDataPropertiesPage}
	 */
	public InhomogeneousDataPropertiesPage getInhomogeneousDataPropertiesPage() {
		return inhomogeneousDataPropertiesPage;
	}

	/**
	 * @return the filteredRowOfColumnIDs, see {@link #filteredRowOfColumnIDs}
	 */
	public List<String> getFilteredRowOfColumnIDs() {
		return filteredRowOfColumnIDs;
	}

	/**
	 * @param filteredRowOfColumnIDs
	 *            setter, see {@link filteredRowOfColumnIDs}
	 */
	public void setFilteredRowOfColumnIDs(List<String> filteredRowOfColumnIDs) {
		this.filteredRowOfColumnIDs = filteredRowOfColumnIDs;
	}

	/**
	 * @return the columnOfRowIDs, see {@link #columnOfRowIDs}
	 */
	public List<String> getColumnOfRowIDs() {
		return columnOfRowIDs;
	}

	/**
	 * @param columnOfRowIDs
	 *            setter, see {@link columnOfRowIDs}
	 */
	public void setColumnOfRowIDs(List<String> columnOfRowIDs) {
		this.columnOfRowIDs = columnOfRowIDs;
	}

	/**
	 * @param dataSetDescription
	 *            setter, see {@link dataSetDescription}
	 */
	public void setDataSetDescription(DataSetDescription dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
		loadDataSetPage.setDataSetDescription(dataSetDescription);
		dataSetTypePage.setDataSetDescription(dataSetDescription);
		numericalDataPage.setDataSetDescription(dataSetDescription);
		categoricalDataPage.setDataSetDescription(dataSetDescription);
		inhomogeneousDataPropertiesPage.setDataSetDescription(dataSetDescription);
		addGroupingsPage.setDataSetDescription(dataSetDescription);
	}

}
