/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.impl;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.info.dataset.spi.ITablePerspectiveDataSetItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;

/**
 * descriptive stats
 * 
 * @author Samuel Gratzl
 * 
 */
public class TablePerspectiveItem implements ITablePerspectiveDataSetItem {
	private ExpandItem tablePerspectiveItem;

	private Label recordPerspectiveLabel;
	private Label recordPerspectiveCount;
	// private Label unmappedRecordElements;
	private Label dimensionPerspectiveLabel;
	private Label dimensionPerspectiveCount;

	@Override
	public ExpandItem create(ExpandBar expandBar) {
		this.tablePerspectiveItem = new ExpandItem(expandBar, SWT.WRAP);
		tablePerspectiveItem.setText("Perspective: <no selection>");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(2, false));

		recordPerspectiveLabel = new Label(c, SWT.NONE);
		recordPerspectiveLabel.setText("");
		recordPerspectiveLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		recordPerspectiveCount = new Label(c, SWT.NONE);
		recordPerspectiveCount.setText("");
		recordPerspectiveCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		// unmappedRecordElements = new Label(c, SWT.NONE);
		// unmappedRecordElements.setText("");
		// unmappedRecordElements.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 0));

		dimensionPerspectiveLabel = new Label(c, SWT.NONE);
		dimensionPerspectiveLabel.setText("");
		dimensionPerspectiveLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		dimensionPerspectiveCount = new Label(c, SWT.NONE);
		dimensionPerspectiveCount.setText("");
		dimensionPerspectiveCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		// unmappedDimensionElements = new Label(c, SWT.NONE);
		// unmappedDimensionElements.setText("");
		// unmappedDimensionElements.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 0));

		tablePerspectiveItem.setControl(c);
		tablePerspectiveItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		tablePerspectiveItem.setExpanded(false);

		return tablePerspectiveItem;
	}

	@Override
	public void update(IDataDomain dataDomain, TablePerspective tablePerspective) {
		short stringLength = 28;

		if (dataDomain instanceof ATableBasedDataDomain) {
			ATableBasedDataDomain tableBasedDD = (ATableBasedDataDomain) dataDomain;

			int nrRecords = tableBasedDD.getTable().depth();
			int nrDimensions = tableBasedDD.getTable().size();
			String recordName = tableBasedDD.getRecordDenomination(true, true);

			String dimensionName = tableBasedDD.getDimensionDenomination(true, true);

			recordPerspectiveLabel.setText(recordName + ":");
			dimensionPerspectiveLabel.setText(dimensionName + ":");

			if (tablePerspective != null) {
				String tpLabel = "Persp.: " + tablePerspective.getLabel();
				if (tpLabel.length() > stringLength)
					tpLabel = tpLabel.substring(0, stringLength - 3) + "...";
				tablePerspectiveItem.setText(tpLabel);
				tablePerspectiveItem.setExpanded(true);

				recordPerspectiveCount.setText("" + tablePerspective.getNrRecords() + " ("
						+ String.format("%.2f", tablePerspective.getNrRecords() * 100f / nrRecords) + "%)");
				if (tablePerspective.getRecordPerspective().getUnmappedElements() > 0) {
					recordPerspectiveCount.setToolTipText("Unmapped: "
							+ tablePerspective.getRecordPerspective().getUnmappedElements() + " - The number of "
							+ recordName
							+ " that are in the original stratification but can't be mapped to this dataset");
				} else {
					recordPerspectiveCount.setToolTipText("");
				}

				dimensionPerspectiveCount.setText("" + tablePerspective.getNrDimensions() + " ("
						+ String.format("%.2f", tablePerspective.getNrDimensions() * 100f / nrDimensions) + "%)");
				if (tablePerspective.getDimensionPerspective().getUnmappedElements() > 1) {
					dimensionPerspectiveCount.setToolTipText("Unmapped: "
							+ tablePerspective.getDimensionPerspective().getUnmappedElements() + " - The number of "
							+ dimensionName
							+ " that are in the original stratification but can't be mapped to this dataset");
				} else {
					dimensionPerspectiveCount.setToolTipText("");
				}

				((Composite) tablePerspectiveItem.getControl()).layout();
			} else {
				tablePerspectiveItem.setText("Perspective: <no selection>");
				tablePerspectiveItem.setExpanded(false);
				recordPerspectiveCount.setText("<no selection>");
				recordPerspectiveCount.setToolTipText("");
				dimensionPerspectiveCount.setText("<no selection>");
				dimensionPerspectiveCount.setToolTipText("");
			}
		}
	}
}
