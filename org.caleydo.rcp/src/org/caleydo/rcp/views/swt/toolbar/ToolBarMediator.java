package org.caleydo.rcp.views.swt.toolbar;

import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.event.EEventType;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.ViewActivationCommandEventContainer;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;
import org.caleydo.rcp.views.CaleydoViewPart;
import org.caleydo.rcp.views.opengl.AGLViewPart;
import org.caleydo.rcp.views.opengl.GLGlyphView;
import org.caleydo.rcp.views.opengl.GLHeatMapView;
import org.caleydo.rcp.views.opengl.GLHierarchicalHeatMapView;
import org.caleydo.rcp.views.opengl.GLParCoordsView;
import org.caleydo.rcp.views.opengl.GLPathwayView;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;

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
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer,
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
