package org.caleydo.rcp.wizard.firststart;

import org.caleydo.rcp.wizard.project.NewProjectImportDataPage;
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages()
	{
		addPage(new FetchPathwayDataPage());
		
		setWindowTitle("Caleydo First Start Wizard");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	public boolean performCancel()
	{
		//TODO: Stop application!
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.
	 * IWizardPage)
	 */
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

		// Create the dialog
		WizardDialog firstStartWizard = new WizardDialog(shell, new FirstStartWizard());
		firstStartWizard.open();

		// Dispose the display
		display.dispose();
	}
}