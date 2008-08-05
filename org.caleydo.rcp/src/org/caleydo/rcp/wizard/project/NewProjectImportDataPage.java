package org.caleydo.rcp.wizard.project;

import org.caleydo.rcp.action.file.FileLoadDataAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * New project wizard: import data from files to new project.
 * 
 * @author Marc Streit
 */
public class NewProjectImportDataPage
	extends WizardPage
{

	public static final String PAGE_NAME = "Import data to new project";

	/**
	 * Constructor.
	 */
	public NewProjectImportDataPage()
	{

		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromImageData(new ImageData(
				"resources/splash/splash.png")));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite topLevel = new Composite(parent, SWT.NONE);
		topLevel.setLayout(new FillLayout());

		FileLoadDataAction fileLoadDataAction = new FileLoadDataAction(topLevel);
		fileLoadDataAction.run();

		setControl(topLevel);
		setPageComplete(true);
	}
}