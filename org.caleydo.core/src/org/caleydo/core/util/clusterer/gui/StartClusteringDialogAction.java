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
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.event.data.StartClusteringEvent;
import org.caleydo.core.event.view.browser.ChangeURLEvent;
import org.caleydo.core.io.gui.IDataOKListener;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.algorithm.affinity.AffinityTab;
import org.caleydo.core.util.clusterer.algorithm.kmeans.KMeansTab;
import org.caleydo.core.util.clusterer.algorithm.tree.TreeTab;
import org.caleydo.core.util.clusterer.initialization.AClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action containing the gui for configuring the clustering algorithms.
 * 
 * @author Alexander Lex
 * @author Bernhard Schlegl
 * 
 */
public class StartClusteringDialogAction extends Action implements
		ActionFactory.IWorkbenchAction, IDataOKListener {

	public final static String ID = "org.caleydo.core.util.clusterer.gui.StartClusteringAction";
	public static final String TEXT = "Clustering";
	public static final String ICON = "resources/icons/view/tablebased/clustering.png";

	private Composite parentComposite;

	private String clusterTargetName;
	private String distanceMeasureName;

	private String[] typeOptions = { "Choose Dataset First", "Invisible" };
	private Combo clusterTypeCombo;

	private String[] distanceMeasureOptions = EDistanceMeasure.getNames();

	private ATableBasedDataDomain dataDomain;
	private AClusterConfiguration clusterConfiguration;

	private RecordPerspective recordPerspective = null;
	private DimensionPerspective dimensionPerspective = null;

	private TabFolder tabFolder;

	private DataChooserComposite dataChooser;
	private StartClusteringDialog parent;

	/**
	 * Constructor.
	 */
	public StartClusteringDialogAction(StartClusteringDialog parent,
			final Composite parentComposite, ATableBasedDataDomain dataDomain,
			DimensionPerspective dimensionPerspective, RecordPerspective recordPerspective) {
		super(TEXT);
		setId(ID);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));

		this.parent = parent;
		this.parentComposite = parentComposite;

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

			this.dataDomain = config.getDataDomain();
			dimensionPerspective = config.getDimensionPerspective();
			recordPerspective = config.getRecordPerspective();
		} else {
			this.dataDomain = dataDomain;

		}

		this.dimensionPerspective = dimensionPerspective;
		this.recordPerspective = recordPerspective;
		if (this.dataDomain != null) {
			typeOptions[0] = this.dataDomain.getRecordDenomination(true, false);
			typeOptions[1] = this.dataDomain.getDimensionDenomination(true, false);
		}
	}

	@Override
	public void run() {

		createGUI();
	}

	private void createGUI() {
		Composite composite = new Composite(parentComposite, 0);

		composite.setLayout(new GridLayout(1, false));

		if (dataDomain == null) {
			Group dataChooserGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
			dataChooserGroup.setText("Choose Dataset:");
			dataChooserGroup.setLayout(new GridLayout(1, false));
			dataChooser = new DataChooserComposite(this, dataChooserGroup, SWT.NONE);
			dataChooser.setLayout(new GridLayout(1, false));
			GridData gridData = new GridData(SWT.BEGINNING, SWT.TOP, true, false);
			// gridData.grabExcessHorizontalSpace = true;
			gridData.minimumWidth = 300;
			dataChooser.setLayoutData(gridData);
		}

		Group clusterDimensionGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		clusterDimensionGroup.setText("Cluster:");
		clusterDimensionGroup.setLayout(new GridLayout(1, false));

		clusterTypeCombo = new Combo(clusterDimensionGroup, SWT.DROP_DOWN);
		clusterTypeCombo.setItems(typeOptions);
		clusterTypeCombo.select(0);
		clusterTargetName = typeOptions[0];

		if (dataDomain == null) {
			clusterTypeCombo.setEnabled(false);
		}

		Group distanceMeasureGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		distanceMeasureGroup.setText("Distance measure:");
		distanceMeasureGroup.setLayout(new GridLayout(1, false));

		final Combo distMeasureCombo = new Combo(distanceMeasureGroup, SWT.DROP_DOWN);
		distMeasureCombo.setItems(distanceMeasureOptions);
		distMeasureCombo.setEnabled(true);
		distMeasureCombo.select(0);
		distanceMeasureName = distanceMeasureOptions[0];
		distMeasureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				distanceMeasureName = distMeasureCombo.getText();
			}
		});

		tabFolder = new TabFolder(composite, SWT.BORDER);

		// check whether the algorithm supports all distance measures
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int selectionIndex = tabFolder.getSelectionIndex();
				AClusterTab tab = (AClusterTab) tabFolder.getItem(selectionIndex)
						.getData();
				int selectedMeasure = distMeasureCombo.getSelectionIndex();
				String[] supportedMeasures = tab.getSupportedDistanceMeasures();
				distMeasureCombo.setItems(supportedMeasures);
				if (supportedMeasures.length - 1 < selectedMeasure)
					distMeasureCombo.select(0);
				else
					distMeasureCombo.select(selectedMeasure);

				distanceMeasureName = distMeasureCombo.getText();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		composite.addHelpListener(new HelpListener() {

			@Override
			public void helpRequested(HelpEvent e) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView("org.caleydo.view.browser");

					final String URL_HELP_CLUSTERING = "http://www.caleydo.org/help/gene_expression.html#Clustering";
					ChangeURLEvent changeURLEvent = new ChangeURLEvent();
					changeURLEvent.setSender(this);
					changeURLEvent.setUrl(URL_HELP_CLUSTERING);
					GeneralManager.get().getEventPublisher().triggerEvent(changeURLEvent);
				} catch (PartInitException partInitException) {
				}
			}
		});

		new TreeTab(tabFolder);
		new AffinityTab(tabFolder);
		new KMeansTab(tabFolder);
		// new OtherClusterersTab(tabFolder);

		Button helpButton = new Button(composite, SWT.PUSH);
		helpButton.setText("Help");
		helpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView("org.caleydo.view.browser");

					String stHelp = "http://www.caleydo.org/help/gene_expression.html#Cobweb";

					ChangeURLEvent changeURLEvent = new ChangeURLEvent();
					changeURLEvent.setSender(this);
					changeURLEvent.setUrl(stHelp);
					GeneralManager.get().getEventPublisher().triggerEvent(changeURLEvent);
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
			}
		});

		tabFolder.pack();
		// if (dataDomain != null) {
		// setDataDependendStuff();
		// }
		// composite.pack();
	}

	public void execute(boolean cancelPressed) {

		if (cancelPressed) {
			clusterConfiguration = null;
			return;
		}
		int selectionIndex = tabFolder.getSelectionIndex();
		TabItem tabItem = tabFolder.getItems()[selectionIndex];
		AClusterTab clusterTab = (AClusterTab) tabItem.getData();

		clusterConfiguration = clusterTab.getClusterConfiguration();

		if (clusterTargetName.equals(typeOptions[0]))
			clusterConfiguration.setClusterTarget(EClustererTarget.RECORD_CLUSTERING);
		else if (clusterTargetName.equals(typeOptions[1]))
			clusterConfiguration.setClusterTarget(EClustererTarget.DIMENSION_CLUSTERING);
		else {
			throw new IllegalStateException("Unkonwn Cluster Target: "
					+ clusterTargetName);
		}

		clusterConfiguration.setDistanceMeasure(EDistanceMeasure
				.getTypeForName(distanceMeasureName));

		clusterConfiguration.setSourceRecordPerspective(recordPerspective);
		clusterConfiguration.setSourceDimensionPerspective(dimensionPerspective);

		clusterConfiguration.setOptionalTargetRecordPerspective(parent
				.getTargetRecordPerspective());
		clusterConfiguration.setOptionalTargetDimensionPerspective(parent
				.getTargetDimensionPerspective());

		ClusteringProgressBar progressBar = new ClusteringProgressBar(
				clusterConfiguration.getClusterAlgorithmName());
		progressBar.run();

		if (clusterConfiguration == null)
			return;

		StartClusteringEvent event = null;
		// if (clusterState != null && set != null)

		event = new StartClusteringEvent(clusterConfiguration);
		event.setDataDomainID(dataDomain.getDataDomainID());
		GeneralManager.get().getEventPublisher().triggerEvent(event);

	}

	@Override
	public void dispose() {
	}

	public AClusterConfiguration getClusterState() {
		return clusterConfiguration;
	}

	@Override
	public void dataOK() {
		dataDomain = dataChooser.getDataDomain();
		recordPerspective = dataChooser.getRecordPerspective();
		dimensionPerspective = dataChooser.getDimensionPerspective();

		setDataDependendStuff();
	}

	private void setDataDependendStuff() {
		typeOptions[0] = dataDomain.getRecordDenomination(true, false);
		typeOptions[1] = dataDomain.getDimensionDenomination(true, false);
		clusterTypeCombo.setItems(typeOptions);
		clusterTypeCombo.select(0);
		clusterTypeCombo.setEnabled(true);
		clusterTargetName = typeOptions[0];
		parent.dataOK();
	}

}
