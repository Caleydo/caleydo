package org.caleydo.core.io.gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.gui.util.LabelEditorDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * File dialog for opening raw text data files.
 * 
 * @author Marc Streit
 */
public class ImportDataDialog
	extends Dialog {

	private static int MAX_PREVIEW_TABLE_ROWS = 50;
	private static int MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION = 10;

	private Composite composite;

	private Text txtFileName;
	private Text txtStartParseAtLine;
	private Text txtMin;
	private Text txtMax;

	private Button buttonHomogeneous;
	private Button buttonUncertaintyDataProvided;

	private Table previewTable;

	private ArrayList<Button> skipColumn = new ArrayList<Button>();

	private Combo idCombo;

	private ArrayList<IDType> idTypes;

	private String inputFile = "";
	private String filePath = "";

	private LoadDataParameters loadDataParameters = new LoadDataParameters();;

	private String mathFilterMode = "Log2";

	private boolean useGeneClusterInfo = false;
	private boolean useExperimentClusterInfo = false;
	private boolean isUncertaintyDataProvided = false;

	private ATableBasedDataDomain dataDomain = null;

	// FIXME: this is never set to false. for loading general data this needs to be set.
	private boolean isGenetic = true;

	public ImportDataDialog(Shell parentShell) {
		super(parentShell);

		this.dataDomain =
			(ATableBasedDataDomain) DataDomainManager.get()
				.createDataDomain("org.caleydo.datadomain.genetic");
	}

	public ImportDataDialog(Shell parentShell, IDataDomain dataDomain) {
		super(parentShell);
		this.dataDomain = (ATableBasedDataDomain) dataDomain;
	}

	public ImportDataDialog(Shell parentShell, String inputFile, IDataDomain dataDomain) {
		this(parentShell, dataDomain);
		this.inputFile = inputFile;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Open Text Data File");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		createGUI(parent);
		return parent;
	}

	@Override
	protected void okPressed() {

		boolean success = readStorageDefinition();
		if (success) {
			success = DataTableUtils.createStorages(loadDataParameters);
		}
		readParameters();

		dataDomain.setLoadDataParameters(loadDataParameters);

		DataTable table = DataTableUtils.createData(dataDomain);
		if (table == null)
			throw new IllegalStateException("Problem while creating set!");

		super.okPressed();
	}

	private void createGUI(Composite parent) {
		int numGridCols = 5;

		loadDataParameters.setDataDomain(dataDomain);

		composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(numGridCols, false);
		composite.setLayout(layout);

		Group inputFileGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		inputFileGroup.setText("Input file");
		inputFileGroup.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = numGridCols;
		inputFileGroup.setLayoutData(gridData);

		Button buttonFileChooser = new Button(inputFileGroup, SWT.PUSH);
		buttonFileChooser.setText("Choose data file..");
		// buttonFileChooser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		txtFileName = new Text(inputFileGroup, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buttonFileChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				FileDialog fileDialog = new FileDialog(new Shell());
				fileDialog.setText("Open");
				fileDialog.setFilterPath(filePath);
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);
				loadDataParameters.setFileName(fileDialog.open());

				txtFileName.setText(loadDataParameters.getFileName());

				createDataPreviewTable("\t");

//				if (isGenetic)
//					determineFileIDType();
			}
		});

		Group startParseAtLineGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		startParseAtLineGroup.setText("Ignore lines in header");
		startParseAtLineGroup.setLayout(new GridLayout(1, false));

		txtStartParseAtLine = new Text(startParseAtLineGroup, SWT.BORDER);
		txtStartParseAtLine.setLayoutData(new GridData(50, 15));
		txtStartParseAtLine.setText("1");
		txtStartParseAtLine.setTextLimit(2);
		txtStartParseAtLine.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				// Add 1 because the number that the user enters is human readable and not array index
				// (starting with 0).
				loadDataParameters.setStartParseFileAtLine(Integer.valueOf(txtStartParseAtLine.getText())
					.intValue());

				createDataPreviewTable("\t");
				composite.pack();
			}
		});

		if (isGenetic) {

			Group idTypeGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
			idTypeGroup.setText("ID type");
			idTypeGroup.setLayout(new RowLayout());

			idCombo = new Combo(idTypeGroup, SWT.DROP_DOWN);
			idTypes = new ArrayList<IDType>();

			HashSet<IDType> tempIDTypes = GeneralManager.get().getIDMappingManager().getIDTypes();

			for (IDType idType : tempIDTypes) {
				if (!idType.isInternalType())
					idTypes.add(idType);
			}

			String[] idTypesAsString = new String[idTypes.size()];
			int index = 0;
			for (IDType idType : idTypes) {
				idTypesAsString[index] = idType.getTypeName();
				index++;
			}
			idCombo.setItems(idTypesAsString);
			idCombo.setEnabled(true);
			idCombo.select(0);
			idCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableColumn idColumn = previewTable.getColumn(1);
					idColumn.setText(idCombo.getText());
				}
			});
		}

		createDelimiterGroup();
		createFilterGroup();
		createDataPropertiesGroup();

		previewTable = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		previewTable.setHeaderVisible(true);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = numGridCols;
		gridData.heightHint = 400;
		gridData.widthHint = 1000;
		previewTable.setLayoutData(gridData);

		// Check if an external file name is given to the action
		if (!inputFile.isEmpty()) {
			txtFileName.setText(inputFile);
			loadDataParameters.setFileName(inputFile);
			mathFilterMode = "Log10";
			// mathFilterCombo.select(1);

			createDataPreviewTable("\t");
		}
	}

	private void createDelimiterGroup() {
		Group delimiterGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		delimiterGroup.setText("Separated by (delimiter)");
		delimiterGroup.setLayout(new RowLayout());

		final Button[] buttonDelimiter = new Button[6];

		buttonDelimiter[0] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[0].setSelection(true);
		buttonDelimiter[0].setText("TAB");
		buttonDelimiter[0].setBounds(10, 5, 75, 30);

		buttonDelimiter[1] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[1].setText(";");
		buttonDelimiter[1].setBounds(10, 30, 75, 30);

		buttonDelimiter[2] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[2].setText(",");
		buttonDelimiter[2].setBounds(10, 55, 75, 30);

		buttonDelimiter[3] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[3].setText(".");
		buttonDelimiter[3].setBounds(10, 55, 75, 30);

		buttonDelimiter[4] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[4].setText("SPACE");
		buttonDelimiter[4].setBounds(10, 55, 75, 30);

		buttonDelimiter[5] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[5].setText("Other");
		buttonDelimiter[5].setBounds(10, 55, 75, 30);

		final Text txtCustomizedDelimiter = new Text(delimiterGroup, SWT.BORDER);
		txtCustomizedDelimiter.setBounds(0, 0, 75, 30);
		txtCustomizedDelimiter.setTextLimit(1);
		txtCustomizedDelimiter.setEnabled(false);
		txtCustomizedDelimiter.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				createDataPreviewTable(txtCustomizedDelimiter.getText());
				composite.pack();
			}

		});

		buttonDelimiter[0].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				if (loadDataParameters.getFileName().isEmpty())
					return;

				createDataPreviewTable("\t");

				composite.pack();
			}
		});

		buttonDelimiter[1].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);
				txtCustomizedDelimiter.setEnabled(false);

				if (loadDataParameters.getFileName().isEmpty())
					return;

				createDataPreviewTable(";");

				composite.pack();
			}
		});

		buttonDelimiter[2].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);
				txtCustomizedDelimiter.setEnabled(false);

				if (loadDataParameters.getFileName().isEmpty())
					return;

				createDataPreviewTable(",");

				composite.pack();
			}
		});

		buttonDelimiter[3].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);
				txtCustomizedDelimiter.setEnabled(false);

				if (loadDataParameters.getFileName().isEmpty())
					return;

				createDataPreviewTable(".");

				composite.pack();
			}
		});

		buttonDelimiter[4].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[5].setSelection(false);
				txtCustomizedDelimiter.setEnabled(false);

				if (loadDataParameters.getFileName().isEmpty())
					return;

				createDataPreviewTable(" ");

				composite.pack();
			}
		});

		buttonDelimiter[5].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				txtCustomizedDelimiter.setEnabled(true);

				if (loadDataParameters.getFileName().isEmpty())
					return;

				createDataPreviewTable(" ");

				composite.pack();
			}
		});
	}

	private void createFilterGroup() {
		Group filterGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		filterGroup.setText("Apply filter");
		filterGroup.setLayout(new RowLayout());
		filterGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Combo mathFilterCombo = new Combo(filterGroup, SWT.DROP_DOWN);
		String[] filterOptions = { "Normal", "Log10", "Log2" };
		mathFilterCombo.setItems(filterOptions);
		mathFilterCombo.setEnabled(true);
		mathFilterCombo.select(2);
		mathFilterCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mathFilterMode = mathFilterCombo.getText();
			}
		});

		final Button buttonMin = new Button(filterGroup, SWT.CHECK);
		buttonMin.setText("Min");
		buttonMin.setEnabled(true);
		buttonMin.setSelection(false);
		buttonMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtMin.setEnabled(buttonMin.getSelection());
			}
		});

		txtMin = new Text(filterGroup, SWT.BORDER);
		txtMin.setEnabled(false);
		txtMin.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				// for (char c : chars) {
				// TODO
				// if (!('0' <= chars[i] && chars[i] <= '9'))
				// {
				// e.doit = false;
				// return;
				// }
				// }
			}
		});

		final Button buttonMax = new Button(filterGroup, SWT.CHECK);
		buttonMax.setText("Max");
		buttonMax.setEnabled(true);
		buttonMax.setSelection(false);
		buttonMax.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				txtMax.setEnabled(buttonMax.getSelection());
			}
		});

		txtMax = new Text(filterGroup, SWT.BORDER);
		txtMax.setEnabled(false);
		txtMax.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				// for (char c : chars) {
				// TODO
				// if (!('0' <= chars[i] && chars[i] <= '9'))
				// {
				// e.doit = false;
				// return;
				// }
				// }
			}
		});

	}

	private void createDataPropertiesGroup() {
		Group dataPropertiesGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		dataPropertiesGroup.setText("Data properties");
		dataPropertiesGroup.setLayout(new RowLayout());
		dataPropertiesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buttonHomogeneous = new Button(dataPropertiesGroup, SWT.CHECK);
		buttonHomogeneous.setText("Homogeneous data");
		buttonHomogeneous.setEnabled(true);
		buttonHomogeneous.setSelection(true);

		buttonUncertaintyDataProvided = new Button(dataPropertiesGroup, SWT.CHECK);
		buttonUncertaintyDataProvided.setText("Uncertainty data");
		buttonUncertaintyDataProvided.setEnabled(true);
		buttonUncertaintyDataProvided.setSelection(isUncertaintyDataProvided);
		buttonUncertaintyDataProvided.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isUncertaintyDataProvided = buttonUncertaintyDataProvided.getSelection();
			}
		});
	}

	private void createDataPreviewTable(final String sDelimiter) {

		this.loadDataParameters.setDelimiter(sDelimiter);

		// boolean clusterInfo = false;

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
			file = GeneralManager.get().getResourceLoader().getResource(loadDataParameters.getFileName());

			String line = "";

			// Ignore unwanted header files of file
			for (int iIgnoreLineIndex = 0; iIgnoreLineIndex < loadDataParameters.getStartParseFileAtLine() - 1; iIgnoreLineIndex++) {
				file.readLine();
			}

			String nextToken = "";
			StringTokenizer tokenizer;
			TableColumn column;
			TableItem item;
			int colIndex = 0;

			// Read labels
			if ((line = file.readLine()) != null) {
				tokenizer = new StringTokenizer(line, sDelimiter, false);
				column = new TableColumn(previewTable, SWT.NONE);
				column.setWidth(100);
				column.setText("");

				while (tokenizer.hasMoreTokens()) {
					nextToken = tokenizer.nextToken();

					// Check for group information
					if (nextToken.equals("GROUP_NUMBER") || nextToken.equals("Cluster_Number")) {
						useGeneClusterInfo = true;
						// If group info is detected no more columns are parsed
						break;
					}

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
				tokenizer = new StringTokenizer(line, sDelimiter, true);
				item = new TableItem(previewTable, SWT.NONE);
				item.setText("Row " + (rowCount + 1)); // +1 to be intuitive for a non programmer :)
				colIndex = 0;

				while (tokenizer.hasMoreTokens()) {
					nextToken = tokenizer.nextToken();

					// check for experiment cluster info
					if (nextToken.equals("Cluster_Number") || nextToken.equals("Cluster_Repr"))
						useExperimentClusterInfo = true;

					// Check for empty cells
					if (nextToken.equals(sDelimiter) && !isCellFilled) {
						item.setText(colIndex + 1, "");
						colIndex++;
					}
					else if (nextToken.equals(sDelimiter) && isCellFilled) {
						isCellFilled = false; // reset
					}
					else {
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

				tokenizer = new StringTokenizer(line, sDelimiter, true);
				nextToken = tokenizer.nextToken();

				// probably weeks performance
				if (nextToken.equals("Cluster_Number") || nextToken.equals("Cluster_Repr"))
					useExperimentClusterInfo = true;
			}

		}
		catch (FileNotFoundException e) {
			throw new IllegalStateException("File not found!");
		}
		catch (IOException ioe) {
			throw new IllegalStateException("Input/output problem!");
		}
		
		if (isGenetic)
			determineFileIDType();
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
							textColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
						}
						else {
							textColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
						}

						item.setForeground(((Integer) e.widget.getData("column")), textColor);
					}
				}
			});

			skipColumn.add(skipButton);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = editor.grabVertical = true;
			editor.setEditor(skipButton, tmpItem, iColIndex);
		}
	}

	/**
	 * Reads the min and max values (if set) from the dialog
	 */
	private void readParameters() {
		if (txtMin.getEnabled() && !txtMin.getText().isEmpty()) {
			float fMin = Float.parseFloat(txtMin.getText());
			if (!Float.isNaN(fMin)) {
				loadDataParameters.setMinDefined(true);
				loadDataParameters.setMin(fMin);
			}
		}
		if (txtMax.getEnabled() && !txtMax.getText().isEmpty()) {
			float fMax = Float.parseFloat(txtMax.getText());
			if (!Float.isNaN(fMax)) {
				loadDataParameters.setMaxDefined(true);
				loadDataParameters.setMax(fMax);
			}
		}

		if (isGenetic) {
			loadDataParameters.setFileIDType(idTypes.get(idCombo.getSelectionIndex()));
		}

		loadDataParameters.setMathFilterMode(mathFilterMode);
		loadDataParameters.setIsDataHomogeneous(buttonHomogeneous.getSelection());
	}

	/**
	 * prepares the storage creation definition from the preview table. The storage creation definition
	 * consists of the definition which columns in the data-CSV-file should be read, which should be skipped
	 * and the storage-labels.
	 * 
	 * @return <code>true</code>if the preparation was successful, <code>false</code> otherwise
	 */
	private boolean readStorageDefinition() {
		ArrayList<String> storageLabels = new ArrayList<String>();

		StringBuffer inputPattern = new StringBuffer("SKIP" + ";");

		for (int columnIndex = 2; columnIndex < previewTable.getColumnCount(); columnIndex++) {

			if (!skipColumn.get(columnIndex - 2).getSelection()) {
				inputPattern.append("SKIP;");
			}
			else {

				// in uncertainty mode each second column is flagged with "CERTAINTY"
				if (isUncertaintyDataProvided && (columnIndex % 2 != 0)) {
					inputPattern.append("CERTAINTY;");
					continue;
				}

				// here we try to guess the datatype
				// TODO: move this to the preview window where it can be modified by the user
				String dataType = "FLOAT";
				try {
					int testSize =
						previewTable.getItemCount() - loadDataParameters.getStartParseFileAtLine() - 1;
					for (int rowCount = 1; rowCount < testSize; rowCount++) {
						String testString = previewTable.getItem(rowCount).getText(columnIndex);
						if (!testString.isEmpty())
							Float.parseFloat(testString);
					}
				}
				catch (NumberFormatException nfe) {
					dataType = "STRING";
				}

				inputPattern.append(dataType + ";");

				String labelText = previewTable.getColumn(columnIndex).getText();
				storageLabels.add(labelText);
			}
		}

		if (useGeneClusterInfo) {
			inputPattern.append("GROUP_NUMBER;GROUP_REPRESENTATIVE;");
		}
		inputPattern.append("ABORT;");

		loadDataParameters.setInputPattern(inputPattern.toString());
		loadDataParameters.setFileName(txtFileName.getText());
		loadDataParameters.setStorageLabels(storageLabels);
		loadDataParameters.setUseExperimentClusterInfo(useExperimentClusterInfo);

		if (loadDataParameters.getFileName().equals("")) {
			MessageDialog.openError(new Shell(), "Invalid filename", "Invalid filename");
			return false;
		}

		return true;
	}

	public LoadDataParameters getLoadDataParameters() {
		return loadDataParameters;
	}

	public void setLoadDataParameters(LoadDataParameters loadDataParameters) {
		this.loadDataParameters = loadDataParameters;
	}

	private void determineFileIDType() {

		IDMappingManager idMappingManager = GeneralManager.get().getIDMappingManager();

		TableItem[] items = previewTable.getItems();
		ArrayList<String> idList = new ArrayList<String>();
		int rowIndex = 1;
		while (rowIndex < items.length && rowIndex <= MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION) {
			idList.add(items[rowIndex].getText(1));
			rowIndex++;
		}
		if (idTypes == null) {
			idTypes = new ArrayList<IDType>();
			HashSet<IDType> alIDTypesTemp = GeneralManager.get().getIDMappingManager().getIDTypes();
			for (IDType idType : alIDTypesTemp) {
				if (!idType.isInternalType())
					idTypes.add(idType);
			}
		}

		int maxCorrectElements = 0;
		IDType mostProbableIDType = null;

		for (IDType idType : idTypes) {

			int currentCorrectElements = 0;

			for (String currentID : idList) {

				if (idType.getStorageType().equals(EStorageType.INT)) {
					try {
						Integer idInt = Integer.valueOf(currentID);
						if (idMappingManager.doesElementExist(idType, idInt)) {
							currentCorrectElements++;
						}
					}
					catch (NumberFormatException e) {
					}
				}
				else if (idType.getStorageType().equals(EStorageType.STRING)) {
					if (idMappingManager.doesElementExist(idType, currentID)) {
						currentCorrectElements++;
					}
					else if (idType.getTypeName().equals("REFSEQ_MRNA")) {
						if (currentID.contains(".")) {
							if (idMappingManager.doesElementExist(idType,
								currentID.substring(0, currentID.indexOf(".")))) {
								currentCorrectElements++;
							}
						}
					}
				}
			}

			if (currentCorrectElements >= idList.size()) {
				idCombo.select(idTypes.indexOf(idType));
				TableColumn idColumn = previewTable.getColumn(1);
				idColumn.setText(idType.getTypeName());
				return;
			}
			if (currentCorrectElements >= maxCorrectElements) {
				maxCorrectElements = currentCorrectElements;
				mostProbableIDType = idType;
			}
		}

		idCombo.select(idTypes.indexOf(mostProbableIDType));
		TableColumn idColumn = previewTable.getColumn(1);
		idColumn.setText(mostProbableIDType.getTypeName());

	}
}
