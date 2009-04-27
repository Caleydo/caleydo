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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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

	private Composite composite;

	private String clusterAlgo;
	private String clusterType;
	private String distmeasure;
	private int clusterCnt = 0;
	private float clusterFactor = 1f;

	ClusterState clusterState = null;

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
		composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Label lblClusterAlgo = new Label(composite, SWT.NONE);
		lblClusterAlgo.setText("Cluster algo:");
		lblClusterAlgo.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		Group clusterGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		clusterGroup.setLayout(new RowLayout());

		final Combo clusterAlgoCombo = new Combo(clusterGroup, SWT.DROP_DOWN);
		String[] sArAlgoOptions = { "tree", "cobweb", "affi", "kmeans" };

		clusterAlgoCombo.setItems(sArAlgoOptions);
		clusterAlgoCombo.setEnabled(true);
		clusterAlgoCombo.select(0);
		clusterAlgo = sArAlgoOptions[0];
		clusterAlgoCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clusterAlgo = clusterAlgoCombo.getText();

			}
		});

		ModifyListener listenerInt = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChangedInt((Text) e.widget);
			}
		};

		ModifyListener listenerFloat = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valueChangedFloat((Text) e.widget);
			}
		};

		Label lblClusterCnt = new Label(composite, SWT.NONE);
		lblClusterCnt.setText("Cluster cnt for KMeans:");
		lblClusterCnt.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final Text clusterCnt = new Text(composite, SWT.NONE);
		clusterCnt.addModifyListener(listenerInt);

		Label lblClusterFactor = new Label(composite, SWT.NONE);
		lblClusterFactor.setText("Factor for affinity prop:");
		lblClusterFactor.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		final Text clusterFactor = new Text(composite, SWT.NONE);
		clusterFactor.addModifyListener(listenerFloat);

		Label lblClusterType = new Label(composite, SWT.NONE);
		lblClusterType.setText("Cluster type:");
		lblClusterType.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		Group clusterTypeGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		clusterTypeGroup.setLayout(new RowLayout());

		final Combo clusterTypeCombo = new Combo(clusterTypeGroup, SWT.DROP_DOWN);
		String[] sArTypeOptions = { "gene", "exp", "bi" };
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

		Label lblDistMeasure = new Label(composite, SWT.NONE);
		lblDistMeasure.setText("Distance measure:");
		lblDistMeasure.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		Group distMeasureGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		distMeasureGroup.setLayout(new RowLayout());

		final Combo distMeasureCombo = new Combo(distMeasureGroup, SWT.DROP_DOWN);
		String[] sArDistOptions = { "euclid", "pearson" };
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
			clusterCnt = Integer.parseInt(text.getText());
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
				clusterFactor = temp;
		}
		catch (NumberFormatException e) {
			System.out.println("unvalid input");
		}

	}

	public void execute() {

		clusterState = new ClusterState();

		if (clusterAlgo.equals("cobweb"))
			clusterState.setClustererAlgo(EClustererAlgo.COBWEB_CLUSTERER);
		else if (clusterAlgo.equals("affi")) {
			clusterState.setClustererAlgo(EClustererAlgo.AFFINITY_PROPAGATION);
			clusterState.setAffinityPropClusterFactor(clusterFactor);
		}
		else if (clusterAlgo.equals("tree"))
			clusterState.setClustererAlgo(EClustererAlgo.TREE_CLUSTERER);
		else if (clusterAlgo.equals("kmeans")) {
			clusterState.setClustererAlgo(EClustererAlgo.KMEANS_CLUSTERER);
			if (clusterCnt != 0)
				System.out.println(clusterCnt);
			clusterState.setKMeansClusterCnt(clusterCnt);
		}

		if (clusterType.equals("gene"))
			clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
		else if (clusterType.equals("exp"))
			clusterState.setClustererType(EClustererType.EXPERIMENTS_CLUSTERING);
		else if (clusterType.equals("bi"))
			clusterState.setClustererType(EClustererType.BI_CLUSTERING);

		if (distmeasure.equals("euclid"))
			clusterState.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		else if (distmeasure.equals("pearson"))
			clusterState.setDistanceMeasure(EDistanceMeasure.PEARSON_CORRELATION);

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
