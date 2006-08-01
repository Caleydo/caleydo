package cerberus.view.gui.swt.data.set;

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
import cerberus.manager.GeneralManager;
import cerberus.manager.SetManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.data.DataViewInter;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class SetTableViewRep implements DataViewInter
{
	protected final int iNewId;
	protected GeneralManager refGeneralManager;
	protected SetManager refSetManager;
	protected Composite refSWTContainer;
	
	protected Set[] refAllSetItems;
	protected Table refTable;
	
	public SetTableViewRep(int iNewId, GeneralManager refGeneralManager)
	{
		this.iNewId = iNewId;
		this.refGeneralManager = refGeneralManager;
				
		//FIXME: do the following code in a method
		retrieveNewGUIContainer();
		initView();
		drawView();
	}
	
	public void initView()
	{
		refSetManager = refGeneralManager.getSingelton().getSetManager();

		//load data
		refAllSetItems = refSetManager.getAllSetItems();
		
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
		
		//insert SET in table
		for (int setDimIndex = 0; setDimIndex < refAllSetItems.length ; setDimIndex++) {
			TableItem item = new TableItem(refTable, SWT.NONE);
			
			item.setText(new String [] {
					Integer.toString(refAllSetItems[setDimIndex].getId()).toString(),
					refAllSetItems[setDimIndex].getLabel()});
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
