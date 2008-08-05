package org.caleydo.rcp.wizard.project;

import org.caleydo.rcp.action.file.FileOpenProjectAction;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard that appears after Caleydo startup.
 * 
 * @author Marc Streit
 */
public class CaleydoProjectWizard
	extends Wizard
{

	/**
	 * Constructor.
	 */
	public CaleydoProjectWizard()
	{

		super();

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages()
	{

		addPage(new NewOrExistingProjectPage());
		addPage(new NewProjectImportDataPage());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish()
	{

		// // DirectoryPage dirPage = getDirectorymPage();
		// if (dirPage.useDefaultDirectory()) {
		// System.out.println("Using default directory");
		// } else {
		// ChooseDirectoryPage choosePage = getChoosePage();
		// System.out.println("Using directory: " + choosePage.getDirectory());
		// }
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	public boolean performCancel()
	{

		// TODO: shutdown caleydo core
		System.out.println("Perform Cancel called");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish()
	{

		// Disable finish button
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.
	 * IWizardPage)
	 */
	public IWizardPage getNextPage(IWizardPage page)
	{

		if (page instanceof NewOrExistingProjectPage)
		{
			if (((NewOrExistingProjectPage) getPage(NewOrExistingProjectPage.PAGE_NAME))
					.newOrExisting())
			{
				NewProjectImportDataPage nextPage = (NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME);

				return nextPage;
			}
			else
			{
				this.performFinish();

				FileOpenProjectAction fileOpenProjectAction = new FileOpenProjectAction(this
						.getShell());
				fileOpenProjectAction.run();
			}
		}

		return page;
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
		WizardDialog projectWizardDialog = new WizardDialog(shell, new CaleydoProjectWizard());
		projectWizardDialog.open();

		// Dispose the display
		display.dispose();
	}
}