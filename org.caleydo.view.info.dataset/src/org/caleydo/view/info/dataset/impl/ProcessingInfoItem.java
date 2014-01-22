/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.impl;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.view.info.dataset.spi.IDataDomainDataSetItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

/**
 * how the data were processed
 * 
 * @author Samuel Gratzl
 * 
 */
public class ProcessingInfoItem implements IDataDomainDataSetItem {

	/** String used for formatting floats to 3 digits after the . using String.format() */
	static final String THREE_DIGIT_FORMAT = "%.3f";
	static final String TWO_DIGIT_FORMAT = "%.2f";

	private ExpandItem processingItem;
	private StyledText processingInfo;

	@Override
	public ExpandItem create(ExpandBar expandBar) {
		this.processingItem = new ExpandItem(expandBar, SWT.WRAP);
		processingItem.setText("Processing Info");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(1, false));

		processingInfo = new StyledText(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		processingInfo.setBackgroundMode(SWT.INHERIT_FORCE);
		processingInfo.setText("No processing");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint = 60;
		processingInfo.setLayoutData(gd);
		processingInfo.setEditable(false);
		processingInfo.setWordWrap(true);

		// transformationLabel.set
		// transformationLabel.();

		processingItem.setControl(c);
		processingItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		return processingItem;
	}

	@Override
	public void update(IDataDomain dataDomain) {
		if (!DataSupportDefinitions.numericalTables.apply(dataDomain)) {
			processingItem.setExpanded(false);
			processingInfo.setText("");
			return;
		}
		NumericalProperties numProp = dataDomain.getDataSetDescription().getDataDescription()
				.getNumericalProperties();
		String processingMessage = "";

		if (numProp.getzScoreNormalization() != null) {
			if (numProp.getzScoreNormalization().equals(NumericalProperties.ZSCORE_COLUMNS)) {
				processingMessage += "Z-standardised on "
						+ ((ATableBasedDataDomain) dataDomain).getColumnIDCategory() + "\n" + "";
			} else if (numProp.getzScoreNormalization().equals(NumericalProperties.ZSCORE_ROWS)) {
				processingMessage += "Z-standardised on " + ((ATableBasedDataDomain) dataDomain).getRowIDCategory()
						+ System.lineSeparator();
			}

		}
		if (numProp.getDataTransformation() != null) {
			processingMessage += "Scale: " + numProp.getDataTransformation() + System.lineSeparator();
		}
		if (numProp.getClipToStdDevFactor() != null) {
			processingMessage += "Clipped to " + String.format(TWO_DIGIT_FORMAT, numProp.getClipToStdDevFactor())
					+ " \u03C3" + System.lineSeparator();
		} else if (numProp.getMax() != null || numProp.getMin() != null) {
			processingMessage += "Clipped to max:  " + String.format(TWO_DIGIT_FORMAT, numProp.getMax()) + ", min:"
					+ String.format("%.2f", numProp.getMin()) + System.lineSeparator();
		}
		NumericalTable table = (NumericalTable) ((ATableBasedDataDomain) dataDomain).getTable();
		if (table.getDataCenter() != null) {
			processingMessage += "Centered at " + String.format(TWO_DIGIT_FORMAT, table.getDataCenter())
					+ System.lineSeparator();
		}
		processingInfo.setText(processingMessage);
	}
}
