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
package org.caleydo.core.view.contextmenu.item;

import org.caleydo.core.event.view.group.ExportContentGroupsEvent;
import org.caleydo.core.event.view.group.ExportDimensionGroupsEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

/**
 * Item for exporting groups/clusters
 * 
 * @author Bernhard Schlegl
 */
public class ExportClustersItem
	extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ExportClustersItem() {
		// setIconTexture(EIconTextures.CM_LOAD_DEPENDING_PATHWAYS);
		setLabel("Export Groups");
	}

	/**
	 * Depending on which group info should be handled a boolean has to be table. True for genes, false for
	 * experiments
	 * 
	 * @param bGeneGroup
	 *            if true gene groups will be handled, if false experiment groups
	 */
	public void setGeneExperimentFlag(boolean bGeneGroup) {

		if (bGeneGroup) {
			ExportContentGroupsEvent exportGroupEvent = new ExportContentGroupsEvent();
			exportGroupEvent.setSender(this);
			registerEvent(exportGroupEvent);
		}
		else {
			ExportDimensionGroupsEvent exportGroupEvent = new ExportDimensionGroupsEvent();
			exportGroupEvent.setSender(this);
			registerEvent(exportGroupEvent);
		}
	}
}
