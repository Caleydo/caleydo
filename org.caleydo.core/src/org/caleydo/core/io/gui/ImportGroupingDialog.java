/**
 * 
 */
package org.caleydo.core.io.gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.gui.util.LabelEditorDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.GroupingParseSpecification;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for loading groupings for datasets.
 * 
 * @author Christian Partl
 * 
 */
public class ImportGroupingDialog extends Dialog {

	/**
	 * Maximum number of previewed rows in {@link #previewTable}.
	 */
	private static int MAX_PREVIEW_TABLE_ROWS = 50;

	/**
	 * The maximum number of ids that are tested in order to determine the
	 * {@link IDType}.
	 */
	private static int MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION = 10;

	/**
	 * Composite that is the parent of all gui elements of this dialog.
	 */
	private Composite parentComposite;

	/**
	 * Textfield for the input file name.
	 */
	private Text fileNameTextField;

	/**
	 * Specifies the number of lines that shall be skipped at the start of the
	 * file while parsing.
	 */
	private Text linesToSkipTextField;

	/**
	 * File name of the input file.
	 */
	private String inputFileName;

	/**
	 * Table that displays a preview of the data of the file specified by
	 * {@link #inputFileName}.
	 */
	private Table previewTable;

	/**
	 * The {@link GroupingParseSpecification} created using this dialog.
	 */
	private GroupingParseSpecification groupingParseSpecification = new GroupingParseSpecification();

	/**
	 * Combo box to specify the row ID Type.s
	 */
	private Combo rowIDCombo;

	/**
	 * The current id category.
	 */
	private IDCategory rowIDCategory;

	/**
	 * The IDTypes available for {@link #rowIDCategory}.
	 */
	private ArrayList<IDType> rowIDTypes;

	/**
	 * List of buttons, each created for one column to specify whether this
	 * column should be loaded or not.
	 */
	private ArrayList<Button> selectedColumnButtons = new ArrayList<Button>();

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
				selectedColumns.add(columnIndex);
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

		createRecordIDTypeGroup(parentComposite);

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
//				parentComposite.pack();
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

