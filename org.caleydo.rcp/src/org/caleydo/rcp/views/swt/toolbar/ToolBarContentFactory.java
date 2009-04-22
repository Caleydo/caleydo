package org.caleydo.rcp.views.swt.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;
import org.caleydo.rcp.perspective.PartListener;
import org.caleydo.rcp.views.opengl.GLParCoordsView;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.ClinicalParCoordsToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.GlyphToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.HeatMapToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.HierarchicalHeatMapToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.ParCoordsToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.RemoteRenderingToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.browser.HTMLBrowserToolBarContent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Factory to create toolbar contents in dependency of views.
 * @author Werner Puff
 */
public class ToolBarContentFactory {

	Logger log = Logger.getLogger(ToolBarContentFactory.class.getName());
	
	/** reference to singleton instance */
	private static ToolBarContentFactory toolBarContentFactory = null;

	/** Maps view types (=key) to toolbar-content classes (=value) */
	private HashMap<String, Class<?>> contentMap;
	
	/** Hidden default constructor. */
	public ToolBarContentFactory() {
		
	}

	/**
	 * Returns a singleton toolBarContentFactory instance. 
	 * The singleton is created on first call to this method.
	 * @return singleton GeneralManager instance
	 */
	public static ToolBarContentFactory get() {
		if (toolBarContentFactory == null) {
			toolBarContentFactory = new ToolBarContentFactory();
			toolBarContentFactory.init();
		}
		return toolBarContentFactory;
	}

	/**
	 * Initializes an instance of this class. 
	 * Must be called before the first usage.  
	 */
	public void init() {
		contentMap = new HashMap<String, Class<?>>();
		
		// FIXME puff: mapping should be read from config file (use wiring framework?)
		AToolBarContent toolBarContent;
		
		toolBarContent = new HeatMapToolBarContent();
		contentMap.put(toolBarContent.getViewClass().getName(), HeatMapToolBarContent.class);

		toolBarContent = new HierarchicalHeatMapToolBarContent();
		contentMap.put(toolBarContent.getViewClass().getName(), HierarchicalHeatMapToolBarContent.class);

		toolBarContent = new ParCoordsToolBarContent();
		contentMap.put(toolBarContent.getViewClass().getName(), ParCoordsToolBarContent.class);

		toolBarContent = new RemoteRenderingToolBarContent();
		contentMap.put(toolBarContent.getViewClass().getName(), RemoteRenderingToolBarContent.class);

		toolBarContent = new ClinicalParCoordsToolBarContent();
		contentMap.put(toolBarContent.getViewClass().getName(), ClinicalParCoordsToolBarContent.class);
		
		toolBarContent = new GlyphToolBarContent();
		contentMap.put(toolBarContent.getViewClass().getName(), GlyphToolBarContent.class);
		
		toolBarContent = new HTMLBrowserToolBarContent();
		contentMap.put(toolBarContent.getViewClass().getName(), HTMLBrowserToolBarContent.class);
	}

	/**
	 * Looks for toolbar content providers for a specified list of view types
	 * @param viewTypes collection of view-types to obtain a tool bar content for
	 * @return toolbar content providers for the given view types
	 */
	public List<AToolBarContent> getToolBarContent(Integer ... viewIDs) {
		return getToolBarContent(viewIDs);
	}
	
	/**
	 * Looks for toolbar content providers for a specified list of view types
	 * @param viewTypes collection of view-types to obtain a tool bar content for
	 * @return toolbar content providers for the given view types
	 */
	public List<AToolBarContent> getToolBarContent(List<Integer> viewIDs) {
		List<AToolBarContent> contents = new ArrayList<AToolBarContent>();

		boolean isViewAttached = true;
		if (viewIDs.size() > 0) {
			isViewAttached = isViewAttached(viewIDs.get(0));
		}

		for (int viewID : viewIDs) {
			IView view = retrieveView(viewID);
			if (view != null) {
				AToolBarContent content = getContent((IView) view);
				if (content != null) {
					int renderType = retrieveRenderType(view);
					content.setRenderType(renderType);
					content.setAttached(isViewAttached);
					contents.add(content);
				}
			}
		}
		return contents;
	}

	/**
	 * Retrieves a view to a given view-id from the view manager
	 * TODO: move this method to ViewManager?
	 * @param viewID to get to related view
	 * @return view related to the given view-id
	 */
	private IView retrieveView(int viewID) {
		IViewManager canvasManager = GeneralManager.get().getViewGLCanvasManager();
		IView view = canvasManager.getGLEventListener(viewID);
		if (view == null) {
			view = canvasManager.getItem(viewID);
		}
		return view;
	}

	/**
	 * determines and return the appropiate toolbar-content type for a given view.
	 * @param view to get the content type for
	 * @param isAttached information about attach status of the view, true is attached, false otherwise
	 * @return toolbar-content-type of the given view
	 */
	private int retrieveRenderType(IView view) {
		int type = AToolBarContent.STANDARD_RENDERING;
		if (view instanceof AGLEventListener) {
			AGLEventListener glView = (AGLEventListener) view;
			if (glView.isRenderedRemote()) {
				type = AToolBarContent.REMOTE_RENDERING;
			}
		}
		return type;
	}

	/**
	 * determines if a view is attached to the caleydo's main window or not
	 * @param viewID id of the view as used by {@link IViewManager}
	 * @return true if the view is attached, false otherwise
	 */
	private boolean isViewAttached(int viewID) {

		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
			// FIXXXME what should be done when during startup?
			return true;
		}

		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
		IView view = retrieveView(viewID);
		boolean isAttached = true;

		IViewPart relatedView = null;
		if (view instanceof GLRemoteRendering) {
			relatedView = workbenchPage.findView(GLRemoteRenderingView.ID);
		} else if (view instanceof GLParallelCoordinates) {
			relatedView = workbenchPage.findView(GLParCoordsView.ID);
		}

		if (relatedView != null) {
			isAttached = PartListener.isViewAttached(relatedView);
		}

		return isAttached;
	}

	private AToolBarContent getContent(IView view) {
		AToolBarContent content = null;
		String type = view.getClass().getName();
		Class<?> contentClass = contentMap.get(type);
		if (contentClass != null) {
			
			try {
				content = (AToolBarContent) contentClass.newInstance();
				content.setTargetViewID(view.getID());
				if (view instanceof AGLEventListener) {
					if (((AGLEventListener) view).isRenderedRemote()) {
						content.setRenderType(AToolBarContent.REMOTE_RENDERING);
					}
				}
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		} else {
			log.warning("no toolbar content providing class known for " + type);
		}
		return content;
	}
}
