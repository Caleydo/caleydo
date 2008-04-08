/*
 * @(#)FormatFillColorList.java	1.2 04.02.2003
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
import java.awt.Rectangle;
import java.util.Map;
import java.util.Vector;

import javax.swing.JColorChooser;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;
import org.jgraph.pad.resources.Translator;

public class FormatGradientColorList extends FormatFillColorList {

	protected void fillCustomItems(Vector items){
		VertexView v;
		AttributeMap map;

		for (int i = 0; i < colors.length; i++) {
			v =
				new VertexView(null);
			map = new AttributeMap();
			GraphConstants.setBounds(map, new Rectangle(point, size));
			GraphConstants.setGradientColor(map, colors[i]);
			GraphConstants.setOpaque(map, true);
			v.changeAttributes(map);
			items.add(v);
		}
	}

	protected void fillApplyMap(CellView source, Map target) {
		Color value = GraphConstants.getGradientColor(source.getAttributes());
		if (value == null)
			return;
		GraphConstants.setOpaque(target, true);
		GraphConstants.setGradientColor(target, value);
	}
	

	protected void selectAndFillMap(Map target) {
		Color value =
			JColorChooser.showDialog(
				graphpad.getFrame(),
				Translator.getString("ColorDialog"),
				null);
		if (value != null) {
			GraphConstants.setOpaque(target, true);
			GraphConstants.setGradientColor(target, value);
		}
	}
	
}
