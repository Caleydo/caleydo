package org.caleydo.rcp.views.swt.toolbar.content.pathway;

import java.util.logging.Logger;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.bucket.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.pathway.DisableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.DisableTexturesEvent;
import org.caleydo.core.manager.event.view.pathway.EnableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.EnableTexturesEvent;
import org.caleydo.core.manager.general.GeneralManager;

public class PathwayToolBarMediator {

	Logger log = Logger.getLogger(PathwayToolBarMediator.class.getName());
	
	IEventPublisher eventPublisher;
	
	public PathwayToolBarMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public void loadPathway(PathwayGraph pathway) {
		log.info("loadPathway()");
		LoadPathwayEvent event = new LoadPathwayEvent();
		event.setPathwayID(pathway.getID());
		eventPublisher.triggerEvent(event);
	}
	
	public void enableTextures() {
		log.info("enablePathwayTextures()");
		EnableTexturesEvent event = new EnableTexturesEvent();
		eventPublisher.triggerEvent(event);
	}
	
	public void disableTextures() {
		log.info("disablePathwayTextures()");
		DisableTexturesEvent event = new DisableTexturesEvent();
		eventPublisher.triggerEvent(event);
	}

	public void enableNeighborhood() {
		log.info("enableNeighborhoodTextures()");
		EnableNeighborhoodEvent event = new EnableNeighborhoodEvent();
		eventPublisher.triggerEvent(event);
	}
	
	public void disableNeighborhood() {
		log.info("disableNeighborhoodTextures()");
		DisableNeighborhoodEvent event = new DisableNeighborhoodEvent();
		eventPublisher.triggerEvent(event);
	}

}
