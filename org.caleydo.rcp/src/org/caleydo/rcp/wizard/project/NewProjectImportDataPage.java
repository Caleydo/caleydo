package org.caleydo.rcp.wizard.project;

import org.caleydo.rcp.action.file.FileLoadDataAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
	
	public FileLoadDataAction fileLoadDataAction;

	/**
	 * Constructor.
	 */
	public NewProjectImportDataPage()
	{
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(
				this.getClass().getClassLoader().getResource(
						"resources/wizard/wizard.png")));
		
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent)
	{
		Composite topLevel = new Composite(parent, SWT.NONE);
		topLevel.setLayout(new FillLayout());

		fileLoadDataAction = new FileLoadDataAction(topLevel);
		fileLoadDataAction.run();

		setControl(topLevel);
//		setPageComplete(true);
	}
	
	public FileLoadDataAction getFileLoadDataAction()
	{
		return fileLoadDataAction;
	}
}