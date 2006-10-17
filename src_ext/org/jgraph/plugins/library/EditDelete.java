/*
 * @(#)EditDelete.java	1.2 30.01.2003
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
package  org.jgraph.plugins.library;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.pad.resources.Translator;

public class EditDelete extends LibraryAction {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		if (getCurrentLibraryDocument().getLibraryPanel().hasFocus()) {
			int r =
				JOptionPane.showConfirmDialog(
					null,
					Translator.getString("DeleteFromLibMessage"),
					Translator.getString("DeleteFromLibTitle"),
					JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.YES_OPTION)
				getCurrentLibraryDocument().getLibraryPanel().delete();
		} else {
			Object[] cells = getCurrentGraph().getSelectionCells();
			if (cells != null) {
				cells =
					DefaultGraphModel
						.getDescendants(getCurrentGraph().getModel(), cells)
						.toArray();
				getCurrentGraph().getModel().remove(cells);
			}
		}
	}

}
