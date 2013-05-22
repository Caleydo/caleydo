/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;

/**
 * Wizard that guides the user through the different steps of importing a dataset: 1. Dataset Specification, 2. Dataset
 * Transformation, 3. Loading of groupings. The user may finish the import after completing the first step.
 *
 * @author Christian Partl
 *
 */
public class DataImportWizard extends Wizard {

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
	 * The data page chosen by the user (numerical, categorical, or inhomogeneous).
	 */
	private AImportDataPage chosenDataTypePage;

	/**
	 * Determines whether all required data has been specified and the dialog can be finished.
	 */
	private boolean requiredDataSpecified = false;

	/**
	 * The total number of rows in the loaded dataset.
	 */
	private int totalNumberOfRows;

	/**
	 * The total number of rows in the loaded dataset.
	 */
	private int totalNumberOfColumns;

	/**
	 * Matrix that only contains the selected data columns (no id column) and data rows (no header rows).
	 */
	private List<List<String>> filteredDataMatrix;

	private Set<AImportDataPage> visitedPages = new HashSet<AImportDataPage>();

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
		addGroupingsPage = new AddGroupingsPage(dataSetDescription);

		IWizardContainer wizardContainer = getContainer();
		if (wizardContainer instanceof IPageChangeProvider) {
			IPageChangeProvider pageChangeProvider = (IPageChangeProvider) wizardContainer;
			pageChangeProvider.addPageChangedListener(loadDataSetPage);
			pageChangeProvider.addPageChangedListener(dataSetTypePage);
			pageChangeProvider.addPageChangedListener(numericalDataPage);
			pageChangeProvider.addPageChangedListener(categoricalDataPage);
			pageChangeProvider.addPageChangedListener(addGroupingsPage);
		}

		addPage(loadDataSetPage);
		addPage(dataSetTypePage);
		addPage(categoricalDataPage);
		addPage(numericalDataPage);
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

		// ATableBasedDataDomain dataDomain;

		DataLoader.loadData(dataSetDescription);

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
		if (!visitedPages.contains(dataSetTypePage) || chosenDataTypePage == null)
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

	public void addVisitedPage(AImportDataPage page) {
		visitedPages.add(page);
	}

	/**
	 * @param totalNumberOfColumns
	 *            setter, see {@link #totalNumberOfColumns}
	 */
	public void setTotalNumberOfColumns(int totalNumberOfColumns) {
		this.totalNumberOfColumns = totalNumberOfColumns;
	}

	/**
	 * @return the totalNumberOfColumns, see {@link #totalNumberOfColumns}
	 */
	public int getTotalNumberOfColumns() {
		return totalNumberOfColumns;
	}

	/**
	 * @param totalNumberOfRows
	 *            setter, see {@link #totalNumberOfRows}
	 */
	public void setTotalNumberOfRows(int totalNumberOfRows) {
		this.totalNumberOfRows = totalNumberOfRows;
	}

	/**
	 * @return the totalNumberOfRows, see {@link #totalNumberOfRows}
	 */
	public int getTotalNumberOfRows() {
		return totalNumberOfRows;
	}

	/**
	 * @param chosenDataTypePage
	 *            setter, see {@link chosenDataTypePage}
	 */
	public void setChosenDataTypePage(AImportDataPage chosenDataTypePage) {
		this.chosenDataTypePage = chosenDataTypePage;
	}

	/**
	 * @return the chosenDataTypePage, see {@link #chosenDataTypePage}
	 */
	public AImportDataPage getChosenDataTypePage() {
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

}
