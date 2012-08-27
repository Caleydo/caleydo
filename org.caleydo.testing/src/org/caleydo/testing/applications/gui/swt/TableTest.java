/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/

package org.caleydo.testing.applications.gui.swt;

/*
 * Table example snippet: resize columns as table resizes For a list of all SWT
 * example snippets see http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableTest
{

	public static void main(String[] args)
	{

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final Composite comp = new Composite(shell, SWT.NONE);
		final Table table = new Table(comp, SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		final TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Column 1");
		final TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText("Column 2");
		for (int i = 0; i < 10; i++)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { "item 0" + i, "item 1" + i });
		}
		comp.addControlListener(new ControlAdapter()
		{

			public void controlResized(ControlEvent e)
			{

				Rectangle area = comp.getClientArea();
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

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}