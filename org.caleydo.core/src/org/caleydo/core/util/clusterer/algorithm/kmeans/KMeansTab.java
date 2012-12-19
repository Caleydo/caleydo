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

	/**
	 *
	 */
	public KMeansTab(TabFolder tabFolder) {
		super(tabFolder);
		createTab();
	}

	private void createTab() {
		clusterTab = new TabItem(tabFolder, SWT.NONE);
		clusterTab.setData(this);
		clusterTab.setText("KMeans");

		// ModifyListener listenerIntGenes = new ModifyListener() {
		// @Override
		// public void modifyText(ModifyEvent e) {
		// valueChangedInt((Text) e.widget, true);
		// }
		// };
		// ModifyListener listenerIntExperiments = new ModifyListener() {
		// @Override
		// public void modifyText(ModifyEvent e) {
		// valueChangedInt((Text) e.widget, false);
		// }
		// };

		// Group distanceMeasureGroup = new Group(composite,
		// SWT.SHADOW_ETCHED_IN);
		// distanceMeasureGroup.setText("Distance measure:");
		// distanceMeasureGroup.setLayout(new GridLayout(1, false));
		//
		// final Combo distMeasureCombo = new Combo(distanceMeasureGroup,
		// SWT.DROP_DOWN);
		// distMeasureCombo.setItems(sArDistOptionsWeka);
		// distMeasureCombo.setEnabled(true);
		// distMeasureCombo.select(0);
		// distmeasure = sArDistOptionsWeka[0];
		// distMeasureCombo.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// distmeasure = distMeasureCombo.getText();
		// }
		// });

		Composite composite = new Composite(tabFolder, SWT.NONE);
		clusterTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		final Label clusterNumberLabel = new Label(composite, SWT.SHADOW_ETCHED_IN);
		clusterNumberLabel.setText("Number clusters for clustering genes");
		clusterNumberLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		final Text clusterNumberText = new Text(composite, SWT.SHADOW_ETCHED_IN);
		clusterNumberText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {
				try {
					int temp = Integer.parseInt(clusterNumberText.getText());
					if (temp > 0) {
						nrClustersRecords = temp;

					} else {
						throw new NumberFormatException(
								"Only positive values > 0 allowed");
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
		clusterNumberText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clusterNumberText.setText("5");
		clusterNumberText
				.setToolTipText("Positive integer value. Range: 1 up to the number of samples in data set");

		// final Label lblClusterCntExperiments = new Label(composite,
		// SWT.SHADOW_ETCHED_IN);
		// lblClusterCntExperiments.setText("Number clusters for clustering experiments");
		// lblClusterCntExperiments.setLayoutData(new GridData(GridData.END,
		// GridData.CENTER, false, false));
		//
		// final Text clusterCntExperiments = new Text(composite,
		// SWT.SHADOW_ETCHED_IN);
		// clusterCntExperiments.addModifyListener(listenerIntExperiments);
		// clusterCntExperiments.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));
		// clusterCntExperiments.setText("5");
		// clusterCntExperiments
		// .setToolTipText("Positive integer value. Range: 1 up to the number of samples in data set");
		// clusterCntExperiments.setEnabled(false);

		// clusterTypeCombo.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// clusterType = clusterTypeCombo.getText();
		// if (clusterType.equals(typeOptions[0])) {
		// clusterNumberText.setEnabled(true);
		// clusterCntExperiments.setEnabled(false);
		// } else if (clusterType.equals(typeOptions[1])) {
		// clusterNumberText.setEnabled(false);
		// clusterCntExperiments.setEnabled(true);
		// } else {
		// clusterNumberText.setEnabled(true);
		// clusterCntExperiments.setEnabled(true);
		// }
		// }
		// });

	}

	// private void valueChangedInt(Text text {
	// if (!text.isFocusControl())
	// return;
	//
	// int temp = 0;
	//
	// try {
	// temp = Integer.parseInt(text.getText());
	// if (temp > 0) {
	// if (bGeneFactor == true)
	// nrClustersRecords = temp;
	// else
	// nrClustersDimensions = temp;
	// } else {
	// Shell shell = new Shell();
	// MessageBox messageBox = new MessageBox(shell, SWT.OK);
	// messageBox.setText("Start Clustering");
	// messageBox.setMessage("Number of clusters must be positive");
	// messageBox.open();
	// }
	// } catch (NumberFormatException e) {
	// System.out.println("Invalid input");
	// }
	//
	// }

	@Override
	public AClusterAlgorithmConfiguration getClusterConfiguration() {
		KMeansClusterConfiguration clusterConfiguration = new KMeansClusterConfiguration();
		clusterConfiguration.setNumberOfClusters(nrClustersRecords);
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
