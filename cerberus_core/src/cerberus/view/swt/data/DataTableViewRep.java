package cerberus.view.swt.data;

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

import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.IVirtualArrayManager;
import cerberus.manager.data.IStorageManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.system.StringConversionTool;
import cerberus.view.AViewRep;
import cerberus.view.ViewType;

public class DataTableViewRep 
extends AViewRep 
implements IDataTableView {
	
	private static final int MAX_TABLE_ROWS = 15;
	
	protected IStorageManager refStorageManager;

	protected IVirtualArrayManager refSelectionManager;

	protected IStorage[] refAllStorageItems;

	protected IStorage refCurrentStorage;

	protected IVirtualArray refCurrentSelection;

	protected Table refTable;
	
	protected Button refPreviousPageButton;
	
	protected Button refNextPageButton;
	
	protected int iCurrentlyRequestedCollectionId;
	
	protected boolean bTriggeredUpdateFlag;
	
	protected int iCurrentTablePage;

	public DataTableViewRep(IGeneralManager refGeneralManager, 
			int iParentId) {
		
		super(refGeneralManager,
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
	 * @see cerberus.view.AViewRep#initView(org.eclipse.swt.widgets.Composite)
	 */
	public void initViewSwtComposit( Composite swtContainer ) {
		
		refStorageManager = (IStorageManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.STORAGE);

		refSelectionManager = (IVirtualArrayManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIRTUAL_ARRAY);
	}

	public void drawView() {
		
		// not implemented in this class
	}

	public void initTable() {

		Composite dataTableComposite = new Composite(refSWTContainer, SWT.NONE);
 		dataTableComposite.setLayout(new RowLayout());
				
		refTable = new Table(dataTableComposite, SWT.BORDER | SWT.V_SCROLL);
		refTable.setHeaderVisible(true);
		refTable.setLinesVisible(true);
		//refTable.setSize(300, 400);
		refTable.setLayoutData(new RowData(300, 400));

		// Paging: previous
		refPreviousPageButton = new Button(dataTableComposite, SWT.BOTTOM | SWT.PUSH);
		refPreviousPageButton.setText("<");
		refPreviousPageButton.setEnabled(false);
		refPreviousPageButton.setLayoutData(new RowData(50, 40));
		refPreviousPageButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				iCurrentTablePage--;
				
				if (iCurrentTablePage == 0)
				{
					refPreviousPageButton.setEnabled(false);
				}
					
				drawStorageTable();
			}
		});
		
		// Paging: next
		refNextPageButton = new Button(dataTableComposite, SWT.BOTTOM | SWT.PUSH);
		refNextPageButton.setText(">");
		refNextPageButton.setEnabled(false);
		refNextPageButton.setLayoutData(new RowData(50, 40));
		refNextPageButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				iCurrentTablePage++;
				refPreviousPageButton.setEnabled(true);				
				drawStorageTable();
			}
		});
		
		initTableEditor();
	}

	public void createStorageTable(int iRequestedStorageId) {
		
		refCurrentStorage = refStorageManager
			.getItemStorage(iRequestedStorageId);
		
		// Reset paging
		iCurrentTablePage = 0;
		refPreviousPageButton.setEnabled(false);
		refNextPageButton.setEnabled(false);
		
		drawStorageTable();
	}
	
	protected void drawStorageTable() {
		
		TableItem item;
		TableColumn column;

		int iTableColumnIndex = 0;
		int iTmpNumberOfDataItems = 0;	
		int iNumberOfTableItems = refCurrentStorage.getMaximumLengthOfAllArrays();
		int iStartItemIndex = iCurrentTablePage * MAX_TABLE_ROWS;
		
		reinitializeTable();
		
		int iNumberOfTableItemsToLoad = iNumberOfTableItems;
		
		if (iNumberOfTableItems > MAX_TABLE_ROWS)
		{
			iNumberOfTableItemsToLoad = MAX_TABLE_ROWS;
			refNextPageButton.setEnabled(true);
		}

		for (int iTableRowIndex = 0; iTableRowIndex < iNumberOfTableItemsToLoad; iTableRowIndex++)
		{
			new TableItem(refTable, SWT.NONE);
		}

		if (refCurrentStorage.getSize(StorageType.INT) > 1)
		{
			int[] intData = refCurrentStorage.getArrayInt();

			column = new TableColumn(refTable, SWT.NONE);
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
				item = refTable.getItem(dataIndex);
				item.setText(iTableColumnIndex, Float
						.toString(intData[iStartItemIndex + dataIndex]));
			}

			column.pack();
			iTableColumnIndex++;
		}

		if (refCurrentStorage.getSize(StorageType.FLOAT) > 1)
		{
			float[] floatData = refCurrentStorage.getArrayFloat();
			
			column = new TableColumn(refTable, SWT.NONE);
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
				item = refTable.getItem(dataIndex);
				item.setText(iTableColumnIndex, Float
						.toString(floatData[iStartItemIndex + dataIndex]));
			}

			column.pack();
			iTableColumnIndex++;
		}

		if (refCurrentStorage.getSize(StorageType.STRING) > 1)
		{
			String[] stringData = refCurrentStorage.getArrayString();

			column = new TableColumn(refTable, SWT.NONE);
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
				item = refTable.getItem(dataIndex);
				item.setText(iTableColumnIndex, stringData[iStartItemIndex + dataIndex]);
			}

			column.pack();
			iTableColumnIndex++;
		}

		if (refCurrentStorage.getSize(StorageType.BOOLEAN) >= 1)
		{
			boolean[] booleanData = refCurrentStorage.getArrayBoolean();

			column = new TableColumn(refTable, SWT.NONE);
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
				item = refTable.getItem(dataIndex);
				item.setText(iTableColumnIndex, Boolean
						.toString(booleanData[iStartItemIndex + dataIndex]));
			}

			column.pack();
			iTableColumnIndex++;
		}
		
		// Check if last page is reached		
		if (iCurrentTablePage >= (iNumberOfTableItems / (float)MAX_TABLE_ROWS - 1))
		{
			refNextPageButton.setEnabled(false);
		}
	}

	public void createSelectionTable(int iRequestedSelectionId) {
		
		TableItem item;

		reinitializeTable();
		
		// No paging needed for selections
		refPreviousPageButton.setEnabled(false);
		refNextPageButton.setEnabled(false);
		
		iCurrentlyRequestedCollectionId = iRequestedSelectionId;

		final TableColumn offsetColumn = new TableColumn(refTable, 
				SWT.NONE);
		offsetColumn.setText("Offset");
		final TableColumn lengthColumn = new TableColumn(refTable, 
				SWT.NONE);
		lengthColumn.setText("Length");
		final TableColumn multiOffsetColumn = new TableColumn(refTable,
				SWT.NONE);
		multiOffsetColumn.setText("MultiOffset");
		final TableColumn multiRepeatColumn = new TableColumn(refTable,
				SWT.NONE);
		multiRepeatColumn.setText("MultiRepeat");

		refCurrentSelection = refSelectionManager
				.getItemVirtualArray(iRequestedSelectionId);

		item = new TableItem(refTable, SWT.NONE);
		item.setText(
				new String[] { Integer.toString(refCurrentSelection.getOffset()),
				Integer.toString(refCurrentSelection.length()),
				Integer.toString(refCurrentSelection.getMultiOffset()),
				Integer.toString(refCurrentSelection.getMultiRepeat()) });

		offsetColumn.pack();
		lengthColumn.pack();
		multiOffsetColumn.pack();
		multiRepeatColumn.pack();
	}

	public void redrawTable() {
		
		refSWTContainer.redraw();
	}

	public void setExternalGUIContainer(Composite refSWTContainer) {
		
		this.refSWTContainer = refSWTContainer;
	}

	public void reinitializeTable() {
		
		refTable.removeAll();

		// remove old columns
		for (int columnIndex = refTable.getColumnCount() - 1; columnIndex >= 0; columnIndex--)
		{
			refTable.getColumn(columnIndex).dispose();
		}
	}

	protected void initTableEditor() {
		
		final TableEditor editor = new TableEditor(refTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		refTable.addListener(SWT.MouseDown, new Listener()
		{
			public void handleEvent(Event event)
			{
				Rectangle clientArea = refTable.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = refTable.getTopIndex();
				while (index < refTable.getItemCount())
				{
					boolean visible = false;
					final TableItem item = refTable.getItem(index);
					for (int columnIndex = 0; columnIndex < refTable.getColumnCount(); columnIndex++)
					{
						Rectangle rect = item.getBounds(columnIndex);
						if (rect.contains(pt))
						{
							final int columnIndexFinal = columnIndex;
							final Text text = new Text(refTable, SWT.NONE);
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
	
	protected void updateData(TableItem refUpdatedItem, 
			int iColumnIndexOfItem) {
		
		IVirtualArray tmpSelection =
			refSelectionManager.getItemVirtualArray(iCurrentlyRequestedCollectionId);
		
		tmpSelection.getWriteToken();
		
		switch(iColumnIndexOfItem)
		{
		// offset
		case 0:
			tmpSelection.setOffset(StringConversionTool.convertStringToInt(
					refUpdatedItem.getText(iColumnIndexOfItem), -1));
			tmpSelection.returnWriteToken();
			break;
		// length
		case 1:
			tmpSelection.setLength(StringConversionTool.convertStringToInt(
					refUpdatedItem.getText(iColumnIndexOfItem), -1));	
			tmpSelection.returnWriteToken();
			break;
		// mulit offset
		case 2:
			tmpSelection.setMultiOffset(StringConversionTool.convertStringToInt(
					refUpdatedItem.getText(iColumnIndexOfItem), -1));
			tmpSelection.returnWriteToken();
			break;
		// multi repeat
		case 3:
			tmpSelection.setMultiRepeat(StringConversionTool.convertStringToInt(
					refUpdatedItem.getText(iColumnIndexOfItem), -1));
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