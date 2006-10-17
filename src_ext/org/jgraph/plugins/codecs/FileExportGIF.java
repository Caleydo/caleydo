/*
 * @(#)FileExportGIF.java	1.2 01.02.2003
 *
 * Copyright (C) 2003 gaudenz alder
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
package org.jgraph.plugins.codecs;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jgraph.pad.coreframework.actions.AbstractActionFile;
import org.jgraph.pad.resources.Translator;

//import Acme.JPM.Encoders.GifEncoder;
//import org.shetline.io.*;

//import com.eteks.filter.Web216ColorsFilter;

public class FileExportGIF extends AbstractActionFile {

	/* File type to pass to ImageIO. Default is "jpg". */
	protected transient String fileType = "gif";

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
				BufferedImage img = getCurrentGraph().getImage(null, 5);
				
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(convertToGif(img));

				// Write to file
				fos.flush();
				fos.close();
			};
		} catch (IOException ex) {
			graphpad.error(ex.getMessage());
		}
	}

	/** convert Image to GIF-encoded data, reducing the number of colors if needed */
	public static byte [] convertToGif(Image oImgBuffer) throws IOException
	{
		ByteArrayOutputStream oOut = null;
//		try {
			oOut = new ByteArrayOutputStream();
//			GIFOutputStream.writeGIF(oOut,oImgBuffer);
//		} catch(IOException ioe) {
			// GIFOutputStream throws IOException when GIF contains too many colors
			// if this happens, filter image to reduce number of colors
//			final FilteredImageSource filter = new FilteredImageSource(oImgBuffer.getSource(),new Web216ColorsFilter());
//			oOut = new ByteArrayOutputStream();
//			GIFOutputStream.writeGIF(oOut,filter); TODO fix this
//		}
		return oOut.toByteArray();
	}

}
