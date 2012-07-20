/**
 * 
 */
package org.caleydo.core.io.gui;

import org.caleydo.core.io.DataSetDescription;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.WizardPage;

/**
 * Base class for pages that are used in {@link DataImportWizard}.
 * 
 * @author Christian Partl
 * 
 */
public abstract class AImportDataPage extends WizardPage implements IPageChangedListener {

	/**
	 * The {@link DataSetDescription} for which data is defined in subclasses.
	 */
	protected DataSetDescription dataSetDescription;

	/**
	 * @param pageName
	 */
	protected AImportDataPage(String pageName, DataSetDescription dataSetDescription) {
		super(pageName);
		this.dataSetDescription = dataSetDescription;
	}

	/**
	 * Fills {@link #dataSetDescription} with values specified by the user.
	 */
	public abstract void fillDataSetDescription();

	@Override
	public void pageChanged(PageChangedEvent event) {
		
		if (event.getSelectedPage() == getNextPage()) {
			fillDataSetDescription();
			((DataImportWizard) getWizard()).addVisitedPage(this);
		}
	}

}
