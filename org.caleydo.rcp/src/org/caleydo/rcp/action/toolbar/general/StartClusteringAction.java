package org.caleydo.rcp.action.toolbar.general;

import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.EDistanceMeasure;
import org.caleydo.rcp.dialog.file.StartClusteringDialog;
import org.caleydo.rcp.image.IImageKeys;
import org.eclipse.jface.action.Action;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Action responsible for starting clustering
 * 
 * @author Bernhard Schlegl
 */
public class StartClusteringAction
	extends Action
	implements ActionFactory.IWorkbenchAction {

	public final static String ID = "org.caleydo.rcp.StartClusteringAction";

	private Composite parentComposite;

	private String clusterType;
	private String distmeasure;
	private int iClusterCnt = 5;
	private float fclusterFactor = 1f;

	String[] sArTypeOptions = { "Gene", "Experiment" }; //, "Bi-Clustering" };
	String[] sArDistOptions = { "Euclid distance", "Pearson correlation" };

	ClusterState clusterState = new ClusterState();

	private TabItem treeClusteringTab;
	private TabItem affinityPropagationTab;
	private TabItem kMeansTab;
	private TabItem cobwebTab;

	/**
	 * Constructor.
	 */
	public StartClusteringAction(final Composite parentComposite) {
		super("Start Clustering");
		setId(ID);
		setToolTipText("Start Clustering");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.caleydo.rcp",
			IImageKeys.FILE_OPEN_XML_CONFIG_FILE));

		this.parentComposite = parentComposite;

	}

	@Override
	public void run() {

		createGUI();

	}

	private void createGUI() {

		Composite composite = new Composite(parentComposite, SWT.OK);
//		composite.setLayout(new FillLayout(SWT.VERTICAL));

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
		clusterTypeCombo.setEnabled(true);
		clusterTypeCombo.select(0);
		clusterType = sArTypeOptions[0];
		clusterTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clusterType = clusterTypeCombo.getText();
			}
		});

		final Label lblClusterCnt = new Label(composite, SWT.SHADOW_ETCHED_IN);
		lblClusterCnt.setText("Cluster count for KMeans:");
		lblClusterCnt.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		ModifyListener listenerInt = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChangedInt((Text) e.widget);
			}
		};

		final Text clusterCnt = new Text(composite, SWT.SHADOW_ETCHED_IN);
		clusterCnt.addModifyListener(listenerInt);
		clusterCnt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clusterCnt.setText("5");
		clusterCnt.setToolTipText("Integer value. Range: 1 up to the number of samples in data set");

	}

	private void createAffinityPropagationTab(TabFolder tabFolder) {
		affinityPropagationTab = new TabItem(tabFolder, SWT.NONE);
		affinityPropagationTab.setText("Affinity Propagation");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		affinityPropagationTab.setControl(composite);
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

		final Label lblClusterFactor = new Label(composite, SWT.SHADOW_ETCHED_IN);
		lblClusterFactor.setText("Factor for affinity propagagtion:");
		lblClusterFactor.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		ModifyListener listenerFloat = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChangedFloat((Text) e.widget);
			}
		};

		final Text clusterFactor = new Text(composite, SWT.SHADOW_ETCHED_IN);
		clusterFactor.addModifyListener(listenerFloat);
		clusterFactor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clusterFactor.setText("1.0");
		clusterFactor
			.setToolTipText("Float value. Range: 1 up to 10. The bigger the value the less clusters will be formed");

	}

	private void createTreeClusteringTab(TabFolder tabFolder) {
		treeClusteringTab = new TabItem(tabFolder, SWT.NONE);
		treeClusteringTab.setText("Tree Clusterer");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		treeClusteringTab.setControl(composite);
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

	public void valueChangedInt(Text text) {
		if (!text.isFocusControl())
			return;

		try {
			iClusterCnt = Integer.parseInt(text.getText());
		}
		catch (NumberFormatException e) {
			System.out.println("unvalid input");
		}

	}

	public void valueChangedFloat(Text text) {
		if (!text.isFocusControl())
			return;

		float temp = 0;

		try {
			temp = Float.parseFloat(text.getText());
			if (temp >= 1f && temp < 10)
				fclusterFactor = temp;
		}
		catch (NumberFormatException e) {
			System.out.println("unvalid input");
		}

	}

	public void execute(boolean cancelPressed) {

		if(cancelPressed) {
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

		clusterState.setAffinityPropClusterFactor(fclusterFactor);
		clusterState.setKMeansClusterCnt(iClusterCnt);

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
