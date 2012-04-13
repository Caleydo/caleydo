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
 * Table example snippet: place arbitrary controls in a table For a list of all
 * SWT example snippets see http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class TableEditorTest
{

	public static void main(String[] args)
	{

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Table table = new Table(shell, SWT.BORDER | SWT.MULTI);
		table.setLinesVisible(true);
		for (int i = 0; i < 3; i++)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(100);
		}
		for (int i = 0; i < 12; i++)
		{
			new TableItem(table, SWT.NONE);
		}
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++)
		{
			TableEditor editor = new TableEditor(table);
			CCombo combo = new CCombo(table, SWT.NONE);
			combo.setText("CCombo");
			combo.add("item 1");
			combo.add("item 2");
			editor.grabHorizontal = true;
			editor.setEditor(combo, items[i], 0);
			editor = new TableEditor(table);
			Text text = new Text(table, SWT.NONE);
			text.setText("Text");
			editor.grabHorizontal = true;
			editor.setEditor(text, items[i], 1);
			editor = new TableEditor(table);
			Button button = new Button(table, SWT.CHECK);
			button.pack();
			editor.minimumWidth = button.getSize().x;
			editor.horizontalAlignment = SWT.LEFT;
			editor.setEditor(button, items[i], 2);
		}
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
