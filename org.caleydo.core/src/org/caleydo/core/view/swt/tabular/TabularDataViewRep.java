package org.caleydo.core.view.swt.tabular;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateSet;
import org.caleydo.core.command.data.CmdDataCreateStorage;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNStorages;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Simple HTML browser.
 * 
 * @author Marc Streit
 */
public class TabularDataViewRep
	extends AView
	implements IView, IMediatorReceiver, IMediatorSender
{
	private static int MAX_PREVIEW_TABLE_ROWS = 50;

	private Composite composite;

	private Text txtFileName;
	private Text txtStartParseAtLine;
	private Text txtMin;
	private Text txtMax;

	private Table previewTable;

	private ArrayList<Combo> arComboDataClass;
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
	public TabularDataViewRep(final int iParentContainerId, final String sLabel)
	{
		super(iParentContainerId, sLabel, ViewType.SWT_TABULAR_DATA_VIEWER);

		arComboDataClass = new ArrayList<Combo>();
		arComboDataType = new ArrayList<Combo>();
	}

	public void setInputFile(String sInputFile)
	{
		this.sInputFile = sInputFile;
	}

	@Override
	protected void initViewSwtComposite(Composite parent)
	{
		final Composite highLevelComposite = new Composite(parent, SWT.NONE);
		highLevelComposite.setLayout(new GridLayout(1, false));

		GridData gridData = new GridData(GridData.FILL_BOTH);

		Composite upperComposite = new Composite(highLevelComposite, SWT.NONE);
		upperComposite.setLayout(new FillLayout());
		upperComposite.setLayoutData(gridData);

		Button applyButton = new Button(highLevelComposite, SWT.PUSH);
		applyButton.setText("Apply");

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.heightHint = 30;
		gridData.widthHint = 500;

		applyButton.setLayoutData(gridData);
		applyButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				execute();
			}
		});

		composite = new Composite(upperComposite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Button buttonFileChooser = new Button(composite, SWT.PUSH);
		buttonFileChooser.setText("Choose data file..");

		txtFileName = new Text(composite, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		buttonFileChooser.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent event)
			{

				FileDialog fileDialog = new FileDialog(composite.getShell());
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
		lblStartParseAtLine.setLayoutData(new GridData(GridData.END, GridData.BEGINNING,
				false, false));

		txtStartParseAtLine = new Text(composite, SWT.BORDER);
		txtStartParseAtLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
				false));
		txtStartParseAtLine.setText("1");
		txtStartParseAtLine.setTextLimit(2);
		txtStartParseAtLine.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				iStartParseFileAtLine = Integer.valueOf(txtStartParseAtLine.getText()).intValue();

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
		buttonDelimiter[0].addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable("\t");

				composite.pack();
			}
		});

		buttonDelimiter[1] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[1].setText(";");
		buttonDelimiter[1].setBounds(10, 30, 75, 30);
		buttonDelimiter[1].addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(";");

				composite.pack();
			}
		});

		buttonDelimiter[2] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[2].setText(",");
		buttonDelimiter[2].setBounds(10, 55, 75, 30);
		buttonDelimiter[2].addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(",");

				composite.pack();
			}
		});

		buttonDelimiter[3] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[3].setText(".");
		buttonDelimiter[3].setBounds(10, 55, 75, 30);
		buttonDelimiter[3].addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(".");

				composite.pack();
			}
		});

		buttonDelimiter[4] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[4].setText("SPACE");
		buttonDelimiter[4].setBounds(10, 55, 75, 30);
		buttonDelimiter[4].addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[5].setSelection(false);

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(" ");

				composite.pack();
			}
		});

		buttonDelimiter[5] = new Button(delimiterGroup, SWT.CHECK);
		buttonDelimiter[5].setText("Other");
		buttonDelimiter[5].setBounds(10, 55, 75, 30);
		buttonDelimiter[5].addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);

				if (sFileName.isEmpty())
					return;

				createDataPreviewTable(" ");

				composite.pack();
			}
		});

		final Text txtCustomizedDelimiter = new Text(delimiterGroup, SWT.NONE);
		txtCustomizedDelimiter.setBounds(0, 0, 75, 30);
		txtCustomizedDelimiter.setTextLimit(1);
		txtCustomizedDelimiter.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{

				createDataPreviewTable(txtCustomizedDelimiter.getText());
				composite.pack();
			}

		});
		txtCustomizedDelimiter.addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent e)
			{

				buttonDelimiter[0].setSelection(false);
				buttonDelimiter[1].setSelection(false);
				buttonDelimiter[2].setSelection(false);
				buttonDelimiter[3].setSelection(false);
				buttonDelimiter[4].setSelection(false);
				buttonDelimiter[5].setSelection(true);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
			}

		});

		Label lblMathFilter = new Label(composite, SWT.NONE);
		lblMathFilter.setText("Apply Filter:");
		lblMathFilter.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false,
				false));

		Group mathFiltergGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		mathFiltergGroup.setLayout(new FillLayout());
		// delimiterGroup.setText("Math filter");

		final Combo dataRepCombo = new Combo(mathFiltergGroup, SWT.DROP_DOWN);
		String[] sArOptions = { "Normal", "Log10", "Log2" };
		dataRepCombo.setItems(sArOptions);
		dataRepCombo.setEnabled(true);
		dataRepCombo.select(0);
		dataRepCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				sDataRepMode = dataRepCombo.getText();

			}
		});

		final Button buttonMin = new Button(mathFiltergGroup, SWT.CHECK);
		buttonMin.setText("Min");
		buttonMin.setEnabled(true);
		buttonMin.setSelection(false);
		buttonMin.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				txtMin.setEnabled(true);
			}
		});

		txtMin = new Text(mathFiltergGroup, SWT.BORDER);
		txtMin.setEnabled(false);
		txtMin.addListener(SWT.Verify, new Listener()
		{
			public void handleEvent(Event e)
			{
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++)
				{
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
		buttonMax.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				txtMax.setEnabled(true);
			}
		});

		txtMax = new Text(mathFiltergGroup, SWT.BORDER);
		txtMax.setEnabled(false);
		txtMax.addListener(SWT.Verify, new Listener()
		{
			public void handleEvent(Event e)
			{
				// Only allow digits
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++)
				{
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
		previewTable.setLinesVisible(false);
		// previewTable.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 300;
		data.widthHint = 700;
		previewTable.setLayoutData(data);

		// Check if an external file name is given to the action
		if (!sInputFile.isEmpty())
		{
			txtFileName.setText(sInputFile);
			sFileName = sInputFile;
			sDataRepMode = "Log10";
			dataRepCombo.select(1);

			createDataPreviewTable("\t");
		}
	}

	public void drawView()
	{

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

		final TableEditor editor = new TableEditor(previewTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		previewTable.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub
				super.widgetSelected(e);

				String sSelection = e.item.toString();
				Integer iSelectedRowIndex = new Integer(sSelection.substring(sSelection
						.lastIndexOf(' ') + 1, sSelection.lastIndexOf('}')));

				Integer iDavidID = GeneralManager.get().getIDMappingManager().getID(
						EMappingType.EXPRESSION_INDEX_2_DAVID, iSelectedRowIndex);

				if (iDavidID == null || iDavidID == -1)
					return;

				SelectionDelta tmpDelta = new SelectionDelta(EIDType.DAVID);
				tmpDelta.addSelection(iDavidID, ESelectionType.MOUSE_OVER);

				Collection<SelectionCommand> colSelectionCommand = new ArrayList<SelectionCommand>();
				colSelectionCommand.add(new SelectionCommand(ESelectionCommandType.CLEAR,
						ESelectionType.MOUSE_OVER));

				triggerUpdate(EMediatorType.SELECTION_MEDIATOR, tmpDelta, colSelectionCommand);
			}
		});

		previewTable.addListener(SWT.MouseDown, new Listener()
		{
			public void handleEvent(Event event)
			{
				Rectangle clientArea = previewTable.getClientArea();
				Point pt = new Point(event.x, event.y);

				int index = 0; // only make caption line editable

				boolean visible = false;
				final TableItem item = previewTable.getItem(index);
				for (int iColIndex = 1; iColIndex < previewTable.getColumnCount(); iColIndex++)
				{
					Rectangle rect = item.getBounds(iColIndex);
					if (rect.contains(pt))
					{
						final int column = iColIndex;
						final Text text = new Text(previewTable, SWT.NONE);
						Listener textListener = new Listener()
						{
							public void handleEvent(final Event e)
							{
								switch (e.type)
								{
									case SWT.FocusOut:
										item.setText(column, text.getText());
										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail)
										{
											case SWT.TRAVERSE_RETURN:
												item.setText(column, text.getText());

												// FALL THROUGH
											case SWT.TRAVERSE_ESCAPE:
												text.dispose();
												e.doit = false;
										}
										break;
								}
							}
						};

						text.addListener(SWT.FocusOut, textListener);
						text.addListener(SWT.Traverse, textListener);
						editor.setEditor(text, item, iColIndex);
						text.setText(item.getText(iColIndex));
						text.selectAll();
						text.setFocus();
						return;
					}

					if (!visible && rect.intersects(clientArea))
					{
						visible = true;
					}
				}

				if (!visible)
					return;
				index++;
			}
		});

		// Read preview table
		BufferedReader brFile;
		try
		{
			brFile = GeneralManager.get().getResourceLoader().getResource(sFileName);

			String sLine = "";

			// Ignore unwanted header files of file
			for (int iIgnoreLineIndex = 2; iIgnoreLineIndex < iStartParseFileAtLine; iIgnoreLineIndex++)
			{
				brFile.readLine();
			}

			String sTmpNextToken = "";
			StringTokenizer tokenizer;
			TableColumn column;
			TableItem item;
			int iColIndex = 0;

			// Read labels
			if ((sLine = brFile.readLine()) != null)
			{
				tokenizer = new StringTokenizer(sLine, sDelimiter, false);
				item = new TableItem(previewTable, SWT.NONE);

				item.setText(0, "Label");
				column = new TableColumn(previewTable, SWT.NONE);
				column.setWidth(100);

				while (tokenizer.hasMoreTokens())
				{
					sTmpNextToken = tokenizer.nextToken();

					column = new TableColumn(previewTable, SWT.NONE);
					column.setWidth(100);

					item.setText(iColIndex + 1, sTmpNextToken);
					// item.setBackground(iColCount, Display.getCurrent()
					// .getSystemColor(SWT.COLOR_TITLE_BACKGROUND));

					iColIndex++;
				}
			}

			int iRowCount = 0;
			boolean bCellFilled = false;

			while ((sLine = brFile.readLine()) != null && iRowCount < MAX_PREVIEW_TABLE_ROWS)
			{
				// last flag triggers return of delimiter itself
				tokenizer = new StringTokenizer(sLine, sDelimiter, true);
				item = new TableItem(previewTable, SWT.NONE);
				iColIndex = 0;

				while (tokenizer.hasMoreTokens())
				{
					sTmpNextToken = tokenizer.nextToken();

					// Check for empty cells
					if (sTmpNextToken.equals(sDelimiter) && !bCellFilled)
					{
						item.setText(iColIndex + 1, "");
						iColIndex++;
					}
					else if (sTmpNextToken.equals(sDelimiter) && bCellFilled)
					{
						bCellFilled = false; // reset
					}
					else
					{
						bCellFilled = true;
						item.setText(iColIndex + 1, sTmpNextToken);
						iColIndex++;
					}
				}

				bCellFilled = false; // reset

				iRowCount++;
			}
		}
		catch (FileNotFoundException e)
		{
			throw new IllegalStateException("File not found!");
		}
		catch (IOException ioe)
		{
			throw new IllegalStateException("Input/output problem!");
		}

		createDataClassBar();
		createDataTypeBar();

		TableItem[] arTmpLabelColumnItem = previewTable.getItems();

		// arTmpLabelColumnItem[0].setText(0, "Label");
		arTmpLabelColumnItem[0].setBackground(0, previewTable.getDisplay().getSystemColor(
				SWT.COLOR_TITLE_BACKGROUND));
		arTmpLabelColumnItem[1].setText(0, "Data class");
		arTmpLabelColumnItem[1].setBackground(0, previewTable.getDisplay().getSystemColor(
				SWT.COLOR_TITLE_BACKGROUND));
		arTmpLabelColumnItem[2].setText(0, "Data type");
		arTmpLabelColumnItem[2].setBackground(0, previewTable.getDisplay().getSystemColor(
				SWT.COLOR_TITLE_BACKGROUND));

		for (int iItemIndex = 3; iItemIndex < arTmpLabelColumnItem.length; iItemIndex++)
		{
			arTmpLabelColumnItem[iItemIndex].setText(0, "Row " + (iItemIndex - 2));
			arTmpLabelColumnItem[iItemIndex].setBackground(0, Display.getCurrent()
					.getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		}
	}

	private void createDataClassBar()
	{

		for (Combo tmpComboDataClass : arComboDataClass)
		{
			tmpComboDataClass.dispose();
		}

		arComboDataClass.clear();

		TableItem tmpItem = new TableItem(previewTable, SWT.NONE, 1);

		for (int iColIndex = 1; iColIndex < previewTable.getColumnCount(); iColIndex++)
		{
			// previewTable.getColumn (iColIndex).pack();

			// Initialize data type selection combo
			final Combo comboTmpDataClass = new Combo(previewTable, SWT.READ_ONLY);
			comboTmpDataClass.setSize(previewTable.getColumn(iColIndex).getWidth(), 35);
			comboTmpDataClass.setItems(new String[] { "SKIP", "RefSeq ID", "Experiment",
					"Patient" });

			if (iColIndex == 1)
				comboTmpDataClass.select(1);
			else
				comboTmpDataClass.select(2); // by default set columns to
			// experiment

			// should be ignored
			arComboDataClass.add(comboTmpDataClass);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = true;
			editor.setEditor(comboTmpDataClass, tmpItem, iColIndex);

			// Set corresponding column background color to yellow
			for (TableItem tmpTableItem : previewTable.getItems())
			{
				tmpTableItem.setBackground(arComboDataClass.indexOf(comboTmpDataClass) + 1,
						Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			}

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
						{
							tmpItem.setBackground(
									arComboDataClass.indexOf(comboTmpDataClass) + 1,
									highlightColor);
						}
					}
				}

				public void mouseExit(MouseEvent e)
				{
					// Set back to original color
					for (TableItem tmpItem : previewTable.getItems())
					{
						if (comboTmpDataClass.getSelectionIndex() > 0)
						{
							tmpItem.setBackground(
									arComboDataClass.indexOf(comboTmpDataClass) + 1,
									selectionColor);
						}
						else
						{
							tmpItem.setBackground(
									arComboDataClass.indexOf(comboTmpDataClass) + 1,
									originalColor);
						}
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
						arComboDataType.get(iColIndex).select(0);
						// arButtonNormalize.get(iColIndex).setSelection(false);
					}
					else
					{
						arComboDataType.get(iColIndex).setEnabled(true);
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

		TableItem tmpItem = new TableItem(previewTable, SWT.NONE, 2);

		for (int iColIndex = 1; iColIndex < previewTable.getColumnCount(); iColIndex++)
		{
			// previewTable.getColumn (iColIndex).pack();

			// Initialize data type selection combo
			final Combo comboTmpDataType = new Combo(previewTable, SWT.READ_ONLY);
			comboTmpDataType.setSize(previewTable.getColumn(iColIndex).getWidth(), 35);
			comboTmpDataType.setEnabled(false);
			comboTmpDataType.setItems(new String[] { "SKIP", "INT", "FLOAT", "STRING" });

			if (iColIndex == 1)
				comboTmpDataType.select(0);
			else
				comboTmpDataType.select(2); // by default set columns to
			// experiment

			// should be ignored
			arComboDataType.add(comboTmpDataType);

			TableEditor editor = new TableEditor(previewTable);
			editor.grabHorizontal = true;
			editor.setEditor(comboTmpDataType, tmpItem, iColIndex);
		}
	}

	public void execute()
	{
		for (ISet set : GeneralManager.get().getSetManager().getAllItems())
		{
			if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA)
			{
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

	private void createData()
	{
		ArrayList<Integer> iAlStorageId = new ArrayList<Integer>();
		String sStorageIDs = "";

		// Build input pattern from data type combos
		sInputPattern = "";

		Combo tmpComboDataType;
		for (int iColIndex = 0; iColIndex < arComboDataType.size(); iColIndex++)
		{
			tmpComboDataType = arComboDataType.get(iColIndex);

			if (arComboDataClass.get(iColIndex).getText().equals("RefSeq ID"))
			{
				sInputPattern = sInputPattern + "SKIP" + ";";
				continue;
			}

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
				CmdDataCreateStorage cmdCreateStorage = (CmdDataCreateStorage) GeneralManager
						.get().getCommandManager().createCommandByType(
								ECommandType.CREATE_STORAGE);

				cmdCreateStorage.setAttributes(EManagedObjectType.STORAGE_NUMERICAL);
				cmdCreateStorage.doCommand();

				INumericalStorage storage = (INumericalStorage) cmdCreateStorage
						.getCreatedObject();

				storage.setLabel(previewTable.getItem(0).getText(iColIndex + 1));

				iAlStorageId.add(storage.getID());

				if (!sStorageIDs.equals(""))
					sStorageIDs += IGeneralManager.sDelimiter_Parser_DataItems;

				sStorageIDs = sStorageIDs + storage.getID();

			}
		}

		sInputPattern += "ABORT;";

		sFileName = txtFileName.getText();

		if (sFileName.equals(""))
		{
			MessageDialog.openError(composite.getShell(), "Invalid filename",
					"Invalid filename");
			return;
		}

		// Trigger file loading command
		CmdLoadFileNStorages cmdLoadCsv = (CmdLoadFileNStorages) GeneralManager.get()
				.getCommandManager().createCommandByType(ECommandType.LOAD_DATA_FILE);

		// ISWTGUIManager iSWTGUIManager =
		// GeneralManager.get().getSWTGUIManager();
		// iSWTGUIManager.setProgressBarVisible(true);

		cmdLoadCsv.setAttributes(iAlStorageId, sFileName, sInputPattern, sDelimiter,
				iStartParseFileAtLine - 1, -1);
		cmdLoadCsv.doCommand();

		// Create SET
		CmdDataCreateSet cmdCreateSet = (CmdDataCreateSet) GeneralManager.get()
				.getCommandManager().createCommandByType(ECommandType.CREATE_SET_DATA);
		cmdCreateSet.setAttributes(null, iAlStorageId, ESetType.GENE_EXPRESSION_DATA);
		cmdCreateSet.doCommand();
		ISet set = cmdCreateSet.getCreatedObject();
		iCreatedSetID = set.getID();

		// iSWTGUIManager.setProgressBarVisible(false);

		CmdLoadFileLookupTable cmdLoadLookupTableFile = (CmdLoadFileLookupTable) GeneralManager
				.get().getCommandManager().createCommandByType(
						ECommandType.LOAD_LOOKUP_TABLE_FILE);

		cmdLoadLookupTableFile.setAttributes(sFileName, iStartParseFileAtLine, -1,
				"REFSEQ_MRNA_2_EXPRESSION_INDEX REVERSE LUT_1", sDelimiter,
				"DAVID_2_EXPRESSION_INDEX");
		cmdLoadLookupTableFile.doCommand();

		if (!txtMin.getText().isEmpty())
		{
			float fMin = Float.parseFloat(txtMin.getText());
			if (!Float.isNaN(fMin))
			{
				set.setMin(fMin);
			}

		}

		if (!txtMax.getText().isEmpty())
		{
			float fMax = Float.parseFloat(txtMax.getText());
			if (!Float.isNaN(fMax))
			{
				set.setMax(fMax);
			}
		}

		// TODO only ok for homogeneous sets (meaning only sets with the same
		// data type and range)
		if (sDataRepMode.equals("Normal"))
		{
			set.setExternalDataRepresentation(EExternalDataRepresentation.NORMAL, true);
		}
		else if (sDataRepMode.equals("Log10"))
		{
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG10, true);
		}
		else if (sDataRepMode.equals("Log2"))
		{
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG2, true);
		}
		else
		{
			throw new IllegalStateException("Unknown data representation type");
		}
	}

	private void setDataInViews()
	{
		for (AGLEventListener tmpGLEventListener : GeneralManager.get()
				.getViewGLCanvasManager().getAllGLEventListeners())
		{
			tmpGLEventListener.clearSets();
			tmpGLEventListener.addSet(iCreatedSetID);

			if (tmpGLEventListener.getClass().getSuperclass().equals(AStorageBasedView.class))
			{
				((AStorageBasedView) tmpGLEventListener).initData();
			}
		}
	}

	private void clearOldData()
	{
		GeneralManager.get().getSetManager().unregisterItem(iOldSetID);
	}

	@Override
	public void triggerUpdate(EMediatorType eMediatorType, ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand)
	{
		GeneralManager.get().getEventPublisher().triggerUpdate(eMediatorType, this,
				selectionDelta, colSelectionCommand);
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand, EMediatorType mediatorType)
	{

	}
}
