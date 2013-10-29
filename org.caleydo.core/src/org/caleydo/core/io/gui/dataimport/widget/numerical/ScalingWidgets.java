/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.numerical;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table.Transformation;
import org.caleydo.core.io.NumericalProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * Group of widgets for defining scaling items.
 *
 * @author Christian Partl
 *
 */
public class ScalingWidgets implements INumericalDataPropertiesWidgets {

	/**
	 * Combo to define the scaling method that should be applied to the data.
	 */
	protected Combo scalingCombo;

	protected Group scalingGroup;

	protected Map<Integer, String> scalingTextMap = new HashMap<>();

	@Override
	public void create(Composite parent, Listener listener) {
		scalingGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		scalingGroup.setText("Data Scale");
		scalingGroup.setLayout(new GridLayout(2, false));
		scalingGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label scalingExplanationLabel = new Label(scalingGroup, SWT.WRAP);
		scalingExplanationLabel
				.setText("Specify the way the data should be scaled. A linear scale is best if you are interested in absolute relationships within your data. A logarithmic scale is best if you are interested in the relative differences between data points and if your data is distributed inhomogeneously on a wide scale. Gene expression data, for example, is usually interpreted on a logarithmic scale.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 200;
		scalingExplanationLabel.setLayoutData(gridData);

		Label scalingMethodLabel = new Label(scalingGroup, SWT.NONE);
		scalingMethodLabel.setText("Scaling Method");

		scalingCombo = new Combo(scalingGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		gridData = new GridData();
		gridData.widthHint = 120;
		scalingCombo.setLayoutData(gridData);

		String[] scalingOptions = { Transformation.LINEAR, NumericalTable.Transformation.LOG2,
				NumericalTable.Transformation.LOG10 };
		scalingTextMap.put(0, Transformation.LINEAR);
		scalingTextMap.put(1, NumericalTable.Transformation.LOG2);
		scalingTextMap.put(2, NumericalTable.Transformation.LOG10);
		scalingCombo.setItems(scalingOptions);
		scalingCombo.setEnabled(true);

		scalingCombo.select(0);
		scalingGroup.layout(true, true);
	}

	@Override
	public boolean isContentValid() {
		return true;
	}

	@Override
	public void updateProperties(final NumericalProperties numericalProperties) {
		String previousMathFiltermode = numericalProperties.getDataTransformation();
		if (previousMathFiltermode.equals("Log10"))
			scalingCombo.select(1);
		else if (previousMathFiltermode.equals("Log2"))
			scalingCombo.select(2);
		else
			scalingCombo.select(0);
	}

	@Override
	public void setProperties(NumericalProperties numericalProperties) {
		numericalProperties.setDataTransformation(scalingTextMap.get(scalingCombo.getSelectionIndex()));
	}

	@Override
	public void dispose() {
		scalingGroup.dispose();
	}

}
