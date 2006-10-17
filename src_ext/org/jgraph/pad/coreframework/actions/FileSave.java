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

import java.awt.event.ActionEvent;
import java.io.FileOutputStream;

/**
 * Action opens a dialog to select the file. After that the action saves the
 * current graph to the selected file.
 */
public class FileSave extends AbstractActionFile {

	public void actionPerformed(ActionEvent e) {
		{
			String fileName = getCurrentDocument().getDocComponent().getName();
			if (fileName == null) {
				graphpad.getCommand("FileSaveAs").actionPerformed(e);
				return;
			}
			try {
				getCurrentDocument().getJGraphpadCEFile().saveFile(new FileOutputStream(fileName));
				getCurrentDocument().setModified(false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
