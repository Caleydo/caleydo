package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.logging.Logger;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.event.view.pathway.DisableTexturesEvent;
import org.caleydo.core.manager.event.view.pathway.EnableTexturesEvent;

public class PathwayToolBarMediator
	implements IMediatorSender {

	Logger log = Logger.getLogger(PathwayToolBarMediator.class.getName());
	
	IEventPublisher eventPublisher;
	
	public PathwayToolBarMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public void enablePathwayTextures() {
		log.info("enablePathwayTextures()");
		EnableTexturesEvent event = new EnableTexturesEvent();
		eventPublisher.triggerEvent(event);
	}
	
	public void disablePathwayTextures() {
		log.info("disablePathwayTextures()");
		DisableTexturesEvent event = new DisableTexturesEvent();
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void triggerEvent(EMediatorType mediatorType, IEventContainer eventContainer) {
		// TODO Auto-generated method stub
	}

}
