/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.nominal;

import org.caleydo.core.util.clusterer.gui.AClusterTab;
import org.caleydo.core.util.clusterer.initialization.AClusterAlgorithmConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class OtherClusterersTab extends AClusterTab {

	private Button alphabetical;
	private Button other;

	public OtherClusterersTab(TabFolder tabFolder) {
		super(tabFolder);
		createCustomTab();
	}

	private void createCustomTab() {
		clusterTab = new TabItem(tabFolder, SWT.NONE);
		clusterTab.setText("Custom Algorithms");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		clusterTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		alphabetical = new Button(composite, SWT.RADIO);
		alphabetical.setText("Alphabetical");

		other = new Button(composite, SWT.RADIO);
		other.setText("Other");

	}



	@Override
	public AClusterAlgorithmConfiguration getClusterConfiguration() {
		return new NominalClusterConfiguration();
	}

}
