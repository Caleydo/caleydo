/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.impl;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.view.info.dataset.spi.IDataDomainDataSetItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;

/**
 * infos about the dcurrent selected data set
 * 
 * @author Samuel Gratzl
 * 
 */
public class DataSetItem implements IDataDomainDataSetItem {
	private ExpandItem dataSetItem;
	private Label recordLabel;
	private Label recordCount;
	private Label dimensionLabel;
	private Label dimensionCount;

	@Override
	public ExpandItem create(ExpandBar expandBar) {
		this.dataSetItem = new ExpandItem(expandBar, SWT.WRAP);
		dataSetItem.setText("Data Set: <no selection>");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(2, false));

		recordLabel = new Label(c, SWT.NONE);
		recordLabel.setText("");
		recordLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		recordCount = new Label(c, SWT.NONE);
		recordCount.setText("");
		recordCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		dimensionLabel = new Label(c, SWT.NONE);
		dimensionLabel.setText("");
		dimensionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		dimensionCount = new Label(c, SWT.NONE);
		dimensionCount.setText("");
		dimensionCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		dataSetItem.setControl(c);
		dataSetItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		dataSetItem.setExpanded(false);

		return dataSetItem;
	}

	@Override
	public void update(IDataDomain dataDomain) {
		final int stringLength = 28;

		String dsLabel = "Dataset: " + dataDomain.getLabel();
		if (dsLabel.length() > stringLength)
			dsLabel = dsLabel.substring(0, stringLength - 3) + "...";

		// dataSetItem.setText("Data Set: " + dataDomain.getLabel().substring(0, 15));
		dataSetItem.setText(dsLabel);

		if (dataDomain instanceof ATableBasedDataDomain) {
			ATableBasedDataDomain tableBasedDD = (ATableBasedDataDomain) dataDomain;

			dataSetItem.setExpanded(true);

			int nrRecords = tableBasedDD.getTable().depth();
			int nrDimensions = tableBasedDD.getTable().size();
			String recordName = tableBasedDD.getRecordDenomination(true, true);
			recordLabel.setText(recordName + ":");
			recordCount.setText("" + nrRecords);

			String dimensionName = tableBasedDD.getDimensionDenomination(true, true);
			dimensionLabel.setText(dimensionName + ":");
			dimensionCount.setText("" + nrDimensions);

			((Composite) dataSetItem.getControl()).layout();
		} else {
			dataSetItem.setExpanded(true);
		}
	}
}
