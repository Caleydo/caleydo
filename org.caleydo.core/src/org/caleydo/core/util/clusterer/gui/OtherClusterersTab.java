package org.caleydo.core.util.clusterer.gui;

import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.ClustererType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class OtherClusterersTab {

	private TabItem customTab;
	private Button alphabetical;
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

		clusterState.setClustererType(ClustererType.RECORD_CLUSTERING);
		return clusterState;
	}
}
