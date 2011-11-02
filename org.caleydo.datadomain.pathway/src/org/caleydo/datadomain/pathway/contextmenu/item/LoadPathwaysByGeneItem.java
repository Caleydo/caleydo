package org.caleydo.datadomain.pathway.contextmenu.item;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

/**
 * <p>
 * Item for loading all pathway by specifying a gene. The event can either be
 * specified manually or the convenience method
 * {@link LoadPathwaysByGeneItem#setRefSeqInt(int)} can be used, which creates
 * the event automatically.
 * </p>
 * <p>
 * Text and icon have default values but can be overriden.
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class LoadPathwaysByGeneItem extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public LoadPathwaysByGeneItem() {
		super();
		//setIconTexture(EIconTextures.CM_LOAD_DEPENDING_PATHWAYS);
		setLabel("Load depending Pathways");
	}

	/**
	 * Convenience method that automatically creates a
	 * {@link LoadPathwaysByGeneEvent} based on a RefSeqInt
	 * 
	 * @param david
	 *            the david ID
	 */
	public void setDavidID(IDType idType, int david, String dataDomainID) {
		
		LoadPathwaysByGeneEvent loadPathwaysByGeneEvent = new LoadPathwaysByGeneEvent();
		loadPathwaysByGeneEvent.setSender(this);
		loadPathwaysByGeneEvent.setGeneID(david);
		loadPathwaysByGeneEvent.setTableIDType(idType);
		loadPathwaysByGeneEvent.setDataDomainID(dataDomainID);
		registerEvent(loadPathwaysByGeneEvent);
	}
}
