/*
 * @(#)FileExportJPG.java	1.2 01.02.2003
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jgraph.pad.resources.Translator;

public class FileExportJPG extends AbstractActionFile {

	/* File type to pass to ImageIO. Default is "jpg". */
	protected transient String fileType = "jpg";
	
	public FileExportJPG(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			String file =
				saveDialog(
					Translator.getString("FileSaveAsLabel") + " "+fileType.toUpperCase(),
					fileType.toLowerCase(),
					fileType.toUpperCase()+" Image");
			if (getCurrentDocument().getModel().getRootCount() > 0) {
				BufferedImage img = getCurrentGraph().getImage(getCurrentGraph().getBackground(), 5);
				ImageIO.write(img, fileType.toLowerCase(), new File(file));
			};
		} catch (IOException ex) {
			graphpad.error(ex.getMessage());
		}
	}

}
