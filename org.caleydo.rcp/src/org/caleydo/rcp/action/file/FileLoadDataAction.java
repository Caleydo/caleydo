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
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.swt.tabular.LabelEditorDialog;
import org.caleydo.rcp.dialog.file.LoadDataDialog;
import org.caleydo.rcp.image.IImageKeys;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

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

	private static int MAX_PREVIEW_TABLE_ROWS = 50;

	private Composite composite;

	private Text txtFileName;
	private Text txtStartParseAtLine;
	private Text txtMin;
	private Text txtMax;

	private Table previewTable;

	private ArrayList<Button> arSkipColumn;
	private ArrayList<Combo> arComboDataType;

	private String sInputFile = "";
	private String sFileName = "";
	private String sFilePath = "";
	private String sInputPattern = "SKIP;ABORT";
	private String sDelimiter = "";
	private int iCreatedSetID = -1;
	private int iStartParseFileAtLine = 2;

	private String sDataRepMode = "Normal";
	// private boolean bLogFilter = false;

	private int iOldSetID;

	/**
	 * Constructor.
	 */
	public FileLoadDataAction(final Composite parentComposite) {
		super("Load Data");
		setId(ID);
		setToolTipText("Import data from text file");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.caleydo.rcp",
			IImageKeys.FILE_OPEN_XML_CONFIG_FILE));

		this.parentComposite = parentComposite;

		arSkipColumn = new ArrayList<Button>();
		arComboDataType = new ArrayList<Combo>();
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
		// // Check if load data GUI is embedded in a wizard or if a own dialog
		// // must be created.
		// if (parentComposite == null)
		// {
		// Shell shell = new Shell();
		// // shell.setMaximized(true);
		// LoadDataDialog loadDataFileDialog = new LoadDataDialog(shell);
		// loadDataFileDialog.open();
		// }
		// else
		// {
		createGUI();
		// }
	}

	private void createGUI() {
		composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Button buttonFileChooser = new Button(composite, SWT.PUSH);
		buttonFileChooser.setText("Choose data file..");

		txtFileName = new Text(composite, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

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

		Label lblStartParseAtLine = new Label(composite, SWT.NONE);
		lblStartParseAtLine.setText("Ignore lines in header:");
		lblStartParseAtLine.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		txtStartParseAtLine = new Text(composite, SWT.BORDER);
		txtStartParseAtLine.setLayoutData(new GridData(50, 15));
		txtStartParseAtLine.setText("1");
		txtStartParseAtLine.setTextLimit(2);
		txtStartParseAtLine.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				iStartParseFileAtLine = Integer.valueOf(txtStartParseAtLine.getText()).intValue();

				createDataPreviewTable("\t");
				composite.pack();
			}
		});

		Label lblDelimiter = new Label(composite, SWT.NONE);
		lblDelimiter.setText("Separated by:");
		lblDelimiter.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		Group delimiterGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		delimiterGroup.setLayout(new RowLayout());
		// delimiterGroup.setText("Delimiter (Separator)");

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

		Label lblMathFilter = new Label(composite, SWT.NONE);
		lblMathFilter.setText("Apply Filter:");
		lblMathFilter.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		Group mathFiltergGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		mathFiltergGroup.setLayout(new RowLayout());
		// delimiterGroup.setText("Math filter");

		final Combo dataRepCombo = new Combo(mathFiltergGroup, SWT.DROP_DOWN);
		String[] sArOptions = { "Normal", "Log10", "Log2" };
		dataRepCombo.setItems(sArOptions);
		dataRepCombo.setEnabled(true);
		dataRepCombo.select(0);
		dataRepCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sDataRepMode = dataRepCombo.getText();

			}
		});

		final Button buttonMin = new Button(mathFiltergGroup, SWT.CHECK);
		buttonMin.setText("Min");
		buttonMin.setEnabled(true);
		buttonMin.setSelection(false);
		buttonMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtMin.setEnabled(buttonMin.getSelection());
			}
		});

		txtMin = new Text(mathFiltergGroup, SWT.BORDER);
		txtMin.setEnabled(false);
		txtMin.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (char c : chars) {
					// TODO
					// if (!('0' <= chars[i] && chars[i] <= '9'))
					// {
					// e.doit = false;
					// return;
					// }
				}
			}
		});

		final Button buttonMax = new Button(mathFiltergGroup, SWT.CHECK);
		buttonMax.setText("Max");
		buttonMax.setEnabled(true);
		buttonMax.setSelection(false);
		buttonMax.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				txtMax.setEnabled(buttonMax.getSelection());
			}
		});

		txtMax = new Text(mathFiltergGroup, SWT.BORDER);
		txtMax.setEnabled(false);
		txtMax.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (char c : chars) {
					// TODO
					// if (!('0' <= chars[i] && chars[i] <= '9'))
					// {
					// e.doit = false;
					// return;
					// }
				}
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

		// Check if an external file name is given to the action
		if (!sInputFile.isEmpty()) {
			txtFileName.setText(sInputFile);
			sFileName = sInputFile;
			sDataRepMode = "Log10";
			dataRepCombo.select(1);

			createDataPreviewTable("\t");
		}
	}

	private void createDataPreviewTable(final String sDelimiter) {
		this.sDelimiter = sDelimiter;

		// Clear table if not empty
		previewTable.removeAll();

		for (TableColumn tmpColumn : previewTable.getColumns()) {
			tmpColumn.dispose();
		}

		final TableEditor editor = new TableEditor(previewTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// previewTable.addListener(SWT.MouseDown, new Listener()
		// {
		// public void handleEvent(Event event)
		// {
		// Rectangle clientArea = previewTable.getClientArea();
		// Point pt = new Point(event.x, event.y);
		//
		// int index = 0; // only make caption line editable
		//
		// boolean visible = false;
		// final TableItem item = previewTable.getItem(index);
		// for (int iColIndex = 1; iColIndex < previewTable.getColumnCount(); iColIndex++)
		// {
		// Rectangle rect = item.getBounds(iColIndex);
		// if (rect.contains(pt))
		// {
		// final int column = iColIndex;
		// final Text text = new Text(previewTable, SWT.NONE);
		// Listener textListener = new Listener()
		// {
		// public void handleEvent(final Event e)
		// {
		// switch (e.type)
		// {
		// case SWT.FocusOut:
		// item.setText(column, text.getText());
		// text.dispose();
		// break;
		// case SWT.Traverse:
		// switch (e.detail)
		// {
		// case SWT.TRAVERSE_RETURN:
		// item.setText(column, text.getText());
		//
		// // FALL THROUGH
		// case SWT.TRAVERSE_ESCAPE:
		// text.dispose();
		// e.doit = false;
		// }
		// break;
		// }
		// }
		// };
		//
		// text.addListener(SWT.FocusOut, textListener);
		// text.addListener(SWT.Traverse, textListener);
		// editor.setEditor(text, item, iColIndex);
		// text.setText(item.getText(iColIndex));
		// text.selectAll();
		// text.setFocus();
		// return;
		// }
		//
		// if (!visible && rect.intersects(clientArea))
		// {
		// visible = true;
		// }
		// }
		//
		// if (!visible)
		// return;
		// index++;
		// }
		// });

		// Read preview table
		BufferedReader brFile;
		try {
			brFile = GeneralManager.get().getResourceLoader().getResource(sFileName);

			String sLine = "";

			// Ignore unwanted header files of file
			for (int iIgnoreLineIndex = 2; iIgnoreLineIndex < iStartParseFileAtLine; iIgnoreLineIndex++) {
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

			int iRowCount = 0;
			boolean bCellFilled = false;

			while ((sLine = brFile.readLine()) != null && iRowCount < MAX_PREVIEW_TABLE_ROWS) {
				// last flag triggers return of delimiter itself
				tokenizer = new StringTokenizer(sLine, sDelimiter, true);
				item = new TableItem(previewTable, SWT.NONE);
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

		createDataClassBar();
		createDataTypeBar();

		TableItem[] arTmpLabelColumnItem = previewTable.getItems();
		arTmpLabelColumnItem[0].setText(0, "Use column");
		arTmpLabelColumnItem[1].setText(0, "Data type");

		for (int iItemIndex = 2; iItemIndex < arTmpLabelColumnItem.length; iItemIndex++) {
			arTmpLabelColumnItem[iItemIndex].setText(0, "Row " + (iItemIndex - 1));
		}

		// for (TableColumn column : previewTable.getColumns())
		// column.pack();

		// previewTable.pack();
		// composite.pack();
	}

	private void createDataClassBar() {
		TableItem tmpItem = new TableItem(previewTable, SWT.NONE, 1);
		Button skipButton;
		for (int iColIndex = 2; iColIndex < previewTable.getColumnCount(); iColIndex++) {
			tmpItem = previewTable.getItem(0);
			tmpItem.setText("");

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

			// previewTable.getColumn(iColIndex).pack();

			// Initialize data type selection combo
			// final Combo comboTmpDataClass = new Combo(previewTable, SWT.READ_ONLY);
			// comboTmpDataClass.setSize(previewTable.getColumn(iColIndex).getWidth(), 35);
			// comboTmpDataClass.setItems(new String[] { "SKIP", "RefSeq ID", "Experiment", "Patient" });

			// if (iColIndex == 1)
			// comboTmpDataClass.select(1);
			// else
			// comboTmpDataClass.select(2); // by default set columns to experiment

			// should be ignored
			// arComboDataClass.add(comboTmpDataClass);

			// TableEditor editor = new TableEditor(previewTable);
			// editor.grabHorizontal = true;
			// editor.setEditor(comboTmpDataClass, tmpItem, iColIndex);

			// // Set corresponding column background color to red
			// for (TableItem tmpTableItem : previewTable.getItems())
			// {
			// tmpTableItem.setBackground(arComboDataClass.indexOf(comboTmpDataClass) + 1,
			// Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			// tmpTableItem
			// }

			// comboTmpDataClass.addMouseTrackListener(new MouseTrackAdapter()
			// {
			// public Color originalColor = txtFileName.getBackground();
			// public Color highlightColor = Display.getCurrent().getSystemColor(
			// SWT.COLOR_YELLOW);
			// public Color selectionColor = Display.getCurrent().getSystemColor(
			// SWT.COLOR_RED);
			//
			// @Override
			// public void mouseEnter(MouseEvent e)
			// {
			// // Set corresponding column background color to yellow
			// for (TableItem tmpItem : previewTable.getItems())
			// {
			// if (comboTmpDataClass.getSelectionIndex() == 0)
			// {
			// tmpItem.setBackground(
			// arComboDataClass.indexOf(comboTmpDataClass) + 1,
			// highlightColor);
			// }
			// }
			// }
			//
			// @Override
			// public void mouseExit(MouseEvent e)
			// {
			// // Set back to original color
			// for (TableItem tmpItem : previewTable.getItems())
			// {
			// if (comboTmpDataClass.getSelectionIndex() > 0)
			// {
			// tmpItem.setBackground(
			// arComboDataClass.indexOf(comboTmpDataClass) + 1,
			// selectionColor);
			// }
			// else
			// {
			// tmpItem.setBackground(
			// arComboDataClass.indexOf(comboTmpDataClass) + 1,
			// originalColor);
			// }
			// }
			// }
			// });
			//
			// comboTmpDataClass.addSelectionListener(new SelectionAdapter()
			// {
			//
			// @Override
			// public void widgetSelected(SelectionEvent e)
			// {
			//
			// int iColIndex = arComboDataClass.indexOf(comboTmpDataClass);
			//
			// if (comboTmpDataClass.getSelectionIndex() == 0
			// || comboTmpDataClass.getSelectionIndex() == 1)
			// {
			// arComboDataType.get(iColIndex).setEnabled(false);
			// arComboDataType.get(iColIndex).select(0);
			// // arButtonNormalize.get(iColIndex).setSelection(false);
			// }
			// else
			// {
			// arComboDataType.get(iColIndex).setEnabled(true);
			// // arButtonNormalize.get(iColIndex).setSelection(true);
			// }
			//
			// if (comboTmpDataClass.getText().equals("RefSeq ID"))
			// arComboDataType.get(iColIndex).select(1);
			// else if (comboTmpDataClass.getText().equals("Experiment"))
			// arComboDataType.get(iColIndex).select(2);
			// }
			// });
		}
	}

	private void createDataTypeBar() {

		for (Combo tmpComboDataType : arComboDataType) {
			tmpComboDataType.dispose();
		}

		arComboDataType.clear();

		TableItem tmpItem = new TableItem(previewTable, SWT.NONE, 0);

		for (int iColIndex = 2; iColIndex < previewTable.getColumnCount(); iColIndex++) {
			// previewTable.getColumn (iColIndex).pack();

			// Initialize data type selection combo
			final Combo comboTmpDataType = new Combo(previewTable, SWT.READ_ONLY);
			// comboTmpDataType.setSize(100, previewTable.getItemHeight());
			comboTmpDataType.setEnabled(true);
			comboTmpDataType.setItems(new String[] { "INT", "FLOAT", "STRING" });
			comboTmpDataType.computeSize(SWT.DEFAULT, previewTable.getItemHeight());

			comboTmpDataType.select(1);

			// should be ignored
			arComboDataType.add(comboTmpDataType);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = true;
			editor.minimumHeight = comboTmpDataType.getSize().y;
			editor.minimumWidth = comboTmpDataType.getSize().x;
			editor.setEditor(comboTmpDataType, tmpItem, iColIndex);

			// previewTable.getColumn(iColIndex).pack();
		}

		// previewTable.pack();
	}

	public void execute() {
		for (ISet set : GeneralManager.get().getSetManager().getAllItems()) {
			if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
				iOldSetID = set.getID();
				break;
			}
		}

		createData();
		setDataInViews();
		clearOldData();

		// TODO: review
		// Application.applicationMode = EApplicationMode.STANDARD;
	}

	private void createData() {
		ArrayList<Integer> iAlStorageId = new ArrayList<Integer>();
		String sStorageIDs = "";

		// Build input pattern from data type combos
		sInputPattern = sInputPattern + "SKIP" + ";";

		Combo tmpComboDataType;
		for (int iColIndex = 2; iColIndex < arComboDataType.size(); iColIndex++) {
			tmpComboDataType = arComboDataType.get(iColIndex);

			if (!arSkipColumn.get(iColIndex).getSelection()) {
				sInputPattern = sInputPattern + "SKIP" + ";";
				continue;
			}

			if (tmpComboDataType.getText().equals("FLOAT") || tmpComboDataType.getText().equals("SKIP")) {
				sInputPattern = sInputPattern + tmpComboDataType.getText() + ";";
			}

			if (tmpComboDataType.getText().equals("FLOAT")) // currently we only allow parsing float data
			{
				// Create data storage
				CmdDataCreateStorage cmdCreateStorage =
					(CmdDataCreateStorage) GeneralManager.get().getCommandManager().createCommandByType(
						ECommandType.CREATE_STORAGE);

				cmdCreateStorage.setAttributes(EManagedObjectType.STORAGE_NUMERICAL);
				cmdCreateStorage.doCommand();

				INumericalStorage storage = (INumericalStorage) cmdCreateStorage.getCreatedObject();

				storage.setLabel(previewTable.getColumn(iColIndex).getText());

				iAlStorageId.add(storage.getID());

				if (!sStorageIDs.equals("")) {
					sStorageIDs += IGeneralManager.sDelimiter_Parser_DataItems;
				}

				sStorageIDs = sStorageIDs + storage.getID();

			}
		}

		sInputPattern += "ABORT;";

		sFileName = txtFileName.getText();

		if (sFileName.equals("")) {
			MessageDialog.openError(parentComposite.getShell(), "Invalid filename", "Invalid filename");
			return;
		}

		// Trigger file loading command
		CmdLoadFileNStorages cmdLoadCsv =
			(CmdLoadFileNStorages) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.LOAD_DATA_FILE);

		// ISWTGUIManager iSWTGUIManager =
		// GeneralManager.get().getSWTGUIManager();
		// iSWTGUIManager.setProgressBarVisible(true);

		cmdLoadCsv.setAttributes(iAlStorageId, sFileName, sInputPattern, sDelimiter,
			iStartParseFileAtLine - 1, -1);
		cmdLoadCsv.doCommand();

		// Create SET
		CmdDataCreateSet cmdCreateSet =
			(CmdDataCreateSet) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.CREATE_SET_DATA);
		cmdCreateSet.setAttributes(iAlStorageId, ESetType.GENE_EXPRESSION_DATA);
		cmdCreateSet.doCommand();
		ISet set = cmdCreateSet.getCreatedObject();
		iCreatedSetID = set.getID();

		// iSWTGUIManager.setProgressBarVisible(false);

		CmdLoadFileLookupTable cmdLoadLookupTableFile =
			(CmdLoadFileLookupTable) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.LOAD_LOOKUP_TABLE_FILE);

		cmdLoadLookupTableFile.setAttributes(sFileName, iStartParseFileAtLine, -1,
			"REFSEQ_MRNA_2_EXPRESSION_INDEX REVERSE LUT", sDelimiter, "REFSEQ_MRNA_INT_2_EXPRESSION_INDEX");
		cmdLoadLookupTableFile.doCommand();

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

		if (sDataRepMode.equals("Normal")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.NORMAL, true);
		}
		else if (sDataRepMode.equals("Log10")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG10, true);
		}
		else if (sDataRepMode.equals("Log2")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG2, true);
		}
		else
			throw new IllegalStateException("Unknown data representation type");
	}

	private void setDataInViews() {
		for (AGLEventListener tmpGLEventListener : GeneralManager.get().getViewGLCanvasManager()
			.getAllGLEventListeners()) {
			tmpGLEventListener.clearSets();
			tmpGLEventListener.addSet(iCreatedSetID);

			if (tmpGLEventListener.getClass().getSuperclass().equals(AStorageBasedView.class)) {
				((AStorageBasedView) tmpGLEventListener).initData();
			}
		}
	}

	private void clearOldData() {
		GeneralManager.get().getSetManager().unregisterItem(iOldSetID);
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
