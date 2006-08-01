package cerberus.view.gui.swt.data.storage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import cerberus.data.collection.Set;
import cerberus.data.collection.Storage;
import cerberus.manager.GeneralManager;
import cerberus.manager.SetManager;
import cerberus.manager.StorageManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.data.DataTableViewInter;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class StorageTableViewRep implements DataTableViewInter
{
	protected final int iNewId;
	protected GeneralManager refGeneralManager;
	protected StorageManager refStorageManager;
	protected Composite refSWTContainer;
	
	protected Storage[] refAllStorageItems;
	protected Table refTable;
	
	public StorageTableViewRep(int iNewId, GeneralManager refGeneralManager)
	{
		this.iNewId = iNewId;
		this.refGeneralManager = refGeneralManager;
				
		retrieveNewGUIContainer();
		initView();
		drawView();
	}
	
	public void initView()
	{
		refStorageManager = refGeneralManager.getSingelton().getStorageManager();

		//load data
		refAllStorageItems = refStorageManager.getAllStorageItems();
		
		initTable();
	}

	public void drawView()
	{		
		createTable();
	}

	public void retrieveNewGUIContainer()
	{
		SWTNativeWidget refSWTNativeWidget = 
			(SWTNativeWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(ManagerObjectType.GUI_SWT_NATIVE_WIDGET);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}

	public void retrieveExistingGUIContainer()
	{
		// TODO Auto-generated method stub

	}

	protected void initTable()
	{
		refTable = new Table(refSWTContainer, SWT.BORDER | SWT.V_SCROLL);
		refTable.setHeaderVisible(true);
		refTable.setLinesVisible(true);
		refTable.setSize(400, 300);
	}
	
	protected void createTable()
	{
		final TableColumn idColumn = new TableColumn(refTable, SWT.NONE);
		idColumn.setText("Id");
		final TableColumn labelColumn = new TableColumn(refTable, SWT.NONE);
		labelColumn.setText("Label");
		
		Storage[] allStorages;
		Storage refCurrentStorage;
		float[] floatData;
		
		//insert SET in table
		for (int storageDimIndex = 0; storageDimIndex < refAllStorageItems.length ; storageDimIndex++) {
			TableItem item = new TableItem(refTable, SWT.NONE);
			
			item.setText(new String [] {
					Integer.toString(refAllStorageItems[storageDimIndex].getId()).toString(),
					refAllStorageItems[storageDimIndex].getLabel()});
			
			refCurrentStorage = refAllStorageItems[storageDimIndex];
			floatData = refCurrentStorage.getArrayFloat();
			for(int dataIndex = 0; dataIndex < floatData.length; dataIndex++)
			{
				System.out.println("Data: " +floatData[dataIndex]);
			}				
		}			
		
		refSWTContainer.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = refSWTContainer.getClientArea();
				Point preferredSize = refTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - 2*refTable.getBorderWidth();
				if (preferredSize.y > area.height + refTable.getHeaderHeight())
				{
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = refTable.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				Point oldSize = refTable.getSize();
				if (oldSize.x > area.width) 
				{
					// table is getting smaller so make the columns 
					// smaller first and then resize the table to
					// match the client area width
					idColumn.setWidth(width/3);
					labelColumn.setWidth(width - idColumn.getWidth());
					refTable.setSize(area.width, area.height);
				} else 
				{
					// table is getting bigger so make the table 
					// bigger first and then make the columns wider
					// to match the client area width
					refTable.setSize(area.width, area.height);
					idColumn.setWidth(width/3);
					labelColumn.setWidth(width - idColumn.getWidth());
				}
			}
		});
	}
}
