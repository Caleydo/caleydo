/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.gui.dataimport.PreviewTable.IPreviewCallback;
import org.caleydo.core.io.gui.dataimport.widget.LoadFileWidget;
import org.caleydo.core.io.gui.dataimport.widget.RowConfigWidget;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.base.IProvider;
import org.caleydo.core.util.base.IntegerCallback;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.system.BrowserUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

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
	// private LabelWidget label;

	private LoadFileWidget loadFile;

	private RowConfigWidget rowConfig;

	private PreviewTable previewTable;

	private Button useGroupingNamesFromRowButton;

	private Button useCustomGroupingNamesButton;

	private Spinner rowWithGroupingNamesSpinner;

	private Label groupingNamesRowLabel;

	/**
	 * The row id category for which groupings should be loaded.
	 */
	private final IDCategory rowIDCategory;

	/**
	 * The {@link GroupingParseSpecification} created using this {@link #dialog} .
	 */
	private final GroupingParseSpecification spec;

	private int numRowsInFile = 0;

	private List<String> customGroupingNames = new ArrayList<>();

	/**
	 * @param parentShell
	 */
	public ImportGroupingDialog(Shell parentShell, IDCategory rowIDCategory) {
		this(parentShell, rowIDCategory, null);
	}

	public ImportGroupingDialog(Shell parentShell, IDCategory rowIDCategory, GroupingParseSpecification existing) {
		super(parentShell);
		this.rowIDCategory = rowIDCategory;
		spec = new GroupingParseSpecification();
		if (existing == null) {
			spec.setDelimiter("\t");
			spec.setNumberOfHeaderLines(1);
			spec.setRowOfColumnIDs(0);
		} else {
			this.spec.setColumnIDSpecification(existing.getColumnIDSpecification());
			this.spec.setColumnOfRowIds(existing.getColumnOfRowIds());
			this.spec.setColumns(existing.getColumns());
			this.spec.setContainsColumnIDs(existing.isContainsColumnIDs());
			this.spec.setDataSourcePath(existing.getDataSourcePath());
			this.spec.setDelimiter(existing.getDelimiter());
			this.spec.setGroupingNames(existing.getGroupingNames());
			this.spec.setNumberOfHeaderLines(existing.getNumberOfHeaderLines());
			this.spec.setRowIDSpecification(existing.getRowIDSpecification());
			this.spec.setRowOfColumnIDs(existing.getRowOfColumnIDs());
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

		parentComposite = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 900;
		gd.heightHint = 670;
		parentComposite.setLayoutData(gd);

		loadFile = new LoadFileWidget(parentComposite, "Open Grouping File", new ICallback<String>() {
			@Override
			public void on(String data) {
				onSelectFile(data);
			}
		}, new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		// label = new LabelWidget(parentComposite, "Grouping Name");

		rowConfig = new RowConfigWidget(parentComposite, new IntegerCallback() {
			@Override
			public void on(int data) {
				previewTable.onNumHeaderRowsChanged(data);
				if (data == 0) {
					if (!useCustomGroupingNamesButton.getSelection()) {
						onUseCustomGroupingNames();
						useCustomGroupingNamesButton.setSelection(true);
						useGroupingNamesFromRowButton.setSelection(false);
					}
				} else if (data < rowWithGroupingNamesSpinner.getSelection()) {
					rowWithGroupingNamesSpinner.setSelection(data);
				}
			}
		}, new IntegerCallback() {
			@Override
			public void on(int data) {
				previewTable.onColumnOfRowIDChanged(data);
			}
		}, new ICallback<IDTypeParsingRules>() {
			@Override
			public void on(IDTypeParsingRules data) {
				previewTable.setRowIDTypeParsingRules(data);
			}
		}, new IProvider<String>() {
			@Override
			public String get() {
				return previewTable.getValue(rowConfig.getNumHeaderRows(), rowConfig.getColumnOfRowID() - 1);
			}
		});
		rowConfig.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		createGroupingNameGroup();

		previewTable = new PreviewTable(parentComposite, this.spec, new IPreviewCallback() {
			@Override
			public void on(int numColumn, int numRow, List<? extends List<String>> dataMatrix) {
				onPreviewChanged(numColumn, numRow, dataMatrix);
			}
		}, true);

		init();

		parentComposite.pack();

		return parent;
	}

	private void createGroupingNameGroup() {
		Group groupingNameGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		groupingNameGroup.setText("Grouping Names");
		groupingNameGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		groupingNameGroup.setLayout(new GridLayout(2, false));

		useGroupingNamesFromRowButton = new Button(groupingNameGroup, SWT.RADIO);
		useGroupingNamesFromRowButton.setText("Use Grouping Names from Header Row");
		useGroupingNamesFromRowButton.setSelection(true);
		useGroupingNamesFromRowButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		useGroupingNamesFromRowButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onUseGroupingNamesFromRows();
			}
		});

		groupingNamesRowLabel = new Label(groupingNameGroup, SWT.NONE);
		groupingNamesRowLabel.setText("Row with Grouping Names");

		rowWithGroupingNamesSpinner = new Spinner(groupingNameGroup, SWT.BORDER);
		rowWithGroupingNamesSpinner.setMaximum(Integer.MAX_VALUE);
		rowWithGroupingNamesSpinner.setMinimum(1);
		rowWithGroupingNamesSpinner.setIncrement(1);
		rowWithGroupingNamesSpinner.setSelection(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gridData.widthHint = 70;
		rowWithGroupingNamesSpinner.setLayoutData(gridData);
		rowWithGroupingNamesSpinner.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				int rowWithGroupingNames = rowWithGroupingNamesSpinner.getSelection();
				previewTable.onRowOfColumnIDChanged(rowWithGroupingNames);
				if (rowWithGroupingNames > spec.getNumberOfHeaderLines()) {
					previewTable.onNumHeaderRowsChanged(rowWithGroupingNames);
					rowConfig.setNumHeaderRows(rowWithGroupingNames);
				}
			}
		});

		useCustomGroupingNamesButton = new Button(groupingNameGroup, SWT.RADIO);
		useCustomGroupingNamesButton.setText("Use Custom Grouping Names");
		useCustomGroupingNamesButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		useCustomGroupingNamesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onUseCustomGroupingNames();
			}
		});
		setGroupingNamesGroupEnabled(false);
	}

	private void onUseGroupingNamesFromRows() {
		groupingNamesRowLabel.setEnabled(true);
		rowWithGroupingNamesSpinner.setEnabled(true);
		int rowWithGroupingNames = rowWithGroupingNamesSpinner.getSelection();
		previewTable.onRowOfColumnIDChanged(rowWithGroupingNames);
		if (rowWithGroupingNames > spec.getNumberOfHeaderLines()) {
			previewTable.onNumHeaderRowsChanged(rowWithGroupingNames);
			rowConfig.setNumHeaderRows(rowWithGroupingNames);
		}
		previewTable.clearCustomHeaderRows();
	}

	private void onUseCustomGroupingNames() {
		groupingNamesRowLabel.setEnabled(false);
		rowWithGroupingNamesSpinner.setEnabled(false);
		previewTable.onRowOfColumnIDChanged(-1);
		customGroupingNames.clear();
		int groupingNumber = 1;
		for (int i = 1; i <= previewTable.getNumColumns(); i++) {
			if (i == rowConfig.getColumnOfRowID()) {
				customGroupingNames.add("");
			} else {
				customGroupingNames.add("Grouping " + groupingNumber);
				groupingNumber++;
			}
		}
		previewTable.addCustomHeaderRow(new IDataProvider() {

			@Override
			public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
				if (columnIndex + 1 == rowConfig.getColumnOfRowID())
					return;
				customGroupingNames.set(columnIndex, newValue.toString());
			}

			@Override
			public int getRowCount() {
				return 1;
			}

			@Override
			public Object getDataValue(int columnIndex, int rowIndex) {
				if (columnIndex + 1 == rowConfig.getColumnOfRowID())
					return "";
				return customGroupingNames.get(columnIndex);
			}

			@Override
			public int getColumnCount() {
				return customGroupingNames.size();
			}
		}, true);
	}

	private void setGroupingNamesGroupEnabled(boolean enabled) {
		useGroupingNamesFromRowButton.setEnabled(enabled);
		groupingNamesRowLabel.setEnabled(enabled ? useGroupingNamesFromRowButton.getSelection() : false);
		rowWithGroupingNamesSpinner.setEnabled(enabled ? useGroupingNamesFromRowButton.getSelection() : false);
		useCustomGroupingNamesButton.setEnabled(enabled);
	}

	@Override
	protected void helpPressed() {
		BrowserUtils.openURL("http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/loading-data");
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

		// this.label.setText(spec.getGroupingName());
		// this.label.setEnabled(true);

		this.rowConfig.setCategoryID(rowIDCategory);
		this.rowConfig.setNumHeaderRows(spec.getNumberOfHeaderLines());
		this.rowConfig.setColumnOfRowIds(spec.getColumnOfRowIds() + 1);

		this.previewTable.generatePreview(spec.getColumns());

		this.rowConfig.setIDType(IDType.getIDType(spec.getRowIDSpecification().getIdType()));
	}

	private void initWidgetsWithDefaultValues() {

		this.loadFile.setFileName("");

		// this.label.setText("");
		// this.label.setEnabled(false);

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
		// TODO remove
		// spec.setGroupingNames(spec.getDataSourcePath().substring(
		// spec.getDataSourcePath().lastIndexOf(File.separator) + 1, spec.getDataSourcePath().lastIndexOf(".")));
		if (useGroupingNamesFromRowButton.getSelection()) {
			// TODO fill grouping names
		} else {
			// TODO fill grouping names
		}
	}

	@Override
	public GroupingParseSpecification call() {
		if (this.open() == Window.OK)
			return this.spec;
		else
			return null;
	}

	public void onSelectFile(String inputFileName) {
		// this.label.setText(inputFileName.substring(inputFileName.lastIndexOf(File.separator) + 1,
		// inputFileName.lastIndexOf(".")));

		spec.setDataSourcePath(inputFileName);

		// this.label.setEnabled(true);
		this.rowConfig.setEnabled(true);
		setGroupingNamesGroupEnabled(true);

		this.previewTable.generatePreview(true);
		numRowsInFile = previewTable.getNumRows();
		// this.parentComposite.layout(true, true);
	}

	protected void onPreviewChanged(int totalNumberOfColumns, int totalNumberOfRows,
			List<? extends List<String>> dataMatrix) {
		this.rowConfig.setMaxDimension(totalNumberOfColumns, totalNumberOfRows);
		this.rowConfig.determineConfigFromPreview(dataMatrix, this.rowIDCategory);
		// parentComposite.pack();
		parentComposite.layout(true);
	}

	/**
	 * @return the numRowsInFile, see {@link #numRowsInFile}
	 */
	public int getNumRowsInFile() {
		return numRowsInFile;
	}
}
