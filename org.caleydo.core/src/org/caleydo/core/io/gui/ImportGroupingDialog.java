/**
 * 
 */
package org.caleydo.core.io.gui;

import org.caleydo.core.id.IDCategory;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for loading groupings for datasets.
 * 
 * @author Christian Partl
 * 
 */
public class ImportGroupingDialog extends Dialog {

	/**
	 * Composite that is the parent of all gui elements of this dialog.
	 */
	private Composite parentComposite;

	/**
	 * Textfield for the input file name.
	 */
	private Text fileNameTextField;

	/**
	 * File name of the input file.
	 */
	private String inputFileName;

	/**
	 * @param parentShell
	 */
	protected ImportGroupingDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		createGUI(parent);
		return parent;
	}

	private void createGUI(Composite parent) {

		int numGridCols = 4;

		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);

		Group inputFileGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		inputFileGroup.setText("Input file");
		inputFileGroup.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = numGridCols;
		inputFileGroup.setLayoutData(gridData);

		Button openFileButton = new Button(inputFileGroup, SWT.PUSH);
		openFileButton.setText("Open Grouping File");
		// buttonFileChooser.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));

		fileNameTextField = new Text(inputFileGroup, SWT.BORDER);
		fileNameTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		openFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				FileDialog fileDialog = new FileDialog(new Shell());
				fileDialog.setText("Open");
//				fileDialog.setFilterPath(filePath);
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				inputFileName = fileDialog.open();

				if (inputFileName == null)
					return;

//				dataSetDescription.setDataSourcePath(inputFile);
//				fileNameTextField.setText(inputFile);
//
//				txtDataSetLabel.setText(determineDataSetLabel());
//
//				createDataPreviewTable("\t");
			}
		});

		// allRegisteredIDCategories.clear();
		// allRegisteredIDCategories.addAll(IDCategory.getAllRegisteredIDCategories());
		//
		// createRowIDCategoryGroup();
		// createRecordIDTypeGroup();
		// createColumnIDCategoryGroup();
		// createDimensionIDTypeGroup();
		//
		// Group dataSetLabelGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		// dataSetLabelGroup.setText("Data set name");
		// dataSetLabelGroup.setLayout(new GridLayout(1, false));
		// gridData = new GridData(GridData.FILL_HORIZONTAL);
		// gridData.horizontalSpan = numGridCols;
		// dataSetLabelGroup.setLayoutData(gridData);
		//
		// txtDataSetLabel = new Text(dataSetLabelGroup, SWT.BORDER);
		// txtDataSetLabel.setText(determineDataSetLabel());
		// txtDataSetLabel.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));
		//
		// Group startParseAtLineGroup = new Group(composite,
		// SWT.SHADOW_ETCHED_IN);
		// startParseAtLineGroup.setText("Ignore lines in header");
		// startParseAtLineGroup.setLayout(new GridLayout(1, false));
		//
		// txtStartParseAtLine = new Text(startParseAtLineGroup, SWT.BORDER);
		// txtStartParseAtLine.setLayoutData(new GridData(50, 15));
		// txtStartParseAtLine.setText("1");
		// txtStartParseAtLine.setTextLimit(2);
		// txtStartParseAtLine.addModifyListener(new ModifyListener() {
		// @Override
		// public void modifyText(ModifyEvent e) {
		//
		// // Add 1 because the number that the user enters is human
		// // readable and not array index
		// // (starting with 0).
		// dataSetDescription.setNumberOfHeaderLines(Integer
		// .valueOf(txtStartParseAtLine.getText()));
		//
		// createDataPreviewTable("\t");
		// composite.pack();
		// }
		// });
		//
		// createDelimiterGroup();
		// createFilterGroup();
		// createDataPropertiesGroup();
		//
		// previewTable = new Table(composite, SWT.MULTI | SWT.BORDER |
		// SWT.FULL_SELECTION);
		// previewTable.setLinesVisible(true);
		// previewTable.setHeaderVisible(true);
		// gridData = new GridData(GridData.FILL_BOTH);
		// gridData.horizontalSpan = numGridCols;
		// gridData.heightHint = 400;
		// gridData.widthHint = 800;
		// previewTable.setLayoutData(gridData);
		//
		// // Check if an external file name is given to the action
		// if (!inputFile.isEmpty()) {
		// fileNameTextField.setText(inputFile);
		// dataSetDescription.setDataSourcePath(inputFile);
		// mathFilterMode = "Log10";
		// // mathFilterCombo.select(1);
		//
		// createDataPreviewTable("\t");
		// }
	}

}
