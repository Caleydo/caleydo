/*
 * @(#)FormatShapeImage.java	1.2 29.01.2003
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

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgraph.pad.resources.Translator;
import org.jgraph.pad.util.ImageIconBean;

/**
 * Open a dialog to load an image of the selected cells
 */
public class FormatShapeImage extends AbstractActionFile {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e){
			// Should also offer a possibility to enter URL here
	  		String name = openDialog(Translator.getString("ImageDialog"), null, null);
			if (name != null) {
				try {
					//File f = new File(name);
					//ImageIcon icon = new ImageIcon(ImageIO.read(f));
					// JDK 1.3
					ImageIcon icon = new ImageIconBean(name);
					AttributeMap map = new AttributeMap();
					GraphConstants.setIcon(map, icon);
					graphpad.getCurrentDocument() .setSelectionAttributes(map);
				} catch (Exception ex) {
					graphpad.error(ex.toString());
					ex.printStackTrace();
				}
			}
	}

}
