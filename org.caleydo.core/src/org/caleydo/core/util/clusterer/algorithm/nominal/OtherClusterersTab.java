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
