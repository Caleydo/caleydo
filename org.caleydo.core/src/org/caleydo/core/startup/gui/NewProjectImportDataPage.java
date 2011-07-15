package org.caleydo.core.startup.gui;

import org.caleydo.core.io.gui.ImportDataDialog;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * New project wizard: import data from files to new project.
 * 
 * @author Marc Streit
 */
public class NewProjectImportDataPage
	extends WizardPage {

	public static final String PAGE_NAME = "Import data to new project";

	private String inputFile;

	private IDataDomain dataDomain;

	/**
	 * Constructor.
	 */
	public NewProjectImportDataPage() {
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
			.getResource("resources/wizard/wizard.png")));

		setPageComplete(true);
	}

	/**
	 * Constructor.
	 */
	public NewProjectImportDataPage(String inputFile, IDataDomain dataDomain) {
		this();

		this.inputFile = inputFile;
		this.dataDomain = dataDomain;
	}

	@Override
	public void createControl(Composite parent) {
		if (inputFile != null) {
			new ImportDataDialog(parent.getShell(), inputFile, dataDomain);
		}
		else {
			new ImportDataDialog(parent.getShell(), dataDomain);
		}
	}
}