/*
 * @(#)FormatTextFontColorList.java	1.2 02.02.2003
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JColorChooser;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;
import org.jgraph.pad.resources.Translator;

public class FormatTextFontColorList extends AbstractActionListCellColor {

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#fillApplyMap(CellView, Map)
	 */
	protected void fillApplyMap(CellView source, Map target) {
		GraphConstants.setForeground(
			target,
			GraphConstants.getForeground(source.getAttributes()));
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#fillCustomItems(ArrayList)
	 */
	protected void fillCustomItems(ArrayList items) {
		for (int i = 0; i < colors.length; i++) {
			VertexView v = new VertexView(" A");
			AttributeMap map = new AttributeMap();
			GraphConstants.setBounds(
				map,
				new Rectangle(new Point(0, 0), new Dimension(14, 15)));
			GraphConstants.setForeground(map, colors[i]);
			v.changeAttributes(map);
			items.add(v);
		}
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#fillResetMap(Map)
	 */
	protected void fillResetMap(Map target) {
			GraphConstants.setForeground(target, Color.BLACK);
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#selectAndFillMap(Map)
	 */
	protected void selectAndFillMap(Map target) {
		Color value =
			JColorChooser.showDialog(
				graphpad.getFrame(),
				Translator.getString("ColorDialog"),
				null);
		if (value != null) {
			GraphConstants.setForeground(target, value);
		}
	}

}
