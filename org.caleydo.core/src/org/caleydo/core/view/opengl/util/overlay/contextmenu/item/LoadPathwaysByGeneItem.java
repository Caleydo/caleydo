package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * <p>
 * Item for loading all pathway by specifying a gene. The event can either be specified manually or the
 * convenience method {@link LoadPathwaysByGeneItem#setRefSeqInt(int)} can be used, which creates the event
 * automatically.
 * </p>
 * <p>
 * Text and icon have default values but can be overriden.
 * </p>
 * 
 * @author Alexander Lex
 */
public class LoadPathwaysByGeneItem
	extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public LoadPathwaysByGeneItem() {
		super();
		setIconTexture(EIconTextures.CM_LOAD_DEPENDING_PATHWAYS);
		setText("Load depending Pathways");
	}

	/**
	 * Convenience method that automatically creates a {@link LoadPathwaysByGeneEvent} based on a RefSeqInt
	 * 
	 * @param david
	 *            the david ID
	 */
	public void setDavid(int david) {
		LoadPathwaysByGeneEvent loadPathwaysByGeneEvent = new LoadPathwaysByGeneEvent();
		loadPathwaysByGeneEvent.setSender(this);
		loadPathwaysByGeneEvent.setGeneID(david);
		loadPathwaysByGeneEvent.setIdType(EIDType.DAVID);
		registerEvent(loadPathwaysByGeneEvent);
	}
}
