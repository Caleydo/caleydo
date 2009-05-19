package org.caleydo.rcp.action.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateSet;
import org.caleydo.core.command.data.CmdDataCreateStorage;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNStorages;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.view.swt.tabular.LabelEditorDialog;
import org.caleydo.rcp.dialog.file.LoadDataDialog;
import org.eclipse.jface.action.Action;
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
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action responsible for importing data to current Caleydo project.
 * 
 * @author Marc Streit
 */
public class FileLoadDataAction
	extends Action
	implements ActionFactory.IWorkbenchAction {

	public final static String ID = "org.caleydo.rcp.FileLoadDataAction";

	private Composite parentComposite;
	private Composite composite;

	private static int MAX_PREVIEW_TABLE_ROWS = 50;

	private Text txtFileName;
	private Text txtGeneTreeFileName;
	private Text txtExperimentsTreeFileName;
	private Text txtStartParseAtLine;
	private Text txtMin;
	private Text txtMax;

	private Table previewTable;

	private ArrayList<Button> arSkipColumn;

	private String sInputFile = "";
	private String sFileName = "";
	private String sGeneTreeFileName = "";
	private String sExperimentsFileName = "";
	private String sFilePath = "";
	private String sInputPattern = "";// SKIP;";
	private String sDelimiter = "";
	private int iStartParseFileAtLine = 1;  // ID row should be ignored

	private String sMathFilterMode = "Normal";

	private boolean bUseClusterInfo = false;

//	private Combo useIDTypeCombo;
	
	/**
	 * Constructor.
	 */
	public FileLoadDataAction(final Composite parentComposite) {
		super("Load data");
		this.parentComposite = parentComposite;

		arSkipColumn = new ArrayList<Button>();
	}

	/**
	 * Constructor.
	 */
	public FileLoadDataAction(final Composite parentComposite, String sInputFile) {
		this(parentComposite);
		this.sInputFile = sInputFile;
	}

	@Override
	public void run() {

		createGUI();
	}

	private void createGUI() {
		composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);

		Group inputFileGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		inputFileGroup.setText("Input file");
		inputFileGroup.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		inputFileGroup.setLayoutData(gridData);
		
		Button buttonFileChooser = new Button(inputFileGroup, SWT.PUSH);
		buttonFileChooser.setText("Choose data file..");
//		buttonFileChooser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		txtFileName = new Text(inputFileGroup, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buttonFileChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				FileDialog fileDialog = new FileDialog(parentComposite.getShell());
				fileDialog.setText("Open");
				fileDialog.setFilterPath(sFilePath);
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);
				sFileName = fileDialog.open();

				txtFileName.setText(sFileName);

				createDataPreviewTable("\t");
			}
		});

		Button buttonTreeChooser = new Button(inputFileGroup, SWT.PUSH);
		buttonTreeChooser.setText("Choose gene tree file.. (optional)");

		txtGeneTreeFileName = new Text(inputFileGroup, SWT.BORDER);
		txtGeneTreeFileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buttonTreeChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				FileDialog fileDialog = new FileDialog(parentComposite.getShell());
				fileDialog.setText("Open");
				fileDialog.setFilterPath(sFilePath);
				String[] filterExt = { "*.xml*" };
				fileDialog.setFilterExtensions(filterExt);
				sGeneTreeFileName = fileDialog.open();

				txtGeneTreeFileName.setText(sGeneTreeFileName);
			}
		});
		
		Button buttonExperimentsTreeChooser = new Button(inputFileGroup, SWT.PUSH);
		buttonExperimentsTreeChooser.setText("Choose experiments tree file.. (optional)");

		txtExperimentsTreeFileName = new Text(inputFileGroup, SWT.BORDER);
		txtExperimentsTreeFileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buttonExperimentsTreeChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				FileDialog fileDialog = new FileDialog(parentComposite.getShell());
				fileDialog.setText("Open");
				fileDialog.setFilterPath(sFilePath);
				String[] filterExt = { "*.xml*" };
				fileDialog.setFilterExtensions(filterExt);
				sExperimentsFileName = fileDialog.open();

				txtExperimentsTreeFileName.setText(sExperimentsFileName);
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
				
				// Add 1 because the number that the user enters is human readable and not array index (starting with 0).
				iStartParseFileAtLine = Integer.valueOf(txtStartParseAtLine.getText()).intValue();

				createDataPreviewTable("\t");
				composite.pack();
			}
		});
		
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

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable("\t");

				if (sFileName.isEmpty())
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

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(";");

				if (sFileName.isEmpty())
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

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(",");

				if (sFileName.isEmpty())
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

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(".");

				if (sFileName.isEmpty())
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

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(" ");

				if (sFileName.isEmpty())
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

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(" ");

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(" ");

				composite.pack();
			}
		});
		
		Group filterGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		filterGroup.setText("Apply filter");
		filterGroup.setLayout(new RowLayout());
		filterGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Combo mathFilterCombo = new Combo(filterGroup, SWT.DROP_DOWN);
		String[] filterOptions = { "Normal", "Log10", "Log2" };
		mathFilterCombo.setItems(filterOptions);
		mathFilterCombo.setEnabled(true);
		mathFilterCombo.select(0);
		mathFilterCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sMathFilterMode = mathFilterCombo.getText();

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

		previewTable = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		previewTable.setHeaderVisible(true);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		gridData.heightHint = 400;
		gridData.widthHint = 1000;
		previewTable.setLayoutData(gridData);

		// Check if an external file name is given to the action
		if (!sInputFile.isEmpty()) {
			txtFileName.setText(sInputFile);
			sFileName = sInputFile;
			sMathFilterMode = "Log10";
			mathFilterCombo.select(1);

			createDataPreviewTable("\t");
		}
	}

	private void createDataPreviewTable(final String sDelimiter) {
		this.sDelimiter = sDelimiter;

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
		BufferedReader brFile;
		try {
			brFile = GeneralManager.get().getResourceLoader().getResource(sFileName);

			String sLine = "";

			// Ignore unwanted header files of file
			for (int iIgnoreLineIndex = 0; iIgnoreLineIndex < iStartParseFileAtLine-1; iIgnoreLineIndex++) {
				brFile.readLine();
			}

			String sTmpNextToken = "";
			StringTokenizer tokenizer;
			TableColumn column;
			TableItem item;
			int iColIndex = 0;

			// Read labels
			if ((sLine = brFile.readLine()) != null) {
				tokenizer = new StringTokenizer(sLine, sDelimiter, false);
				column = new TableColumn(previewTable, SWT.NONE);
				column.setWidth(100);
				column.setText("");

				while (tokenizer.hasMoreTokens()) {
					sTmpNextToken = tokenizer.nextToken();

					// Check for group information
					if (sTmpNextToken.equals("GROUP_NUMBER") || sTmpNextToken.equals("Cluster_Number")) {
						bUseClusterInfo = true;
						// If group info is detected no more columns are parsed
						break;
					}

					final TableColumn dataColumn = new TableColumn(previewTable, SWT.NONE);
					dataColumn.setWidth(100);
					dataColumn.setText(sTmpNextToken);

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

					iColIndex++;
				}
			}

			createDataClassBar();

			int iRowCount = 0;
			boolean bCellFilled = false;

			// Read raw data
			while ((sLine = brFile.readLine()) != null && iRowCount < MAX_PREVIEW_TABLE_ROWS) {
				// last flag triggers return of delimiter itself
				tokenizer = new StringTokenizer(sLine, sDelimiter, true);
				item = new TableItem(previewTable, SWT.NONE);
				item.setText("Row " + (iRowCount + 1)); // +1 to be intuitive for a non programmer :)
				iColIndex = 0;

				while (tokenizer.hasMoreTokens()) {
					sTmpNextToken = tokenizer.nextToken();

					// Check for empty cells
					if (sTmpNextToken.equals(sDelimiter) && !bCellFilled) {
						item.setText(iColIndex + 1, "");
						iColIndex++;
					}
					else if (sTmpNextToken.equals(sDelimiter) && bCellFilled) {
						bCellFilled = false; // reset
					}
					else {
						bCellFilled = true;
						item.setText(iColIndex + 1, sTmpNextToken);
						iColIndex++;
					}
				}

				bCellFilled = false; // reset

				iRowCount++;
			}
		}
		catch (FileNotFoundException e) {
			throw new IllegalStateException("File not found!");
		}
		catch (IOException ioe) {
			throw new IllegalStateException("Input/output problem!");
		}
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

			arSkipColumn.add(skipButton);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = editor.grabVertical = true;
			editor.setEditor(skipButton, tmpItem, iColIndex);
		}
	}

	public boolean execute() {

		return createData();
	}

	private boolean createData() {
		ArrayList<Integer> iAlStorageId = new ArrayList<Integer>();
		String sStorageIDs = "";

		sInputPattern = "SKIP" + ";";

		for (int iColIndex = 2; iColIndex < previewTable.getColumnCount(); iColIndex++) {

			if (!arSkipColumn.get(iColIndex - 2).getSelection()) {
				sInputPattern = sInputPattern + "SKIP" + ";";
				continue;
			}
			else {
				sInputPattern = sInputPattern + "FLOAT" + ";";
			}

			// Currently we only allow parsing float data
			// Create data storage
			CmdDataCreateStorage cmdCreateStorage =
				(CmdDataCreateStorage) GeneralManager.get().getCommandManager().createCommandByType(
					ECommandType.CREATE_STORAGE);

			cmdCreateStorage.setAttributes(EManagedObjectType.STORAGE_NUMERICAL);
			cmdCreateStorage.doCommand();

			INumericalStorage storage = (INumericalStorage) cmdCreateStorage.getCreatedObject();

			String labelText = previewTable.getColumn(iColIndex).getText();
			storage.setLabel(labelText);

			iAlStorageId.add(storage.getID());

			if (!sStorageIDs.equals("")) {
				sStorageIDs += IGeneralManager.sDelimiter_Parser_DataItems;
			}

			sStorageIDs = sStorageIDs + storage.getID();
		}

		if (bUseClusterInfo) {
			sInputPattern += "GROUP_NUMBER;GROUP_REPRESENTATIVE;";
		}

		sInputPattern += "ABORT;";

		sFileName = txtFileName.getText();

		if (sFileName.equals("")) {
			MessageDialog.openError(parentComposite.getShell(), "Invalid filename", "Invalid filename");
			return false;
		}

		// Create SET
		CmdDataCreateSet cmdCreateSet =
			(CmdDataCreateSet) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.CREATE_SET_DATA);
		
		IUseCase useCase = GeneralManager.get().getUseCase();
		
		if (useCase.getUseCaseMode() == EUseCaseMode.GENETIC_DATA) {
			cmdCreateSet.setAttributes(iAlStorageId, ESetType.GENE_EXPRESSION_DATA);		}
		else if (useCase.getUseCaseMode() == EUseCaseMode.UNSPECIFIED_DATA) {
			cmdCreateSet.setAttributes(iAlStorageId, ESetType.UNSPECIFIED);		
		}
		else {
			throw new IllegalStateException("Not implemented.");
		}
		
		cmdCreateSet.doCommand();
