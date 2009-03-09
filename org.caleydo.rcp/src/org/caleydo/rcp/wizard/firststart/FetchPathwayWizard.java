package org.caleydo.rcp.wizard.firststart;

import java.net.InetAddress;

import org.caleydo.rcp.Application;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard that appears when Caleydo is started the first time.
 * 
 * @author Marc Streit
 */
public class FetchPathwayWizard
	extends Wizard {

	/**
	 * Constructor.
	 */
	public FetchPathwayWizard() {
		super();
	}

	@Override
	public void addPages() {
		// Check if Caleydo will be started the first time and no internet connection is detected
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
		Application.bLoadPathwayData = false;

		MessageBox messageBox = new MessageBox(new Shell(), SWT.ERROR);
		messageBox.setText("No internet connection found");
		messageBox
			.setMessage("It is not possible to fetch pathways. "
				+ "Caleydo will start without pathway data. \nAlso the integrated web browser might not work as expected.");
		messageBox.open();

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

	/**
	 * For testing purposes
	 */
	public static void main(String[] args) {
		Display display = new Display();

		// Create the parent shell for the dialog, but don't show it
		Shell shell = new Shell(display);

		// Create the dialog
		WizardDialog firstStartWizard = new WizardDialog(shell, new FetchPathwayWizard());
		firstStartWizard.open();

		// Dispose the display
		display.dispose();
	}
}