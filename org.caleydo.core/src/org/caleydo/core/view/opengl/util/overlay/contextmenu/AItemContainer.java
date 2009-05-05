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
	implements Iterable<AContextMenuItem> {

	private ArrayList<AContextMenuItem> contextMenuItems;

	/**
	 * Constructor
	 */
	public AItemContainer() {
		contextMenuItems = new ArrayList<AContextMenuItem>();
	}

	/**
	 * Adds a context menu item to the container
	 * 
	 * @param contextMenuItem
	 */
	public void addContextMenuItem(AContextMenuItem contextMenuItem) {
		contextMenuItems.add(contextMenuItem);
	}

	/**
	 * Returns the list of context menu items
	 * @return
	 */
	public ArrayList<AContextMenuItem> getContextMenuItems() {
		return contextMenuItems;
	}

	@Override
	public Iterator<AContextMenuItem> iterator() {
		return contextMenuItems.iterator();
	}
}
