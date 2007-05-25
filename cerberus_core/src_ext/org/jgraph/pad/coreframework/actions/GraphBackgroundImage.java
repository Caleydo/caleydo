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
package org.jgraph.pad.coreframework.actions;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import org.jgraph.pad.resources.Translator;

/**
 * Action opens a file dialog to select an image.
 * Afterthat the Action applies the selected image
 * to the graph background.
 */
public class GraphBackgroundImage extends AbstractActionFile {

	public void actionPerformed(ActionEvent e) {
		String name =
			openDialog(Translator.getString("ImageDialog"), null, null);
		if (name != null) {
			try {
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Image value = toolkit.getImage(name);
				getCurrentDocument().setBackgroundImage(value);
				graphpad.getCurrentGraph() .repaint();
			} catch (Exception ex) {
				graphpad.error(ex.toString());
			}
		}
	}

}
