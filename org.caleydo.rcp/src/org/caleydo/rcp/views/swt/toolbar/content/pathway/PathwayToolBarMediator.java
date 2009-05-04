package org.caleydo.rcp.views.swt.toolbar.content.pathway;

import java.util.logging.Logger;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.pathway.DisableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.DisableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.DisableTexturesEvent;
import org.caleydo.core.manager.event.view.pathway.EnableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.EnableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.EnableTexturesEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * mediator for pathway-related toolbar items
 * @author Werner Puff
 */
public class PathwayToolBarMediator {

	Logger log = Logger.getLogger(PathwayToolBarMediator.class.getName());
	
	IEventPublisher eventPublisher;
	
	public PathwayToolBarMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public void loadPathway(PathwayGraph pathway) {
		log.info("loadPathway()");
		LoadPathwayEvent event = new LoadPathwayEvent();
		event.setSender(this);
		event.setPathwayID(pathway.getID());
		eventPublisher.triggerEvent(event);
	}
	
	public void enableTextures() {
		log.info("enablePathwayTextures()");
		EnableTexturesEvent event = new EnableTexturesEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}
	
	public void disableTextures() {
		log.info("disablePathwayTextures()");
		DisableTexturesEvent event = new DisableTexturesEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void enableNeighborhood() {
		log.info("enableNeighborhood()");
		EnableNeighborhoodEvent event = new EnableNeighborhoodEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}
	
	public void disableNeighborhood() {
		log.info("disableNeighborhood()");
		DisableNeighborhoodEvent event = new DisableNeighborhoodEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void enableGeneMapping() {
		log.info("enableGeneMapping()");
		EnableGeneMappingEvent event = new EnableGeneMappingEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}
	
	public void disableGeneMapping() {
		log.info("disableGeneMappingTextures()");
		DisableGeneMappingEvent event = new DisableGeneMappingEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}
	
}
