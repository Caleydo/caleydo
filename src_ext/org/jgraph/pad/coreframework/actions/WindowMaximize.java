/*
 * @(#)WindowMaximize.java	1.2 09.02.2003
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

import javax.swing.JInternalFrame;

import org.jgraph.pad.coreframework.GPAbstractActionDefault;

/**
 * Maximizes all JInternalFrames at the
 * Desktop.
 */
public class WindowMaximize extends GPAbstractActionDefault {

	/**
	 * Calls the method setMaximum(true) for each
	 * JInternalFrame.
	 *
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JInternalFrame[] ajif = graphpad.getAllFrames();

		for (int i = 0; i < ajif.length; i++) {
			try {
				ajif[i].setMaximum(true);
			} catch (java.beans.PropertyVetoException pvex) {

			}
		}
	}

}
