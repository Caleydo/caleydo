/**
 *
 */
package org.caleydo.view.tourguide.internal.external.ui;

import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.base.IntegerCallback;
import org.caleydo.view.tourguide.internal.external.GroupLabelParseSpecification;
import org.eclipse.jface.dialogs.MessageDialog;
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
public class ImportExternalGroupLabelScoreDialog extends AImportExternalScoreDialog<GroupLabelParseSpecification> {
	/**
	 * The row id category for which groupings should be loaded.
	 */
	private final ATableBasedDataDomain dataDomain;

	private final boolean inDimensionDirection;

	private RowStratificationConfigWidget rowConfig;

	public ImportExternalGroupLabelScoreDialog(Shell parentShell,  ATableBasedDataDomain dataDomain,
			boolean inDimensionDirection) {
		this(parentShell, dataDomain, inDimensionDirection, null);
	}

	public ImportExternalGroupLabelScoreDialog(Shell parentShell, ATableBasedDataDomain dataDomain,
			boolean inDimensionDirection,
			GroupLabelParseSpecification existing) {
		super(parentShell, existing);
		this.dataDomain = dataDomain;
		this.inDimensionDirection = inDimensionDirection;
	}

	@Override
	protected GroupLabelParseSpecification createDummy() {
		return new GroupLabelParseSpecification();
	}

	@Override
	protected void createRowConfig(Composite parent) {
		rowConfig = new RowStratificationConfigWidget(parent, dataDomain, inDimensionDirection,
				new IntegerCallback() {
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
		rowConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	@Override
	protected void initWidgetsFromGroupParseSpecification(Display display) {
		super.initWidgetsFromGroupParseSpecification(display);

		this.rowConfig.setNumHeaderRows(spec.getNumberOfHeaderLines());
		this.rowConfig.setColumnOfRowIds(spec.getColumnOfRowIds() + 1);
		this.rowConfig.setPerspectiveKey(spec.getPerspectiveKey());
	}

	@Override
	protected void initWidgetsWithDefaultValues(Display display) {
		super.initWidgetsWithDefaultValues(display);

		this.rowConfig.setEnabled(false);
	}


	@Override
	protected boolean validate() {
		if (!super.validate())
			return false;

		if (this.rowConfig.getPerspectiveKey() == null) {
			MessageDialog.openError(new Shell(), "Invalid Row Stratification Selection", "Please select the Stratification for which scores should be imported");
			return false;
		}
		return true;
	}

	@Override
	protected void save() {
		super.save();
		spec.setPerspectiveKey(this.rowConfig.getPerspectiveKey());
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
