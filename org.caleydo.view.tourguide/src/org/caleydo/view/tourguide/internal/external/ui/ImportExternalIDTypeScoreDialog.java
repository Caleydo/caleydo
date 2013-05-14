/**
 *
 */
package org.caleydo.view.tourguide.internal.external.ui;

import java.util.List;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.gui.dataimport.widget.IProvider;
import org.caleydo.core.io.gui.dataimport.widget.IntegerCallback;
import org.caleydo.core.io.gui.dataimport.widget.RowConfigWidget;
import org.caleydo.view.tourguide.internal.external.ScoreParseSpecification;
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
public class ImportExternalIDTypeScoreDialog extends AImportExternalScoreDialog<ScoreParseSpecification> {
	/**
	 * The row id category for which groupings should be loaded.
	 */
	private final IDCategory rowIDCategory;

	private RowConfigWidget rowConfig;

	public ImportExternalIDTypeScoreDialog(Shell parentShell, IDCategory rowIDCategory) {
		this(parentShell, rowIDCategory, null);
	}

	public ImportExternalIDTypeScoreDialog(Shell parentShell, IDCategory rowIDCategory, ScoreParseSpecification existing) {
		super(parentShell, existing);
		this.rowIDCategory = rowIDCategory;
	}

	@Override
	protected ScoreParseSpecification createDummy() {
		return new ScoreParseSpecification();
	}

	@Override
	protected void createRowConfig(Composite parent) {
		rowConfig = new RowConfigWidget(parent, new IntegerCallback() {
			@Override
			public void on(int data) {
				previewTable.onNumHeaderRowsChanged(data);
			}
		}, new IntegerCallback() {
			@Override
			public void on(int data) {
				previewTable.onColumnOfRowIDChanged(data);
			}
		}, new IProvider<String>() {

			@Override
			public String get() {
				return null;
			}
		});
		rowConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	@Override
	protected void initWidgetsFromGroupParseSpecification(Display display) {
		super.initWidgetsFromGroupParseSpecification(display);

		this.rowConfig.setCategoryID(rowIDCategory);
		this.rowConfig.setNumHeaderRows(spec.getNumberOfHeaderLines());
		this.rowConfig.setColumnOfRowIds(spec.getColumnOfRowIds() + 1);

		this.rowConfig.setIDType(IDType.getIDType(spec.getRowIDSpecification().getIdType()));
	}

	@Override
	protected void initWidgetsWithDefaultValues(Display display) {
		super.initWidgetsWithDefaultValues(display);
		this.rowConfig.setCategoryID(rowIDCategory);
		this.rowConfig.setEnabled(false);
	}


	@Override
	protected boolean validate() {
		if (!super.validate())
			return false;
		if (this.rowConfig.getIDType() == null) {
			MessageDialog.openError(new Shell(), "Invalid Row ID Type", "Please select the ID type of the rows");
			return false;
		}
		return true;
	}

	@Override
	protected void save() {
		super.save();
		spec.setRowIDSpecification(this.rowConfig.getIDSpecification());
	}

	@Override
	public void onSelectFile(String inputFileName) {
		this.rowConfig.setEnabled(true);
		super.onSelectFile(inputFileName);
	}

	@Override
	protected void onPreviewChanged(int totalNumberOfColumns, int totalNumberOfRows,
			List<? extends List<String>> dataMatrix) {
		this.rowConfig.setMaxDimension(totalNumberOfColumns, totalNumberOfRows);
		this.rowConfig.determineConfigFromPreview(dataMatrix, this.rowIDCategory);
		super.onPreviewChanged(totalNumberOfColumns, totalNumberOfRows, dataMatrix);
	}
}