	private void createRecordIDTypeGroup(Composite parent) {
		Group idTypeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		idTypeGroup.setText("Row ID type");
		idTypeGroup.setLayout(new RowLayout());
		idTypeGroup.setLayoutData(new GridData(SWT.LEFT));
		rowIDCombo = new Combo(idTypeGroup, SWT.DROP_DOWN);
		rowIDTypes = new ArrayList<IDType>();

		fillRecordIDTypeCombo();

		rowIDCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableColumn idColumn = previewTable.getColumn(1);
				idColumn.setText(rowIDCombo.getText());
			}
		});
	}

	private void fillRecordIDTypeCombo() {
		ArrayList<IDType> tempIDTypes = rowIDCategory.getIdTypes();

		rowIDTypes.clear();
		for (IDType idType : tempIDTypes) {
			if (!idType.isInternalType())
				rowIDTypes.add(idType);
		}

		String[] idTypesAsString = new String[rowIDTypes.size()];
		int index = 0;
		for (IDType idType : rowIDTypes) {
			idTypesAsString[index] = idType.getTypeName();
			index++;
		}

		rowIDCombo.setItems(idTypesAsString);
		rowIDCombo.setEnabled(true);
		rowIDCombo.select(0);

	}

	private void createDelimiterGroup(Composite parent) {
		Group delimiterGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		delimiterGroup.setText("Separated by (delimiter)");
		delimiterGroup.setLayout(new RowLayout());

		final Button[] delimiterButtons = new Button[6];

		delimiterButtons[0] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[0].setSelection(true);
		delimiterButtons[0].setText("TAB");
		delimiterButtons[0].setData("\t");
		delimiterButtons[0].setBounds(10, 5, 75, 30);

		delimiterButtons[1] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[1].setText(";");
		delimiterButtons[1].setData(";");
		delimiterButtons[1].setBounds(10, 30, 75, 30);

		delimiterButtons[2] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[2].setText(",");
		delimiterButtons[2].setData(",");
		delimiterButtons[2].setBounds(10, 55, 75, 30);

		delimiterButtons[3] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[3].setText(".");
		delimiterButtons[3].setData(".");
		delimiterButtons[3].setBounds(10, 55, 75, 30);

		delimiterButtons[4] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[4].setText("SPACE");
		delimiterButtons[4].setData(" ");
		delimiterButtons[4].setBounds(10, 55, 75, 30);

		delimiterButtons[5] = new Button(delimiterGroup, SWT.RADIO);
		delimiterButtons[5].setText("Other");
		delimiterButtons[5].setBounds(10, 55, 75, 30);

		final Text customizedDelimiterTextField = new Text(delimiterGroup, SWT.BORDER);
		customizedDelimiterTextField.setBounds(0, 0, 75, 30);
		customizedDelimiterTextField.setTextLimit(1);
		customizedDelimiterTextField.setEnabled(false);
		customizedDelimiterTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				groupingParseSpecification.setDelimiter(customizedDelimiterTextField
						.getText());
				createDataPreviewTable();
				// composite.pack();
			}

		});

		SelectionAdapter radioGroupSelectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button selectedButton = (Button) e.getSource();
				if (selectedButton != delimiterButtons[5]) {
					customizedDelimiterTextField.setEnabled(false);
					groupingParseSpecification.setDelimiter((String) selectedButton
							.getData());
					createDataPreviewTable();
				} else {
					customizedDelimiterTextField.setEnabled(true);
					groupingParseSpecification.setDelimiter(" ");
					createDataPreviewTable();
				}
			}
		};

		for (int i = 0; i < delimiterButtons.length; i++) {
			delimiterButtons[i].addSelectionListener(radioGroupSelectionListener);
		}

	}

	private void createDataPreviewTable() {

		String delimiter = groupingParseSpecification.getDelimiter();

		// Clear table if not empty
		previewTable.removeAll();

		for (TableColumn tmpColumn : previewTable.getColumns()) {
			tmpColumn.dispose();
		}

		final TableEditor editor = new TableEditor(previewTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Read preview table
		BufferedReader file;
		try {
			file = GeneralManager.get().getResourceLoader()
					.getResource(groupingParseSpecification.getDataSourcePath());

			String line = "";

			// Ignore unwanted header files of file
			for (int iIgnoreLineIndex = 0; iIgnoreLineIndex < groupingParseSpecification
					.getNumberOfHeaderLines() - 1; iIgnoreLineIndex++) {
				file.readLine();
			}

			String nextToken = "";
			StringTokenizer tokenizer;
			TableColumn column;
			TableItem item;
			int colIndex = 0;

			// Read labels
			if ((line = file.readLine()) != null) {
				tokenizer = new StringTokenizer(line, delimiter, false);
				column = new TableColumn(previewTable, SWT.NONE);
				column.setWidth(100);
				column.setText("");

				while (tokenizer.hasMoreTokens()) {
					nextToken = tokenizer.nextToken();

					final TableColumn dataColumn = new TableColumn(previewTable, SWT.NONE);
					dataColumn.setWidth(100);
					dataColumn.setText(nextToken);

					dataColumn.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							LabelEditorDialog dialog = new LabelEditorDialog(new Shell());
							String sLabel = dialog.open(dataColumn.getText());

							if (sLabel != null && !sLabel.isEmpty()) {
								dataColumn.setText(sLabel);
							}
						}
					});

					colIndex++;
				}
			}

			createDataClassBar();

			int rowCount = 0;
			boolean isCellFilled = false;

			// Read raw data
			while ((line = file.readLine()) != null && rowCount < MAX_PREVIEW_TABLE_ROWS) {
				// last flag triggers return of delimiter itself
				tokenizer = new StringTokenizer(line, delimiter, true);
				item = new TableItem(previewTable, SWT.NONE);
				item.setText("Row " + (rowCount + 1)); // +1 to be intuitive for
				// a non programmer :)
				colIndex = 0;

				while (tokenizer.hasMoreTokens()) {
					nextToken = tokenizer.nextToken();

					// Check for empty cells
					if (nextToken.equals(delimiter) && !isCellFilled) {
						item.setText(colIndex + 1, "");
						colIndex++;
					} else if (nextToken.equals(delimiter) && isCellFilled) {
						isCellFilled = false; // reset
					} else {
						isCellFilled = true;
						item.setText(colIndex + 1, nextToken);
						colIndex++;
					}
				}

				isCellFilled = false; // reset

				rowCount++;
			}

			// check for experiment cluster info in the rest of the file
			while ((line = file.readLine()) != null) {

				tokenizer = new StringTokenizer(line, delimiter, true);

				if (!tokenizer.hasMoreTokens())
					continue;

				nextToken = tokenizer.nextToken();
			}

		} catch (FileNotFoundException e) {
			throw new IllegalStateException("File not found!");
		} catch (IOException ioe) {
			throw new IllegalStateException("Input/output problem!");
		}

		ArrayList<IDCategory> idCategories = new ArrayList<IDCategory>();
		idCategories.add(rowIDCategory);
		determineRowIDType(idCategories);
		
		parentComposite.pack();
	}

	private void determineRowIDType(ArrayList<IDCategory> idCategories) {

		TableItem[] items = previewTable.getItems();
		ArrayList<String> idList = new ArrayList<String>();
		int rowIndex = 1;
		while (rowIndex < items.length
				&& rowIndex <= MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION) {
			idList.add(items[rowIndex].getText(1));
			rowIndex++;
		}

		int maxCorrectElements = 0;
		IDType mostProbableIDType = null;

		for (IDCategory idCategory : idCategories) {

			rowIDTypes = new ArrayList<IDType>();
			HashSet<IDType> alIDTypesTemp = IDMappingManagerRegistry.get()
					.getIDMappingManager(idCategory).getIDTypes();
			for (IDType idType : alIDTypesTemp) {
				if (!idType.isInternalType())
					rowIDTypes.add(idType);
			}

			IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
					.getIDMappingManager(idCategory);

			for (IDType idType : rowIDTypes) {

				int currentCorrectElements = 0;

				for (String currentID : idList) {

					if (idType.getColumnType().equals(EColumnType.INT)) {
						try {
							Integer idInt = Integer.valueOf(currentID);
							if (idMappingManager.doesElementExist(idType, idInt)) {
								currentCorrectElements++;
							}
						} catch (NumberFormatException e) {
						}
					} else if (idType.getColumnType().equals(EColumnType.STRING)) {
						if (idMappingManager.doesElementExist(idType, currentID)) {
							currentCorrectElements++;
						} else if (idType.getTypeName().equals("REFSEQ_MRNA")) {
							if (currentID.contains(".")) {
								if (idMappingManager.doesElementExist(idType,
										currentID.substring(0, currentID.indexOf(".")))) {
									currentCorrectElements++;
								}
							}
						}
					}

					if (currentCorrectElements >= idList.size()) {

						if (mostProbableIDType != null) {
							setMostProbableRecordIDType(mostProbableIDType);
						} else {
							rowIDTypes.clear();
							rowIDTypes = new ArrayList<IDType>(rowIDCategory.getIdTypes());
						}

						return;
					}
					if (currentCorrectElements >= maxCorrectElements) {
						maxCorrectElements = currentCorrectElements;
						mostProbableIDType = idType;
					}
				}
			}
		}
		if (mostProbableIDType != null) {
			setMostProbableRecordIDType(mostProbableIDType);
		} else {
			rowIDTypes.clear();
			rowIDTypes = new ArrayList<IDType>(rowIDCategory.getIdTypes());
		}
	}

	private void setMostProbableRecordIDType(IDType mostProbableRecordIDType) {
		fillRecordIDTypeCombo();
		rowIDCombo.select(rowIDTypes.indexOf(mostProbableRecordIDType));

		TableColumn idColumn = previewTable.getColumn(1);
		idColumn.setText(mostProbableRecordIDType.getTypeName());
	}

	private void createDataClassBar() {

		TableItem tmpItem = new TableItem(previewTable, SWT.NONE);
		tmpItem.setText("Use column");

		Button skipButton;
		for (int iColIndex = 2; iColIndex < previewTable.getColumnCount(); iColIndex++) {
			skipButton = new Button(previewTable, SWT.CHECK | SWT.CENTER);
			skipButton.setSelection(true);
			skipButton.setData("column", iColIndex);
			skipButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Color textColor = null;
					boolean bSkipColumn = false;

					for (TableItem item : previewTable.getItems()) {
						bSkipColumn = !((Button) e.widget).getSelection();

						if (bSkipColumn) {
							textColor = Display.getCurrent().getSystemColor(
									SWT.COLOR_GRAY);
						} else {
							textColor = Display.getCurrent().getSystemColor(
									SWT.COLOR_BLACK);
						}

						item.setForeground(((Integer) e.widget.getData("column")),
								textColor);
					}
				}
			});

			selectedColumnButtons.add(skipButton);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = editor.grabVertical = true;
			editor.setEditor(skipButton, tmpItem, iColIndex);
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

}
