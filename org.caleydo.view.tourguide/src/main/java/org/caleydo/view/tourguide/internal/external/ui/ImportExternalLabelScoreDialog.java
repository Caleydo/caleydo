/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.tourguide.internal.external.ui;

import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.base.IntegerCallback;
import org.caleydo.view.tourguide.api.external.ExternalLabelParseSpecification;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for loading groupings for datasets.
 *
 * @author Christian Partl
 *
 */
public class ImportExternalLabelScoreDialog extends AImportExternalScoreDialog<ExternalLabelParseSpecification> {
	/**
	 * The row id category for which groupings should be loaded.
	 */
	private final IDataDomain dataDomain;

	private RowDataDomainConfigWidget rowConfig;

	public ImportExternalLabelScoreDialog(Shell parentShell, IDataDomain dataDomain) {
		this(parentShell, dataDomain, null);
	}

	public ImportExternalLabelScoreDialog(Shell parentShell, IDataDomain dataDomain,
			ExternalLabelParseSpecification existing) {
		super(parentShell, existing);
		this.dataDomain = dataDomain;
	}

	@Override
	protected ExternalLabelParseSpecification createDummy() {
		return new ExternalLabelParseSpecification();
	}

	@Override
	protected void createRowConfig(Composite parent) {
		rowConfig = new RowDataDomainConfigWidget(parent, new IntegerCallback() {
					@Override
			public void on(int data) {
						previewTable.onNumHeaderRowsChanged(data);
					}
				}, new IntegerCallback() {
					@Override
					public void on(int data) {
						previewTable.onColumnOfRowIDChanged(data);
					}
				});
		rowConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
	}

	@Override
	protected void initWidgetsFromGroupParseSpecification(Display display) {
		super.initWidgetsFromGroupParseSpecification(display);

		this.rowConfig.setNumHeaderRows(spec.getNumberOfHeaderLines());
		this.rowConfig.setColumnOfRowIds(spec.getColumnOfRowIds() + 1);
	}

	@Override
	protected void initWidgetsWithDefaultValues(Display display) {
		super.initWidgetsWithDefaultValues(display);

		this.rowConfig.setEnabled(false);
	}


	@Override
	protected void save() {
		super.save();
		spec.setDataDomainID(this.dataDomain.getDataDomainID());
	}

	@Override
	public void onSelectFile(String inputFileName) {
		super.onSelectFile(inputFileName);
		this.rowConfig.setEnabled(true);
	}

	@Override
	protected void onPreviewChanged(int totalNumberOfColumns, int totalNumberOfRows,
			List<? extends List<String>> dataMatrix) {
		this.rowConfig.setMaxDimension(totalNumberOfColumns, totalNumberOfRows);
		this.rowConfig.determineConfigFromPreview(dataMatrix);
		super.onPreviewChanged(totalNumberOfColumns, totalNumberOfRows, dataMatrix);
	}
}
