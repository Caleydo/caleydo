/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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

		final Label lblClusterFactor = new Label(composite, SWT.NONE);
		lblClusterFactor.setText("Factor for clustering (Range 1-10)");
		lblClusterFactor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		clusterFactorText = new Text(composite, SWT.BORDER);
		clusterFactorText.addModifyListener(listenerFloatGenes);
		clusterFactorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		clusterFactorText.setText("9.0");
		clusterFactorText
				.setToolTipText("Float value. Range: 1 up to 10. The bigger the value the less clusters will be formed");
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
