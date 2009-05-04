package org.caleydo.rcp.views.swt;

import java.util.logging.Logger;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;

public class SearchViewMediator {

	Logger log = Logger.getLogger(SearchViewMediator.class.getName());
	
	IEventPublisher eventPublisher;
	
	public SearchViewMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public void loadPathway(int pathwayID) {
		log.info("loadPathway()");
		LoadPathwayEvent event = new LoadPathwayEvent();
		event.setSender(this);
		event.setPathwayID(pathwayID);
		eventPublisher.triggerEvent(event);
	}
	
	public void loadURLInBrowser(String url) {
		log.info("loadURLInBrowser()");
		ChangeURLEvent event = new ChangeURLEvent();
		event.setSender(this);
		event.setUrl(url);
		eventPublisher.triggerEvent(event);
	}
	
	public void loadPathwayByGene(int davidID) {
		log.info("loadPathwayByGene()");
		LoadPathwaysByGeneEvent loadPathwaysByGeneEvent = new LoadPathwaysByGeneEvent();
		loadPathwaysByGeneEvent.setSender(this);
		loadPathwaysByGeneEvent.setGeneID((davidID));
		loadPathwaysByGeneEvent.setIdType(EIDType.DAVID);
		eventPublisher.triggerEvent(loadPathwaysByGeneEvent);		
	}
}
