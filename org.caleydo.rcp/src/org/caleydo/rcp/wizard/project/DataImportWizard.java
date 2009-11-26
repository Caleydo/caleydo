package org.caleydo.rcp.wizard.project;

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
public class DataImportWizard
	extends Wizard {

	private String sInputFile;

	/**
	 * Constructor.
	 */
	public DataImportWizard(final Shell parentShell) {
		super();

		this.setWindowTitle("Caleydo - Import Data");
		parentShell.setText("Open project file");
		Monitor primary = parentShell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = parentShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		parentShell.setLocation(x, y);
		parentShell.setActive();
	}

	/**
	 * Constructor.
	 */
	public DataImportWizard(final Shell parentShell, String sInputFile) {
		this(parentShell);

		this.sInputFile = sInputFile;
	}

	@Override
	public void addPages() {
		addPage(new NewProjectImportDataPage(sInputFile));
	}

	@Override
	public boolean performFinish() {
		if (((NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME)).isPageComplete()) {

			if (!((NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME))
				.getFileLoadDataAction().execute()) {
				return false;
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean performCancel() {
		return true;
	}

	@Override
	public boolean canFinish() {
		if (((NewProjectImportDataPage) getPage(NewProjectImportDataPage.PAGE_NAME)).isPageComplete())
			return true;

		return false;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return null;

	}
}