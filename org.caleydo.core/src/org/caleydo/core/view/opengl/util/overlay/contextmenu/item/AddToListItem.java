package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Item that adds a selected element to the list heat map for detailed inspection FIXME: this is not
 * implemented yet - only the skeleton is here
 * 
 * @author Alexander Lex
 */
public class AddToListItem
	extends AContextMenuItem {

	public AddToListItem() {
		super();
		setIconTexture(EIconTextures.SAVE_TO_LIST_HEAT_MAP);
		setText("Save to list heat map");
	}

}
