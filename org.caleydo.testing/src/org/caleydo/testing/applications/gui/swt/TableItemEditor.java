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
* TableEditor example snippet: edit a cell in a table (in place, fancy) For a
* list of all SWT example snippets see
* http://dev.eclipse.org/viewcvs/index.cgi/
* %7Echeckout%7E/platform-swt-home/dev.html#snippets
*/
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class TableItemEditor
{
	public static void main(String[] args)
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		final Table table = new Table(shell, SWT.BORDER | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		for (int i = 0; i < 3; i++)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(100);
			column.setText("Column " +i);
			column.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent event)
				{
					System.out.println("haha");
					
				}

				public void widgetSelected(SelectionEvent event)
				{
					System.out.println("haha");
					
				}
				
			});
		}
		for (int i = 0; i < 3; i++)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { "" + i, "" + i, "" + i });
		}
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		
		table.addListener(SWT.MouseDown, new Listener()
		{
			public void handleEvent(Event event)
			{
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount())
				{
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int i = 0; i < table.getColumnCount(); i++)
					{
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt))
						{
							final int column = i;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener()
							{
								public void handleEvent(final Event e)
								{
									switch (e.type)
									{
										case SWT.FocusOut:
											item.setText(column, text.getText());
											text.dispose();
											break;
										case SWT.Traverse:
											switch (e.detail)
											{
												case SWT.TRAVERSE_RETURN:
													item.setText(column, text.getText());
													// FALL THROUGH
												case SWT.TRAVERSE_ESCAPE:
													text.dispose();
													e.doit = false;
											}
											break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea))
						{
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
		shell.pack();
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}