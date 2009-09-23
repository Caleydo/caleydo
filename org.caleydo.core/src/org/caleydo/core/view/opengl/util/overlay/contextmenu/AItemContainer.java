package org.caleydo.core.view.opengl.util.overlay.contextmenu;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Abstract implementation for a container for items grouped by a specific topic. It holds a list of
 * AContextMenuItem which should be specified by inheriting classes. Subclasses of AItemContainer are intended
 * to be constructed out of one single point of references (for example a single ID). They can be passed to
 * the {@link ContextMenu} as a whole.
 * 
 * @author Alexander Lex
 */
public abstract class AItemContainer
	implements Iterable<IContextMenuEntry> {

	private ArrayList<IContextMenuEntry> contextMenuEntries;

	/**
	 * Constructor
	 */
	public AItemContainer() {
		contextMenuEntries = new ArrayList<IContextMenuEntry>();
	}

	/**
	 * Adds a context menu item to the container
	 * 
	 * @param contextMenuItem
	 */
	public void addContextMenuItem(AContextMenuItem contextMenuItem) {
		contextMenuEntries.add(contextMenuItem);
	}

	public void addItemContainer(AItemContainer container) {
		for (IContextMenuEntry item : container) {
			contextMenuEntries.add(item);
		}
	}

	/**
	 * Adds a separator to the context menu
	 */
	public void addSeparator() {
		contextMenuEntries.add(new Separator());
	}

	public void addHeading(String text) {
		contextMenuEntries.add(new Heading(text));
	}

	/**
	 * Returns the list of context menu items
	 * 
	 * @return
	 */
	public ArrayList<IContextMenuEntry> getContextMenuItems() {
		return contextMenuEntries;
	}

	@Override
	public Iterator<IContextMenuEntry> iterator() {
		return contextMenuEntries.iterator();
	}
}
