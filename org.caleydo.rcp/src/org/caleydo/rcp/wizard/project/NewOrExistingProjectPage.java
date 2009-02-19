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

	public enum EProjectType
	{
		NEW_PROJECT, EXISTING_PROJECT, PATHWAY_VIEWER_MODE, SAMPLE_DATA_RANDOM, SAMPLE_DATA_REAL
	}

	private EProjectType projectType = EProjectType.SAMPLE_DATA_REAL;

	/**
	 * Constructor.
	 */
	public NewOrExistingProjectPage()
	{
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource("resources/wizard/wizard.png")));

		this.setDescription("Do you want to create a new project or load an existing one?");

		setPageComplete(false);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		Button buttonSampleDataMode = new Button(composite, SWT.RADIO);
		buttonSampleDataMode.setText("Start with sample gene expression data\n(see: http://www.ncbi.nlm.nih.gov/pubmed/17241883)");
//		buttonSampleDataMode.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		buttonSampleDataMode.setSelection(true);
		
		Button buttonRandomSampleDataMode = new Button(composite, SWT.RADIO);
		buttonRandomSampleDataMode.setText("Start with random generated sample gene expression data");
		
		Button buttonNewProject = new Button(composite, SWT.RADIO);
		buttonNewProject.setText("Load data from file (CSV, TXT)");
		setPageComplete(true);

		Button buttonPathwayViewerMode = new Button(composite, SWT.RADIO);
		buttonPathwayViewerMode.setText("Pathway viewer mode");

		Button buttonExistingProject = new Button(composite, SWT.RADIO);
		buttonExistingProject.setText("Open existing project");
		buttonExistingProject.setEnabled(false);
		
		// Link sampleDataPaperLink = new Link(composite, SWT.BORDER);
		// sampleDataPaperLink.setText("See: <a>http://www.ncbi.nlm.nih.gov/pubmed/17241883</a>");

		buttonNewProject.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				projectType = EProjectType.NEW_PROJECT;
				setPageComplete(true);
			}
		});

		buttonExistingProject.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				projectType = EProjectType.EXISTING_PROJECT;
				setPageComplete(true);
			}
		});

		buttonPathwayViewerMode.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				projectType = EProjectType.PATHWAY_VIEWER_MODE;
				setPageComplete(true);
			}
		});

		buttonRandomSampleDataMode.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				projectType = EProjectType.SAMPLE_DATA_RANDOM;
				setPageComplete(true);
			}
		});

		buttonSampleDataMode.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				projectType = EProjectType.SAMPLE_DATA_REAL;
				setPageComplete(true);
			}
		});

		setControl(composite);
		composite.pack();
	}

	public EProjectType getProjectType()
	{
		return projectType;
	}
}
