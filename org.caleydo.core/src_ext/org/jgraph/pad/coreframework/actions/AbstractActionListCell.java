/*
 * @(#)AbstractActionListCell.java	1.2 02.02.2003
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JComboBox;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;

public abstract class AbstractActionListCell extends AbstractActionList {

	public static final String IDENTIFIER_RESET = " X";

	public static final String IDENTIFIER_SELECT = "...";

	protected static java.util.List arrowPoints = new ArrayList();

	protected static java.util.List arrowEndPoints = new ArrayList();

	static {
		arrowPoints.add(new Point(0, 6));
		arrowPoints.add(new Point(40, 6));
		arrowEndPoints.add(new Point(0, 6));
		arrowEndPoints.add(new Point(14, 6));
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionList#getToolBarComponent()
	 */
	protected JComboBox getToolBarComponent() {
		JComboBox combo = super.getToolBarComponent();

		combo.setRenderer(new GPGraphListCellRenderer(this, graphpad));
		combo.setPreferredSize(new Dimension(45, 26));
		combo.setMaximumSize(combo.getPreferredSize());
		return combo;
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionList#getItems()
	 */
	protected Object[] getItems() {
		ArrayList items = new ArrayList();

		items.add(IDENTIFIER_RESET);
		items.add(IDENTIFIER_SELECT);

		fillCustomItems(items);

		return items.toArray();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		AttributeMap map = new AttributeMap();
		if (source instanceof JComboBox) {
			JComboBox box = (JComboBox) e.getSource();
			Object item = box.getSelectedItem();
			if (item instanceof CellView) {
				fillApplyMap((CellView) item, map);
			}
			if (item == IDENTIFIER_SELECT) {
				selectAndFillMap(map);
			} else if (item == IDENTIFIER_RESET) {
				fillResetMap(map);
			}

		}
		setSelectionAttributes(map);

	}

	protected abstract void fillCustomItems(ArrayList items);

	protected abstract void fillResetMap(Map target);

	protected abstract void fillApplyMap(CellView source, Map target);

	protected abstract void selectAndFillMap(Map target);

	protected Dimension size = new Dimension(14, 15);

	protected Point point = new Point(0, 0);

}
