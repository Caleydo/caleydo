/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.numerical;

import org.caleydo.core.io.KNNImputeDescription;
import org.caleydo.core.io.NumericalProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * Widgets for specifying dataset imputation
 *
 * @author Christian Partl
 *
 */
public class ImputationWidgets implements INumericalDataPropertiesWidgets {

	/**
	 * Button to determine whether imputation is used.
	 */
	protected Button useImputationButton;

	/**
	 * Group that contains widgets associated with imputation
	 */
	protected Group imputationGroup;

	@Override
	public void create(Composite parent, Listener listener) {
		imputationGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		imputationGroup.setText("Data Imputation");
		imputationGroup.setLayout(new GridLayout(1, false));
		imputationGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label imputationExplanationLabel = new Label(imputationGroup, SWT.WRAP);
		imputationExplanationLabel
				.setText("Impute missing and NaN values from other values using a k-nearest neighbor algorithm.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.widthHint = 600;
		imputationExplanationLabel.setLayoutData(gridData);
		useImputationButton = new Button(imputationGroup, SWT.CHECK);
		useImputationButton.setText("Use imputation");
		useImputationButton.addListener(SWT.Selection, listener);
		useImputationButton.setSelection(false);

		imputationGroup.layout(true, true);

	}

	@Override
	public boolean isContentValid() {
		return true;
	}

	@Override
	public void updateProperties(NumericalProperties numericalProperties) {
		useImputationButton.setSelection(numericalProperties.getImputeDescription() != null);
	}

	@Override
	public void setProperties(NumericalProperties numericalProperties) {
		if (useImputationButton.getSelection()) {
			numericalProperties.setImputeDescription(new KNNImputeDescription());
		} else {
			numericalProperties.setImputeDescription(null);
		}
	}

	@Override
	public void dispose() {
		imputationGroup.dispose();
	}

}
