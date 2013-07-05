/**
 *
 */
package org.caleydo.core.io.gui.dataimport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.gui.dataimport.PreviewTable.IPreviewCallback;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.io.gui.dataimport.widget.IntegerCallback;
import org.caleydo.core.io.gui.dataimport.widget.LabelWidget;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.io.gui.dataimport.widget.RowConfigWidget;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.link.LinkHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for loading groupings for datasets.
 *
 * @author Christian Partl
 *
 */
public class ImportGroupingDialog extends AHelpButtonDialog implements SafeCallable<GroupingParseSpecification> {
	/**
	 * Composite that is the parent of all gui elements of this this.
	 */
	private Composite parentComposite;

	/**
	 * Textfield for the grouping name.
	 */
	private LabelWidget label;

	private LoadFileWidget loadFile;

	private RowConfigWidget rowConfig;

	private PreviewTable previewTable;

	/**
	 * The row id category for which groupings should be loaded.
	 */
	private final IDCategory rowIDCategory;

	/**
	 * The {@link GroupingParseSpecification} created using this {@link #dialog} .
	 */
	private final GroupingParseSpecification spec;

	/**
	 * @param parentShell
	 */
	public ImportGroupingDialog(Shell parentShell, IDCategory rowIDCategory) {
		this(parentShell, rowIDCategory, null);
	}

	public ImportGroupingDialog(Shell parentShell, IDCategory rowIDCategory,
			GroupingParseSpecification existing) {
		super(parentShell);
		this.rowIDCategory = rowIDCategory;
		spec = new GroupingParseSpecification();
		if (existing == null) {
			spec.setDelimiter("\t");
			spec.setNumberOfHeaderLines(1);
		} else {
			this.spec.setColumnIDSpecification(spec
					.getColumnIDSpecification());
			this.spec.setColumnOfRowIds(spec.getColumnOfRowIds());
			this.spec.setColumns(spec.getColumns());
			this.spec.setContainsColumnIDs(spec.isContainsColumnIDs());
			this.spec.setDataSourcePath(spec.getDataSourcePath());
			this.spec.setDelimiter(spec.getDelimiter());
			this.spec.setGroupingName(spec.getGroupingName());
			this.spec.setNumberOfHeaderLines(spec.getNumberOfHeaderLines());
			this.spec.setRowIDSpecification(spec.getRowIDSpecification());
			this.spec.setRowOfColumnIDs(spec.getRowOfColumnIDs());
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Import Grouping");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		int numGridCols = 2;

		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);

		loadFile = new LoadFileWidget(parentComposite, "Open Grouping File", new ICallback<String>() {
			@Override
			public void on(String data) {
				onSelectFile(data);
			}
		});

		label = new LabelWidget(parentComposite, "Grouping Name");

		rowConfig = new RowConfigWidget(parentComposite, new IntegerCallback() {
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
		rowConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		previewTable = new PreviewTable(parentComposite, this.spec, new IPreviewCallback() {
			@Override
			public void on(int numColumn, int numRow, List<? extends List<String>> dataMatrix) {
				onPreviewChanged(numColumn, numRow, dataMatrix);
			}
		});

		init();

		parentComposite.pack();

		return parent;
	}

	@Override
	protected void helpPressed() {
		LinkHandler.openLink("http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/loading-data");
	}

	@Override
	public void okPressed() {
		if (!validate())
			return;

		save();
		super.okPressed();
	}

	/**
	 * Initializes all widgets of the {@link #dialog}. This method should be called after all widgets of the dialog were
	 * created.
	 */
	public void init() {
		if (spec.getDataSourcePath() != null && new File(spec.getDataSourcePath()).exists()) {
			initWidgetsFromGroupParseSpecification();
		} else {
			initWidgetsWithDefaultValues();
		}
	}

	private void initWidgetsFromGroupParseSpecification() {
		this.loadFile.setFileName(spec.getDataSourcePath());

		this.label.setText(spec.getGroupingName());
		this.label.setEnabled(true);

		this.rowConfig.setCategoryID(rowIDCategory);
		this.rowConfig.setNumHeaderRows(spec.getNumberOfHeaderLines());
		this.rowConfig.setColumnOfRowIds(spec.getColumnOfRowIds() + 1);

		this.previewTable.generatePreview(spec.getColumns());

		this.rowConfig.setIDType(IDType.getIDType(spec.getRowIDSpecification().getIdType()));
	}

	private void initWidgetsWithDefaultValues() {

		this.loadFile.setFileName("");

		this.label.setText("");
		this.label.setEnabled(false);

		this.rowConfig.setCategoryID(rowIDCategory);
		this.rowConfig.setEnabled(false);
	}


	private boolean validate() {
		if (this.loadFile.getFileName().isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid Filename", "Please specify a file to load");
			return false;
		}

		if (this.rowConfig.getIDType() == null) {
			MessageDialog.openError(new Shell(), "Invalid Row ID Type", "Please select the ID type of the rows");
			return false;
		}
		return true;
	}

	private void save() {
		ArrayList<Integer> selectedColumns = new ArrayList<Integer>(this.previewTable.getSelectedColumns());
		selectedColumns.remove(spec.getColumnOfRowIds());
		spec.setColumns(selectedColumns);
		spec.setRowIDSpecification(this.rowConfig.getIDSpecification());
		spec.setContainsColumnIDs(false);
		spec.setGroupingName(this.label.getText());
	}

	@Override
	public GroupingParseSpecification call() {
		if (this.open() == Window.OK)
			return this.spec;
		else
			return null;
	}

	public void onSelectFile(String inputFileName) {
		this.label.setText(inputFileName.substring(inputFileName.lastIndexOf(File.separator) + 1,
				inputFileName.lastIndexOf(".")));

		spec.setDataSourcePath(inputFileName);

		this.label.setEnabled(true);
		this.rowConfig.setEnabled(true);

		this.previewTable.generatePreview();
	}

	protected void onPreviewChanged(int totalNumberOfColumns, int totalNumberOfRows,
			List<? extends List<String>> dataMatrix) {
		this.rowConfig.setMaxDimension(totalNumberOfColumns, totalNumberOfRows);
		this.rowConfig.determineConfigFromPreview(dataMatrix, this.rowIDCategory);
		parentComposite.pack();
		parentComposite.layout(true);
	}
}
