/*
 * @(#)FormatLineColorList.java	1.2 04.02.2003
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
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JColorChooser;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.pad.resources.Translator;

public class FormatLineColorList extends AbstractActionListCellColor {

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#fillCustomItems(ArrayList)
	 */
	protected void fillCustomItems(ArrayList items) {
		for (int i = 0; i < colors.length; i++) {
			EdgeView edge =
				new EdgeView(" ");
			AttributeMap map = new AttributeMap();
			GraphConstants.setPoints(map, arrowPoints);
			GraphConstants.setLineColor(map, colors[i]);
			GraphConstants.setLabelPosition(map, center);
			edge.changeAttributes(map);
			items.add(edge);
		}
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#fillResetMap(Map)
	 */
	protected void fillResetMap(Map target) {
		GraphConstants.setRemoveAttributes(
			target,
			new Object[] { GraphConstants.LINECOLOR });
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#fillApplyMap(CellView, Map)
	 */
	protected void fillApplyMap(CellView source, Map target) {
		GraphConstants.setLineColor(
			target,
			GraphConstants.getLineColor(source.getAttributes()));
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#selectAndFillMap(Map)
	 */
	protected void selectAndFillMap(Map target) {
		if (getCurrentGraph().getSelectionCount() > 0) {
			Color value =
				JColorChooser.showDialog(
					graphpad.getFrame(),
					Translator.getString("ColorDialog"),
					null);
			if (value != null) {
				GraphConstants.setLineColor(target, value);
			}
		}
	}

}
