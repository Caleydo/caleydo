package org.caleydo.rcp.wizard.project;

import java.lang.reflect.Method;

import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.Application;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 1st wizard page: The user have to choose if she wants to create a new project or load an existing one.
 * 
 * @author Marc Streit
 */
public class NewOrExistingProjectPage
	extends WizardPage {

	public static final String PAGE_NAME = "Project Wizard";

	private static final String HCC_SAMPLE_DATASET_PAPER_LINK = "http://www.ncbi.nlm.nih.gov/pubmed/17241883";

	public Wizard parentWizard = null;

	public enum EProjectType {
		NEW_PROJECT,
		EXISTING_PROJECT,
		PATHWAY_VIEWER_MODE,
		SAMPLE_DATA_RANDOM,
		SAMPLE_DATA_REAL
	}

	private EUseCaseMode useCaseMode = EUseCaseMode.GENETIC_DATA;

	private EProjectType projectType = EProjectType.SAMPLE_DATA_REAL;

	private TabItem generalDataUseCaseTab;
	private TabItem geneticDataUseCaseTab;

	/**
	 * Constructor.
	 */
	public NewOrExistingProjectPage() {
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader().getResource(
			"resources/wizard/wizard.png")));

		this.setDescription("Which data do you want to analyze?");

		parentWizard = (Wizard) this.getWizard();

		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		parentWizard = (Wizard) this.getWizard();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		final TabFolder tabFolder = new TabFolder(composite, SWT.BORDER);

		createGeneticUseCaseTab(tabFolder);
		createGeneralDataUseCaseTab(tabFolder);

		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (((TabItem) e.item) == generalDataUseCaseTab) {
					useCaseMode = EUseCaseMode.UNSPECIFIED_DATA;
					
					// Turn off pathway data loading for general data analysis use case
					Application.bLoadPathwayData = false;
				}
				else if (((TabItem) e.item) == generalDataUseCaseTab)
					useCaseMode = EUseCaseMode.GENETIC_DATA;
				else
					throw new IllegalStateException("Not implemented!");
			}
		});

		tabFolder.pack();

		setControl(composite);
		composite.pack();
	}

	private void createGeneticUseCaseTab(TabFolder tabFolder) {

		geneticDataUseCaseTab = new TabItem(tabFolder, SWT.NONE);
		geneticDataUseCaseTab.setText("Genetic Analysis");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		geneticDataUseCaseTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		Button buttonSampleDataMode = new Button(composite, SWT.RADIO);
		buttonSampleDataMode.setText("Start with sample gene expression data");
		// buttonSampleDataMode.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		buttonSampleDataMode.setSelection(true);
		buttonSampleDataMode.setLayoutData(new GridData(GridData.FILL_BOTH));

		Link link = new Link(composite, SWT.NULL);
		link.setText("(see: <a href=\"HCC_SAMPLE_DATASET_PAPER_LINK\">" + HCC_SAMPLE_DATASET_PAPER_LINK
			+ "</a>)");
		link.setLayoutData(new GridData(GridData.FILL_BOTH));
		link.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				String osName = System.getProperty("os.name");
				try {
					if (osName.startsWith("Mac OS")) {
						Class fileMgr = Class.forName("com.apple.eio.FileManager");
						Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
						openURL.invoke(null, new Object[] { HCC_SAMPLE_DATASET_PAPER_LINK });
					}
					else if (osName.startsWith("Windows")) {
						Runtime.getRuntime().exec(
							"rundll32 url.dll,FileProtocolHandler " + HCC_SAMPLE_DATASET_PAPER_LINK);
					}
					else {
						// Assume Unix or Linux
						String[] browsers =
							{ "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
						String browser = null;
						for (int count = 0; count < browsers.length && browser == null; count++)
							if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] })
								.waitFor() == 0) {
								browser = browsers[count];
							}
						if (browser == null)
							throw new Exception("Could not find web browser");
						else {
							Runtime.getRuntime()
								.exec(new String[] { browser, HCC_SAMPLE_DATASET_PAPER_LINK });
						}
					}
				}
				catch (Exception exception) {
				}
			}
		});

		Button buttonRandomSampleDataMode = new Button(composite, SWT.RADIO);
		buttonRandomSampleDataMode.setText("Start with random generated sample gene expression data");
		buttonRandomSampleDataMode.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button buttonNewProject = new Button(composite, SWT.RADIO);
		buttonNewProject.setText("Load data from file (CSV, TXT)");
		buttonNewProject.setLayoutData(new GridData(GridData.FILL_BOTH));
		setPageComplete(true);

		Button buttonPathwayViewerMode = new Button(composite, SWT.RADIO);
		buttonPathwayViewerMode.setText("Pathway viewer mode");
		buttonPathwayViewerMode.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button buttonExistingProject = new Button(composite, SWT.RADIO);
		buttonExistingProject.setText("Open existing project");
		buttonExistingProject.setEnabled(false);
		buttonExistingProject.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button btnLoadPathwayData = new Button(composite, SWT.CHECK);
		btnLoadPathwayData.setText("Load KEGG and BioCarta pathway data");
		btnLoadPathwayData.setSelection(true);
		btnLoadPathwayData.setEnabled(true);
		btnLoadPathwayData.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Application.caleydoCore.getGeneralManager().getPreferenceStore()
		// .getBoolean(PreferenceConstants.PATHWAY_DATA_OK)
		// ||
		if (Application.caleydoCore.getGeneralManager().getPreferenceStore().getBoolean(
			PreferenceConstants.FIRST_START)
			&& !Application.isInternetConnectionOK()) {
			btnLoadPathwayData.setEnabled(false);
		}

		btnLoadPathwayData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean bLoadPathwayData = ((Button) e.widget).getSelection();

				if (projectType == EProjectType.PATHWAY_VIEWER_MODE && !bLoadPathwayData) {
					MessageBox messageBox = new MessageBox(new Shell(), SWT.OK);
					messageBox.setText("Load KEGG and BioCarta pathway data");
					messageBox
						.setMessage("You have selected the pathway viewer mode. Therefore pathway data loading cannot be turned off.");
					messageBox.open();

					((Button) e.widget).setSelection(true);

					return;
				}

				Application.bLoadPathwayData = bLoadPathwayData;
			}
		});

		// Link sampleDataPaperLink = new Link(composite, SWT.BORDER);
		// sampleDataPaperLink.setText("See: <a>http://www.ncbi.nlm.nih.gov/pubmed/17241883</a>");

		buttonNewProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectType = EProjectType.NEW_PROJECT;
				setPageComplete(true);
			}
		});

		buttonExistingProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectType = EProjectType.EXISTING_PROJECT;
				setPageComplete(true);
			}
		});

		buttonPathwayViewerMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectType = EProjectType.PATHWAY_VIEWER_MODE;
				btnLoadPathwayData.setSelection(true);
				setPageComplete(true);
			}
		});

		buttonRandomSampleDataMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectType = EProjectType.SAMPLE_DATA_RANDOM;
				setPageComplete(true);
			}
		});

		buttonSampleDataMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectType = EProjectType.SAMPLE_DATA_REAL;
				setPageComplete(true);
			}
		});
	}

	private void createGeneralDataUseCaseTab(TabFolder tabFolder) {

		generalDataUseCaseTab = new TabItem(tabFolder, SWT.NONE);
		generalDataUseCaseTab.setText("General Data Analysis");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		generalDataUseCaseTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		Button buttonNewProject = new Button(composite, SWT.RADIO);
		buttonNewProject.setText("Load data from file (CSV, TXT)");
		buttonNewProject.setLayoutData(new GridData(GridData.FILL_BOTH));
		buttonNewProject.setSelection(true);

		setPageComplete(true);

		Button buttonExistingProject = new Button(composite, SWT.RADIO);
		buttonExistingProject.setText("Open existing project");
		buttonExistingProject.setEnabled(false);
		buttonExistingProject.setLayoutData(new GridData(GridData.FILL_BOTH));

		buttonNewProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectType = EProjectType.NEW_PROJECT;
				setPageComplete(true);
			}
		});

		buttonExistingProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectType = EProjectType.EXISTING_PROJECT;
				setPageComplete(true);
			}
		});
	}

	public EProjectType getProjectType() {
		return projectType;
	}

	public EUseCaseMode getUseCaseMode() {
		return useCaseMode;
	}
}
