package org.caleydo.core.view.swt.data;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.IVirtualArrayManager;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.AViewRep;
import org.caleydo.core.view.ViewType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class DataTableViewRep 
extends AViewRep 
implements IDataTableView {
	
	private static final int MAX_TABLE_ROWS = 15;
	
	protected IStorageManager storageManager;

	protected IVirtualArrayManager virtualArrayManager;

	protected IStorage[] allStorageItems;

	protected IStorage currentStorage;

	protected IVirtualArray currentSelection;

	protected Table table;
	
	protected Button previousPageButton;
	
	protected Button nextPageButton;
	
	protected int iCurrentlyRequestedCollectionId;
	
	protected boolean bTriggeredUpdateFlag;
	
	protected int iCurrentTablePage;

	public DataTableViewRep(IGeneralManager generalManager, 
			int iParentId) {
		
		super(generalManager,
				-1, 
				iParentId,
				"",
				ViewType.SWT_DATA_TABLE);

		bTriggeredUpdateFlag = false;
		initViewSwtComposit(null);
		
		//initView();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.AViewRep#initView(org.eclipse.swt.widgets.Composite)
	 */
	public void initViewSwtComposit( Composite swtContainer ) {
		
		storageManager = generalManager.getStorageManager();
		virtualArrayManager = generalManager.getVirtualArrayManager();
	}

	public void drawView() {
		
		// not implemented in this class
	}

	public void initTable() {

		Composite dataTableComposite = new Composite(swtContainer, SWT.NONE);
 		dataTableComposite.setLayout(new RowLayout());
				
		table = new Table(dataTableComposite, SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		//table.setSize(300, 400);
		table.setLayoutData(new RowData(300, 400));

		// Paging: previous
		previousPageButton = new Button(dataTableComposite, SWT.BOTTOM | SWT.PUSH);
		previousPageButton.setText("<");
		previousPageButton.setEnabled(false);
		previousPageButton.setLayoutData(new RowData(50, 40));
		previousPageButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				iCurrentTablePage--;
				
				if (iCurrentTablePage == 0)
				{
					previousPageButton.setEnabled(false);
				}
					
				drawStorageTable();
			}
		});
		
		// Paging: next
		nextPageButton = new Button(dataTableComposite, SWT.BOTTOM | SWT.PUSH);
		nextPageButton.setText(">");
		nextPageButton.setEnabled(false);
		nextPageButton.setLayoutData(new RowData(50, 40));
		nextPageButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				iCurrentTablePage++;
				previousPageButton.setEnabled(true);				
				drawStorageTable();
			}
		});
		
		initTableEditor();
	}

	public void createStorageTable(int iRequestedStorageId) {
		
		currentStorage = storageManager
			.getItemStorage(iRequestedStorageId);
		
		// Reset paging
		iCurrentTablePage = 0;
		previousPageButton.setEnabled(false);
		nextPageButton.setEnabled(false);
		
		drawStorageTable();
	}
	
	protected void drawStorageTable() {
		
		TableItem item;
		TableColumn column;

		int iTableColumnIndex = 0;
		int iTmpNumberOfDataItems = 0;	
		int iNumberOfTableItems = currentStorage.getMaximumLengthOfAllArrays();
		int iStartItemIndex = iCurrentTablePage * MAX_TABLE_ROWS;
		
		reinitializeTable();
		
		int iNumberOfTableItemsToLoad = iNumberOfTableItems;
		
		if (iNumberOfTableItems > MAX_TABLE_ROWS)
		{
			iNumberOfTableItemsToLoad = MAX_TABLE_ROWS;
			nextPageButton.setEnabled(true);
		}

		for (int iTableRowIndex = 0; iTableRowIndex < iNumberOfTableItemsToLoad; iTableRowIndex++)
		{
			new TableItem(table, SWT.NONE);
		}

		if (currentStorage.getSize(StorageType.INT) > 1)
		{
			int[] intData = currentStorage.getArrayInt();

			column = new TableColumn(table, SWT.NONE);
			column.setText("Int data");
			
			if (intData.length > iNumberOfTableItemsToLoad)
			{
				if (iCurrentTablePage >= 
					(iNumberOfTableItems / (float)MAX_TABLE_ROWS - 1))
				{
					iTmpNumberOfDataItems = iNumberOfTableItems % MAX_TABLE_ROWS;
				}
				else 
					iTmpNumberOfDataItems = iNumberOfTableItemsToLoad;
			}
			else
			{
				iTmpNumberOfDataItems = intData.length;
			}
				
			for (int dataIndex = 0; dataIndex < iTmpNumberOfDataItems; dataIndex++)
			{
				item = table.getItem(dataIndex);
				item.setText(iTableColumnIndex, Float
						.toString(intData[iStartItemIndex + dataIndex]));
			}

			column.pack();
			iTableColumnIndex++;
		}

		if (currentStorage.getSize(StorageType.FLOAT) > 1)
		{
			float[] floatData = currentStorage.getArrayFloat();
			
			column = new TableColumn(table, SWT.NONE);
			column.setText("Float data");

			if (floatData.length > iNumberOfTableItemsToLoad)
			{
				if (iCurrentTablePage >= 
					(iNumberOfTableItems / (float)MAX_TABLE_ROWS - 1))
				{
					iTmpNumberOfDataItems = iNumberOfTableItems % MAX_TABLE_ROWS;
				}
				else 
					iTmpNumberOfDataItems = iNumberOfTableItemsToLoad;
			}
			else
			{
				iTmpNumberOfDataItems = floatData.length;
			}
			
			for (int dataIndex = 0; dataIndex < iTmpNumberOfDataItems; dataIndex++)
			{
				item = table.getItem(dataIndex);
				item.setText(iTableColumnIndex, Float
						.toString(floatData[iStartItemIndex + dataIndex]));
			}

			column.pack();
			iTableColumnIndex++;
		}

		if (currentStorage.getSize(StorageType.STRING) > 1)
		{
			String[] stringData = currentStorage.getArrayString();

			column = new TableColumn(table, SWT.NONE);
			column.setText("String data");

			if (stringData.length > iNumberOfTableItemsToLoad)
			{
				if (iCurrentTablePage >= 
					(iNumberOfTableItems / (float)MAX_TABLE_ROWS - 1))
				{
					iTmpNumberOfDataItems = iNumberOfTableItems % MAX_TABLE_ROWS;
				}
				else 
					iTmpNumberOfDataItems = iNumberOfTableItemsToLoad;
			}
			else
			{
				iTmpNumberOfDataItems = stringData.length;
			}
			
			for (int dataIndex = 0; dataIndex < iTmpNumberOfDataItems; dataIndex++)
			{
				item = table.getItem(dataIndex);
				item.setText(iTableColumnIndex, stringData[iStartItemIndex + dataIndex]);
			}

			column.pack();
			iTableColumnIndex++;
		}

		if (currentStorage.getSize(StorageType.BOOLEAN) >= 1)
		{
			boolean[] booleanData = currentStorage.getArrayBoolean();

			column = new TableColumn(table, SWT.NONE);
			column.setText("Boolean data");

			if (booleanData.length > iNumberOfTableItemsToLoad)
			{
				if (iCurrentTablePage >= 
					(iNumberOfTableItems / (float)MAX_TABLE_ROWS - 1))
				{
					iTmpNumberOfDataItems = iNumberOfTableItems % MAX_TABLE_ROWS;
				}
				else 
					iTmpNumberOfDataItems = iNumberOfTableItemsToLoad;
			}
			else
			{
				iTmpNumberOfDataItems = booleanData.length;
			}
			
			for (int dataIndex = 0; dataIndex < iTmpNumberOfDataItems; dataIndex++)
			{
				item = table.getItem(dataIndex);
				item.setText(iTableColumnIndex, Boolean
						.toString(booleanData[iStartItemIndex + dataIndex]));
			}

			column.pack();
			iTableColumnIndex++;
		}
		
		// Check if last page is reached		
		if (iCurrentTablePage >= (iNumberOfTableItems / (float)MAX_TABLE_ROWS - 1))
		{
			nextPageButton.setEnabled(false);
		}
	}

	public void createSelectionTable(int iRequestedSelectionId) {
		
		TableItem item;

		reinitializeTable();
		
		// No paging needed for selections
		previousPageButton.setEnabled(false);
		nextPageButton.setEnabled(false);
		
		iCurrentlyRequestedCollectionId = iRequestedSelectionId;

		final TableColumn offsetColumn = new TableColumn(table, 
				SWT.NONE);
		offsetColumn.setText("Offset");
		final TableColumn lengthColumn = new TableColumn(table, 
				SWT.NONE);
		lengthColumn.setText("Length");
		final TableColumn multiOffsetColumn = new TableColumn(table,
				SWT.NONE);
		multiOffsetColumn.setText("MultiOffset");
		final TableColumn multiRepeatColumn = new TableColumn(table,
				SWT.NONE);
		multiRepeatColumn.setText("MultiRepeat");

		currentSelection = virtualArrayManager
				.getItemVirtualArray(iRequestedSelectionId);

		item = new TableItem(table, SWT.NONE);
		item.setText(
				new String[] { Integer.toString(currentSelection.getOffset()),
				Integer.toString(currentSelection.length()),
				Integer.toString(currentSelection.getMultiOffset()),
				Integer.toString(currentSelection.getMultiRepeat()) });

		offsetColumn.pack();
		lengthColumn.pack();
		multiOffsetColumn.pack();
		multiRepeatColumn.pack();
	}

	public void redrawTable() {
		
		swtContainer.redraw();
	}

	public void setExternalGUIContainer(Composite swtContainer) {
		
		this.swtContainer = swtContainer;
	}

	public void reinitializeTable() {
		
		table.removeAll();

		// remove old columns
		for (int columnIndex = table.getColumnCount() - 1; columnIndex >= 0; columnIndex--)
		{
			table.getColumn(columnIndex).dispose();
		}
	}

	protected void initTableEditor() {
		
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener(SWT.MouseDown, new Listener()
		{
			public void handleEvent(Event event)
			{
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount())
				{
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++)
					{
						Rectangle rect = item.getBounds(columnIndex);
						if (rect.contains(pt))
						{
							final int columnIndexFinal = columnIndex;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener()
							{
								public void handleEvent(final Event event)
								{
									switch (event.type)
									{
									case SWT.FocusOut:
										item.setText(columnIndexFinal, text.getText());
										text.dispose();
										break;
									case SWT.Traverse:
										switch (event.detail)
										{
										case SWT.TRAVERSE_RETURN:
											item.setText(columnIndexFinal, text.getText());
											//bTriggeredUpdateFlag = true;	
											
											// Only update if cell has text
											if (!text.getText().equals(""))
											{
												updateData(item, columnIndexFinal);
											}
											
										// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											event.doit = false;
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, columnIndex);
							text.setText(item.getText(columnIndex));
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
					{
						return;
					}
					index++;
				}
			}
		});
	}
	
	protected void updateData(TableItem updatedItem, 
			int iColumnIndexOfItem) {
		
		IVirtualArray tmpSelection =
			virtualArrayManager.getItemVirtualArray(iCurrentlyRequestedCollectionId);
		
		tmpSelection.getWriteToken();
		
		switch(iColumnIndexOfItem)
		{
		// offset
		case 0:
			tmpSelection.setOffset(StringConversionTool.convertStringToInt(
					updatedItem.getText(iColumnIndexOfItem), -1));
			tmpSelection.returnWriteToken();
			break;
		// length
		case 1:
			tmpSelection.setLength(StringConversionTool.convertStringToInt(
					updatedItem.getText(iColumnIndexOfItem), -1));	
			tmpSelection.returnWriteToken();
			break;
		// mulit offset
		case 2:
			tmpSelection.setMultiOffset(StringConversionTool.convertStringToInt(
					updatedItem.getText(iColumnIndexOfItem), -1));
			tmpSelection.returnWriteToken();
			break;
		// multi repeat
		case 3:
			tmpSelection.setMultiRepeat(StringConversionTool.convertStringToInt(
					updatedItem.getText(iColumnIndexOfItem), -1));
			tmpSelection.returnWriteToken();
			break;
		default:
			tmpSelection.returnWriteToken();
			break;
		}
	}
	
	public void updateSelection( int iSelectionId ) {
		
//		if (bTriggeredUpdateFlag == true)
//		{
//			bTriggeredUpdateFlag = false;
//			return;		
//		}
		
		createSelectionTable( iSelectionId );
	}
}