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

import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.ClustererType;
import org.caleydo.core.util.clusterer.initialization.EClustererAlgo;
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

	public ClusterConfiguration getClusterState() {
		ClusterConfiguration clusterState = new ClusterConfiguration();
		if (alphabetical.getSelection())
			clusterState.setClustererAlgo(EClustererAlgo.ALPHABETICAL);

		clusterState.setClustererType(ClustererType.RECORD_CLUSTERING);
		return clusterState;
	}
}
