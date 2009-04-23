package org.caleydo.rcp.views.swt.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.histogram.GLHistogram;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;
import org.caleydo.core.view.swt.browser.GenomeHTMLBrowserViewRep;
import org.caleydo.rcp.views.CaleydoViewPart;
import org.caleydo.rcp.views.opengl.GLGlyphView;
import org.caleydo.rcp.views.opengl.GLHeatMapView;
import org.caleydo.rcp.views.opengl.GLHierarchicalHeatMapView;
import org.caleydo.rcp.views.opengl.GLHistogramView;
import org.caleydo.rcp.views.opengl.GLParCoordsView;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.caleydo.rcp.views.swt.HTMLBrowserView;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.GlyphToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.HeatMapToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.HierarchicalHeatMapToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.ParCoordsToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.RemoteRenderingToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.browser.HTMLBrowserToolBarContent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Factory to create toolbar contents in dependency of views.
 * @author Werner Puff
 */
public class ToolBarContentFactory {

	Logger log = Logger.getLogger(ToolBarContentFactory.class.getName());
	
	/** reference to singleton instance */
	private static ToolBarContentFactory toolBarContentFactory = null;

	/** Maps views to its {@link ToolBarInfo} */
	private HashMap<Class<? extends IView>, ToolBarInfo> toolBarInfos;
	
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
		toolBarInfos = new HashMap<Class<? extends IView>, ToolBarInfo>();

		// FIXME wpuff: mapping should be read from config file (use wiring framework?)
		ToolBarInfo info;
		
		info = new ToolBarInfo();
		info.viewClass = GLHeatMap.class;
		info.contentClass = HeatMapToolBarContent.class;
		info.rcpID = GLHeatMapView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);
		
		info = new ToolBarInfo();
		info.viewClass = GLHierarchicalHeatMap.class;
		info.contentClass = HierarchicalHeatMapToolBarContent.class;
		info.rcpID = GLHierarchicalHeatMapView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLParallelCoordinates.class;
		info.contentClass = ParCoordsToolBarContent.class;
		info.rcpID = GLParCoordsView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLRemoteRendering.class;
		info.contentClass = RemoteRenderingToolBarContent.class;
		info.rcpID = GLRemoteRenderingView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

//		info = new ToolBarInfo();
//		info.viewClass = ; // FIXME gl-view class of clinical par coords
//		info.contentClass = ClinicalParCoordsToolBarContent.class;
//		info.rcpID = ClinicalGLParCoordsView.ID; 
//		info.ignored = false;
//		toolBarInfos.put(info.viewClass, info);
		
		info = new ToolBarInfo();
		info.viewClass = GLGlyph.class;
		info.contentClass = GlyphToolBarContent.class;
		info.rcpID = GLGlyphView.ID; 
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);
		
		info = new ToolBarInfo();
		info.viewClass = GenomeHTMLBrowserViewRep.class;
		info.contentClass = HTMLBrowserToolBarContent.class;
		info.rcpID = HTMLBrowserView.ID; 
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLHistogram.class;
		info.contentClass = null;
		info.rcpID = GLHistogramView.ID; 
		info.ignored = true;
		toolBarInfos.put(info.viewClass, info);
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

		IView view = retrieveView(viewID);

		if (view instanceof CaleydoViewPart) {
			return ((CaleydoViewPart) view).isAttached();
		} else if (view instanceof AView) {
			CaleydoViewPart relatedView = getRelatedViewPart(view);
			if (relatedView != null) {
				return relatedView.isAttached();
			}
		}

		log.warning("Could not find view with view-id=" + viewID + " in the workbench");
		return false;
	}

	private CaleydoViewPart getRelatedViewPart(IView view) {

		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		ToolBarInfo info = toolBarInfos.get(view.getClass());
		if (info != null) {
			String rcpViewPartID = info.rcpID;
			for (IWorkbenchWindow window : windows) {
				IWorkbenchPage[] pages = window.getPages();
				for (IWorkbenchPage page : pages) {
					IViewPart relatedView = page.findView(rcpViewPartID);
					if (relatedView != null) {
						if (relatedView instanceof CaleydoViewPart) {
							return (CaleydoViewPart) relatedView;
						}
					}
				}
			}
		}
		return null;
	}
	
	private AToolBarContent getContent(IView view) {
		AToolBarContent content = null;
		ToolBarInfo info = toolBarInfos.get(view.getClass());
		if (info != null) {
			Class<?> contentClass = info.contentClass;
			try {
				content = (AToolBarContent) contentClass.newInstance();
				content.setTargetViewData(view.getSerializableRepresentation());
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
			log.warning("no toolbar content providing class known for " + view + "; add its ToolBarInfo to ToolBarContentFactory");
		}
		return content;
	}

	/**
	 * checks if the given views should be ignored by the toolbar. 
	 * this means that the toolbar should stay as it is, e.g. when activating a 
	 * help view like histogramm 
	 * @param viewIDs list of view-ids as used by IViewManager
	 * @return true if the views should be ignored by the toolbar, false otherwise
	 */
	public boolean isIgnored(List<Integer> viewIDs) {
		boolean ignored = false; 
		for (int viewID : viewIDs) {
			IView view = retrieveView(viewID);
			ToolBarInfo info = toolBarInfos.get(view.getClass());
			if (info != null) {
				ignored |= info.ignored;
			}
		}
		return ignored;
	}
}
