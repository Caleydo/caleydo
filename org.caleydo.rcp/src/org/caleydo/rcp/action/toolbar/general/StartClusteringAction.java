package org.caleydo.rcp.action.toolbar.general;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.EDistanceMeasure;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.dialog.StartClusteringDialog;
import org.caleydo.rcp.progress.ClusteringProgressBar;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action responsible for starting clustering
 * 
 * @author Bernhard Schlegl
 */
public class StartClusteringAction
	extends Action
	implements ActionFactory.IWorkbenchAction {

	public final static String ID = "org.caleydo.rcp.StartClusteringAction";
	public static final String TEXT = "Clustering";
	public static final String ICON = "resources/icons/view/storagebased/clustering.png";

	private Composite parentComposite;

	private String clusterType;
	private String distmeasure;
	private int iClusterCntGenes = 5;
	private int iClusterCntExperiments = 5;
	private float fclusterFactorGenes = 1f;
	private float fclusterFactorExperiments = 1f;

	private String[] sArTypeOptions = {"DETERMINED_DEPENDING_ON_USE_CASE", "Experiment", "Bi-Clustering"};
	private String[] sArDistOptions = { "Euclid distance", "Pearson correlation" };
	private String[] sArDistOptionsWeka = { "Euclid distance", "Manhattan distance"};
	
	private ClusterState clusterState = new ClusterState();

	private TabItem treeClusteringTab;
	private TabItem affinityPropagationTab;
	private TabItem kMeansTab;
	private TabItem cobwebTab;
	
	private Text clusterFactorGenes = null;
	private Text clusterFactorExperiments = null;

	/**
	 * Constructor.
	 */
	public StartClusteringAction(final Composite parentComposite) {
		super(TEXT);
		setId(ID);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));

		this.parentComposite = parentComposite;
		
		// Determine content label dynamically
		sArTypeOptions[0] = GeneralManager.get().getUseCase().getContentLabel(true, false);
	}

	@Override
	public void run() {

		createGUI();

	}

	private void createGUI() {

		Composite composite = new Composite(parentComposite, SWT.OK);
		// composite.setLayout(new FillLayout(SWT.VERTICAL));

		final TabFolder tabFolder = new TabFolder(composite, SWT.BORDER);

		createTreeClusteringTab(tabFolder);
		createAffinityPropagationTab(tabFolder);
		createKMeansTab(tabFolder);
		createCobwebTab(tabFolder);

		// set default value for cluster algo
		clusterState.setClustererAlgo(EClustererAlgo.TREE_CLUSTERER);

		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (((TabItem) e.item) == treeClusteringTab) {
					clusterState.setClustererAlgo(EClustererAlgo.TREE_CLUSTERER);
				}
				else if (((TabItem) e.item) == affinityPropagationTab) {
					clusterState.setClustererAlgo(EClustererAlgo.AFFINITY_PROPAGATION);
				}
				else if (((TabItem) e.item) == kMeansTab) {
					clusterState.setClustererAlgo(EClustererAlgo.KMEANS_CLUSTERER);
				}
				else if (((TabItem) e.item) == cobwebTab) {
					clusterState.setClustererAlgo(EClustererAlgo.COBWEB_CLUSTERER);
				}
				else
					throw new IllegalStateException("Not implemented!");
			}
		});

		tabFolder.pack();
		composite.pack();
	}

	private void createCobwebTab(TabFolder tabFolder) {
		cobwebTab = new TabItem(tabFolder, SWT.NONE);
		cobwebTab.setText("Cobweb");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		cobwebTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		final Combo clusterTypeCombo = new Combo(composite, SWT.DROP_DOWN);
		clusterTypeCombo.setItems(sArTypeOptions);
		clusterTypeCombo.setEnabled(true);
		clusterTypeCombo.select(0);
		clusterType = sArTypeOptions[0];
		clusterTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clusterType = clusterTypeCombo.getText();
			}
		});

	}

	private void createKMeansTab(TabFolder tabFolder) {
		kMeansTab = new TabItem(tabFolder, SWT.NONE);
		kMeansTab.setText("KMeans");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		kMeansTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		final Combo clusterTypeCombo = new Combo(composite, SWT.DROP_DOWN);
		clusterTypeCombo.setItems(sArTypeOptions);
		clusterTypeCombo.select(0);
		clusterType = sArTypeOptions[0];

		ModifyListener listenerIntGenes = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChangedInt((Text) e.widget, true);
			}
		};
		ModifyListener listenerIntExperiments = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChangedInt((Text) e.widget, false);
			}
		};

		final Combo distMeasureCombo = new Combo(composite, SWT.DROP_DOWN);
		distMeasureCombo.setItems(sArDistOptionsWeka);
		distMeasureCombo.setEnabled(true);
		distMeasureCombo.select(0);
		distmeasure = sArDistOptionsWeka[0];
		distMeasureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				distmeasure = distMeasureCombo.getText();
			}
		});
		
		final Label lblClusterCntGenes = new Label(composite, SWT.SHADOW_ETCHED_IN);
		lblClusterCntGenes.setText("Number clusters for clustering genes");
		lblClusterCntGenes.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final Text clusterCntGenes = new Text(composite, SWT.SHADOW_ETCHED_IN);
		clusterCntGenes.addModifyListener(listenerIntGenes);
		clusterCntGenes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clusterCntGenes.setText("5");
		clusterCntGenes.setToolTipText("Integer value. Range: 1 up to the number of samples in data set");

		final Label lblClusterCntExperiments = new Label(composite, SWT.SHADOW_ETCHED_IN);
		lblClusterCntExperiments.setText("Number clusters for clustering experiments");
		lblClusterCntExperiments.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final Text clusterCntExperiments = new Text(composite, SWT.SHADOW_ETCHED_IN);
		clusterCntExperiments.addModifyListener(listenerIntExperiments);
		clusterCntExperiments.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clusterCntExperiments.setText("5");
		clusterCntExperiments
			.setToolTipText("Integer value. Range: 1 up to the number of samples in data set");
		clusterCntExperiments.setEnabled(false);

		clusterTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clusterType = clusterTypeCombo.getText();
				if (clusterType.equals(sArTypeOptions[0])) {
					clusterCntGenes.setEnabled(true);
					clusterCntExperiments.setEnabled(false);
				}
				else if (clusterType.equals(sArTypeOptions[1])) {
					clusterCntGenes.setEnabled(false);
					clusterCntExperiments.setEnabled(true);
				}
				else {
					clusterCntGenes.setEnabled(true);
					clusterCntExperiments.setEnabled(true);
				}
			}
		});

	}

	private void createAffinityPropagationTab(TabFolder tabFolder) {
		affinityPropagationTab = new TabItem(tabFolder, SWT.NONE);
		affinityPropagationTab.setText("Affinity Propagation");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		affinityPropagationTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		final Combo clusterTypeCombo = new Combo(composite, SWT.DROP_DOWN);
		clusterTypeCombo.setItems(sArTypeOptions);
		clusterTypeCombo.select(0);
		clusterType = sArTypeOptions[0];

		final Combo distMeasureCombo = new Combo(composite, SWT.DROP_DOWN);
		distMeasureCombo.setItems(sArDistOptions);
		distMeasureCombo.setEnabled(true);
		distMeasureCombo.select(0);
		distmeasure = sArDistOptions[0];
		distMeasureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				distmeasure = distMeasureCombo.getText();
			}
		});

		ModifyListener listenerFloatGenes = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChangedFloat((Text) e.widget, true);
			}
		};

		ModifyListener listenerFloatExperiments = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChangedFloat((Text) e.widget, false);
			}
		};

		final Label lblClusterFactorGenes = new Label(composite, SWT.SHADOW_ETCHED_IN);
		lblClusterFactorGenes.setText("Factor for clustering genes");
		lblClusterFactorGenes.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		clusterFactorGenes = new Text(composite, SWT.SHADOW_ETCHED_IN);
		clusterFactorGenes.addModifyListener(listenerFloatGenes);
		clusterFactorGenes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clusterFactorGenes.setText("1.0");
		clusterFactorGenes
			.setToolTipText("Float value. Range: 1 up to 10. The bigger the value the less clusters will be formed");

		final Label lblClusterFactorExperiments = new Label(composite, SWT.SHADOW_ETCHED_IN);
		lblClusterFactorExperiments.setText("Factor for clustering experiments");
		lblClusterFactorExperiments.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		clusterFactorExperiments = new Text(composite, SWT.SHADOW_ETCHED_IN);
		clusterFactorExperiments.addModifyListener(listenerFloatExperiments);
		clusterFactorExperiments.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clusterFactorExperiments.setText("1.0");
		clusterFactorExperiments
			.setToolTipText("Float value. Range: 1 up to 10. The bigger the value the less clusters will be formed");
		clusterFactorExperiments.setEnabled(false);

		clusterTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clusterType = clusterTypeCombo.getText();
				if (clusterType.equals(sArTypeOptions[0])) {
					clusterFactorGenes.setEnabled(true);
					clusterFactorExperiments.setEnabled(false);
				}
				else if (clusterType.equals(sArTypeOptions[1])) {
					clusterFactorGenes.setEnabled(false);
					clusterFactorExperiments.setEnabled(true);
				}
				else {
					clusterFactorGenes.setEnabled(true);
					clusterFactorExperiments.setEnabled(true);
				}

			}
		});

	}

	private void createTreeClusteringTab(TabFolder tabFolder) {
		treeClusteringTab = new TabItem(tabFolder, SWT.NONE);
		treeClusteringTab.setText("Tree Clusterer");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		treeClusteringTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		final Combo clusterTypeCombo = new Combo(composite, SWT.DROP_DOWN);
		clusterTypeCombo.setItems(sArTypeOptions);
		clusterTypeCombo.select(0);
		clusterType = sArTypeOptions[0];
		clusterTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clusterType = clusterTypeCombo.getText();
			}
		});

		final Combo distMeasureCombo = new Combo(composite, SWT.DROP_DOWN);
		distMeasureCombo.setItems(sArDistOptions);
		distMeasureCombo.setEnabled(true);
		distMeasureCombo.select(0);
		distmeasure = sArDistOptions[0];
		distMeasureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				distmeasure = distMeasureCombo.getText();
			}
		});

	}

	private void valueChangedInt(Text text, boolean bGeneFactor) {
		if (!text.isFocusControl())
			return;

		int temp = 0;

		try {
			temp = Integer.parseInt(text.getText());
			if (temp > 0) {
				if (bGeneFactor == true)
					iClusterCntGenes = temp;
				else
					iClusterCntExperiments = temp;
			}
			else {
				Shell shell = new Shell();
				MessageBox messageBox = new MessageBox(shell, SWT.OK);
				messageBox.setText("Start Clustering");
				messageBox.setMessage("Number of clusters must be positive");
				messageBox.open();
			}
		}
		catch (NumberFormatException e) {
			System.out.println("Invalid input");
		}

	}

	private void valueChangedFloat(Text text, boolean bGeneFactor) {
		if (!text.isFocusControl())
			return;

		float temp = 0;

		try {
			temp = Float.parseFloat(text.getText());
			if (temp >= 1f && temp < 10) {
				if (bGeneFactor == true)
					fclusterFactorGenes = temp;
				else
					fclusterFactorExperiments = temp;
			}
			else {
				Shell shell = new Shell();
				MessageBox messageBox = new MessageBox(shell, SWT.OK);
				messageBox.setText("Start Clustering");
				messageBox.setMessage("Factor for affinity propagation has to be between 1.0 and 10.0");
				messageBox.open();
			}
		}
		catch (NumberFormatException e) {
			System.out.println("Invalid input");
		}

	}

	public void execute(boolean cancelPressed) {

		if (cancelPressed) {
			clusterState = null;
			return;
		}

		if (clusterType.equals(sArTypeOptions[0]))
			clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
		else if (clusterType.equals(sArTypeOptions[1]))
			clusterState.setClustererType(EClustererType.EXPERIMENTS_CLUSTERING);
		else if (clusterType.equals(sArTypeOptions[2]))
			clusterState.setClustererType(EClustererType.BI_CLUSTERING);

		if (distmeasure.equals(sArDistOptions[0]))
			clusterState.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		else if (distmeasure.equals(sArDistOptions[1]))
			clusterState.setDistanceMeasure(EDistanceMeasure.PEARSON_CORRELATION);
		else if (distmeasure.equals(sArDistOptionsWeka[1]))
			clusterState.setDistanceMeasure(EDistanceMeasure.MANHATTAHN_DISTANCE);

		clusterState.setAffinityPropClusterFactorGenes(fclusterFactorGenes);
		clusterState.setAffinityPropClusterFactorExperiments(fclusterFactorExperiments);
		clusterState.setKMeansClusterCntGenes(iClusterCntGenes);
		clusterState.setKMeansClusterCntExperiments(iClusterCntExperiments);

		ClusteringProgressBar progressBar = new ClusteringProgressBar(clusterState.getClustererAlgo());
		progressBar.run();

	}

	/**
	 * For testing purposes
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		StartClusteringDialog dialog = new StartClusteringDialog(new Shell());
		dialog.open();
	}

	@Override
	public void dispose() {
	}

	public ClusterState getClusterState() {
		return clusterState;
	}
}
