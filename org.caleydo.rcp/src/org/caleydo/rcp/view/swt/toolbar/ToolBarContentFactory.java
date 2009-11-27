package org.caleydo.rcp.view.swt.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.histogram.GLHistogram;
import org.caleydo.core.view.opengl.canvas.hyperbolic.GLHyperbolic;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.remote.dataflipper.GLDataFlipper;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.GLParallelCoordinates;
import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.GLScatterplot;
import org.caleydo.rcp.Activator;
import org.caleydo.rcp.view.CaleydoRCPViewPart;
import org.caleydo.rcp.view.opengl.RcpGLDataFlipperView;
import org.caleydo.rcp.view.opengl.RcpGLGlyphView;
import org.caleydo.rcp.view.opengl.RcpGLHeatMapView;
import org.caleydo.rcp.view.opengl.RcpGLHistogramView;
import org.caleydo.rcp.view.opengl.RcpGLHyperbolicView;
import org.caleydo.rcp.view.opengl.RcpGLParCoordsView;
import org.caleydo.rcp.view.opengl.RcpGLRadialHierarchyView;
import org.caleydo.rcp.view.opengl.RcpGLRemoteRenderingView;
import org.caleydo.rcp.view.opengl.RcpGLScatterplotView;
import org.caleydo.rcp.view.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.GlyphToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.HeatMapToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.ParCoordsToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.dataflipper.DataFlipperToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.hyperbolic.HyperbolicToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.radial.RadialHierarchyToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.remote.RemoteRenderingToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.scatterplot.ScatterplotToolBarContent;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Factory to create toolbar contents in dependency of views.
 * 
 * @author Werner Puff
 */
public class ToolBarContentFactory {

	/** reference to singleton instance */
	private static ToolBarContentFactory toolBarContentFactory = null;

	/** Maps views to its {@link ToolBarInfo} */
	private HashMap<Class<? extends IView>, ToolBarInfo> toolBarInfos;

	/** Hidden default constructor. */
	public ToolBarContentFactory() {

	}

	/**
	 * Returns a singleton toolBarContentFactory instance. The singleton is created on first call to this
	 * method.
	 * 
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
	 * Initializes an instance of this class. Must be called before the first usage.
	 */
	public void init() {
		toolBarInfos = new HashMap<Class<? extends IView>, ToolBarInfo>();

		// FIXME wpuff: mapping should be read from config file (use wiring framework?)
		ToolBarInfo info;

		info = new ToolBarInfo();
		info.viewClass = GLHeatMap.class;
		info.contentClass = HeatMapToolBarContent.class;
		info.rcpID = RcpGLHeatMapView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

		// info = new ToolBarInfo();
		// info.viewClass = GLHierarchicalHeatMap.class;
		// info.contentClass = HierarchicalHeatMapToolBarContent.class;
		// info.rcpID = GLHierarchicalHeatMapView.ID;
		// info.ignored = false;
		// toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLParallelCoordinates.class;
		info.contentClass = ParCoordsToolBarContent.class;
		info.rcpID = RcpGLParCoordsView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLRemoteRendering.class;
		info.contentClass = RemoteRenderingToolBarContent.class;
		info.rcpID = RcpGLRemoteRenderingView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);
		
		info = new ToolBarInfo();
		info.viewClass = GLDataFlipper.class;
		info.contentClass = DataFlipperToolBarContent.class;
		info.rcpID = RcpGLDataFlipperView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

