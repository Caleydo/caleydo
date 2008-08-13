package org.caleydo.rcp.action.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.media.opengl.GLEventListener;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.data.CmdDataCreateSet;
import org.caleydo.core.command.data.CmdDataCreateStorage;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNStorages;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.AGLEventListenerStorageBasedView;
import org.caleydo.core.view.opengl.canvas.heatmap.GLCanvasHeatMap;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.dialog.file.FileLoadDataDialog;
import org.caleydo.rcp.image.IImageKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Action responsible for importing data to current Caleydo project.
 * 
 * @author Marc Streit
 */
public class FileLoadDataAction
	extends Action
	implements ActionFactory.IWorkbenchAction
{

	public final static String ID = "org.caleydo.rcp.FileLoadDataAction";

	private Composite parentComposite;

	private IWorkbenchWindow window;

	private static int MAX_PREVIEW_TABLE_ROWS = 50;

	private Composite composite;

	private Text txtFileName;
	private Text txtStartParseAtLine;

	private Table previewTable;

	private ArrayList<Combo> arComboDataClass;
	private ArrayList<Combo> arComboDataType;
	private ArrayList<Button> arButtonNormalize;

	private String sFileName;

	protected String sFilePath = "";

	private String sInputPattern = "SKIP;ABORT";

	private String sDelimiter = "";

	private int iCreatedSetID = -1;

	private int iStartParseFileAtLine = 0;

	/**
	 * Constructor.
	 */
	public FileLoadDataAction(final Composite parentComposite)
	{

		super("Load Data");
		setId(ID);
		setToolTipText("Import data from text file");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.caleydo.rcp",
				IImageKeys.FILE_OPEN_XML_CONFIG_FILE));

		this.parentComposite = parentComposite;

		arComboDataClass = new ArrayList<Combo>();
		arComboDataType = new ArrayList<Combo>();
		arButtonNormalize = new ArrayList<Button>();
	}

	/**
	 * Constructor.
	 */
	public FileLoadDataAction(final IWorkbenchWindow window)
	{

		this((Composite) null);

		parentComposite = null;
		this.window = window;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{

		// Check if load data GUI is embedded in a wizard or if a own dialog
		// must be created.
		if (parentComposite == null && window != null)
		{
			FileLoadDataDialog loadDataFileDialog = new FileLoadDataDialog(window.getShell());
			loadDataFileDialog.open();
		}
		else
		{
			createGUI();
		}
	}

	private void createGUI()
	{

		composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Button buttonFileChooser = new Button(composite, SWT.PUSH);
		buttonFileChooser.setText("Choose data file..");

		txtFileName = new Text(composite, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		buttonFileChooser.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent event)
			{

				FileDialog fileDialog = new FileDialog(parentComposite.getShell());
				fileDialog.setText("Open");
				fileDialog.setFilterPath(sFilePath);
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);
				sFileName = fileDialog.open();

				txtFileName.setText(sFileName);

				createDataPreviewTable("\t");
			}

			public void widgetDefaultSelected(SelectionEvent event)
			{

			}
		});

		Label lblStartParseAtLine = new Label(composite, SWT.NONE);
		lblStartParseAtLine.setText("Ignore lines in header:");
		lblStartParseAtLine.setLayoutData(new GridData(GridData.END, GridData.BEGINNING,
				false, false));

		txtStartParseAtLine = new Text(composite, SWT.BORDER);
		txtStartParseAtLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
				false));
		txtStartParseAtLine.setText("0");
		txtStartParseAtLine.setTextLimit(2);
		txtStartParseAtLine.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				iStartParseFileAtLine = StringConversionTool.convertStringToInt(
						txtStartParseAtLine.getText(), 0);

				createDataPreviewTable("\t");
				composite.pack();
			}
		});

		Label lblDelimiter = new Label(composite, SWT.NONE);
		lblDelimiter.setText("Separated by:");
		lblDelimiter
				.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));

		Group delimiterGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		delimiterGroup.setLayout(new FillLayout());
		// delimiterGroup.setText("Delimiter");

		final Button[] buttonDelimiter = new Button[6];

		buttonDelimiter[0] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[0].setSelection(true);
		buttonDelimiter[0].setText("TAB");
		buttonDelimiter[0].setBounds(10, 5, 75, 30);
		buttonDelimiter[0].addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{

				createDataPreviewTable("\t");

				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				composite.pack();
			}
		});

		buttonDelimiter[1] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[1].setText(";");
		buttonDelimiter[1].setBounds(10, 30, 75, 30);
		buttonDelimiter[1].addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{

				createDataPreviewTable(";");

				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				composite.pack();
			}
		});

		buttonDelimiter[2] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[2].setText(",");
		buttonDelimiter[2].setBounds(10, 55, 75, 30);
		buttonDelimiter[2].addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{

				createDataPreviewTable(",");

				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				composite.pack();
			}
		});

		buttonDelimiter[3] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[3].setText(".");
		buttonDelimiter[3].setBounds(10, 55, 75, 30);
		buttonDelimiter[3].addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{

				createDataPreviewTable(".");

				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				composite.pack();
			}
		});

		buttonDelimiter[4] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[4].setText("SPACE");
		buttonDelimiter[4].setBounds(10, 55, 75, 30);
		buttonDelimiter[4].addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{

				createDataPreviewTable(" ");

				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				composite.pack();
			}
		});

		buttonDelimiter[5] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[5].setText("Other");
		buttonDelimiter[5].setBounds(10, 55, 75, 30);
		buttonDelimiter[5].addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{

				createDataPreviewTable(" ");

				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);

				composite.pack();
			}
		});

		final Text txtCustomizedDelimiter = new Text(delimiterGroup, SWT.NONE);
		txtCustomizedDelimiter.setBounds(0, 0, 75, 30);
		txtCustomizedDelimiter.setTextLimit(1);
		txtCustomizedDelimiter.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{

				createDataPreviewTable(txtCustomizedDelimiter.getText());
				composite.pack();
			}

		});
		txtCustomizedDelimiter.addFocusListener(new FocusListener()
		{

			public void focusGained(FocusEvent e)
			{

				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(true);
			}

			public void focusLost(FocusEvent e)
			{
			}

		});

		Label lblPreview = new Label(composite, SWT.NONE);
		lblPreview.setText("Data preview:");
		lblPreview.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));

		previewTable = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		previewTable.setLinesVisible(true);
		previewTable.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 300;
		data.widthHint = 700;
		previewTable.setLayoutData(data);
	}

	private void createDataPreviewTable(final String sDelimiter)
	{

		this.sDelimiter = sDelimiter;

		// Clear table if not empty
		previewTable.removeAll();

		for (TableColumn tmpColumn : previewTable.getColumns())
		{
			tmpColumn.dispose();
		}

		// Label column
		TableColumn tmpColumn = new TableColumn(previewTable, SWT.NONE);
		tmpColumn.setText("Label");
		tmpColumn.setWidth(80);

		// Read preview table
		BufferedReader brFile;
		try
		{
			brFile = new BufferedReader(new FileReader(sFileName));

			String sLine = "";

			// Ignore unwanted header files of file
			for (int iIgnoreLineIndex = 0; iIgnoreLineIndex < iStartParseFileAtLine; iIgnoreLineIndex++)
			{
				brFile.readLine();
			}

			String sTmpNextToken = "";
			StringTokenizer tokenizer;

			// Read labels
			if ((sLine = brFile.readLine()) != null)
			{
				tokenizer = new StringTokenizer(sLine, sDelimiter, false);

				while (tokenizer.hasMoreTokens())
				{
					sTmpNextToken = tokenizer.nextToken();
					tmpColumn = new TableColumn(previewTable, SWT.NONE);
					tmpColumn.setWidth(80);
					tmpColumn.setText(sTmpNextToken);
				}
			}

			int iRowCount = 0;
			boolean bCellFilled = false;
			while ((sLine = brFile.readLine()) != null && iRowCount < MAX_PREVIEW_TABLE_ROWS)
			{
				// last flag triggers return of delimiter itself
				tokenizer = new StringTokenizer(sLine, sDelimiter, true);

				TableItem item = new TableItem(previewTable, SWT.NONE);
				int iColumnCount = 0;

				while (tokenizer.hasMoreTokens())
				{
					sTmpNextToken = tokenizer.nextToken();

					// Check for empty cells
					if (sTmpNextToken.equals(sDelimiter) && !bCellFilled)
					{
						item.setText(iColumnCount + 1, "");
						iColumnCount++;
					}
					else if (sTmpNextToken.equals(sDelimiter) && bCellFilled)
					{
						bCellFilled = false; // reset
					}
					else
					{
						bCellFilled = true;
						item.setText(iColumnCount + 1, sTmpNextToken);
						iColumnCount++;
					}
				}

				bCellFilled = false; // reset

				iRowCount++;
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException ioe)
		{
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}

		// Insert label column describing the rows' content
		// TableColumn tmpLabelColumn = new TableColumn(previewTable, SWT.NONE,
		// 0);
		// tmpLabelColumn.setText("");

		createDataClassBar();
		createDataTypeBar();
		createNormalizeBar();

		TableItem[] arTmpLabelColumnItem = previewTable.getItems();

		arTmpLabelColumnItem[0].setText(0, "Data class");
		arTmpLabelColumnItem[0].setBackground(0, previewTable.getDisplay().getSystemColor(
				SWT.COLOR_GREEN));
		arTmpLabelColumnItem[1].setText(0, "Data type");
		arTmpLabelColumnItem[1].setBackground(0, previewTable.getDisplay().getSystemColor(
				SWT.COLOR_GREEN));
		arTmpLabelColumnItem[2].setText(0, "Normalize");
		arTmpLabelColumnItem[2].setBackground(0, previewTable.getDisplay().getSystemColor(
				SWT.COLOR_GREEN));

		for (int iItemIndex = 3; iItemIndex < arTmpLabelColumnItem.length; iItemIndex++)
		{
			arTmpLabelColumnItem[iItemIndex].setText(0, "Row " + (iItemIndex - 3));
			arTmpLabelColumnItem[iItemIndex].setBackground(0, Display.getCurrent()
					.getSystemColor(SWT.COLOR_GREEN));
		}
	}

	private void createDataClassBar()
	{

		for (Combo tmpComboDataClass : arComboDataClass)
		{
			tmpComboDataClass.dispose();
		}

		arComboDataClass.clear();

		TableItem tmpItem = new TableItem(previewTable, SWT.NONE, 0);

		for (int iColIndex = 1; iColIndex < previewTable.getColumnCount(); iColIndex++)
		{
			// previewTable.getColumn (iColIndex).pack();

			// Initialize data type selection combo
			final Combo comboTmpDataClass = new Combo(previewTable, SWT.READ_ONLY);
			comboTmpDataClass.setSize(previewTable.getColumn(iColIndex).getWidth(), 35);
			comboTmpDataClass.setItems(new String[] { "SKIP", "RefSeq ID", "Experiment",
					"Patient" });
			comboTmpDataClass.select(0); // by default values in that column
											// should be ignored
			arComboDataClass.add(comboTmpDataClass);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = true;
			editor.setEditor(comboTmpDataClass, tmpItem, iColIndex);

			comboTmpDataClass.addMouseTrackListener(new MouseTrackAdapter()
			{

				public Color originalColor = txtFileName.getBackground();
				public Color highlightColor = Display.getCurrent().getSystemColor(
						SWT.COLOR_YELLOW);
				public Color selectionColor = Display.getCurrent().getSystemColor(
						SWT.COLOR_RED);

				public void mouseEnter(MouseEvent e)
				{
					// Set corresponding column background color to yellow
					for (TableItem tmpItem : previewTable.getItems())
					{
						if (comboTmpDataClass.getSelectionIndex() == 0)
							tmpItem.setBackground(
									arComboDataClass.indexOf(comboTmpDataClass) + 1,
									highlightColor);
					}
				}

				public void mouseExit(MouseEvent e)
				{
					// Set back to original color
					for (TableItem tmpItem : previewTable.getItems())
					{
						if (comboTmpDataClass.getSelectionIndex() > 0)
							tmpItem.setBackground(
									arComboDataClass.indexOf(comboTmpDataClass) + 1,
									selectionColor);
						else
							tmpItem.setBackground(
									arComboDataClass.indexOf(comboTmpDataClass) + 1,
									originalColor);
					}
				}
			});

			comboTmpDataClass.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{

					int iColIndex = arComboDataClass.indexOf(comboTmpDataClass);

					if (comboTmpDataClass.getSelectionIndex() == 0
							|| comboTmpDataClass.getSelectionIndex() == 1)
					{
						arComboDataType.get(iColIndex).setEnabled(false);
						arButtonNormalize.get(iColIndex).setEnabled(false);
						// arButtonNormalize.get(iColIndex).setSelection(false);
					}
					else
					{
						arComboDataType.get(iColIndex).setEnabled(true);
						arButtonNormalize.get(iColIndex).setEnabled(true);
						// arButtonNormalize.get(iColIndex).setSelection(true);
					}

					if (comboTmpDataClass.getText().equals("RefSeq ID"))
						arComboDataType.get(iColIndex).select(1);
					else if (comboTmpDataClass.getText().equals("Experiment"))
						arComboDataType.get(iColIndex).select(2);
				}
			});
		}
	}

	private void createDataTypeBar()
	{

		for (Combo tmpComboDataType : arComboDataType)
		{
			tmpComboDataType.dispose();
		}

		arComboDataType.clear();

		TableItem tmpItem = new TableItem(previewTable, SWT.NONE, 1);

		for (int iColIndex = 1; iColIndex < previewTable.getColumnCount(); iColIndex++)
		{
			// previewTable.getColumn (iColIndex).pack();

			// Initialize data type selection combo
			final Combo comboTmpDataType = new Combo(previewTable, SWT.READ_ONLY);
			comboTmpDataType.setSize(previewTable.getColumn(iColIndex).getWidth(), 35);
			comboTmpDataType.setEnabled(false);
			comboTmpDataType.setItems(new String[] { "SKIP", "INT", "FLOAT", "STRING" });
			comboTmpDataType.select(0); // by default values in that column
										// should be ignored
			arComboDataType.add(comboTmpDataType);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = true;
			editor.setEditor(comboTmpDataType, tmpItem, iColIndex);

			// comboTmpDataType.addMouseTrackListener(new MouseTrackAdapter() {
			//
			// public Color originalColor = txtFileName.getBackground();
			// public Color highlightColor =
			// comboTmpDataType.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
			// public Color selectionColor =
			// comboTmpDataType.getDisplay().getSystemColor(SWT.COLOR_BLUE);
			//					
			// public void mouseEnter(MouseEvent e) {
			// // Set corresponding column background color to yellow
			// for (TableItem tmpItem : previewTable.getItems())
			// {
			// if (arComcomboTmpDataType.getSelectionIndex() == 0)
			//tmpItem.setBackground(arComboDataType.indexOf(comboTmpDataType)+1,
			// highlightColor);
			// }
			// }
			//	
			// public void mouseExit(MouseEvent e) {
			// // Set back to original color
			// for (TableItem tmpItem : previewTable.getItems())
			// {
			// if (comboTmpDataType.getSelectionIndex() > 0)
			//tmpItem.setBackground(arComboDataClass.indexOf(comboTmpDataType)+1
			// , selectionColor);
			// else
			//tmpItem.setBackground(arComboDataClass.indexOf(comboTmpDataType)+1
			// , originalColor);
			// }
			// }
			// });
		}
	}

	private void createNormalizeBar()
	{

		for (Button tmpButtonNormalize : arButtonNormalize)
		{
			tmpButtonNormalize.dispose();
		}

		arButtonNormalize.clear();

		TableItem tmpItem = new TableItem(previewTable, SWT.NONE, 2);
		for (int iColIndex = 1; iColIndex < previewTable.getColumnCount(); iColIndex++)
		{
			// previewTable.getColumn (iColIndex).pack();

			// Initialize data type selection combo
			final Button buttonNormalize = new Button(previewTable, SWT.CHECK);
			buttonNormalize.setSize(previewTable.getColumn(iColIndex).getWidth(), 35);
			buttonNormalize.setEnabled(false);
			buttonNormalize.setSelection(false);
			arButtonNormalize.add(buttonNormalize);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = true;
			editor.setEditor(buttonNormalize, tmpItem, iColIndex);
		}
	}

	public void createData()
	{
		ArrayList<Integer> iAlStorageId = new ArrayList<Integer>();
		String sStorageIDs = "";

		// Build input pattern from data type combos
		sInputPattern = "";

		ArrayList<Integer> iAlTmpStorageIdNormalize = new ArrayList<Integer>();

		for (Combo tmpComboDataType : arComboDataType)
		{
			if (tmpComboDataType.getText().equals("FLOAT")
					|| tmpComboDataType.getText().equals("SKIP"))
			{
				sInputPattern = sInputPattern + tmpComboDataType.getText() + ";";
			}

			if (tmpComboDataType.getText().equals("FLOAT")) // currently we only
															// allow parsing
															// float data
			{
				// Create data storage
				CmdDataCreateStorage cmdCreateStorage = (CmdDataCreateStorage) Application.generalManager
						.getCommandManager().createCommandByType(
								CommandType.CREATE_STORAGE);

				cmdCreateStorage.setAttributes(EManagedObjectType.STORAGE_NUMERICAL);
				cmdCreateStorage.doCommand();
				
				int iTmpStorageId = cmdCreateStorage.getCreatedObject().getID();
				iAlStorageId.add(iTmpStorageId);

				if (!sStorageIDs.equals(""))
					sStorageIDs += IGeneralManager.sDelimiter_Parser_DataItems;

				sStorageIDs = sStorageIDs + iTmpStorageId;

				// Add storage to array passed on to normalization
				if (arButtonNormalize.get(arComboDataType.indexOf(tmpComboDataType))
						.getSelection())
				{
					iAlTmpStorageIdNormalize.add(iTmpStorageId);
				}
			}
		}

		sInputPattern += "ABORT;";

		sFileName = txtFileName.getText();

		if (sFileName.equals(""))
		{
			MessageDialog.openError(parentComposite.getShell(), "Invalid filename",
					"Invalid filename");
			return;
		}

		// Trigger file loading command
		CmdLoadFileNStorages cmdLoadCsv = (CmdLoadFileNStorages) Application.generalManager
				.getCommandManager().createCommandByType(CommandType.LOAD_DATA_FILE);

		ISWTGUIManager iSWTGUIManager = Application.generalManager.getSWTGUIManager();
		iSWTGUIManager.setProgressBarVisible(true);

		cmdLoadCsv.setAttributes(iAlStorageId, sFileName, sInputPattern, 0, -1);

		cmdLoadCsv.doCommand();

		// Create Virtual Array
//		CmdDataCreateVirtualArray cmdCreateVirtualArray = (CmdDataCreateVirtualArray) Application.generalManager
//				.getCommandManager().createCommandByType(
//						CommandType.CREATE_VIRTUAL_ARRAY);

//		int iTmpVirtualArrayId = Application.generalManager.getStorageManager().createId(
//				EManagerObjectType.STORAGE_NUMERICAL);
//		cmdCreateVirtualArray.setAttributes(iTmpVirtualArrayId, -1, 0, 0, 0);
//		cmdCreateVirtualArray.doCommand();

		// Create SET
		CmdDataCreateSet cmdCreateSet = (CmdDataCreateSet) Application.generalManager
				.getCommandManager().createCommandByType(CommandType.CREATE_SET_DATA);

		cmdCreateSet.setAttributes(null, iAlStorageId,
				ESetType.GENE_EXPRESSION_DATA);
		cmdCreateSet.doCommand();
		iCreatedSetID = cmdCreateSet.getCreatedObject().getID();

		iSWTGUIManager.setProgressBarVisible(false);

		CmdLoadFileLookupTable cmdLoadLookupTableFile = (CmdLoadFileLookupTable) Application.generalManager
				.getCommandManager().createCommandByType(
						CommandType.LOAD_LOOKUP_TABLE_FILE);

		cmdLoadLookupTableFile.setAttributes(sFileName, iStartParseFileAtLine, -1,
				"DAVID_2_EXPRESSION_STORAGE_ID REVERSE LUT_1", sDelimiter,
				"REFSEQ_MRNA_2_DAVID");
		cmdLoadLookupTableFile.doCommand();
	}

	public void setDataInViews()
	{

		for (GLEventListener tmpGLEventListener : Application.generalManager
				.getViewGLCanvasManager().getAllGLEventListeners())
		{
			if (tmpGLEventListener instanceof GLCanvasHeatMap
					|| tmpGLEventListener.getClass().getSuperclass().equals(
							AGLEventListenerStorageBasedView.class))
			{
				((AGLEventListenerStorageBasedView) tmpGLEventListener).addSet(iCreatedSetID);
				((AGLEventListenerStorageBasedView) tmpGLEventListener).initData();
			}
		}
	}

	/**
	 * For testing purposes
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{

		FileLoadDataDialog dialog = new FileLoadDataDialog(new Shell());
		dialog.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose()
	{

	}
}
