package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * <p>
 * Item for showing all pathways that contain a specific gene in a sub menu, where these pathways can be
 * loaded individually. The sub-pathways can either be specified manually or the convenience method
 * {@link ShowPathwaysByGeneItem#setRefSeqInt(int)} can be used, which creates the sub-menus automatically.
 * </p>
 * <p>
 * Text and icon have default values but can be overriden.
 * </p>
 * 
 * @author Alexander Lex
 */
public class ShowPathwaysByGeneItem
	extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ShowPathwaysByGeneItem() {
		super();
		setIconTexture(EIconTextures.LOAD_DEPENDING_PATHWAYS);
		setText("Pathways");
	}

	/**
	 * Convenience method that automatically creates a {@link LoadPathwaysByGeneEvent} based on a RefSeqInt
	 * 
	 * @param iRefSeq
	 *            the int code associated with a refseq
	 */
	public void setRefSeqInt(int iRefSeq) {
		// RESOLVE GENE LISTS HERE

		addSubItem(new LoadPathwaysByPathwayIDItem(1234));
		addSubItem(new LoadPathwaysByPathwayIDItem(1234));
		addSubItem(new LoadPathwaysByPathwayIDItem(1234));
		// LoadPathwaysByGeneEvent loadPathwaysByGeneEvent = new LoadPathwaysByGeneEvent();
		// loadPathwaysByGeneEvent.setGeneID(iRefSeq);
		// loadPathwaysByGeneEvent.setIdType(EIDType.REFSEQ_MRNA_INT);
		// registerEvent(loadPathwaysByGeneEvent);
	}

}
