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

		// final Combo distMeasureCombo = new Combo(distanceMeasureGroup,
		// SWT.DROP_DOWN);
		// distMeasureCombo.setItems(distanceMeasureOptions);
		// distMeasureCombo.setEnabled(true);
		// distMeasureCombo.select(0);
		// distmeasure = distanceMeasureOptions[0];
		// distMeasureCombo.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// distmeasure = distMeasureCombo.getText();
		// }
		// });

	}

	@Override
	public AClusterAlgorithmConfiguration getClusterConfiguration() {
		TreeClusterConfiguration clusterConfiguration = new TreeClusterConfiguration();
		clusterConfiguration.setTreeClustererAlgo(ETreeClustererAlgo
				.getTypeForName(treeClusterAlgo));
		return clusterConfiguration;
	}

}
