package org.caleydo.rcp.wizard.project;

import org.caleydo.rcp.action.file.FileOpenProjectAction;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;
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
	public CaleydoProjectWizard(final Shell parentShell)
	{
		super();
		
		parentShell.setText("Open project file");
		Monitor primary = parentShell.getDisplay().getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = parentShell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		parentShell.setLocation (x, y);
	}

	@Override
	public void addPages()
	{

		addPage(new NewOrExistingProjectPage());
		addPage(new NewProjectImportDataPage());
	}

	@Override
	public boolean performFinish()
	{	
		if(((NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME)).isPageComplete())
		{
			((NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME))
				.getFileLoadDataAction().execute();
		
			return true;
		}

		return false;
	}

	@Override
	public boolean performCancel()
	{
		return true;
	}

	@Override
	public boolean canFinish()
	{
		if(((NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME)).isPageComplete())
		{
			return true;
		}
		
		return false;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page)
	{

		if (page instanceof NewOrExistingProjectPage)
		{
			if (((NewOrExistingProjectPage) getPage(NewOrExistingProjectPage.PAGE_NAME))
					.newOrExisting())
			{
				NewProjectImportDataPage nextPage = 
					(NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME);
				
				nextPage.setPageComplete(true);
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

//	/**
//	 * For testing purposes
//	 */
//	public static void main(String[] args)
//	{
//
//		Display display = new Display();
//
//		// Create the parent shell for the dialog, but don't show it
//		Shell shell = new Shell(display);
//
//		// Create the dialog
//		WizardDialog projectWizardDialog = new WizardDialog(shell, new CaleydoProjectWizard());
//		projectWizardDialog.open();
//
//		// Dispose the display
//		display.dispose();
//	}
}