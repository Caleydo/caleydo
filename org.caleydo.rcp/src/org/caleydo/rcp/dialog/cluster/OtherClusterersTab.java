package org.caleydo.rcp.dialog.cluster;

import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class OtherClusterersTab {

	private TabItem customTab;
	private Button alphabetical;
	private Button locationShallow;
	private Button locationDeep;
	private Button typeShallow;
	private Button typeDeep;
	private Button metalShallow;
	private Button metalDeep;
	private Button other;

	public OtherClusterersTab(TabFolder tabFolder) {
		createCustomTab(tabFolder);
	}

	private void createCustomTab(TabFolder tabFolder) {
		customTab = new TabItem(tabFolder, SWT.NONE);
		customTab.setText("Custom Algorithms");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		customTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		alphabetical = new Button(composite, SWT.RADIO);
		alphabetical.setText("Alphabetical");

		locationShallow = new Button(composite, SWT.RADIO);
		locationShallow.setText("Location Shallow");
		locationDeep = new Button(composite, SWT.RADIO);
		locationDeep.setText("Location Deep");
		
		typeShallow = new Button(composite, SWT.RADIO);
		typeShallow.setText("Object Type Shallow");
		typeDeep = new Button(composite, SWT.RADIO);
		typeDeep.setText("Object Type Deep");
		
		metalShallow = new Button(composite, SWT.RADIO);
		metalShallow.setText("Metal Shallow");
		metalDeep = new Button(composite, SWT.RADIO);
		metalDeep.setText("Metal Deep");

		other = new Button(composite, SWT.RADIO);
		other.setText("Other");

	}

	public TabItem getTab() {
		return customTab;
	}

	public ClusterState getClusterState() {
		ClusterState clusterState = new ClusterState();
		if (alphabetical.getSelection())
			clusterState.setClustererAlgo(EClustererAlgo.ALPHABETICAL);
		
		if (locationShallow.getSelection())
			clusterState.setClustererAlgo(EClustererAlgo.LOCATION_SHALLOW);
		
		if (locationDeep.getSelection())
			clusterState.setClustererAlgo(EClustererAlgo.LOCATION_DEEP);
		
		if (typeShallow.getSelection())
			clusterState.setClustererAlgo(EClustererAlgo.OBJECT_TYPE_SHALLOW);
		
		if (typeDeep.getSelection())
			clusterState.setClustererAlgo(EClustererAlgo.OBJECT_TYPE_DEEP);
		
		if (metalShallow.getSelection())
			clusterState.setClustererAlgo(EClustererAlgo.METAL_SHALLOW);
		
		if (metalDeep.getSelection())
			clusterState.setClustererAlgo(EClustererAlgo.METAL_DEEP);

		clusterState.setClustererType(EClustererType.CONTENT_CLUSTERING);
		return clusterState;
	}
}
