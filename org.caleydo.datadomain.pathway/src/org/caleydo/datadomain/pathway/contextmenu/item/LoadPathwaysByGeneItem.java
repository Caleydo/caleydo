/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.pathway.contextmenu.item;

import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.pathway.listener.LoadPathwaysByGeneEvent;

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
		// setIconTexture(EIconTextures.CM_LOAD_DEPENDING_PATHWAYS);
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
		loadPathwaysByGeneEvent.setEventSpace(dataDomainID);
		registerEvent(loadPathwaysByGeneEvent);
	}
}
