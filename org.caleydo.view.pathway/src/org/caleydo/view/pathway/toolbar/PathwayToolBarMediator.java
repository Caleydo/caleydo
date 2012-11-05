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
package org.caleydo.view.pathway.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.pathway.LoadPathwayEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.pathway.event.EnableGeneMappingEvent;

/**
 * Mediator for pathway-related toolbar items
 * 
 * @author Werner Puff
 */
public class PathwayToolBarMediator {

	private EventPublisher eventPublisher;

	private String mappingDataDomainID;

	public PathwayToolBarMediator(String mappingDataDomainID) {
		eventPublisher = GeneralManager.get().getEventPublisher();
		this.mappingDataDomainID = mappingDataDomainID;
	}

	public void loadPathway(PathwayGraph pathway) {
		LoadPathwayEvent event = new LoadPathwayEvent();
		event.setSender(this);
		event.setPathwayID(pathway.getID());
		event.setDataDomainID(mappingDataDomainID);
		eventPublisher.triggerEvent(event);
	}

	public void enableGeneMapping() {
		EnableGeneMappingEvent event = new EnableGeneMappingEvent(true);
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void disableGeneMapping() {
		EnableGeneMappingEvent event = new EnableGeneMappingEvent(false);
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}
}
