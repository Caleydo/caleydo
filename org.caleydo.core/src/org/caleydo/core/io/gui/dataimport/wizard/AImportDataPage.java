/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.util.system.BrowserUtils;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
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
	 * Determines whether this page is currently shown.
	 */
	protected boolean isActive = false;

	/**
	 * @param pageName
	 */
	protected AImportDataPage(String pageName, DataSetDescription dataSetDescription) {
		super(pageName, pageName, null);
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource("resources/wizard/wizard.png")));
		this.dataSetDescription = dataSetDescription;
	}

	/**
	 * Fills {@link #dataSetDescription} with values specified by the user.
	 */
	public abstract void fillDataSetDescription();

	@Override
	public void pageChanged(PageChangedEvent event) {

		if (isActive && event.getSelectedPage() == getNextPage()) {
			// System.out.println("Fill desc: " + getTitle());
			fillDataSetDescription();
			((DataImportWizard) getWizard()).addVisitedPage(this);
		}

		if (event.getSelectedPage() == this) {
			isActive = true;
			pageActivated();
		} else {
			isActive = false;
		}
	}

	/**
	 * Called when the page is presented to the user.
	 */
	public abstract void pageActivated();

	@Override
	public void performHelp() {
		// super.performHelp();
		BrowserUtils.openURL("http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/loading-data");

	}

}
