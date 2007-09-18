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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

import org.jgraph.pad.coreframework.GPGraphpadFile;
import org.jgraph.pad.resources.Translator;

public class FileOpenURL extends AbstractActionFile {

    /**
     * Shows a file chooser with the file filters from the file formats to
     * select a file.
     * 
     * Furthermore the method uses the selected file format for the read
     * process.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {    
        String name = JOptionPane.showInputDialog(Translator.getString(
                "URLDialog", new Object[] { "foo.jgx" }));

        // canceled?
        if (name == null)
            return;

        // open the graphpad
        try {
            URL url = new URL(name);
            InputStream in = url.openStream();
			if (url.getFile().endsWith("gz") || url.getFile().endsWith("draw"))
				in = new GZIPInputStream(in);
            if (in != null) {
                GPGraphpadFile file = GPGraphpadFile.read(in);
                if (file == null)
                    return;
                // add the new document with the new graph and the new model
                graphpad.addDocument(null, file);// TODO URL!!
                graphpad.update();
            }

        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(graphpad, ex.getLocalizedMessage(),
                    Translator.getString("Error"), JOptionPane.ERROR_MESSAGE);
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Empty implementation. This Action should be available each time.
     */
    public void update() {
    };
}
