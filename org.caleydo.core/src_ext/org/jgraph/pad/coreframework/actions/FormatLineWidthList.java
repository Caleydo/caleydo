/*
 * @(#)FormatLineWidthList.java	1.2 04.02.2003
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

import java.util.ArrayList;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.pad.resources.Translator;

public class FormatLineWidthList extends AbstractActionListCell {

	protected static float[] widths = new float[] { 1, 2, 3, 4 };

	/**
	 * Constructor for FormatLineWidthList.
	 * @param graphpad
	 */
	public FormatLineWidthList() {
		super();
		String tmp = Translator.getString("Widths");
		if (tmp != null) {
		  try {
			String[] array = tokenize(tmp);
			widths = new float[array.length];
			for (int i = 0; i < array.length; i++) {
				widths[i] = Integer.parseInt(array[i]);
			}
		  } catch (Exception e) {
			// Ignore
		  }
		}
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#fillCustomItems(ArrayList)
	 */
	protected void fillCustomItems(ArrayList items) {
		for (int i = 0; i < widths.length; i++) {
			EdgeView edge =
				new EdgeView(" ");
			AttributeMap map = new AttributeMap();
			GraphConstants.setPoints(map, AbstractActionListCell.arrowPoints);
			GraphConstants.setLineWidth(map, widths[i]);
			GraphConstants.setLabelPosition(map, center);
			edge.changeAttributes(map);
			items.add(edge);
		}
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#fillResetMap(Map)
	 */
	protected void fillResetMap(Map target) {
		GraphConstants.setRemoveAttributes(target, new Object[]{GraphConstants.LINEWIDTH});
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#fillApplyMap(CellView, Map)
	 */
	protected void fillApplyMap(CellView source, Map target) {
		GraphConstants.setLineWidth(
			target,
			GraphConstants.getLineWidth(source.getAttributes()));
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionListCell#selectAndFillMap(Map)
	 */
	protected void selectAndFillMap(Map target) {
		if (getCurrentGraph().getSelectionCount() > 0) {
			try {
				float f =
					Float.parseFloat(
						JOptionPane.showInputDialog(
							Translator.getString("WidthDialog")));
				GraphConstants.setLineWidth(target, f);
			} catch (NullPointerException npe) {
				// ignore
			} catch (Exception ex) {
				graphpad.error(ex.toString());
			}
		}
	}

}
