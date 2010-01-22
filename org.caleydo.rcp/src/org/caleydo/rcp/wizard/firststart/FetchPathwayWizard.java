package org.caleydo.rcp.wizard.firststart;

import java.net.InetAddress;
import java.util.ArrayList;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.Application;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard that appears when Caleydo is started the first time.
 * 
 * @author Marc Streit
 */
public class FetchPathwayWizard
	extends Wizard {

	@Override
	public void addPages() {
		// Check if Caleydo will be started the first time and no Internet connection is detected
		if (!isInternetConnectionOK()) {
			addPage(new ProxyConfigurationPage());
		}

		addPage(new FetchPathwayDataPage());

		setWindowTitle("Pathway Wizard");
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public boolean performCancel() {

		// performCancel is also called when we close the wizard after it has finished.
		// Therefore we have to check, if the fetch operations were successful
		boolean bFetchSuccessful = true;
		PreferenceStore prefStore = GeneralManager.get().getPreferenceStore();
		String sLoadedPathwaySources = prefStore.getString(PreferenceConstants.PATHWAY_DATA_OK);

		if (!sLoadedPathwaySources.contains("BIOCARTA"))
			bFetchSuccessful = false;

		if (!bFetchSuccessful) {
			MessageBox messageBox = new MessageBox(new Shell(), SWT.ERROR);
			messageBox.setText("Pathway fetching aborted");
			messageBox
				.setMessage("Pathway fetching cancelled. \n"
					+ "Caleydo will start without pathway data. \nAlso the integrated web browser might not work as expected.");
			messageBox.open();
		}

		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return null;
	}

	private boolean isInternetConnectionOK() {
		// Check internet connection
		try {
			InetAddress.getByName(ProxyConfigurationPage.TEST_URL);
		}
		catch (Exception e) {
			Application.bIsInterentConnectionOK = false;
			return false;
		}

		Application.bIsInterentConnectionOK = true;
		return true;
	}
}