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
package org.caleydo.core.util.clusterer.gui;

import java.util.ArrayList;
import org.caleydo.core.data.configuration.DataChooserComposite;
import org.caleydo.core.data.configuration.DataConfiguration;
import org.caleydo.core.data.configuration.DataConfigurationChooser;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.event.data.StartClusteringEvent;
import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.io.gui.IDataOKListener;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.algorithm.affinity.AffinityTab;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansTab;
import org.caleydo.core.util.clusterer.algorithm.tree.TreeTab;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.core.util.link.LinkHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * <p>
 * Dialog for configuring and starting clustering. Reads and writes a
 * {@link ClusterConfiguration} bean.
 * </p>
 * <p>
 * Writing is supported for all options except for the
 * {@link ClusterConfiguration#setOptionalTargetDimensionPerspective(org.caleydo.core.data.perspective.variable.DimensionPerspective)}
 * and its record equivalent.
 * </p>
 * <p>
 * Reading (i.e. initializing the dialog based on an existing configuration is
 * only implemented for the following propoerties:
 * </p>
 * 
 * <ul>
 * <li>
 * {@link ClusterConfiguration#getClusterTarget()}</li>
 * <li>
 * {@link ClusterConfiguration#getSourceRecordPerspective()}</li>
 * <li>
 * {@link ClusterConfiguration#getSourceDimensionPerspective()}</li>
 * <li>
 * {@link ClusterConfiguration#isModifyExistingPerspective()}</li>
 * </ul>
 * 
 * @author Alexander Lex
 * @author Bernhard Schlegl
 * @author Christian Partl
 */
public class ClusterDialog extends AHelpButtonDialog implements IDataOKListener {

	public static final String TEXT = "Clustering";
	public static final String ICON = "resources/icons/view/tablebased/clustering.png";

	private Composite parentComposite;

	/** The combo for choosing which type of perspective to cluster */
	private Combo clusterTypeCombo;
	private String[] typeOptions = { "Choose Dataset First", "Invisible" };

	private Combo distanceMeasureCombo;
	private String[] distanceMeasureOptions = EDistanceMeasure.getNames();

	/**
	 * Check button determining whether the selected perspective should be
	 * replaced (check) or not
	 */
	private Button modifyExistingPerspectiveButton;

	private ATableBasedDataDomain dataDomain;
	private ClusterConfiguration clusterConfiguration;

	private TabFolder tabFolder;

	private DataChooserComposite dataChooser;

	public ClusterDialog(Shell parentShell) {
		super(parentShell);
		clusterConfiguration = new ClusterConfiguration();
	}

	public ClusterDialog(Shell parentShell, ATableBasedDataDomain dataDomain) {
		super(parentShell);
		this.dataDomain = dataDomain;
		clusterConfiguration = new ClusterConfiguration();
	}

	/**
	 * Constructor with {@link ClusterConfiguration} for pre-setting values
	 */
	public ClusterDialog(Shell parentShell, ATableBasedDataDomain dataDomain,
			ClusterConfiguration clusterConfiguration) {
		super(parentShell);
		this.dataDomain = dataDomain;
		this.clusterConfiguration = clusterConfiguration;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(TEXT);
		newShell.setImage(GeneralManager.get().getResourceLoader()
				.getImage(newShell.getDisplay(), ICON));

		TrayDialog trayDialog = (TrayDialog) newShell.getData();
		trayDialog.setHelpAvailable(true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		parentComposite = parent;

		if (dataDomain == null) {
			// here we check whether there is a unique set of configurations for
			// the datadomain
			ArrayList<ATableBasedDataDomain> availableDomains = DataDomainManager.get()
					.getDataDomainsByType(ATableBasedDataDomain.class);

			ArrayList<ATableBasedDataDomain> tableBasedDataDomains = new ArrayList<ATableBasedDataDomain>();
			for (ATableBasedDataDomain tempDataDomain : availableDomains) {
				tableBasedDataDomains.add(tempDataDomain);
			}
			DataConfiguration config = DataConfigurationChooser
					.determineDataConfiguration(tableBasedDataDomains, "Clustering",
							false);

			dataDomain = config.getDataDomain();
			clusterConfiguration.setSourceDimensionPerspective(config
					.getDimensionPerspective());
			clusterConfiguration
					.setSourceRecordPerspective(config.getRecordPerspective());

		}

		if (dataDomain != null) {
			typeOptions[0] = dataDomain.getRecordDenomination(true, true);
			typeOptions[1] = dataDomain.getDimensionDenomination(true, true);
		}

		createGUI();

		return parent;
	}

	private void createGUI() {
		Composite composite = new Composite(parentComposite, 0);

		composite.setLayout(new GridLayout(2, false));

		if (dataDomain == null) {
			Group dataChooserGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
			dataChooserGroup.setText("Choose Dataset:");
			dataChooserGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
					2, 1));
			dataChooserGroup.setLayout(new GridLayout(1, false));
			dataChooser = new DataChooserComposite(this, dataChooserGroup, null, SWT.NONE);
			// dataChooser.setLayout(new GridLayout(1, false));
			GridData gridData = new GridData(SWT.BEGINNING, SWT.TOP, true, false);
			// gridData.grabExcessHorizontalSpace = true;
			gridData.minimumWidth = 300;
			dataChooser.setLayoutData(gridData);
		}

		Label clusterDimensionGroup = new Label(composite, SWT.NONE);
		clusterDimensionGroup.setText("Cluster:");
		// clusterDimensionGroup.setLayout(new GridLayout(1, false));

		clusterTypeCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		clusterTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		clusterTypeCombo.setItems(typeOptions);

		EClustererTarget target = clusterConfiguration.getClusterTarget();
		if (target == null || target.equals(EClustererTarget.RECORD_CLUSTERING)) {
			clusterTypeCombo.select(0);

		} else {
			clusterTypeCombo.select(1);
		}

		if (dataDomain == null) {
			clusterTypeCombo.setEnabled(false);
		}

		Label distanceMeasureGroup = new Label(composite, SWT.NONE);
		distanceMeasureGroup.setText("Distance Measure:");
		// distanceMeasureGroup.setLayout(new GridLayout(1, false));

		distanceMeasureCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		distanceMeasureCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		distanceMeasureCombo.setItems(distanceMeasureOptions);
		distanceMeasureCombo.setEnabled(true);
		distanceMeasureCombo.select(0);

		tabFolder = new TabFolder(composite, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		// check whether the algorithm supports all distance measures
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int selectionIndex = tabFolder.getSelectionIndex();
				AClusterTab tab = (AClusterTab) tabFolder.getItem(selectionIndex)
						.getData();
				if (tab == null)
					return;
				int selectedMeasure = distanceMeasureCombo.getSelectionIndex();
				String[] supportedMeasures = tab.getSupportedDistanceMeasures();
				distanceMeasureCombo.setItems(supportedMeasures);
				if (supportedMeasures.length - 1 < selectedMeasure)
					distanceMeasureCombo.select(0);
				else
					distanceMeasureCombo.select(selectedMeasure);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		new KMeansTab(tabFolder);
		new AffinityTab(tabFolder);
		new TreeTab(tabFolder);

		Group modifyPerspectiveGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		modifyPerspectiveGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
				2, 1));
		modifyPerspectiveGroup.setLayout(new GridLayout(1, false));
		modifyPerspectiveGroup.setText("Modify selected or add new grouping");

		Label replaceExplanation = new Label(modifyPerspectiveGroup, SWT.WRAP);
		replaceExplanation
				.setText("Select whether you want to add a new grouping, or whether you want the selected grouping to be changed.");
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.widthHint = 250;
		replaceExplanation.setLayoutData(gd);

		modifyExistingPerspectiveButton = new Button(modifyPerspectiveGroup, SWT.CHECK);
		modifyExistingPerspectiveButton.setLayoutData(new GridData());
		modifyExistingPerspectiveButton.setText("Add new Grouping");
		modifyExistingPerspectiveButton.setSelection(!clusterConfiguration
				.isModifyExistingPerspective());
		// new OtherClusterersTab(tabFolder);

		tabFolder.pack();
		composite.pack();
		composite.layout();

	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		if (!isDataOK())
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		return control;

	}

	private boolean isDataOK() {
		if (dataDomain != null
				&& clusterConfiguration.getSourceRecordPerspective() != null
				&& clusterConfiguration.getSourceDimensionPerspective() != null)
			return true;

		return false;
	}

	@Override
	protected void okPressed() {
		// startClusteringAction.execute(false);

		int selectionIndex = tabFolder.getSelectionIndex();
		TabItem tabItem = tabFolder.getItems()[selectionIndex];
		AClusterTab clusterTab = (AClusterTab) tabItem.getData();

		clusterConfiguration.setClusterAlgorithmConfiguration(clusterTab
				.getClusterConfiguration());

		if (clusterTypeCombo.getText().equals(typeOptions[0]))
			clusterConfiguration.setClusterTarget(EClustererTarget.RECORD_CLUSTERING);
		else if (clusterTypeCombo.getText().equals(typeOptions[1]))
			clusterConfiguration.setClusterTarget(EClustererTarget.DIMENSION_CLUSTERING);
		else {
			throw new IllegalStateException("Unkonwn Cluster Target: "
					+ clusterTypeCombo.getText());
		}

		clusterConfiguration.setDistanceMeasure(EDistanceMeasure
				.getTypeForName(distanceMeasureCombo.getText()));

		clusterConfiguration
				.setModifyExistingPerspective(!modifyExistingPerspectiveButton
						.getSelection());

		// clusterConfiguration.setSourceRecordPerspective(recordPerspective);
		// clusterConfiguration.setSourceDimensionPerspective(dimensionPerspective);
		//
		// clusterConfiguration.setOptionalTargetRecordPerspective(parent
		// .getTargetRecordPerspective());
		// clusterConfiguration.setOptionalTargetDimensionPerspective(parent
		// .getTargetDimensionPerspective());

		ClusteringProgressBar progressBar = new ClusteringProgressBar(
				clusterConfiguration.getClusterAlgorithmConfiguration()
						.getClusterAlgorithmName());
		progressBar.run();

		if (clusterConfiguration == null)
			return;

		StartClusteringEvent event = null;
		// if (clusterState != null && set != null)

		event = new StartClusteringEvent(clusterConfiguration);
		event.setDataDomainID(dataDomain.getDataDomainID());
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		// startClusteringAction.execute(true);

		clusterConfiguration = null;

		super.cancelPressed();
	}

	/**
	 * Returns the ClusterState as determined by the Cluster Dialog, or null if
	 * the dialog was canceled.
	 * 
	 * @return
	 */
	public ClusterConfiguration getClusterConfiguration() {
		return clusterConfiguration;

	}

	@Override
	public void dataOK() {

		dataDomain = dataChooser.getDataDomain();
		clusterConfiguration.setSourceRecordPerspective(dataChooser
				.getRecordPerspective());
		clusterConfiguration.setSourceDimensionPerspective(dataChooser
				.getDimensionPerspective());

		setDataDependendStuff();
		getButton(OK).setEnabled(true);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	private void setDataDependendStuff() {
		typeOptions[0] = dataDomain.getRecordDenomination(true, false);
		typeOptions[1] = dataDomain.getDimensionDenomination(true, false);
		clusterTypeCombo.setItems(typeOptions);
		clusterTypeCombo.select(0);
		clusterTypeCombo.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.gui.util.AHelpButtonDialog#helpPressed()
	 */
	@Override
	protected void helpPressed() {
		LinkHandler
				.openLink("http://www.icg.tugraz.at/project/caleydo/help/manipulating-data#clustering");
	}

}
