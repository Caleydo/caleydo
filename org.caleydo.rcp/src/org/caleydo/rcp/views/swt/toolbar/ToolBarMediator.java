package org.caleydo.rcp.views.swt.toolbar;

import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.manager.event.EEventType;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.event.ViewActivationCommandEventContainer;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.eclipse.swt.widgets.Display;

/**
 * Event handler to change toolbar according to gloabl caleydo-events. 
 * For example to change to displays toolbar in dependency of the active view. 
 * @author Werner Puff
 */
public class ToolBarMediator
	implements IMediatorReceiver {

	public static Logger log = Logger.getLogger(ToolBarMediator.class.getName());

	/** the related toolbar that should react to events */
	ToolBarView toolBarView;
	
	public ToolBarMediator() {
		
	}

	@Override
	public void handleExternalEvent(IMediatorSender eventTrigger, IEventContainer eventContainer,
		EMediatorType mediatorType) {

		log.info("handleExternalEvent() called");
		
		EEventType eventType = eventContainer.getEventType();
		if (eventType.equals(EEventType.VIEW_COMMAND)) {
			ViewActivationCommandEventContainer activationEvent;
			activationEvent = (ViewActivationCommandEventContainer) eventContainer;

			List<Integer> viewIDs = activationEvent.getViewIDs(); 
			
			ToolBarContentFactory contentFactory = ToolBarContentFactory.get();
			List<AToolBarContent> toolBarContents = contentFactory.getToolBarContent(viewIDs);
			
			IToolBarRenderer renderer = toolBarView.getToolBarRenderer();
			Runnable job = renderer.createRenderJob(toolBarView, toolBarContents);
			Display display = toolBarView.getParentComposite().getDisplay(); 
			display.asyncExec(job);
		}
	}

	public ToolBarView getToolBarView() {
		return toolBarView;
	}

	public void setToolBarView(ToolBarView toolBarView) {
		this.toolBarView = toolBarView;
	}

}
