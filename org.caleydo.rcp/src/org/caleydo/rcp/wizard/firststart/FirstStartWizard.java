package org.caleydo.rcp.wizard.firststart;

import org.caleydo.rcp.Application;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard that appears when Caleydo is started the first time.
 * 
 * @author Marc Streit
 */
public class FirstStartWizard
	extends Wizard
{

	/**
	 * Constructor.
	 */
	public FirstStartWizard()
	{
		super();
	}

	@Override
	public void addPages()
	{
		addPage(new FetchPathwayDataPage());
		
		setWindowTitle("Caleydo First Start Wizard");
	}

	@Override
	public boolean performFinish()
	{
		return true;
	}

	@Override
	public boolean performCancel()
	{
		Application.bDoExit = true;
		
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page)
	{
		return null;
	}

	/**
	 * For testing purposes
	 */
	public static void main(String[] args)
	{
		Display display = new Display();

		// Create the parent shell for the dialog, but don't show it
		Shell shell = new Shell(display);
		shell.setActive();

		// Create the dialog
		WizardDialog firstStartWizard = new WizardDialog(shell, new FirstStartWizard());
		firstStartWizard.open();

		// Dispose the display
		display.dispose();
	}
}