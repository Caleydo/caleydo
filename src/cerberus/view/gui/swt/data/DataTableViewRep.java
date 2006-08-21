package cerberus.view.gui.swt.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import cerberus.data.collection.ISelection;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISelectionManager;
import cerberus.manager.ISetManager;
import cerberus.manager.IStorageManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;

public class DataTableViewRep 
extends AViewRep 
implements IDataTableView
{
	protected IStorageManager refStorageManager;
	protected ISelectionManager refSelectionManager;
	protected Composite refSWTContainer;
	
	protected IStorage[] refAllStorageItems;
	protected IStorage refCurrentStorage;
	protected ISelection refCurrentSelection;

	protected Table refTable;
	
	public DataTableViewRep(IGeneralManager refGeneralManager, int iParentId)
	{
		super(refGeneralManager, -1, iParentId, "");
				
		initView();
	}
		
	public void initView()
	{
		refStorageManager = (IStorageManager)refGeneralManager.
			getManagerByBaseType(ManagerObjectType.STORAGE);
		
		refSelectionManager = (ISelectionManager)refGeneralManager.
			getManagerByBaseType(ManagerObjectType.SELECTION);
	}

	public void drawView()
	{		
		// not implemented in this class
	}

	public void retrieveNewGUIContainer()
	{
		// not implemented in this class
	}

	public void retrieveExistingGUIContainer()
	{
		// not implemented in this class
	}

	public void initTable()
	{
		refTable = new Table(refSWTContainer, SWT.BORDER | SWT.V_SCROLL);
		refTable.setHeaderVisible(true);
		refTable.setLinesVisible(true);
		refTable.setSize(300, 400);
		
		initTableEditor();
	}
	
	public void createStorageTable(int iRequestedStorageId)
	{
		TableItem item;
		TableColumn column;

		reinitializeTable();
		
		refCurrentStorage = refStorageManager.getItemStorage(iRequestedStorageId);

		int iTableColumnIndex = 0;
		int iNumberOfTableRows = 0;
		
		// Find maximum array size to know the needed number of rows in the
		// table.
		int[] storageAllSize = refCurrentStorage.getAllSize();
		for(int storageSizeIndex = 0; storageSizeIndex < storageAllSize.length; storageSizeIndex++)
		{
			if(iNumberOfTableRows < storageAllSize[storageSizeIndex])
			{
				iNumberOfTableRows = storageAllSize[storageSizeIndex];
			}
		}
		
		for(int iTableRowIndex = 0; iTableRowIndex < iNumberOfTableRows; iTableRowIndex++)
		{
			new TableItem(refTable, SWT.NONE);
		}
		
		if (refCurrentStorage.getSize(StorageType.INT) != 0)
		{
			int[] intData = refCurrentStorage.getArrayInt();

			column = new TableColumn(refTable, SWT.NONE);
			column.setText("Int data");
			
			for(int dataIndex = 0; dataIndex < intData.length; dataIndex++)
			{
				item = refTable.getItem(dataIndex);
				item.setText (iTableColumnIndex, Float.toString(intData[dataIndex]));
			}
			
			column.pack();
			iTableColumnIndex++;
		}
		
		if (refCurrentStorage.getSize(StorageType.FLOAT) != 0)
		{
			float[] floatData = refCurrentStorage.getArrayFloat();

			column = new TableColumn(refTable, SWT.NONE);
			column.setText("Float data");
			
			for(int dataIndex = 0; dataIndex < floatData.length; dataIndex++)
			{
				item = refTable.getItem(dataIndex);
				item.setText (iTableColumnIndex, Float.toString(floatData[dataIndex]));
			}
			
			column.pack();
			iTableColumnIndex++;
		}
		
		if (refCurrentStorage.getSize(StorageType.STRING) != 0)
		{
			String[] stringData = refCurrentStorage.getArrayString();

			column = new TableColumn(refTable, SWT.NONE);
			column.setText("String data");
			
			for(int dataIndex = 0; dataIndex < stringData.length; dataIndex++)
			{
				item = refTable.getItem(dataIndex);
				item.setText (iTableColumnIndex, stringData[dataIndex]);
			}
			
			column.pack();
			iTableColumnIndex++;
		}
		
		if (refCurrentStorage.getSize(StorageType.BOOLEAN) != 0)
		{
			boolean[] booleanData = refCurrentStorage.getArrayBoolean();

			column = new TableColumn(refTable, SWT.NONE);
			column.setText("Boolean data");
			
			for(int dataIndex = 0; dataIndex < booleanData.length; dataIndex++)
			{
				item = refTable.getItem(dataIndex);
				item.setText (iTableColumnIndex, Boolean.toString(booleanData[dataIndex]));
			}
			
			column.pack();
			iTableColumnIndex++;
		}
	}
	
	public void createSelectionTable(int iRequestedSelectionId)
	{
		TableItem item;
		
		reinitializeTable();
		
		final TableColumn lengthColumn = new TableColumn(refTable, SWT.NONE);
		lengthColumn.setText("Length");
		final TableColumn offsetColumn = new TableColumn(refTable, SWT.NONE);
		offsetColumn.setText("Offset");
		final TableColumn multiOffsetColumn = new TableColumn(refTable, SWT.NONE);
		multiOffsetColumn.setText("MultiOffset");
		final TableColumn multiRepeatColumn = new TableColumn(refTable, SWT.NONE);
		multiRepeatColumn.setText("MultiRepeat");
		
		refCurrentSelection = refSelectionManager.
			getItemSelection(iRequestedSelectionId);
	
		item = new TableItem(refTable, SWT.NONE);
		item.setText(new String [] {
				Integer.toString(refCurrentSelection.length()), 
				Integer.toString(refCurrentSelection.getOffset()),
				Integer.toString(refCurrentSelection.getMultiOffset()),
				Integer.toString(refCurrentSelection.getMultiRepeat())});
		
		lengthColumn.pack();
		offsetColumn.pack();
		multiOffsetColumn.pack();
		multiRepeatColumn.pack();
	}
	
	public void redrawTable()
	{
		refSWTContainer.redraw();
	}
	
	public void setExternalGUIContainer(Composite refSWTContainer)
	{
		this.refSWTContainer = refSWTContainer;
	}
	
	public void reinitializeTable()
	{
		refTable.removeAll();
		
		// remove old columns
		for(int columnIndex = refTable.getColumnCount()-1; columnIndex >= 0; columnIndex--)
		{
			refTable.getColumn(columnIndex).dispose();
		}
	}
	
	protected void initTableEditor()
	{	
		final TableEditor editor = new TableEditor(refTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		refTable.addListener (SWT.MouseDown, new Listener () 
		{
			public void handleEvent (Event event) 
			{
				Rectangle clientArea = refTable.getClientArea ();
				Point pt = new Point (event.x, event.y);
				int index = refTable.getTopIndex ();
				while (index < refTable.getItemCount ()) 
				{
					boolean visible = false;
					final TableItem item = refTable.getItem (index);
					for (int i = 0; i < refTable.getColumnCount (); i++) 
					{
						Rectangle rect = item.getBounds (i);
						if (rect.contains (pt)) {
							final int column = i;
							final Text text = new Text(refTable, SWT.NONE);
							Listener textListener = new Listener () {
								public void handleEvent (final Event e) {
									switch (e.type) {
										case SWT.FocusOut:
											item.setText (column, text.getText ());
											text.dispose ();
											break;
										case SWT.Traverse:
											switch (e.detail) {
												case SWT.TRAVERSE_RETURN:
													item.setText (column, text.getText ());
													// FALL THROUGH
												case SWT.TRAVERSE_ESCAPE:
													text.dispose ();
													e.doit = false;
											}
											break;
									}
								}
							};
							text.addListener (SWT.FocusOut, textListener);
							text.addListener (SWT.Traverse, textListener);
							editor.setEditor (text, item, i);
							text.setText (item.getText (i));
							text.selectAll ();
							text.setFocus ();
							return;
						}
						if (!visible && rect.intersects (clientArea)) {
							visible = true;
						}
					}
					if (!visible) return;
					index++;
				}
			}
		});
	}
}