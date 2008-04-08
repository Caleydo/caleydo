/*
 * @(#)ToolBoxIcon.java	1.2 05.02.2003
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
package org.jgraph.pad.coreframework.actions.celltoolboxes;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.pad.coreframework.GPUserObject;
import org.jgraph.pad.graphcellsbase.cells.ImageCell;
import org.jgraph.pad.resources.ImageLoader;
import org.jgraph.pad.resources.Translator;

public class ToolBoxIcon extends AbstractDefaultVertexnPortsCreator {

	public GraphCell createCell() {
		return new ImageCell(new GPUserObject());
	}

	public AttributeMap getAttributeMap(GraphCell cell,
			Rectangle2D bounds) {
		AttributeMap attributes = new AttributeMap();
		bounds = reSize(bounds); // thus we can constraint the bounding box
									// size
		GraphConstants.setBounds(attributes, bounds);
		GraphConstants.setIcon(attributes, ImageLoader.getImageIcon(Translator.getString("Icon")));
		String fontName = Translator.getString("FontName");
		try {
			int fontSize = Integer.parseInt(Translator.getString("FontSize"));
			int fontStyle = Integer.parseInt(Translator.getString("FontStyle"));
			GraphConstants.setFont(attributes, new Font(fontName, fontStyle,
					fontSize));
		} catch (Exception e) {
		}
		return attributes;
	}
}
