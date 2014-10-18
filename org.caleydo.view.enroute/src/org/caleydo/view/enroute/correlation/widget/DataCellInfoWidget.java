/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.widget;

import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Christian
 *
 */
public class DataCellInfoWidget extends Composite {

	private Label datasetLabel;
	private Label groupLabel;
	private Label rowLabel;

	/**
	 * @param parent
	 * @param style
	 */
	public DataCellInfoWidget(Composite parent) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(1, true));
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		datasetLabel = new Label(this, SWT.NONE);
		datasetLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		datasetLabel.setText("Dataset: ");

		groupLabel = new Label(this, SWT.NONE);
		groupLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		groupLabel.setText("Group: ");

		rowLabel = new Label(this, SWT.NONE);
		rowLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		rowLabel.setText("Row: ");
	}

	public void updateInfo(DataCellInfo info) {
		datasetLabel.setText("Dataset: " + (info == null ? "" : info.getDataDomainLabel()));
		groupLabel.setText("Group: " + (info == null ? "" : info.getGroupLabel()));
		rowLabel.setText("Row: " + (info == null ? "" : info.getRowLabel()));
	}

}
