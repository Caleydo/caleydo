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
package org.caleydo.core.util.clusterer.algorithm.kmeans;

import org.caleydo.core.util.clusterer.gui.AClusterTab;
import org.caleydo.core.util.clusterer.initialization.AClusterAlgorithmConfiguration;
import org.caleydo.core.util.clusterer.initialization.EDistanceMeasure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author alexsb
 *
 */
public class KMeansTab extends AClusterTab {

	private int nrClustersRecords = 5;
	private Button cacheVectors;

	public KMeansTab(TabFolder tabFolder) {
		super(tabFolder);
		createTab();
	}

	private void createTab() {
		clusterTab = new TabItem(tabFolder, SWT.NONE);
		clusterTab.setData(this);
		clusterTab.setText("KMeans");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		clusterTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		final Label clusterNumberLabel = new Label(composite, SWT.NONE);
		clusterNumberLabel.setText("Number clusters for clustering genes");
		clusterNumberLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		final Text clusterNumberText = new Text(composite, SWT.BORDER);
		clusterNumberText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		clusterNumberText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				try {
					int temp = Integer.parseInt(clusterNumberText.getText());
					if (temp > 0) {
						nrClustersRecords = temp;

					} else {
						throw new NumberFormatException("Only positive values > 0 allowed");
					}
				} catch (NumberFormatException e) {
					Shell shell = new Shell();
					MessageBox messageBox = new MessageBox(shell, SWT.OK);
					messageBox.setText("Start Clustering");
					messageBox.setMessage("Number of clusters must be positive");
					messageBox.open();
					System.out.println("Invalid input");
				}

			}
		});
		clusterNumberText.setText("5");
		clusterNumberText.setToolTipText("Positive integer value. Range: 1 up to the number of samples in data set");

		cacheVectors = new Button(composite, SWT.CHECK);
		cacheVectors.setText("Cache Data Items? (faster but needs more memory)");
		cacheVectors.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	public AClusterAlgorithmConfiguration getClusterConfiguration() {
		KMeansClusterConfiguration clusterConfiguration = new KMeansClusterConfiguration();
		clusterConfiguration.setNumberOfClusters(nrClustersRecords);
		clusterConfiguration.setCacheVectors(cacheVectors.getSelection());
		return clusterConfiguration;
	}

	/**
	 * The Weka K-Means implementation supports not all distance measures,
	 * therefore only a subset is returned
	 */
	@Override
	public String[] getSupportedDistanceMeasures() {
		return new String[] { EDistanceMeasure.EUCLIDEAN_DISTANCE.getName(),
				EDistanceMeasure.MANHATTAN_DISTANCE.getName() };
	}

}
