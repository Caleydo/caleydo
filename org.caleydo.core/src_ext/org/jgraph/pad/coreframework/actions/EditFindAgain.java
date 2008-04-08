/*
 * @(#)EditFindAgain.java	1.2 30.01.2003
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

import javax.swing.JOptionPane;

import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.resources.Translator;

public class EditFindAgain extends GPAbstractActionDefault {


	/**
	 * Action that finds the first cell to match the given regular
	 * expression. The expression is retrieved using a dialog.
	 *
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (getCurrentDocument().getFindPattern() == null) {
			try {
				String exp = JOptionPane.showInputDialog(Translator.getString("FindDialog"));
				if (exp != null && exp.length() > 0)
					getCurrentDocument().setFindPattern(exp.toLowerCase());
				//Pattern.compile(exp); // JDK 1.3
			} catch (NullPointerException npe) {
				// ignore
			} catch (Exception ex) {
				graphpad.error(ex.toString());
				getCurrentDocument().setFindPattern(null);
			}
		}
		if (getCurrentDocument().getFindPattern() != null) {
			Object[] cells = getCurrentGraph().getRoots();
			boolean active = (getCurrentDocument().getLastFound() == null);
			Object match = null;
			if (cells != null && cells.length > 0) {
				for (int i = 0; i < cells.length; i++) {
					if (active) {
						String s = getCurrentGraph().convertValueToString(cells[i]);
						//Matcher m = findPattern.matcher(buttonSelect); // JDK 1.3
						//if (m.matches()) {
						if (s.toLowerCase().startsWith(getCurrentDocument().getFindPattern())) {
							match = cells[i];
							break;
						}
					}
					active = active || cells[i] == getCurrentDocument().getLastFound() ;
				}
			}
			getCurrentDocument().setLastFound(match);
			if (getCurrentDocument().getLastFound() != null) {
				getCurrentGraph().scrollCellToVisible(getCurrentDocument().getLastFound());
				getCurrentGraph().setSelectionCell(getCurrentDocument().getLastFound());
			} else {
				graphpad.error(Translator.getString("NoMatchDialog"));
			}
		}
	}

}
