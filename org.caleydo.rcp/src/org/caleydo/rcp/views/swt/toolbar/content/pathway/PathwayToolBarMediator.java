package org.caleydo.rcp.views.swt.toolbar.content.pathway;

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

	IEventPublisher eventPublisher;
	
	public PathwayToolBarMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public void loadPathway(PathwayGraph pathway) {
		LoadPathwayEvent event = new LoadPathwayEvent();
		event.setSender(this);
		event.setPathwayID(pathway.getID());
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
