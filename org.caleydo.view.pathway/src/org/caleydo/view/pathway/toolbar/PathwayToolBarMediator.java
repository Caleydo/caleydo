package org.caleydo.view.pathway.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.pathway.DisableGeneMappingEvent;
import org.caleydo.core.event.view.pathway.EnableGeneMappingEvent;
import org.caleydo.core.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

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
		EnableGeneMappingEvent event = new EnableGeneMappingEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void disableGeneMapping() {
		DisableGeneMappingEvent event = new DisableGeneMappingEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}
}
