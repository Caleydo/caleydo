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

import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.manager.GeneralManager;
import cerberus.manager.SetManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.data.DataTableViewInter;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class SetTableViewRep implements DataTableViewInter
{
	protected GeneralManager refGeneralManager;
	protected SetManager refSetManager;
	protected Composite refSWTContainer;
	
	protected ISet[] refAllSetItems;
	protected Table refTable;
	
	public SetTableViewRep(GeneralManager refGeneralManager)
	{
		this.refGeneralManager = refGeneralManager;
				
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
	}
}
