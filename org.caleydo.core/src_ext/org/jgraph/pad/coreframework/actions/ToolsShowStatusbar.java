/*
 * @(#)ToolsShowStatusbar.java	1.2 02.02.2003
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
import java.util.Enumeration;

import javax.swing.AbstractButton;

public class ToolsShowStatusbar extends AbstractActionCheckBox {

	/**
	 * @see org.jgraph.pad.actions.AbstractActionToggle#isSelected(String)
	 */
	public boolean isSelected(String actionCommand) {
		return graphpad.getStatusBar().isVisible();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		graphpad.getStatusBar().setVisible(
			!graphpad.getStatusBar().isVisible());

	}
	/** updates all Abstract Buttons from this action
	 */
	public void update(){
		Enumeration e_num = abstractButtons.elements();
		while (e_num.hasMoreElements()) {
			AbstractButton button = (AbstractButton) e_num.nextElement();
			button.setSelected(isSelected(button.getActionCommand()));
		}
	};

}