//		useCase.setSet(cmdCreateSet.getCreatedObject());

		// Trigger file loading command
		CmdLoadFileNStorages cmdLoadCsv =
			(CmdLoadFileNStorages) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.LOAD_DATA_FILE);

		cmdLoadCsv.setAttributes(iAlStorageId, sFileName, sGeneTreeFileName, sExperimentsFileName, sInputPattern, sDelimiter,
			iStartParseFileAtLine, -1);
		cmdLoadCsv.doCommand();
		
		if (!cmdLoadCsv.isParsingOK()) {
			// TODO: Clear created set and storages which are empty
			return false;
		}

		CmdLoadFileLookupTable cmdLoadLookupTableFile =
			(CmdLoadFileLookupTable) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.LOAD_LOOKUP_TABLE_FILE);

		if (useCase.getUseCaseMode() == EUseCaseMode.GENETIC_DATA) {
			cmdLoadLookupTableFile.setAttributes(sFileName, iStartParseFileAtLine, -1,
				"REFSEQ_MRNA_2_EXPRESSION_INDEX REVERSE LUT", sDelimiter, "REFSEQ_MRNA_INT_2_EXPRESSION_INDEX");
		}
		else if (useCase.getUseCaseMode() == EUseCaseMode.UNSPECIFIED_DATA) {
			cmdLoadLookupTableFile.setAttributes(sFileName, iStartParseFileAtLine, -1,
				"UNSPECIFIED_2_EXPRESSION_INDEX REVERSE", sDelimiter, "");			
		}
		else {
			throw new IllegalStateException("Not implemented.");
		}
		
		cmdLoadLookupTableFile.doCommand();

		ISet set = useCase.getSet();
		
		if (!txtMin.getText().isEmpty()) {
			float fMin = Float.parseFloat(txtMin.getText());
			if (!Float.isNaN(fMin)) {
				set.setMin(fMin);
			}
		}

		if (!txtMax.getText().isEmpty()) {
			float fMax = Float.parseFloat(txtMax.getText());
			if (!Float.isNaN(fMax)) {
				set.setMax(fMax);
			}
		}

		if (sMathFilterMode.equals("Normal")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.NORMAL, true);
		}
		else if (sMathFilterMode.equals("Log10")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG10, true);
		}
		else if (sMathFilterMode.equals("Log2")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG2, true);
		}
		else
			throw new IllegalStateException("Unknown data representation type");


		// Since the data is filled to the new set
		// the views of the current use case can be updated.
		useCase.updateSetInViews();
		
		return true;
	}

	/**
	 * For testing purposes
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Shell shell = new Shell();
		// shell.setMaximized(true);
		LoadDataDialog dialog = new LoadDataDialog(shell);
		dialog.open();
	}

	@Override
	public void dispose() {
	}
}
