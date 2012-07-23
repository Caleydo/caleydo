/**
 * 
 */
package org.caleydo.core.io.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.view.RCPViewInitializationData;
import org.caleydo.core.view.RCPViewManager;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Wizard that guides the user through the different steps of importing a
 * dataset: 1. Dataset Specification, 2. Dataset Transformation, 3. Loading of
 * groupings. The user may finish the import after completing the first step.
 * 
 * @author Christian Partl
 * 
 */
public class DataImportWizard extends Wizard {

	/**
	 * The {@link DataSetDescription} specified by this wizard that is used to
	 * load the dataset.
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
	 * Page of the wizard that is used to transform the data of the dataset.
	 */
	private TransformDataPage transformDataPage;

	/**
	 * Determines whether all required data has been specified and the dialog
	 * can be finished.
	 */
	private boolean requiredDataSpecified = false;

	private Set<AImportDataPage> visitedPages = new HashSet<AImportDataPage>();

	/**
	 * 
	 */
	public DataImportWizard() {
		dataSetDescription = new DataSetDescription();
		setWindowTitle("Data Import Wizard");
	}

	public DataImportWizard(DataSetDescription dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
		setWindowTitle("Data Import Wizard");
	}

	@Override
	public void addPages() {
		loadDataSetPage = new LoadDataSetPage(dataSetDescription);
		transformDataPage = new TransformDataPage(dataSetDescription);
		addGroupingsPage = new AddGroupingsPage(dataSetDescription);

		IWizardContainer wizardContainer = getContainer();
		if (wizardContainer instanceof IPageChangeProvider) {
			IPageChangeProvider pageChangeProvider = (IPageChangeProvider) wizardContainer;
			pageChangeProvider.addPageChangedListener(loadDataSetPage);
			pageChangeProvider.addPageChangedListener(transformDataPage);
			pageChangeProvider.addPageChangedListener(addGroupingsPage);
		}

		addPage(loadDataSetPage);
		addPage(transformDataPage);
		addPage(addGroupingsPage);
	}

	@Override
	public boolean performFinish() {

		if (visitedPages.contains(loadDataSetPage)
				|| getContainer().getCurrentPage().equals(loadDataSetPage))
			loadDataSetPage.fillDataSetDescription();
		if (visitedPages.contains(transformDataPage)
				|| getContainer().getCurrentPage().equals(transformDataPage))
			transformDataPage.fillDataSetDescription();
		if (visitedPages.contains(addGroupingsPage)
				|| getContainer().getCurrentPage().equals(addGroupingsPage))
			addGroupingsPage.fillDataSetDescription();

		ATableBasedDataDomain dataDomain;
		try {
			dataDomain = DataLoader.loadData(dataSetDescription);
		} catch (FileNotFoundException e1) {
			// TODO do something intelligent
			e1.printStackTrace();
			throw new IllegalStateException();

		} catch (IOException e1) {
			// TODO do something intelligent
			e1.printStackTrace();
			throw new IllegalStateException();
		}
		try {

			String secondaryID = UUID.randomUUID().toString();
			RCPViewInitializationData rcpViewInitData = new RCPViewInitializationData();
			rcpViewInitData.setDataDomainID(dataDomain.getDataDomainID());
			RCPViewManager.get().addRCPView(secondaryID, rcpViewInitData);

			if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
				PlatformUI
						.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.showView(dataDomain.getDefaultStartViewType(), secondaryID,
								IWorkbenchPage.VIEW_ACTIVATE);

			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean canFinish() {
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

}
