package org.jgraph.plugins.library;

import java.awt.event.ActionEvent;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jgraph.pad.coreframework.actions.AbstractActionFile;
import org.jgraph.pad.resources.Translator;

/**
 * Action that saves the Library to a file.
 *
 */
public class FileLibrarySaveAs extends AbstractActionFile {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String libraryExtension = Translator.getString("LibraryExtension");

		String name =
			saveDialog(
				Translator.getString("FileSaveAsLabel"),
				libraryExtension,
				Translator.getString("JGraphpadLibrary", new Object[]{libraryExtension}));
		if (name != null) {
			LibraryDecorator library = (LibraryDecorator) getCurrentDocument().getPluginsMap().get(LibraryDecorator.LIBRARY_PLUGIN);
			GPLibraryPanel.ScrollablePanel panel =
				library.getLibraryPanel().getPanel();
			if (panel != null) {
				Serializable s = panel.getArchiveableState();
				try {
					Boolean compress = new Boolean(Translator.getString("compressLibraries"));
					ObjectOutputStream out = createOutputStream(name, compress.booleanValue());
					XMLEncoder enc =
						new XMLEncoder(
							new BufferedOutputStream(out));
					enc.writeObject(s);
					enc.close();
				} catch (Exception ex) {
					graphpad.error(ex.toString());
				} finally {
					graphpad.invalidate();
				}
			}
		}
	}

}
