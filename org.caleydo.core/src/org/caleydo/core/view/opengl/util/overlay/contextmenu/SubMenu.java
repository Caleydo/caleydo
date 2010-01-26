package org.caleydo.core.view.opengl.util.overlay.contextmenu;

import java.util.ArrayList;

/**
 * This class stores all data related to a sub-menu of a context menu. A sub-menu is the sum of all the items
 * within one hierarchy of a context menu. This class is ment for package-private access
 * 
 * @author Alexander Lex
 */
class SubMenu {
	/** The list of entries in the sub-menu */
	ArrayList<IContextMenuEntry> contextMenuEntries;
	int contextMenuID = -1;
	float xOrigin = -1;
	float yOrigin = -1;
	float width;
	float height;
	float maxTextWidth;
	boolean isScrollingNecessary = false;
	int nrVisibleElements = 0;
	/** indicates which is the first visible element, default: 1, since the button is always visible */
	int elementRangeStart = 1;

	boolean scrollButtonUpActive = false;
	boolean scrollButtonUpOver = false;

	boolean scrollButtonDownActive = false;
	boolean scrollButtonDownOver = false;

	SubMenu() {
		contextMenuEntries = new ArrayList<IContextMenuEntry>();
	}

	/**
	 * Adds a context menu entry to the sub-menu
	 * 
	 * @param entry
	 *            the entry to be added
	 */
	void add(IContextMenuEntry entry) {
		contextMenuEntries.add(entry);
	}
}
