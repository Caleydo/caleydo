/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.external.ui;

import java.util.List;

import org.caleydo.core.util.base.IntegerCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Samuel Gratzl
 *
 */
public class RowDataDomainConfigWidget {
	private final Group group;
	private final Spinner columnIDSpinner;
	private final Spinner numHeaderRowsSpinner;

	public RowDataDomainConfigWidget(Composite parent, final IntegerCallback onNumHeaderRowsChanged,
			final IntegerCallback onColumnOfRowIDChanged) {
		this.group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Row Configuration");
		group.setLayout(new GridLayout(1, false));

		Composite leftConfigGroupPart = new Composite(group, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		Label startParseAtLineLabel = new Label(leftConfigGroupPart, SWT.NONE);
		startParseAtLineLabel.setText("Number of Header Rows");

		this.numHeaderRowsSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		numHeaderRowsSpinner.setMinimum(0);
		numHeaderRowsSpinner.setMaximum(Integer.MAX_VALUE);
		numHeaderRowsSpinner.setIncrement(1);
		numHeaderRowsSpinner.setSelection(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, true);
		gridData.widthHint = 70;
		numHeaderRowsSpinner.setLayoutData(gridData);
		numHeaderRowsSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				onNumHeaderRowsChanged.on(numHeaderRowsSpinner.getSelection());
			}
		});

		Label columnOfRowIDlabel = new Label(leftConfigGroupPart, SWT.NONE);
		columnOfRowIDlabel.setText("Column with Group Labels");
		columnOfRowIDlabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		// columnOfRowIDGroup.setLayout(new GridLayout(1, false));

		this.columnIDSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		columnIDSpinner.setMinimum(1);
		columnIDSpinner.setMaximum(Integer.MAX_VALUE);
		columnIDSpinner.setIncrement(1);
		columnIDSpinner.setSelection(1);
		gridData = new GridData(SWT.LEFT, SWT.TOP, false, true);
		gridData.widthHint = 70;
		columnIDSpinner.setLayoutData(gridData);
		columnIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				onColumnOfRowIDChanged.on(columnIDSpinner.getSelection());
			}
		});
	}

	public void setLayoutData(Object layoutData) {
		this.group.setLayoutData(layoutData);
	}

	public void setEnabled(boolean enabled) {
		this.group.setEnabled(enabled);
		this.columnIDSpinner.setEnabled(enabled);
		this.numHeaderRowsSpinner.setEnabled(enabled);
	}

	public void setNumHeaderRows(int numberOfHeaderLines) {
		this.numHeaderRowsSpinner.setSelection(numberOfHeaderLines);
	}

	public void setColumnOfRowIds(int i) {
		this.columnIDSpinner.setSelection(i);
	}

	public void setMaxDimension(int totalNumberOfColumns, int totalNumberOfRows) {
		columnIDSpinner.setMaximum(totalNumberOfColumns);
		numHeaderRowsSpinner.setMaximum(totalNumberOfRows);
	}

	public void determineConfigFromPreview(List<? extends List<String>> dataMatrix) {
		guessNumberOfHeaderRows(dataMatrix);
	}

	private void guessNumberOfHeaderRows(List<? extends List<String>> dataMatrix) {
		// In grouping case we can have 0 header rows as there does not have to
		// be an id row
		int numHeaderRows = 0;
		for (int i = 0; i < dataMatrix.size(); i++) {
			List<String> row = dataMatrix.get(i);
			int numFloatsFound = 0;
			for (int j = 0; j < row.size(); j++) {
				String text = row.get(j);
				try {
					// This currently only works for numerical values
					Float.parseFloat(text);
					numFloatsFound++;
					if (numFloatsFound >= 3) {
						this.setNumHeaderRows(numHeaderRows);
						return;
					}
				} catch (Exception e) {

				}
			}
			numHeaderRows++;
		}
	}
}
