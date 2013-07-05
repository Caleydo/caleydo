/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.tree;

import org.caleydo.core.util.clusterer.gui.AClusterTab;
import org.caleydo.core.util.clusterer.initialization.AClusterAlgorithmConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author alexsb
 * 
 */
public class TreeTab extends AClusterTab {

	private String treeClusterAlgo;

	private String[] sArTreeClusterer = ETreeClustererAlgo.getNames();

	/**
	 * 
	 */
	public TreeTab(TabFolder tabFolder) {
		super(tabFolder);
		createTreeClusteringTab();
	}

	private void createTreeClusteringTab() {
		clusterTab = new TabItem(tabFolder, SWT.NONE);
		clusterTab.setData(this);
		clusterTab.setText("Tree Clusterer");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		clusterTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		final Combo treeClustererCombo = new Combo(composite, SWT.DROP_DOWN);
		treeClustererCombo.setItems(sArTreeClusterer);
		treeClustererCombo.select(0);
		treeClusterAlgo = sArTreeClusterer[0];
		treeClustererCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				treeClusterAlgo = treeClustererCombo.getText();
			}
		});
	}

	@Override
	public AClusterAlgorithmConfiguration getClusterConfiguration() {
		TreeClusterConfiguration clusterConfiguration = new TreeClusterConfiguration();
		clusterConfiguration.setTreeClustererAlgo(ETreeClustererAlgo
				.getTypeForName(treeClusterAlgo));
		return clusterConfiguration;
	}

}
