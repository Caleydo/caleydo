/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * GPGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.plugins.layouts;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.jgraph.pad.coreframework.GPAbstractActionDefault;

/**
 * Calls a frame to select the Layoutalgorithm.
 * After selecting the action applies the
 * algorithm to the current graph.
 */
public class GraphApplyLayoutAlgorithm extends GPAbstractActionDefault {

	protected LayoutDialog dialog = null;
	
	/**Implementation
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public synchronized void actionPerformed(ActionEvent e) {
		Frame f = JOptionPane.getFrameForComponent(graphpad);
		if (dialog == null) {
			dialog = new LayoutDialog(f, graphpad.getCurrentGraph());
		}
		dialog.setVisible(true);
	}
}