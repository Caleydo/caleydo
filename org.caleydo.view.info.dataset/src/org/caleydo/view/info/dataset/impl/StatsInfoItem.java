/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.impl;

import static org.caleydo.view.info.dataset.impl.ProcessingInfoItem.THREE_DIGIT_FORMAT;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.view.info.dataset.spi.IDataDomainDataSetItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

/**
 * stats of the current data set
 * 
 * @author Samuel Gratzl
 * 
 */
public class StatsInfoItem implements IDataDomainDataSetItem {

	private ExpandItem statsItem;
	private StyledText stats;

	@Override
	public ExpandItem create(ExpandBar expandBar) {
		this.statsItem = new ExpandItem(expandBar, SWT.WRAP);
		statsItem.setText("Dataset Stats");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(1, false));

		stats = new StyledText(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		stats.setBackgroundMode(SWT.INHERIT_FORCE);
		stats.setText("No processing");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint = 60;
		stats.setLayoutData(gd);
		stats.setEditable(false);
		stats.setWordWrap(true);

		// transformationLabel.set
		// transformationLabel.();

		statsItem.setControl(c);
		statsItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		return statsItem;
	}

	@Override
	public void update(IDataDomain dataDomain) {
		if (!DataSupportDefinitions.numericalTables.apply(dataDomain)) {
			statsItem.setExpanded(false);
			stats.setText("");
			return;
		}
		NumericalTable table = (NumericalTable) ((ATableBasedDataDomain) dataDomain).getTable();
		String n = System.lineSeparator();

		DoubleStatistics dsStats = table.getDatasetStatistics();
		String statsMessage = "";
		statsMessage += "Mean:\t\t\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getMean()) + n;
		statsMessage += "Std. Dev.:\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getSd()) + n;
		statsMessage += "Max:\t\t\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getMax()) + n;
		statsMessage += "Min:\t\t\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getMin()) + n;
		statsMessage += "Skewness:\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getSkewness()) + n;
		statsMessage += "Kurtosis:\t\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getKurtosis()) + n;

		stats.setText(statsMessage);
	}
}
