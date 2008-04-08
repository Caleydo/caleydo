package org.jgraph.plugins.library;

import org.jgraph.pad.coreframework.GPAbstractActionDefault;

public abstract class LibraryAction extends GPAbstractActionDefault {

	public LibraryDecorator getCurrentLibraryDocument() {
		try {
			return (LibraryDecorator) getCurrentDocument().getPluginsMap().get(LibraryDecorator.LIBRARY_PLUGIN);
		} catch (Exception ex) {
			System.err.print("Your graph base class isn't a LibraryDecorator!");
			ex.printStackTrace();
			return null;
		}
	}

}

