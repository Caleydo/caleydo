package org.caleydo.core.view.opengl.util.overlay.contextmenue.item;

import org.caleydo.core.view.opengl.util.overlay.contextmenue.AContextMenueItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

public class LoadPathwayItem
	extends AContextMenueItem {

	public LoadPathwayItem() {
		super();
		setIconTexture(EIconTextures.LOAD_DEPENDEN_PATHWAYS);
		setText("Load depending Pathways");
	}

}
