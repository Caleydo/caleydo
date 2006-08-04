package cerberus.view.gui.swt.data.storage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import cerberus.data.collection.Set;
import cerberus.data.collection.Storage;
import cerberus.data.collection.StorageType;
import cerberus.manager.GeneralManager;
import cerberus.manager.SelectionManager;
import cerberus.manager.SetManager;
import cerberus.manager.StorageManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.data.DataTableViewInter;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class StorageTableViewRep implements DataTableViewInter
{
	protected final GeneralManager refGeneralManager;
	protected StorageManager refStorageManager;
	protected Composite refSWTContainer;
	//protected int iRequestedStorageId;
	
	protected Storage[] refAllStorageItems;
	protected Storage refCurrentStorage;
	protected Table refTable;
	
	public StorageTableViewRep(GeneralManager refGeneralManager)
	{
		this.refGeneralManager = refGeneralManager;
				
		//retrieveNewGUIContainer();
		initView();
	}
		
	public void initView()
	{
		refStorageManager = (StorageManager)refGeneralManager.
			getManagerByBaseType(ManagerObjectType.STORAGE);
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
	}
	
	public void createTable(int iRequestedStorageId)
	{
		TableItem item;
		TableColumn column;

		refCurrentStorage = refStorageManager.getItemStorage(iRequestedStorageId);
		
		refTable.removeAll();
		
		// remove old columns
		for(int columnIndex = refTable.getColumnCount()-1; columnIndex >= 0; columnIndex--)
		{
			refTable.getColumn(columnIndex).dispose();
		}

		int tableColumnIndex = 0;
		
		if (refCurrentStorage.getSize(StorageType.INT) != 0)
		{
			int[] intData = refCurrentStorage.getArrayInt();

			column = new TableColumn(refTable, SWT.NONE);
			column.setText("Int data");
			
			for(int dataIndex = 0; dataIndex < intData.length; dataIndex++)
			{
				item = new TableItem(refTable, SWT.NONE);
				item.setText (tableColumnIndex, Integer.toString(intData[dataIndex]));
			}
			
			column.pack();
			tableColumnIndex++;
		}
		
		if (refCurrentStorage.getSize(StorageType.FLOAT) != 0)
		{
			float[] floatData = refCurrentStorage.getArrayFloat();

			column = new TableColumn(refTable, SWT.NONE);
			column.setText("Float data");
			
			for(int dataIndex = 0; dataIndex < floatData.length; dataIndex++)
			{
				item = refTable.getItem(dataIndex);
				item.setText (tableColumnIndex, Float.toString(floatData[dataIndex]));
			}
			
			column.pack();
			tableColumnIndex++;
		}
		
		if (refCurrentStorage.getSize(StorageType.STRING) != 0)
		{
			String[] stringData = refCurrentStorage.getArrayString();

			column = new TableColumn(refTable, SWT.NONE);
			column.setText("String data");
			
			for(int dataIndex = 0; dataIndex < stringData.length; dataIndex++)
			{
				item = refTable.getItem(dataIndex);
//				if (item == null)
//					item = new TableItem(refTable, SWT.NONE);
				item.setText (tableColumnIndex, stringData[dataIndex]);
			}
			
			column.pack();
			tableColumnIndex++;
		}
		
		if (refCurrentStorage.getSize(StorageType.BOOLEAN) != 0)
		{
			boolean[] booleanData = refCurrentStorage.getArrayBoolean();

			column = new TableColumn(refTable, SWT.NONE);
			column.setText("Boolean data");
			
			for(int dataIndex = 0; dataIndex < booleanData.length; dataIndex++)
			{
				item = refTable.getItem(dataIndex);
				item.setText (tableColumnIndex, Boolean.toString(booleanData[dataIndex]));
			}
			
			column.pack();
			tableColumnIndex++;
		}
	}
	
	public void redrawTable()
	{
		refSWTContainer.redraw();
	}
	
	public void setExternalGUIContainer(Composite refSWTContainer)
	{
		this.refSWTContainer = refSWTContainer;
	}
}
