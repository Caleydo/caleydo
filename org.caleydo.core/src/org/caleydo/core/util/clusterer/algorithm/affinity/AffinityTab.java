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
package org.caleydo.core.util.clusterer.algorithm.affinity;

import org.caleydo.core.util.clusterer.gui.AClusterTab;
import org.caleydo.core.util.clusterer.initialization.AClusterAlgorithmConfiguration;
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
public class AffinityTab extends AClusterTab {

	private float clusterFactor = 1f;

	private Text clusterFactorText = null;

	/**
	 * 
	 */
	public AffinityTab(TabFolder tabFolder) {
		super(tabFolder);
		createAffinityPropagationTab();
	}

	private void createAffinityPropagationTab() {
		clusterTab = new TabItem(tabFolder, SWT.NONE);
		clusterTab.setData(this);
		clusterTab.setText("Affinity Propagation");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		clusterTab.setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		ModifyListener listenerFloatGenes = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				valueChangedFloat((Text) e.widget);
			}
		};

		// ModifyListener listenerFloatExperiments = new ModifyListener() {
		// @Override
		// public void modifyText(ModifyEvent e) {
		// valueChangedFloat((Text) e.widget, false);
		// }
		// };

		final Label lblClusterFactor = new Label(composite, SWT.SHADOW_ETCHED_IN);
		lblClusterFactor.setText("Factor for clustering (Range 1-10)");
		lblClusterFactor.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
				false));

		clusterFactorText = new Text(composite, SWT.SHADOW_ETCHED_IN);
		clusterFactorText.addModifyListener(listenerFloatGenes);
		clusterFactorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clusterFactorText.setText("1.0");
		clusterFactorText
				.setToolTipText("Float value. Range: 1 up to 10. The bigger the value the less clusters will be formed");

		// final Label lblClusterFactorExperiments = new Label(composite,
		// SWT.SHADOW_ETCHED_IN);
		// lblClusterFactorExperiments.setText("Factor for clustering experiments");
		// lblClusterFactorExperiments.setLayoutData(new GridData(GridData.END,
		// GridData.CENTER,
		// false, false));

		// clusterFactorExperiments = new Text(composite, SWT.SHADOW_ETCHED_IN);
		// clusterFactorExperiments.addModifyListener(listenerFloatExperiments);
		// clusterFactorExperiments.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));
		// clusterFactorExperiments.setText("1.0");
		// clusterFactorExperiments
		// .setToolTipText("Float value. Range: 1 up to 10. The bigger the value the less clusters will be formed");
		// clusterFactorExperiments.setEnabled(false);

		// clusterTypeCombo.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// clusterType = clusterTypeCombo.getText();
		// if (clusterType.equals(typeOptions[0])) {
		// clusterFactorText.setEnabled(true);
		// clusterFactorExperiments.setEnabled(false);
		// }
		// else if (clusterType.equals(typeOptions[1])) {
		// clusterFactorText.setEnabled(false);
		// clusterFactorExperiments.setEnabled(true);
		// }
		// else {
		// clusterFactorText.setEnabled(true);
		// clusterFactorExperiments.setEnabled(true);
		// }
		//
		// }
		// });

	}

	private void valueChangedFloat(Text text) {
		if (!text.isFocusControl())
			return;

		float temp = 0;

		try {
			temp = Float.parseFloat(text.getText());
			if (temp >= 1f && temp <= 10) {
				clusterFactor = temp;
			} else {
				Shell shell = new Shell();
				MessageBox messageBox = new MessageBox(shell, SWT.OK);
				messageBox.setText("Start Clustering");
				messageBox
						.setMessage("Factor for affinity propagation has to be between 1.0 and 10.0");
				messageBox.open();
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input");
		}

	}

	@Override
	public AClusterAlgorithmConfiguration getClusterConfiguration() {
		AffinityClusterConfiguration clusterConfiguration = new AffinityClusterConfiguration();
		clusterConfiguration.setClusterFactor(clusterFactor);
		return clusterConfiguration;
	}

}
