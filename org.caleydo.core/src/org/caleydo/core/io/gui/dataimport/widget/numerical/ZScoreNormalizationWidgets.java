/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.numerical;

import org.caleydo.core.io.NumericalProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * Widgets for Z-Score Normalization.
 *
 * @author Christian Partl
 *
 */
public class ZScoreNormalizationWidgets implements INumericalDataPropertiesWidgets {

	/**
	 * Button to determine whether a data center is used.
	 */
	protected Button useNormalizationButton;

	/**
	 * Text field used to define the data center.
	 */
	protected Combo normalizationTargetCombo;

	/**
	 * Group that contains widgets associated with determining the data center.
	 */
	protected Group normalizationGroup;

	protected String rowsName = "rows";

	protected String columnsName = "columns";

	@Override
	public void create(Composite parent, Listener listener) {
		normalizationGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		normalizationGroup.setText("Normalize to standard scores");
		normalizationGroup.setLayout(new GridLayout(2, false));
		normalizationGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label normalizationExplanationLabel = new Label(normalizationGroup, SWT.WRAP);
		normalizationExplanationLabel
				.setText("Replace the raw values with standard scores (z-scores). The data of a column or a row (choose below) will be transformed to have a mean of 0 and a standard deviation of 1.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 600;
		normalizationExplanationLabel.setLayoutData(gridData);
		useNormalizationButton = new Button(normalizationGroup, SWT.CHECK);
		useNormalizationButton.setText("Standardize for ");
		useNormalizationButton.addListener(SWT.Selection, listener);
		useNormalizationButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				useNormalizationButtonSelected();
			}
		});
		useNormalizationButton.setSelection(false);

		normalizationTargetCombo = new Combo(normalizationGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.widthHint = 120;
		normalizationTargetCombo.setLayoutData(gridData);
		normalizationTargetCombo.addListener(SWT.Modify, listener);
		normalizationTargetCombo.setItems(new String[] { rowsName, columnsName });
		normalizationTargetCombo.select(0);
		normalizationTargetCombo.setEnabled(true);
		normalizationGroup.layout(true, true);
	}

	public void setRowAndColumnDenomination(String rowName, String columnName) {
		int index = normalizationTargetCombo.getSelectionIndex();
		normalizationTargetCombo.setItems(new String[] { rowName, columnName });
		normalizationTargetCombo.select(index);
	}

	private void useNormalizationButtonSelected() {
		normalizationTargetCombo.setEnabled(useNormalizationButton.getSelection());
	}

	@Override
	public boolean isContentValid() {
		return true;
	}

	@Override
	public void updateProperties(NumericalProperties numericalProperties) {
		String normalization = numericalProperties.getzScoreNormalization();
		if (normalization == null || normalization.isEmpty()) {
			useNormalizationButton.setSelection(false);
			normalizationTargetCombo.setEnabled(false);
			return;
		}

		useNormalizationButton.setSelection(true);
		normalizationTargetCombo.setEnabled(true);
		if (normalization.equals(NumericalProperties.ZSCORE_ROWS)) {
			normalizationTargetCombo.select(0);
		} else {
			normalizationTargetCombo.select(1);
		}

	}

	@Override
	public void setProperties(NumericalProperties numericalProperties) {
		if (useNormalizationButton.getSelection()) {
			numericalProperties
					.setzScoreNormalization(normalizationTargetCombo.getSelectionIndex() == 0 ? NumericalProperties.ZSCORE_ROWS
							: NumericalProperties.ZSCORE_COLUMNS);
		} else {
			numericalProperties.setzScoreNormalization(null);
		}

	}

	@Override
	public void dispose() {
		normalizationGroup.dispose();
	}

}
