package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;

/**
 * Abstract implementation for a container for items grouped by a specific topic. It holds a list of
 * AContextMenuItem which should be specified by inheriting classes. Subclasses of AItemContainer are intended
 * to be constructed out of one single point of references (for example a single ID). They can be passed to
 * the {@link ContextMenu} as a whole.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public abstract class AContextMenuItemContainer
	implements Iterable<AContextMenuItem> {

	private ArrayList<AContextMenuItem> contextMenuEntries;

	protected ATableBasedDataDomain dataDomain = null;

	/**
	 * Constructor
	 */
	public AContextMenuItemContainer() {
		contextMenuEntries = new ArrayList<AContextMenuItem>();
	}

	/**
	 * Adds a context menu item to the container
	 * 
	 * @param contextMenuItem
	 */
	public void addContextMenuItem(AContextMenuItem contextMenuItem) {
		contextMenuEntries.add(contextMenuItem);
	}

	public void addItemContainer(AContextMenuItemContainer container) {
		for (AContextMenuItem item : container) {
			contextMenuEntries.add(item);
		}
	}

	/**
	 * Adds a separator to the context menu
	 */
	public void addSeparator() {
		contextMenuEntries.add(new SeparatorMenuItem());
	}

	/**
	 * Returns the list of context menu items
	 * 
	 * @return
	 */
	public ArrayList<AContextMenuItem> getContextMenuItems() {
		return contextMenuEntries;
	}

	@Override
	public Iterator<AContextMenuItem> iterator() {
		return contextMenuEntries.iterator();
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}
}
