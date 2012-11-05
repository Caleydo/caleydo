/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.startup.gui;

import java.text.DateFormat;
import java.util.Date;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectManager;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.link.LinkHandler;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
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
 * 1st wizard page: The user has to choose the type of project, if she wants to
 * create a new project or load an existing one, or load sample data
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ChooseProjectTypePage
	extends WizardPage {

	public static final String PAGE_NAME = "Project Wizard";

	private static final String HCC_SAMPLE_DATASET_PAPER_LINK = "http://www.ncbi.nlm.nih.gov/pubmed/17241883";

	private static final String TCGA_LINK = "http://cancergenome.nih.gov";

	private static final String BROAD_GDAC_LINK = "http://gdac.broadinstitute.org";

	public Wizard parentWizard = null;

	private static final int WIDTH = 400;

	/**
	 * If we load a project (i.e. projectMode is either
	 * {@link ProjectMode#SAMPLE_PROJECT} or {@link ProjectMode#LOAD_PROJECT},
	 * this enum specifies the options we have.
	 */
	public enum EProjectLoadType {
		RECENT,
		SPECIFIED
	}

	private ProjectMode projectMode = ProjectMode.SAMPLE_PROJECT;

	private Organism organism;

	/** tab-page for loading sample data */
	private TabItem sampleTab;
	/** tab-page for loading genetic data */
	private TabItem geneticDataUseCaseTab;
	/** tab-page for loading generic data */
	private TabItem generalDataUseCaseTab;
	/** tab-page for loading a project from file system */
	private TabItem loadProjectTab;
	/** tab-page for connecting a client to a running server */
	private TabItem collaborationClientTab;

	/** text field to enter the network name by the user */
	private Text networkNameText;
	/** text field to enter the network address by the user */
	private Text networkAddressText;

	/** type how a existing project should be loaded */
	private EProjectLoadType projectLoadType = EProjectLoadType.RECENT;

	/** text field to enter the file-name to load a project from */
	private Text projectFileName;

	private Button btnSampleProject;

	/**
	 * Constructor.
	 */
	public ChooseProjectTypePage() {
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource("resources/wizard/wizard.png")));

		this.setDescription("What data do you want to load?");

		this.parentWizard = (Wizard) this.getWizard();

		this.setPageComplete(false);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		this.parentWizard = (Wizard) this.getWizard();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		final TabFolder tabFolder = new TabFolder(composite, SWT.BORDER);

		this.projectMode = ProjectMode.valueOf(GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.LAST_CHOSEN_PROJECT_MODE));

		this.createSampleTab(tabFolder);
		this.createGeneticUseCaseTab(tabFolder);
		this.createGeneralDataUseCaseTab(tabFolder);
		this.createLoadProjectTab(tabFolder);

		if (!GeneralManager.RELEASE_MODE)
			this.createCollaborationClientTab(tabFolder);

		// restore the previously selected tab
		if (this.projectMode == null || this.projectMode.equals(ProjectMode.SAMPLE_PROJECT)) {
			tabFolder.setSelection(0);
		}
		else if (this.projectMode.equals(ProjectMode.GENE_EXPRESSION_SAMPLE_DATA)) {
			tabFolder.setSelection(0);
		}
		else if (this.projectMode.equals(ProjectMode.GENE_EXPRESSION_NEW_DATA)) {
			tabFolder.setSelection(1);
		}
		else if (this.projectMode.equals(ProjectMode.UNSPECIFIED_NEW_DATA)) {
			tabFolder.setSelection(2);
		}
		else if (this.projectMode.equals(ProjectMode.LOAD_PROJECT)) {
			tabFolder.setSelection(3);
		}
		// set the default project mode for each specified tab
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((TabItem) e.item) == ChooseProjectTypePage.this.generalDataUseCaseTab) {
					ChooseProjectTypePage.this.projectMode = ProjectMode.UNSPECIFIED_NEW_DATA;
				}
				else if (((TabItem) e.item) == ChooseProjectTypePage.this.sampleTab) {
					if (ChooseProjectTypePage.this.btnSampleProject.getSelection())
						ChooseProjectTypePage.this.projectMode = ProjectMode.SAMPLE_PROJECT;
					else
						ChooseProjectTypePage.this.projectMode = ProjectMode.GENE_EXPRESSION_SAMPLE_DATA;
				}
				else if (((TabItem) e.item) == ChooseProjectTypePage.this.geneticDataUseCaseTab) {
					ChooseProjectTypePage.this.projectMode = ProjectMode.GENE_EXPRESSION_NEW_DATA;
				}
				else if (((TabItem) e.item) == ChooseProjectTypePage.this.loadProjectTab) {
					ChooseProjectTypePage.this.projectMode = ProjectMode.LOAD_PROJECT;
				}
				else
					throw new IllegalStateException("Not implemented!");
			}
		});

		tabFolder.pack();

		this.setControl(composite);
		composite.pack();
	}

	/**
	 * This tab lets you choose between a sample project, which has e.g. cluster
	 * data included and a sample dataset, which is basically just a set csv
	 * file.
	 *
	 * @param tabFolder
	 */
	private void createSampleTab(TabFolder tabFolder) {

		SelectionAdapter linkSelectedAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String link = e.text;
				LinkHandler.openLink(link);
			}

		};

		this.sampleTab = new TabItem(tabFolder, SWT.NONE);
		this.sampleTab.setText("Try Caleydo");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		this.sampleTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		this.btnSampleProject = new Button(composite, SWT.RADIO);
		this.btnSampleProject.setText("Load sample project");
		if (this.projectMode != ProjectMode.GENE_EXPRESSION_SAMPLE_DATA)
			this.btnSampleProject.setSelection(true);

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		this.btnSampleProject.setLayoutData(gd);

		this.btnSampleProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChooseProjectTypePage.this.projectMode = ProjectMode.SAMPLE_PROJECT;
				ChooseProjectTypePage.this.setPageComplete(true);
			}
		});

		Link sampleProjectDescription = new Link(composite, SWT.NULL);
		sampleProjectDescription
				.setText("This sample project loads five linked datasets from the <a href=\""
						+ TCGA_LINK
						+ "\">TCGA</a> GBM dataset made available by the Broad Institute's <a href=\""
						+ BROAD_GDAC_LINK
						+ "\">Genome Data Analysis Center (GDAC)</a>."
						+ "\n"
						+ "\n"
						+ "The datasets are mRNA expression data, microRNA expression, methylation and copy-number data. Additionally some clinical data is available. The project contains 300-550 samples for each dataset. The expression datasets contain about 1,500 pre-selected values, copy number status is availiabe for about 5,000 genes. "
						+ "\n"
						+ "The ideal choice if you want to try out multi-dataset analysis in Calyedo.");

		// sampleProjectDescription
		// .setText("This sample project loads the dataset linked below. The data is pre-filterd to "
		// +
		// "2000 genes and 39 experiments. Hierarchical clustering was run on the datatable. Pathways are loaded. The visualizations "
		// +
		// "use a logarithmic scale. The ideal choice if you want to try out Calyedo.");

		sampleProjectDescription.setBackground(composite.getBackground());
		sampleProjectDescription.addSelectionListener(linkSelectedAdapter);

		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = WIDTH;
		sampleProjectDescription.setLayoutData(gd);

		Button btnSampleData = new Button(composite, SWT.RADIO);
		btnSampleData.setText("Start with sample gene expression data");
		// buttonSampleDataMode.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		if (this.projectMode == ProjectMode.GENE_EXPRESSION_SAMPLE_DATA)
			btnSampleData.setSelection(true);
		else
			btnSampleData.setSelection(false);
		btnSampleData.setLayoutData(new GridData(GridData.FILL_BOTH));

		Text sampleDataDescription = new Text(composite, SWT.MULTI | SWT.WRAP);
		sampleDataDescription
				.setText("This option loads a single sample mRNA expression dataset with samples for hepatocellular carcinoma (about 4000 genes and 39 experiments) through the standard loading dialog. The dataset is made available by the Institute of Pathology at the Medical University of Graz.");
		sampleDataDescription.setBackground(composite.getBackground());
		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = WIDTH;
		sampleDataDescription.setLayoutData(gd);

		Link link = new Link(composite, SWT.NULL);
		link.setText("Dataset: <a href=\"" + HCC_SAMPLE_DATASET_PAPER_LINK + "\">"
				+ HCC_SAMPLE_DATASET_PAPER_LINK + "</a>");
		link.setLayoutData(new GridData(GridData.FILL_BOTH));
		link.addSelectionListener(linkSelectedAdapter);

		btnSampleData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChooseProjectTypePage.this.projectMode = ProjectMode.GENE_EXPRESSION_SAMPLE_DATA;
				ChooseProjectTypePage.this.setPageComplete(true);
			}
		});

		composite.pack();
	}

	private void createGeneticUseCaseTab(TabFolder tabFolder) {

		this.geneticDataUseCaseTab = new TabItem(tabFolder, SWT.NONE);
		this.geneticDataUseCaseTab.setText("Load Genetic Data");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		this.geneticDataUseCaseTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		// GridData gridData = new GridData(GridData.FILL_BOTH);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		// gridData.grabExcessHorizontalSpace = true;

		Button buttonNewProject = new Button(composite, SWT.RADIO);
		buttonNewProject.setText("Load data from file (CSV, TXT)");
		buttonNewProject.setLayoutData(gridData);
		buttonNewProject.setSelection(true);
		this.setPageComplete(true);

		Text geneticDataDescription = new Text(composite, SWT.MULTI | SWT.WRAP);
		geneticDataDescription
				.setText("Load tabular data wich contains identifiers to one of the following types of IDs: "
						+ "DAVID IDs, gene names, RefSeq IDs, ENSEMBL IDs or ENTREZ IDs \n \n"
						+ "Other identifiers are currently not supported. Use the \"Load Other Data\" "
						+ "option if you have other identifiers. ");
		geneticDataDescription.setBackground(composite.getBackground());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = WIDTH;
		geneticDataDescription.setLayoutData(gridData);

		Group groupOrganism = new Group(composite, SWT.NONE);
		groupOrganism.setText("Select organism");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		groupOrganism.setLayoutData(gridData);
		groupOrganism.setLayout(new RowLayout(SWT.VERTICAL));

		Text organismDescription = new Text(groupOrganism, SWT.MULTI | SWT.WRAP);
		organismDescription
				.setText("Please choose whether the data you want to load is for humans or mice. "
						+ "Other organisms are currently not supported.\n");
		organismDescription.setBackground(composite.getBackground());
		RowData rowData = new RowData();
		rowData.width = WIDTH;
		organismDescription.setLayoutData(rowData);

		final Button btnOrganismHuman = new Button(groupOrganism, SWT.RADIO);
		btnOrganismHuman.setText("Human (homo sapiens)");
		btnOrganismHuman.setEnabled(true);
		btnOrganismHuman.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChooseProjectTypePage.this.organism = Organism.HOMO_SAPIENS;
			}
		});

		final Button btnOrganismMouse = new Button(groupOrganism, SWT.RADIO);
		btnOrganismMouse.setText("Mouse (mus musculus)");
		btnOrganismMouse.setEnabled(true);
		btnOrganismMouse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChooseProjectTypePage.this.organism = Organism.MUS_MUSCULUS;
			}
		});

		// Set organism which was used in last session
		Organism lastChosenOrganism = Organism.valueOf(GeneralManager.get()
				.getPreferenceStore().getString(PreferenceConstants.LAST_CHOSEN_ORGANISM));
		if (lastChosenOrganism == Organism.HOMO_SAPIENS) {
			btnOrganismHuman.setSelection(true);
			this.organism = Organism.HOMO_SAPIENS;
		}
		else {
			btnOrganismMouse.setSelection(true);
			this.organism = Organism.MUS_MUSCULUS;
		}

		buttonNewProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChooseProjectTypePage.this.projectMode = ProjectMode.GENE_EXPRESSION_NEW_DATA;
				ChooseProjectTypePage.this.setPageComplete(true);
			}
		});

	}

	private void createGeneralDataUseCaseTab(TabFolder tabFolder) {

		this.generalDataUseCaseTab = new TabItem(tabFolder, SWT.NONE);
		this.generalDataUseCaseTab.setText("Load Other Data");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		this.generalDataUseCaseTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		Text geneticDataDescription = new Text(composite, SWT.MULTI | SWT.WRAP);
		geneticDataDescription
				.setText("Load tabular data wich does not fall under the genetic type, "
						+ "i.e., that does not contain common gene identifiers. \n");
		geneticDataDescription.setBackground(composite.getBackground());
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.widthHint = WIDTH;
		geneticDataDescription.setLayoutData(gridData);

		Button buttonNewProject = new Button(composite, SWT.RADIO);
		buttonNewProject.setText("Load data from file (CSV, TXT)");
		buttonNewProject.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		buttonNewProject.setSelection(true);

		this.setPageComplete(true);

		buttonNewProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChooseProjectTypePage.this.projectMode = ProjectMode.UNSPECIFIED_NEW_DATA;
				ChooseProjectTypePage.this.setPageComplete(true);
			}
		});

	}

	private void createLoadProjectTab(TabFolder tabFolder) {
		this.loadProjectTab = new TabItem(tabFolder, SWT.NONE);
		this.loadProjectTab.setText("Load Project");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		this.loadProjectTab.setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		Text geneticDataDescription = new Text(composite, SWT.MULTI | SWT.WRAP);
		geneticDataDescription
				.setText("Start Caleydo using an existing Caleydo project, or continue where you left-off last time. \n");
		geneticDataDescription.setBackground(composite.getBackground());
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gridData.widthHint = WIDTH;
		geneticDataDescription.setLayoutData(gridData);

		Button recentProject = new Button(composite, SWT.RADIO);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gridData.widthHint = WIDTH;
		recentProject.setLayoutData(gridData);
		String text = "Continue where you stopped last time";
		recentProject.setText(text);

		final Date recentProjectChange = ProjectManager.getRecentProjectLastModified();

		if (recentProjectChange != null)
		{
			DateFormat dataformat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
			String lastModifiedDate = dataformat.format(recentProjectChange);
			text = text + ", on " + lastModifiedDate;
		}
		recentProject.setText(text);

		Button loadProject = new Button(composite, SWT.RADIO);
		loadProject.setText("Load a Caleydo project (*.cal file)");
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gridData.widthHint = WIDTH;
		loadProject.setLayoutData(gridData);

		final Button chooseProjectFile = new Button(composite, SWT.CENTER);
		chooseProjectFile.setEnabled(false);
		chooseProjectFile.setText("Choose File");
		GridData singleCellGD = new GridData(SWT.LEFT, SWT.TOP, false, false);
		// singleCellGD.widthHint = 100;
		chooseProjectFile.setLayoutData(singleCellGD);

		String lastProjectFileName = GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.LAST_MANUALLY_CHOSEN_PROJECT);
		this.projectFileName = new Text(composite, SWT.BORDER);
		this.projectFileName.setEnabled(false);
		this.projectFileName.setText(lastProjectFileName);
		singleCellGD = new GridData(SWT.FILL, SWT.TOP, true, false);
		singleCellGD.grabExcessHorizontalSpace = true;
		// singleCellGD.widthHint = 300;
		this.projectFileName.setLayoutData(singleCellGD);

		try {
			this.projectLoadType = EProjectLoadType.valueOf(GeneralManager.get()
					.getPreferenceStore()
					.getString(PreferenceConstants.LAST_CHOSEN_PROJECT_LOAD_TYPE));
		}
		catch (Exception e) {
			// this happens when no preference value exists jet (legal
			// situation) or when the value could not
			// be matched to the enum
		}
		if ((this.projectLoadType != null && this.projectLoadType.equals(EProjectLoadType.SPECIFIED))
 || recentProjectChange == null)
		{

			if (recentProjectChange == null)
				recentProject.setEnabled(false);

			loadProject.setSelection(true);
			this.projectLoadType = EProjectLoadType.SPECIFIED;
			this.projectFileName.setEnabled(true);
			chooseProjectFile.setEnabled(true);
		}
		else // if (recentProjectFile.exists())
		{
			recentProject.setEnabled(true);
			recentProject.setSelection(true);
			this.projectLoadType = EProjectLoadType.RECENT;

		}

		// if(!)
		// {
		//
		// }
		// else {
		//
		// }

		recentProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChooseProjectTypePage.this.projectLoadType = EProjectLoadType.RECENT;
				chooseProjectFile.setEnabled(false);
				ChooseProjectTypePage.this.projectFileName.setEnabled(false);
				ChooseProjectTypePage.this.setPageComplete(true);
			}
		});

		loadProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChooseProjectTypePage.this.projectLoadType = EProjectLoadType.SPECIFIED;
				ChooseProjectTypePage.this.projectFileName.setEnabled(true);
				chooseProjectFile.setEnabled(true);
				if (ChooseProjectTypePage.this.projectFileName.getText() != null && !ChooseProjectTypePage.this.projectFileName.getText().isEmpty()) {
					ChooseProjectTypePage.this.setPageComplete(true);
				}
				else {
					ChooseProjectTypePage.this.setPageComplete(false);
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
					ChooseProjectTypePage.this.projectFileName.setText(fileName);
					ChooseProjectTypePage.this.setPageComplete(true);
				}
			}
		});
	}

	/**
	 * Creates the tab for connecting a client to a already running
	 * caleydo-server-application to get the use case and basic data from.
	 *
	 * @param tabFolder tab-widget to create the new tab-item in
	 */
	private void createCollaborationClientTab(TabFolder tabFolder) {
		this.collaborationClientTab = new TabItem(tabFolder, SWT.NONE);
		this.collaborationClientTab.setText("Connect to Server");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		this.collaborationClientTab.setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		Label networkNameLabel = new Label(composite, SWT.LEFT);
		networkNameLabel.setText("Network Name:");
		this.networkNameText = new Text(composite, SWT.BORDER);
		this.networkNameText.setText("client");
		GridData gd = new GridData();
		gd.widthHint = 200;
		this.networkNameText.setLayoutData(gd);

		Label networkAddressLabel = new Label(composite, SWT.LEFT);
		networkAddressLabel.setText("Server Address:");
		this.networkAddressText = new Text(composite, SWT.BORDER);
		this.networkAddressText.setText("127.0.0.1");
		gd = new GridData();
		gd.widthHint = 200;
		this.networkAddressText.setLayoutData(gd);

		this.setPageComplete(true);
	}

	public ProjectMode getProjectMode() {
		return this.projectMode;
	}

	/**
	 * Returns the network-name entered by the user in the network-name
	 * text-field
	 *
	 * @return network-name to use
	 */
	public String getNetworkName() {
		return this.networkNameText.getText();
	}

	/**
	 * Returns the network-address entered by the user in the network-address
	 * text-field
	 *
	 * @return network-address to connect to
	 */
	public String getNetworkAddress() {
		return this.networkAddressText.getText();
	}

	/**
	 * Returns the project file-name for open existing projects
	 *
	 * @return
	 */
	public String getProjectFileName() {
		return this.projectFileName.getText();
	}

	public EProjectLoadType getProjectLoadType() {
		return this.projectLoadType;
	}

	public Organism getOrganism() {
		return this.organism;
	}

}
