package org.caleydo.rcp.wizard.project;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
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

	public static final String PAGE_NAME = "Project Wizard";

	public enum EProjectType {
		NEW_PROJECT,
		EXISTING_PROJECT,
		PATHWAY_VIEWER_MODE
	}
	
	private EProjectType projectType;

	/**
	 * Constructor.
	 */
	public NewOrExistingProjectPage()
	{
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(
				this.getClass().getClassLoader().getResource(
						"resources/wizard/wizard.png")));
		
		this.setDescription("Do you want to create a new project or load an existing one?");
		
		setPageComplete(false);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		Button buttonNewProject = new Button(composite, SWT.NONE);
		buttonNewProject.setText("Create new project");

		Button buttonExistingProject = new Button(composite, SWT.NONE);
		buttonExistingProject.setText("Open existing project");
		buttonExistingProject.setEnabled(false);
		
		Button buttonPathwayViewerMode = new Button(composite, SWT.NONE);
		buttonPathwayViewerMode.setText("Pathway viewer mode");

		buttonNewProject.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				projectType = EProjectType.NEW_PROJECT;
				setPageComplete(true);
			}
		});

		buttonExistingProject.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				projectType = EProjectType.EXISTING_PROJECT;
				setPageComplete(true);
			}
		});
		
		buttonPathwayViewerMode.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				projectType = EProjectType.PATHWAY_VIEWER_MODE;
				setPageComplete(true);
			}
		});
		
		setControl(composite);
	}

	public EProjectType getProjectType()
	{
		return projectType;
	}
}
