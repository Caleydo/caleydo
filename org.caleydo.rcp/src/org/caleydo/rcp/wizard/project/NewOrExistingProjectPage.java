package org.caleydo.rcp.wizard.project;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * 1st wizard page: The user have to choose if she wants to create a new project
 * or load an existing one.
 * 
 * @author Marc Streit
 */
public class NewOrExistingProjectPage
	extends WizardPage
{

	public static final String PAGE_NAME = "New or existing project?";

	private boolean bNewProject = true;

	/**
	 * Constructor.
	 */
	public NewOrExistingProjectPage()
	{

		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromImageData(new ImageData(
				"resources/splash/splash.png")));
	}

	public void createControl(Composite parent)
	{

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		Button buttonNewProject = new Button(composite, SWT.RADIO);
		buttonNewProject.setText("Create new project");

		Button buttonExistingProject = new Button(composite, SWT.RADIO);
		buttonExistingProject.setText("Open existing project");

		buttonNewProject.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{

				bNewProject = true;
			}
		});

		buttonExistingProject.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{

				bNewProject = false;
			}
		});

		setControl(composite);
		setPageComplete(true);
	}

	public boolean newOrExisting()
	{

		return bNewProject;
	}
}
