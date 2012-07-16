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

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.gui.util.LabelEditorDialog;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Abstract base class for data import dialogs that are used to load tabular
 * data.
 * 
 * @author Christian Partl
 * 
 */
public abstract class AImportDialog extends Dialog {

	/**
	 * Maximum number of previewed rows in {@link #previewTable}.
	 */
	protected static int MAX_PREVIEW_TABLE_ROWS = 50;

	/**
	 * The maximum number of ids that are tested in order to determine the
	 * {@link IDType}.
	 */
	protected static int MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION = 10;

	/**
	 * Composite that is the parent of all gui elements of this dialog.
	 */
	protected Composite parentComposite;

	/**
	 * Textfield for the input file name.
	 */
	protected Text fileNameTextField;

	/**
	 * Specifies the number of lines that shall be skipped at the start of the
	 * file while parsing.
	 */
	protected Text linesToSkipTextField;

	/**
	 * File name of the input file.
	 */
	protected String inputFileName;

	/**
	 * Table that displays a preview of the data of the file specified by
	 * {@link #inputFileName}.
	 */
	protected Table previewTable;

	/**
	 * The current row id category.
	 */
	protected IDCategory rowIDCategory;

	/**
	 * Combo box to specify the row ID Type.s
	 */
	protected Combo rowIDCombo;

	/**
	 * The IDTypes available for {@link #rowIDCategory}.
	 */
	protected ArrayList<IDType> rowIDTypes;

	/**
	 * List of buttons, each created for one column to specify whether this
	 * column should be loaded or not.
	 */
	protected ArrayList<Button> selectedColumnButtons = new ArrayList<Button>();

	/**
	 * {@link MatrixDefinition} of the dataset that shall be loaded.
	 */
	protected MatrixDefinition matrixDefinition;

	/**
	 * @param parent
	 */
	public AImportDialog(Shell parent) {
		super(parent);
		matrixDefinition = createConcreteMatrixDefinition();
	}

	/**
	 * Implementors are intended to create the concrete {@link MatrixDefinition}
	 * instance in this method.
	 * 
	 * @return The instance of {@link MatrixDefinition} used by subclasses.
	 */
	protected abstract MatrixDefinition createConcreteMatrixDefinition();

	protected void createRowIDTypeGroup(Composite parent) {
		Group idTypeGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		idTypeGroup.setText("Row ID type");
		idTypeGroup.setLayout(new RowLayout());
		idTypeGroup.setLayoutData(new GridData(SWT.LEFT));
		rowIDCombo = new Combo(idTypeGroup, SWT.DROP_DOWN);
		rowIDTypes = new ArrayList<IDType>();

		fillRowIDTypeCombo();

		rowIDCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableColumn idColumn = previewTable.getColumn(1);
				idColumn.setText(rowIDCombo.getText());
			}
		});
	}

	protected void fillRowIDTypeCombo() {
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

	protected void createDelimiterGroup(Composite parent) {
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
				matrixDefinition.setDelimiter(customizedDelimiterTextField.getText());
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
					matrixDefinition.setDelimiter((String) selectedButton.getData());
					createDataPreviewTable();
				} else {
					customizedDelimiterTextField.setEnabled(true);
					matrixDefinition.setDelimiter(" ");
					createDataPreviewTable();
				}
			}
		};

		for (int i = 0; i < delimiterButtons.length; i++) {
			delimiterButtons[i].addSelectionListener(radioGroupSelectionListener);
		}

	}

	protected void createDataPreviewTable() {

		String delimiter = matrixDefinition.getDelimiter();

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
					.getResource(matrixDefinition.getDataSourcePath());

			String line = "";

			// Ignore unwanted header files of file
			for (int iIgnoreLineIndex = 0; iIgnoreLineIndex < matrixDefinition
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

		determineRowIDType();

		parentComposite.pack();
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

	private void determineRowIDType() {

		ArrayList<IDCategory> idCategories = getAvailableIDCategories();

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

					if (idType.getColumnType().equals(EDataType.INT)) {
						try {
							Integer idInt = Integer.valueOf(currentID);
							if (idMappingManager.doesElementExist(idType, idInt)) {
								currentCorrectElements++;
							}
						} catch (NumberFormatException e) {
						}
					} else if (idType.getColumnType().equals(EDataType.STRING)) {
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

	/**
	 * @return List of IDCategories that are considered by the concrete
	 *         implementation of the dialog.
	 */
	protected abstract ArrayList<IDCategory> getAvailableIDCategories();

	/**
	 * This method is called when the most probable record id type according to
	 * the data was determined.
	 * 
	 * @param mostProbableIDType
	 */
	protected abstract void setMostProbableRecordIDType(IDType mostProbableIDType);

}