		// info = new ToolBarInfo();
		// info.viewClass = ; // FIXME gl-view class of clinical par coords
		// info.contentClass = ClinicalParCoordsToolBarContent.class;
		// info.rcpID = ClinicalGLParCoordsView.ID;
		// info.ignored = false;
		// toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLGlyph.class;
		info.contentClass = GlyphToolBarContent.class;
		info.rcpID = RcpGLGlyphView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLHistogram.class;
		info.contentClass = null;
		info.rcpID = RcpGLHistogramView.ID;
		info.ignored = true;
		toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLRadialHierarchy.class;
		info.contentClass = RadialHierarchyToolBarContent.class;
		info.rcpID = RcpGLRadialHierarchyView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLHyperbolic.class;
		info.contentClass = HyperbolicToolBarContent.class;
		info.rcpID = RcpGLHyperbolicView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);

		info = new ToolBarInfo();
		info.viewClass = GLScatterplot.class;
		info.contentClass = ScatterplotToolBarContent.class;
		info.rcpID = RcpGLScatterplotView.ID;
		info.ignored = false;
		toolBarInfos.put(info.viewClass, info);
	}

	/**
	 * Looks for toolbar content providers for a specified list of view types
	 * 
	 * @param viewTypes
	 *            collection of view-types to obtain a tool bar content for
	 * @return toolbar content providers for the given view types
	 */
	public List<AToolBarContent> getToolBarContent(Integer... viewIDs) {
		return getToolBarContent(viewIDs);
	}

	/**
	 * Looks for toolbar content providers for a specified list of view types
	 * 
	 * @param viewTypes
	 *            collection of view-types to obtain a tool bar content for
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
	 * Retrieves a view to a given view-id from the view manager TODO: move this method to ViewManager?
	 * 
	 * @param viewID
	 *            to get to related view
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
	 * 
	 * @param view
	 *            to get the content type for
	 * @param isAttached
	 *            information about attach status of the view, true is attached, false otherwise
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
	 * 
	 * @param viewID
	 *            id of the view as used by {@link IViewManager}
	 * @return true if the view is attached, false otherwise
	 */
	private boolean isViewAttached(int viewID) {

		IView view = retrieveView(viewID);

		if (view instanceof CaleydoRCPViewPart) {
			return ((CaleydoRCPViewPart) view).isAttached();
		}
		else if (view instanceof AView) {
			CaleydoRCPViewPart relatedView = getRelatedViewPart(view);
			if (relatedView != null) {
				return relatedView.isAttached();
			}
		}

		GeneralManager.get().getLogger().log(
			new Status(Status.WARNING, Activator.PLUGIN_ID, "Could not find view with view-id=" + viewID
				+ " in the workbench"));
		return false;
	}

	private CaleydoRCPViewPart getRelatedViewPart(IView view) {

		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		ToolBarInfo info = toolBarInfos.get(view.getClass());
		if (info != null) {
			String rcpViewPartID = info.rcpID;
			for (IWorkbenchWindow window : windows) {
				IWorkbenchPage[] pages = window.getPages();
				for (IWorkbenchPage page : pages) {
					IViewPart relatedView = page.findView(rcpViewPartID);
					if (relatedView != null) {
						if (relatedView instanceof CaleydoRCPViewPart) {
							return (CaleydoRCPViewPart) relatedView;
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
			}
			catch (Exception e) {
				GeneralManager.get().getLogger().log(
					new Status(Status.WARNING, Activator.PLUGIN_ID,
						"No toolbar content providing class known for " + view
							+ "; add its ToolBarInfo to ToolBarContentFactory"));
				// e.printStackTrace();
			}
		}

		return content;
	}

	/**
	 * checks if the given views should be ignored by the toolbar. this means that the toolbar should stay as
	 * it is, e.g. when activating a help view like histogramm
	 * 
	 * @param viewIDs
	 *            list of view-ids as used by IViewManager
	 * @return true if the views should be ignored by the toolbar, false otherwise
	 */
	public boolean isIgnored(List<Integer> viewIDs) {
		boolean ignored = false;
		for (int viewID : viewIDs) {
			ToolBarInfo info;
			IView view;
			try {
				view = retrieveView(viewID);
				info = toolBarInfos.get(view.getClass());
				if (info != null) {
					ignored |= info.ignored;
				}
			}
			catch (IllegalArgumentException ex) {
				ignored = true;
			}
		}
		return ignored;
	}
}
