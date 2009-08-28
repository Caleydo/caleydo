package org.caleydo.rcp.wizard.project;

import java.lang.reflect.Method;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * 1st wizard page: The user has to choose the type of project, if she wants to create a new project or load
 * an existing one, or load sample data
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ChooseProjectTypePage
	extends WizardPage {

	public static final String PAGE_NAME = "Project Wizard";

	private static final String HCC_SAMPLE_DATASET_PAPER_LINK = "http://www.ncbi.nlm.nih.gov/pubmed/17241883";

	public Wizard parentWizard = null;

	public enum EProjectType {
		NEW_PROJECT,
		// PATHWAY_VIEWER_MODE,
		SAMPLE_DATA_RANDOM,
		SAMPLE_DATA_REAL
	}

	public enum EProjectLoadType {
		RECENT,
		SPECIFIED
	}

	private EUseCaseMode projectMode = EUseCaseMode.GENETIC_DATA;

	private EProjectType projectType = EProjectType.SAMPLE_DATA_REAL;

	private EOrganism organism;

	private boolean bLoadKEGGPathwayData;
	private boolean bLoadBioCartaPathwayData;

	private TabItem generalDataUseCaseTab;

	private TabItem geneticDataUseCaseTab;

	/** tab-page for connecting a client to a running server */
	private TabItem collaborationClientTab;

	/** tab-page for loading a project from file system */
	private TabItem loadProjectTab;

	/** text field to enter the network name by the user */
	private Text networkNameText;

	/** text field to enter the network address by the user */
	private Text networkAddressText;

	/** type how a existing project should be loaded */
	private EProjectLoadType projectLoadType = EProjectLoadType.RECENT;

	/** text field to enter the file-name to load a project from */
	private Text projectFileName;
	
	private Button btnLoadPathwayData;

	/**
	 * Constructor.
	 */
	public ChooseProjectTypePage() {
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader().getResource(
			"resources/wizard/wizard.png")));

		this.setDescription("Which data do you want to analyze?");

		parentWizard = (Wizard) this.getWizard();

		setPageComplete(false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		parentWizard = (Wizard) this.getWizard();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		final TabFolder tabFolder = new TabFolder(composite, SWT.BORDER);

		createGeneticUseCaseTab(tabFolder);
		createGeneralDataUseCaseTab(tabFolder);
		createLoadProjectTab(tabFolder);
		createCollaborationClientTab(tabFolder);

		String lastChosenProjectMode =
			GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.LAST_CHOSEN_USE_CASE_MODE);

		if (lastChosenProjectMode == null || lastChosenProjectMode.isEmpty()
			|| lastChosenProjectMode.equals(EUseCaseMode.GENETIC_DATA)) {
			tabFolder.setSelection(0);
			projectMode = EUseCaseMode.GENETIC_DATA;
		}
		else if (lastChosenProjectMode.equals(EUseCaseMode.UNSPECIFIED_DATA)) {
			tabFolder.setSelection(1);
			projectMode = EUseCaseMode.UNSPECIFIED_DATA;
		}
		else if (lastChosenProjectMode.equals(EUseCaseMode.LOAD_PROJECT)) {
			projectMode = EUseCaseMode.LOAD_PROJECT;
			tabFolder.setSelection(2);
		}
		else if (lastChosenProjectMode.equals(EUseCaseMode.COLLABORATION_CLIENT)) {
			projectMode = EUseCaseMode.COLLABORATION_CLIENT;
			tabFolder.setSelection(3);
		}

		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((TabItem) e.item) == generalDataUseCaseTab) {
					projectMode = EUseCaseMode.UNSPECIFIED_DATA;
				}
				else if (((TabItem) e.item) == geneticDataUseCaseTab) {
					projectMode = EUseCaseMode.GENETIC_DATA;
				}
				else if (((TabItem) e.item) == loadProjectTab) {
					projectMode = EUseCaseMode.LOAD_PROJECT;
				}
				else if (((TabItem) e.item) == collaborationClientTab) {
					projectMode = EUseCaseMode.COLLABORATION_CLIENT;
				}
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

		// Button buttonPathwayViewerMode = new Button(composite, SWT.RADIO);
		// buttonPathwayViewerMode.setText("Pathway viewer mode");
		// buttonPathwayViewerMode.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group groupOrganism = new Group(composite, SWT.None);
		groupOrganism.setText("Select organism");
		groupOrganism.setLayout(new RowLayout(SWT.VERTICAL));
		// groupOrganism.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button btnOrganismHuman = new Button(groupOrganism, SWT.RADIO);
		btnOrganismHuman.setText("Human (homo sapiens)");
		btnOrganismHuman.setEnabled(true);
		btnOrganismHuman.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				organism = EOrganism.HOMO_SAPIENS;
			}
		});
		// btnOrganismHuman.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button btnOrganismMouse = new Button(groupOrganism, SWT.RADIO);
		btnOrganismMouse.setText("Mouse (mus musculus)");
		btnOrganismMouse.setEnabled(true);
		btnOrganismMouse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				organism = EOrganism.MUS_MUSCULUS;
			}
		});
		// btnOrganismMouse.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Set organism which was used in last session
		EOrganism lastChosenOrganism =
			EOrganism.valueOf(GeneralManager.get().getPreferenceStore().getString(
				PreferenceConstants.LAST_CHOSEN_ORGANISM));
		if (lastChosenOrganism == EOrganism.HOMO_SAPIENS) {
			btnOrganismHuman.setSelection(true);
			organism = EOrganism.HOMO_SAPIENS;			
		}
		else {
			btnOrganismMouse.setSelection(true);
			organism = EOrganism.MUS_MUSCULUS;
		}
		btnLoadPathwayData = new Button(composite, SWT.CHECK);
		btnLoadPathwayData.setText("Load pathway data");
		btnLoadPathwayData.setEnabled(true);
		btnLoadPathwayData.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Set if pathways were loaded in last session
		String sLastChosenPathwayDataSources =
			GeneralManager.get().getPreferenceStore().getString(
				PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES);

		if (sLastChosenPathwayDataSources.isEmpty())
			btnLoadPathwayData.setSelection(false);
		else
			btnLoadPathwayData.setSelection(true);

		final Group groupPathways = new Group(composite, SWT.None);
		groupPathways.setText("Select pathway database");
		groupPathways.setLayout(new RowLayout());

		final Button btnLoadKEGGPathwayData = new Button(groupPathways, SWT.CHECK);
		btnLoadKEGGPathwayData.setText("KEGG");
		btnLoadKEGGPathwayData.setEnabled(true);
		// btnLoadKEGGPathwayData.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button btnLoadBioCartaPathwayData = new Button(groupPathways, SWT.CHECK);
		btnLoadBioCartaPathwayData.setText("BioCarta");
		btnLoadBioCartaPathwayData.setEnabled(true);
		// btnLoadKEGGPathwayData.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Set pathway databases which was used in last session
		if (sLastChosenPathwayDataSources.contains(EPathwayDatabaseType.KEGG.name())) {
			btnLoadKEGGPathwayData.setSelection(true);
			bLoadKEGGPathwayData = true;
		}
		else {
			btnLoadKEGGPathwayData.setSelection(false);
			bLoadKEGGPathwayData = false;
		}

		if (sLastChosenPathwayDataSources.contains(EPathwayDatabaseType.BIOCARTA.name())) {
			btnLoadBioCartaPathwayData.setSelection(true);
			bLoadBioCartaPathwayData = true;
		}
		else {
			btnLoadBioCartaPathwayData.setSelection(false);
			bLoadBioCartaPathwayData = false;
		}

		if (GeneralManager.get().getPreferenceStore().getBoolean(PreferenceConstants.FIRST_START)
			&& !Application.isInternetConnectionOK()) {
			btnLoadPathwayData.setEnabled(false);
			groupPathways.setEnabled(false);
		}

		btnLoadKEGGPathwayData.setEnabled(btnLoadPathwayData.getSelection());
		btnLoadBioCartaPathwayData.setEnabled(btnLoadPathwayData.getSelection());

		btnLoadPathwayData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean bLoadPathwayData = ((Button) e.widget).getSelection();
				groupPathways.setEnabled(bLoadPathwayData);
				btnLoadKEGGPathwayData.setEnabled(bLoadPathwayData);
				btnLoadBioCartaPathwayData.setEnabled(bLoadPathwayData);
			}
		});

		btnLoadKEGGPathwayData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bLoadKEGGPathwayData = ((Button) e.widget).getSelection();

				if (!bLoadKEGGPathwayData && !bLoadBioCartaPathwayData) {
					btnLoadPathwayData.setSelection(false);
					btnLoadKEGGPathwayData.setEnabled(false);
					btnLoadBioCartaPathwayData.setEnabled(false);
				}
			}
		});

		btnLoadBioCartaPathwayData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bLoadBioCartaPathwayData = ((Button) e.widget).getSelection();
				
				if (!bLoadKEGGPathwayData && !bLoadBioCartaPathwayData) {
					btnLoadPathwayData.setSelection(false);
					btnLoadKEGGPathwayData.setEnabled(false);
					btnLoadBioCartaPathwayData.setEnabled(false);
				}
			}
		});
		//
		// btnLoadPathwayData.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// boolean bLoadPathwayData = ((Button) e.widget).getSelection();
		//
		// // if (projectType == EProjectType.PATHWAY_VIEWER_MODE && !bLoadPathwayData) {
		// // MessageBox messageBox = new MessageBox(new Shell(), SWT.OK);
		// // messageBox.setText("Load KEGG and BioCarta pathway data");
		// // messageBox
		// //
		// .setMessage("You have selected the pathway viewer mode. Therefore pathway data loading cannot be turned off.");
		// // messageBox.open();
		// //
		// // ((Button) e.widget).setSelection(true);
		// //
		// // return;
		// // }
		//
		// // Application.bLoadPathwayData = bLoadPathwayData;
		// }
		// });

		// Link sampleDataPaperLink = new Link(composite, SWT.BORDER);
		// sampleDataPaperLink.setText("See: <a>http://www.ncbi.nlm.nih.gov/pubmed/17241883</a>");

		buttonNewProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectType = EProjectType.NEW_PROJECT;
				setPageComplete(true);
			}
		});

		// buttonPathwayViewerMode.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// projectType = EProjectType.PATHWAY_VIEWER_MODE;
		// btnLoadPathwayData.setSelection(true);
		// setPageComplete(true);
		// }
		// });

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

		buttonNewProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectType = EProjectType.NEW_PROJECT;
				setPageComplete(true);
			}
		});

	}

	private void createLoadProjectTab(TabFolder tabFolder) {
		loadProjectTab = new TabItem(tabFolder, SWT.NONE);
		loadProjectTab.setText("Load Project");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		loadProjectTab.setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		Button recentProject = new Button(composite, SWT.RADIO);
		recentProject.setText("Open project from last session");
		recentProject.setSelection(true);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		recentProject.setLayoutData(gd);

		Button loadProject = new Button(composite, SWT.RADIO);
		loadProject.setText("Specify project-file to load");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		loadProject.setLayoutData(gd);

		final Button chooseProjectFile = new Button(composite, SWT.CENTER);
		chooseProjectFile.setEnabled(false);
		chooseProjectFile.setText("Choose File");

		projectFileName = new Text(composite, SWT.BORDER);
		projectFileName.setEnabled(false);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		projectFileName.setLayoutData(gd);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);

		recentProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectLoadType = EProjectLoadType.RECENT;
				chooseProjectFile.setEnabled(false);
				projectFileName.setEnabled(false);
				setPageComplete(true);
			}
		});

		loadProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectLoadType = EProjectLoadType.SPECIFIED;
				projectFileName.setEnabled(true);
				chooseProjectFile.setEnabled(true);
				if (projectFileName.getText() != null && !projectFileName.getText().isEmpty()) {
					setPageComplete(true);
				}
				else {
					setPageComplete(false);
				}
			}
		});

		chooseProjectFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(new Shell(), SWT.OPEN);
				fileDialog.setText("Load Project");
				String[] filterExt = { "*.cal" };
				fileDialog.setFilterExtensions(filterExt);

				String fileName = fileDialog.open();
				if (fileName != null) {
					projectFileName.setText(fileName);
					setPageComplete(true);
				}
			}
		});
	}

	/**
	 * Creates the tab for connecting a client to a already running caleydo-server-application to get the use
	 * case and basic data from.
	 * 
	 * @param tabFolder
	 *            tab-widget to create the new tab-item in
	 */
	private void createCollaborationClientTab(TabFolder tabFolder) {
		collaborationClientTab = new TabItem(tabFolder, SWT.NONE);
		collaborationClientTab.setText("Connect to Server");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		collaborationClientTab.setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		Label networkNameLabel = new Label(composite, SWT.LEFT);
		networkNameLabel.setText("Network Name:");
		networkNameText = new Text(composite, SWT.BORDER);
		networkNameText.setText("client");
		GridData gd = new GridData();
		gd.widthHint = 200;
		networkNameText.setLayoutData(gd);

		Label networkAddressLabel = new Label(composite, SWT.LEFT);
		networkAddressLabel.setText("Server Address:");
		networkAddressText = new Text(composite, SWT.BORDER);
		networkAddressText.setText("127.0.0.1");
		gd = new GridData();
		gd.widthHint = 200;
		networkAddressText.setLayoutData(gd);

		setPageComplete(true);
	}

	public EProjectType getProjectType() {
		return projectType;
	}

	public EUseCaseMode getUseCaseMode() {
		return projectMode;
	}

	public boolean isKEGGPathwayDataLoadingRequested() {
		return bLoadKEGGPathwayData && btnLoadPathwayData.getSelection();
	}

	public boolean isBioCartaPathwayLoadingRequested() {
		return bLoadBioCartaPathwayData && btnLoadPathwayData.getSelection();
	}

	/**
	 * Returns the network-name entered by the user in the network-name text-field
	 * 
	 * @return network-name to use
	 */
	public String getNetworkName() {
		return networkNameText.getText();
	}

	/**
	 * Returns the network-address entered by the user in the network-address text-field
	 * 
	 * @return network-address to connect to
	 */
	public String getNetworkAddress() {
		return networkAddressText.getText();
	}

	/**
	 * Returns the project file-name for open existing projects
	 * 
	 * @return
	 */
	public String getProjectFileName() {
		return projectFileName.getText();
	}

	public EProjectLoadType getProjectLoadType() {
		return projectLoadType;
	}

	public EOrganism getOrganism() {
		return organism;
	}

}
