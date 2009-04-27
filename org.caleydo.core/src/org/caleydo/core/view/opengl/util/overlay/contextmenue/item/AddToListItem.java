package org.caleydo.core.view.opengl.util.overlay.contextmenue.item;

import org.caleydo.core.view.opengl.util.overlay.contextmenue.AContextMenueItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

public class AddToListItem
	extends AContextMenueItem {

	public AddToListItem() {
		super();
		setIconTexture(EIconTextures.SAVE_TO_LIST_HEAT_MAP);
		setText("Save to list heat map");
	}

}
