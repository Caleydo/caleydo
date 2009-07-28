package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Item that adds a selected element to the bookmark container
 * 
 * @author Alexander Lex
 */
public class BookmarkItem
	extends AContextMenuItem {

	/**
	 * Constructor which takes a single storage index.
	 * 
	 * @param iStorageIndex
	 */
	public BookmarkItem(int iStorageIndex) {
		super();
		setIconTexture(EIconTextures.CM_BOOKMARK);
		setText("Bookmark");
		VirtualArrayUpdateEvent event = new VirtualArrayUpdateEvent();
		IVirtualArrayDelta delta = new VirtualArrayDelta(EVAType.CONTENT_BOOKMARKS, EIDType.EXPRESSION_INDEX);
		delta.add(VADeltaItem.append(iStorageIndex));
		event.setVirtualArrayDelta((VirtualArrayDelta) delta);
		registerEvent(event);
	}

	/**
	 * Constructor which takes an array of storage indices.
	 * 
	 * @param alStorageIndex
	 */
	public BookmarkItem(ArrayList<Integer> alStorageIndex) {
		super();
		setIconTexture(EIconTextures.CM_BOOKMARK);
		setText("Bookmark");
		VirtualArrayUpdateEvent event = new VirtualArrayUpdateEvent();
		IVirtualArrayDelta delta = new VirtualArrayDelta(EVAType.CONTENT_BOOKMARKS, EIDType.EXPRESSION_INDEX);

		for (Integer storageIndex : alStorageIndex)
			delta.add(VADeltaItem.append(storageIndex));

		event.setVirtualArrayDelta((VirtualArrayDelta) delta);
		registerEvent(event);
	}

}
