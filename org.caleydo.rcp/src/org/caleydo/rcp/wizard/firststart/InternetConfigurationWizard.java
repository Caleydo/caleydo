package org.caleydo.rcp.wizard.firststart;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard that appears when Caleydo is started the first time and detects no internet connection.
 * 
 * @author Marc Streit
 */
public class InternetConfigurationWizard
	extends Wizard {

	/**
	 * Constructor.
	 */
	public InternetConfigurationWizard() {
		super();
	}

	@Override
	public void addPages() {
		addPage(new ProxyConfigurationPage());

		setWindowTitle("Internet Configuration Wizard");
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public boolean performCancel() {
		// Application.bDoExit = true;

		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return null;
	}

	/**
	 * For testing purposes
	 */
	public static void main(String[] args) {
		Display display = new Display();

		// Create the parent shell for the dialog, but don't show it
		Shell shell = new Shell(display);

		// Create the dialog
		WizardDialog firstStartWizard = new WizardDialog(shell, new InternetConfigurationWizard());
		firstStartWizard.open();

		// Dispose the display
		display.dispose();
	}
}