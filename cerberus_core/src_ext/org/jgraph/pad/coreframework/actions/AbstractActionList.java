/*
 * @(#)AbstractActionList.java	1.2 01.02.2003
 *
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.pad.coreframework.actions;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ListCellRenderer;

import org.jgraph.graph.GraphConstants;
import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.coreframework.GPBarFactory;

public abstract class AbstractActionList extends GPAbstractActionDefault {

	public static int u2 = GraphConstants.PERMILLE / 2;

	public static Point center = new Point(u2, u2);

	/** Item key for the user item
	 *
	 */
	public static final String ITEM_KEY = "itemKey";

	/**
	 * @see org.jgraph.pad.actions.GPAbstractActionDefault#getMenuComponents()
	 */
	public Component[] getMenuComponents() {
		AbstractButton menu = getMenuBarComponent();
		GPBarFactory.fillMenuButton(menu, getName(), null);

		Object[] items = getItems();

		for (int i = 0; i < items.length; i++) {
			menu.add(getMenuComponent(items[i].toString(), items[i]));
		}

		return new JComponent[] { menu };
	}

	protected JMenu getMenuBarComponent() {
		return new JMenu(this);
	}

	/**
	 * @see org.jgraph.pad.actions.GPAbstractActionDefault#getToolComponents()
	 */
	public Component[] getToolComponents() {
		JComboBox combo = getToolBarComponent();
		combo.addActionListener(this);
		Object[] items = getItems();

		for (int i = 0; i < items.length; i++) {
			Object item = items[i];
			if (item != null)
				combo.addItem(item);
		}

		return new Container[] { combo };
	}

	protected JComboBox getToolBarComponent() {
		JComboBox combo = new JComboBox();
		combo.setName(getName());

		/*
		String tooltip = Translator.getString(PREFIX_COMPONENT + getName() + SUFFIX_TOOL_TIP_TEXT);
		if (tooltip!=null)
			combo.setToolTipText(tooltip);
		*/

		ListCellRenderer renderer = getItemListCellRenderer();
		if (renderer != null)
			combo.setRenderer(renderer);
		return combo;
	}

	protected Object getSelectedItem(ActionEvent e) {
		if (e.getSource() instanceof JComboBox) {
			return ((JComboBox) e.getSource()).getSelectedItem();
		} else if (e.getSource() instanceof JMenuItem) {
			return ((JMenuItem) e.getSource()).getClientProperty(ITEM_KEY);
		}
		return null;
	}

	/** Returns a JMenuItem with a link to this action.
	 */
	protected Component getMenuComponent(
		String actionCommand,
		Object itemValue) {
		JMenuItem item = new JMenuItem(this);
		item.putClientProperty(ITEM_KEY, itemValue);

		GPBarFactory.fillMenuButton(item, getName(), actionCommand);
		String presentationText = getPresentationText(actionCommand);
		if (presentationText != null)
			item.setText(presentationText);
		String presentation = getItemPresentationText(itemValue);
		if (presentation != null)
			item.setText(presentation);

		return item;
	}

	/** You should return a hashtable with the
	 *  possible items.
	 */
	protected abstract Object[] getItems();

	/** Returns the List Cell Renderer for the
	 *  Items. By default returns null.
	 *
	 */
	protected ListCellRenderer getItemListCellRenderer() {
		return null;
	};

	/** Returns the item presentation text
	 *  (buttonEdge.g. for the MenuItem)
	 *
	 *  The default Implemenation returns
	 *  <tt>item.toString()</tt>
	 *
	 */
	protected String getItemPresentationText(Object item) {
		return item.toString();
	}

	protected static String[] tokenize(String input) {
		ArrayList v = new ArrayList();
		StringTokenizer t = new StringTokenizer(input);
		String cmd[];

		while (t.hasMoreTokens())
			v.add(t.nextToken());
		cmd = new String[v.size()];
		for (int i = 0; i < cmd.length; i++)
			cmd[i] = (String) v.get(i);

		return cmd;
	}

}
