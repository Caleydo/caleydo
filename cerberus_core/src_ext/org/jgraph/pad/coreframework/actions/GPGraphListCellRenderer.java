/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * 6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because nobody changed the file significantly since the last
 * 3.0 version of GPGraphpad that was LGPL. By significantly, I mean: 
 *  - less than 3 instructions changes could honnestly have been done from an old fork,
 *  - license or copyright changes in the header don't count
 *  - automaticaly updating imports don't count,
 *  - updating systematically 2 instructions to a library specification update don't count.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jgraph.pad.coreframework.actions;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jgraph.JGraph;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellView;
import org.jgraph.pad.coreframework.GPGraphpad;
import org.jgraph.pad.coreframework.GPPluginInvoker;
import org.jgraph.pad.util.RealGraphCellRenderer;

public class GPGraphListCellRenderer extends DefaultListCellRenderer {
	
	protected static JGraph dummyGraph;
	
	/** reference to the combobox for this renderer
	 */
	protected AbstractActionList action;
	
	/**
	 * Constructor for GPGraphListCellRenderer.
	 */
	public GPGraphListCellRenderer(AbstractActionList action, GPGraphpad graphpad) {
		this.action = action;
		dummyGraph = new JGraph();
	}
	
	/**
	 */
	public Component getListCellRendererComponent(
			JList list,
			Object view,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
		
		if (view instanceof String){
			return new JLabel((String)view);
		}
		
		JComponent c =
			new RealGraphCellRenderer(dummyGraph,
					new CellView[] {(CellView) view });
		Rectangle2D b = ((AbstractCellView) view).getBounds();
		if (b != null)
			c.setBounds(2, 2, (int)b.getWidth(), (int)b.getHeight());
		c.setPreferredSize(new Dimension((int)b.getWidth() , (int)b.getHeight() ));
		
		c.setBounds(2, 2, 5, 5);
		c.setPreferredSize(new Dimension(5 , 5));
		c.setOpaque(true);
		c.setBackground(dummyGraph .getBackground());
		return c;
	}
	
}
