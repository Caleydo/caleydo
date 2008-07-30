package org.caleydo.core.view.swt.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;

public class TestTableViewRep
	extends AView
	implements IView
{

	public TestTableViewRep(IGeneralManager generalManager, int iViewId,
			int iParentContainerId, String sLabel)
	{

		super(generalManager, iViewId, iParentContainerId, sLabel, ViewType.SWT_DATA_EXPLORER);

		initView();
		drawView();
	}

	/**
	 * @see org.caleydo.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer)
	{

	}

	public void drawView()
	{

		createTable();
	}

	protected void createTable()
	{

		final Table table = new Table(swtContainer, SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setSize(400, 300);
		final TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Column 1");
		final TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText("Column 2");
		for (int i = 0; i < 10; i++)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { "item 0" + i, "item 1" + i });
		}
		swtContainer.addControlListener(new ControlAdapter()
		{

			public void controlResized(ControlEvent e)
			{

				Rectangle area = swtContainer.getClientArea();
				Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - 2 * table.getBorderWidth();
				if (preferredSize.y > area.height + table.getHeaderHeight())
				{
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = table.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				Point oldSize = table.getSize();
				if (oldSize.x > area.width)
				{
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					column1.setWidth(width / 3);
					column2.setWidth(width - column1.getWidth());
					table.setSize(area.width, area.height);
				}
				else
				{
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					table.setSize(area.width, area.height);
					column1.setWidth(width / 3);
					column2.setWidth(width - column1.getWidth());
				}
			}
		});
	}
}
