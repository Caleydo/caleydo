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
import cerberus.manager.GeneralManager;
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
		refStorageManager = refGeneralManager.getSingelton().getStorageManager();

		//load data
		//refAllStorageItems = refStorageManager.getAllStorageItems();
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
		
		//TODO: create columns dynamically
		final TableColumn idColumn = new TableColumn(refTable, SWT.NONE);
		idColumn.setText("Id");
		final TableColumn labelColumn = new TableColumn(refTable, SWT.NONE);
		labelColumn.setText("Label");
		
		//init empty table
		if(iRequestedStorageId == -1)
		{
			item = new TableItem(refTable, SWT.NONE);
			item.setText(new String [] {"",""});
		}
		//fill table with regular data
		else
		{
			refTable.removeAll();
	
			//float[] floatData;
			int[] intData;
			String[] stringData;
			
			try 
			{
				refCurrentStorage = refStorageManager.getItemStorage(iRequestedStorageId);
				
				//floatData = refCurrentStorage.getArrayFloat();
				stringData = refCurrentStorage.getArrayString();
				intData = refCurrentStorage.getArrayInt();
				//floatData = refCurrentStorage.getArrayFloat();
			} 
			catch (NullPointerException npe) 
			{
				assert false:"uniqueId was not found";
				return;
			}
					
			for(int dataIndex = 0; dataIndex < intData.length; dataIndex++)
			{
				item = new TableItem(refTable, SWT.NONE);
				item.setText(new String [] {
						Integer.toString(intData[dataIndex]),
						stringData[dataIndex]});
			}
		}
		
		//resize table
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
	
	public void redrawTable()
	{
		refSWTContainer.redraw();
	}
	
	public void setExternalGUIContainer(Composite refSWTContainer)
	{
		this.refSWTContainer = refSWTContainer;
	}

}
