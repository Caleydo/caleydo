package org.caleydo.view.pathway.toolbar;

import javax.xml.crypto.Data;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.view.pathway.DisableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.DisableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.DisableTexturesEvent;
import org.caleydo.core.manager.event.view.pathway.EnableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.EnableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.EnableTexturesEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
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

	public void enableTextures() {
		EnableTexturesEvent event = new EnableTexturesEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void disableTextures() {
		DisableTexturesEvent event = new DisableTexturesEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void enableNeighborhood() {
		EnableNeighborhoodEvent event = new EnableNeighborhoodEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void disableNeighborhood() {
		DisableNeighborhoodEvent event = new DisableNeighborhoodEvent();
		event.setSender(this);
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
