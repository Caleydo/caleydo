/**
 * 
 */
package org.caleydo.core.io.gui;

import java.util.ArrayList;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.MatrixDefinition;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for loading groupings for datasets.
 * 
 * @author Christian Partl
 * 
 */
public class ImportGroupingDialog extends AImportDialog {

	/**
	 * The {@link GroupingParseSpecification} created using this dialog.
	 */
	private GroupingParseSpecification groupingParseSpecification;

	/**
	 * @param parentShell
	 */
	protected ImportGroupingDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void okPressed() {

		if (fileNameTextField.getText().isEmpty()) {
			MessageDialog.openError(new Shell(), "Invalid filename",
					"Please specify a file to load");
			return;
		}

		if (rowIDCombo.getSelectionIndex() == -1) {
			MessageDialog.openError(new Shell(), "Invalid row ID type",
					"Please select the ID type of the rows");
			return;
		}

		ArrayList<Integer> selectedColumns = new ArrayList<Integer>();
		for (int columnIndex = 2; columnIndex < previewTable.getColumnCount(); columnIndex++) {
			if (selectedColumnButtons.get(columnIndex - 2).getSelection()) {
				selectedColumns.add(columnIndex - 1);
			}
		}
		groupingParseSpecification.setColumns(selectedColumns);
		IDSpecification rowIDSpecification = new IDSpecification();
		IDType rowIDType = rowIDTypes.get(rowIDCombo.getSelectionIndex());
		rowIDSpecification.setIdType(rowIDType.toString());
		if (rowIDType.getIDCategory().getCategoryName().equals("GENE"))
			rowIDSpecification.setIDTypeGene(true);
		rowIDSpecification.setIdCategory(rowIDType.getIDCategory().toString());
		if (rowIDType.getTypeName().equalsIgnoreCase("REFSEQ_MRNA")) {
			// for REFSEQ_MRNA we ignore the .1, etc.
			rowIDSpecification.setSubStringExpression("\\.");
		}
		groupingParseSpecification.setRowIDSpecification(rowIDSpecification);
		groupingParseSpecification.setContainsColumnIDs(false);

		super.okPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		createGUI(parent);
		groupingParseSpecification.setDelimiter("\t");
		groupingParseSpecification.setNumberOfHeaderLines(1);
		return parent;
	}

	private void createGUI(Composite parent) {

		int numGridCols = 2;

		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, false);
		parentComposite.setLayout(layout);

		Group inputFileGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		inputFileGroup.setText("Input file");
		inputFileGroup.setLayout(new GridLayout(2, false));
		inputFileGroup.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false,
				numGridCols, 1));

		Button openFileButton = new Button(inputFileGroup, SWT.PUSH);
		openFileButton.setText("Open Grouping File");
		// buttonFileChooser.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));

		fileNameTextField = new Text(inputFileGroup, SWT.BORDER);
		fileNameTextField.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));

		openFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				FileDialog fileDialog = new FileDialog(new Shell());
				fileDialog.setText("Open");
				// fileDialog.setFilterPath(filePath);
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				inputFileName = fileDialog.open();

				if (inputFileName == null)
					return;
				fileNameTextField.setText(inputFileName);

				groupingParseSpecification.setDataSourcePath(inputFileName);
				createDataPreviewTable();
			}
		});

		Group idCategoryGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		idCategoryGroup.setText("Row ID category");
		idCategoryGroup.setLayout(new RowLayout());
		idCategoryGroup.setLayoutData(new GridData(SWT.LEFT));
		Label categoryIDLabel = new Label(idCategoryGroup, SWT.NONE);
		categoryIDLabel.setText(rowIDCategory.getCategoryName());

		createIDTypeGroup(parentComposite, false);

		Group linesToSkipGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		linesToSkipGroup.setText("Ignore lines in header");
		linesToSkipGroup.setLayout(new GridLayout(1, false));
		linesToSkipGroup
				.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		linesToSkipTextField = new Text(linesToSkipGroup, SWT.BORDER);
		linesToSkipTextField.setLayoutData(new GridData(50, 15));
		linesToSkipTextField.setText("1");
		linesToSkipTextField.setTextLimit(2);
		linesToSkipTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				// Add 1 because the number that the user enters is human
				// readable and not array index
				// (starting with 0).
				groupingParseSpecification.setNumberOfHeaderLines(Integer
						.valueOf(linesToSkipTextField.getText()));

				createDataPreviewTable();
				// parentComposite.pack();
			}
		});

		createDelimiterGroup(parentComposite);

		previewTable = new Table(parentComposite, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		previewTable.setHeaderVisible(true);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, numGridCols, 1);
		gridData.heightHint = 400;
		gridData.widthHint = 800;
		previewTable.setLayoutData(gridData);
		//

	}

	@Override
	protected void setMostProbableRecordIDType(IDType mostProbableRecordIDType) {

		if (mostProbableRecordIDType == null) {
			rowIDTypes.clear();
			rowIDTypes = new ArrayList<IDType>(rowIDCategory.getIdTypes());
			rowIDCombo.clearSelection();
			rowIDCombo.setText("<Please select>");
		} else {
			fillIDTypeCombo(rowIDCategory, rowIDTypes, rowIDCombo);
			rowIDCombo.select(rowIDTypes.indexOf(mostProbableRecordIDType));

			TableColumn idColumn = previewTable.getColumn(1);
			idColumn.setText(mostProbableRecordIDType.getTypeName());
		}
	}

	/**
	 * @return the groupingParseSpecification, see
	 *         {@link #groupingParseSpecification}
	 */
	public GroupingParseSpecification getGroupingParseSpecification() {
		return groupingParseSpecification;
	}

	/**
	 * @param rowIDCategory
	 *            setter, see {@link #rowIDCategory}
	 */
	public void setRowIDCategory(IDCategory rowIDCategory) {
		this.rowIDCategory = rowIDCategory;
	}

	@Override
	protected MatrixDefinition createConcreteMatrixDefinition() {
		groupingParseSpecification = new GroupingParseSpecification();
		return groupingParseSpecification;
	}

	@Override
	protected ArrayList<IDCategory> getAvailableIDCategories() {
		ArrayList<IDCategory> idCategories = new ArrayList<IDCategory>();
		idCategories.add(rowIDCategory);
		return idCategories;
	}

}
