package cerberus.view.gui.swt.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class TestTableViewRep 
extends AViewRep 
implements IView
{
	protected Composite refSWTContainer;
	
	public TestTableViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);

		initView();
		retrieveNewGUIContainer();
		drawView();
	}
	
	public void initView()
	{
		// TODO Auto-generated method stub
		
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
	
	protected void createTable()
	{
		final Table table = new Table(refSWTContainer, SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setSize(400, 300);
		final TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Column 1");
		final TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText("Column 2");
		for (int i = 0; i < 10; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {"item 0" + i, "item 1"+i});
		}
		refSWTContainer.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = refSWTContainer.getClientArea();
				Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - 2*table.getBorderWidth();
				if (preferredSize.y > area.height + table.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = table.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				Point oldSize = table.getSize();
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns 
					// smaller first and then resize the table to
					// match the client area width
					column1.setWidth(width/3);
					column2.setWidth(width - column1.getWidth());
					table.setSize(area.width, area.height);
				} else {
					// table is getting bigger so make the table 
					// bigger first and then make the columns wider
					// to match the client area width
					table.setSize(area.width, area.height);
					column1.setWidth(width/3);
					column2.setWidth(width - column1.getWidth());
				}
			}
		});
	}
}
