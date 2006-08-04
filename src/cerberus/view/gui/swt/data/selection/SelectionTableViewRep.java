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

import cerberus.data.collection.Selection;
import cerberus.data.collection.Storage;
import cerberus.manager.GeneralManager;
import cerberus.manager.SelectionManager;
import cerberus.manager.StorageManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.data.DataTableViewInter;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class SelectionTableViewRep implements DataTableViewInter
{
	protected final GeneralManager refGeneralManager;
	protected SelectionManager refSelectionManager;
	protected Composite refSWTContainer;
	
	protected Selection refCurrentSelection;
	protected Table refTable;
	
	public SelectionTableViewRep(GeneralManager refGeneralManager)
	{
		this.refGeneralManager = refGeneralManager;
				
		initView();
	}
		
	public void initView()
	{
		refSelectionManager = (SelectionManager)refGeneralManager.
			getManagerByBaseType(ManagerObjectType.SELECTION);

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
	
	public void createTable(int iRequestedSelectionId)
	{
		TableItem item;
		
		final TableColumn lengthColumn = new TableColumn(refTable, SWT.NONE);
		lengthColumn.setText("Length");
		final TableColumn offsetColumn = new TableColumn(refTable, SWT.NONE);
		offsetColumn.setText("Offset");
		final TableColumn multiOffsetColumn = new TableColumn(refTable, SWT.NONE);
		multiOffsetColumn.setText("MultiOffset");
		final TableColumn multiRepeatColumn = new TableColumn(refTable, SWT.NONE);
		multiRepeatColumn.setText("MultiRepeat");
		
		//init empty table
		if(iRequestedSelectionId == -1)
		{
			item = new TableItem(refTable, SWT.NONE);
			item.setText(new String [] {"",""});
		}
		//fill table with regular data
		else
		{
			refTable.removeAll();
			
			try 
			{
				refCurrentSelection = refSelectionManager.
					getItemSelection(iRequestedSelectionId);

			} 
			catch (NullPointerException npe) 
			{
				assert false:"uniqueId was not found";
				return;
			}
		
			item = new TableItem(refTable, SWT.NONE);
			item.setText(new String [] {
					Integer.toString(refCurrentSelection.length()), 
					Integer.toString(refCurrentSelection.getOffset()),
					Integer.toString(refCurrentSelection.getMultiOffset()),
					Integer.toString(refCurrentSelection.getMultiRepeat())});
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
					lengthColumn.setWidth(width/4);
					offsetColumn.setWidth(width/4);
					multiOffsetColumn.setWidth(width/4);
					multiRepeatColumn.setWidth(width/4);					
					refTable.setSize(area.width, area.height);
				} else 
				{
					// table is getting bigger so make the table 
					// bigger first and then make the columns wider
					// to match the client area width
					refTable.setSize(area.width, area.height);
					lengthColumn.setWidth(width/4);
					offsetColumn.setWidth(width/4);
					multiOffsetColumn.setWidth(width/4);
					multiRepeatColumn.setWidth(width/4);	
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
