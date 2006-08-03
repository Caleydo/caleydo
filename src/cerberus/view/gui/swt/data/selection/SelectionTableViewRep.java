package cerberus.view.gui.swt.data.selection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import cerberus.data.collection.Storage;
import cerberus.manager.GeneralManager;
import cerberus.manager.StorageManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.data.DataTableViewInter;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class SelectionTableViewRep implements DataTableViewInter
{
	protected final GeneralManager refGeneralManager;
	protected StorageManager refStorageManager;
	protected Composite refSWTContainer;
	//protected int iRequestedStorageId;
	
	protected Storage[] refAllStorageItems;
	protected Storage refCurrentStorage;
	protected Table refTable;
	
	public SelectionTableViewRep(GeneralManager refGeneralManager)
	{
		this.refGeneralManager = refGeneralManager;
				
		retrieveNewGUIContainer();
		initView();
		drawView();
	}
	
	public void redrawTable()
	{
		refSWTContainer.redraw();
	}
	
	public void initView()
	{
		refStorageManager = refGeneralManager.getSingelton().getStorageManager();

		initTable();
	}

	public void drawView()
	{		
		//createTable(15301);
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
		refTable.setSize(300, 400);
	}
	
	public void createTable(int iRequestedStorageId)
	{
		refTable.removeAll();
		
		final TableColumn idColumn = new TableColumn(refTable, SWT.NONE);
		idColumn.setText("Id");
		final TableColumn labelColumn = new TableColumn(refTable, SWT.NONE);
		labelColumn.setText("Label");
		
		//float[] floatData;
		int[] intData;
		String[] stringData;
		TableItem item;
		
		refCurrentStorage = refStorageManager.getItemStorage(iRequestedStorageId);
			
		//Just for testing - only the float array is read out
		//in future we have to iterate over all arrays
		
		try 
		{
			//floatData = refCurrentStorage.getArrayFloat();
			stringData = refCurrentStorage.getArrayString();
			intData = refCurrentStorage.getArrayInt();
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
