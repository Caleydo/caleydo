package org.caleydo.rcp.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.view.creator.IViewCreator;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
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

	/** Maps view IDs to its {@link ToolBarInfo} */
	private HashMap<String, ToolBarInfo> toolBarInfos;

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
		toolBarInfos = new HashMap<String, ToolBarInfo>();

		ToolBarInfo info;

		// TODO glyph, clinicalparcoords, dataflipper

		info = new ToolBarInfo();
		info.viewType = "org.caleydo.view.heatmap";
		info.ignored = false;
		toolBarInfos.put(info.viewType, info);

		// Removed because it is empty. Insert again when you have content for the toolbar.
		// info = new ToolBarInfo();
		// info.viewType = "org.caleydo.view.heatmap.hierarchical";
		// info.ignored = false;
		// toolBarInfos.put(info.viewType, info);

		info = new ToolBarInfo();
		info.viewType = "org.caleydo.view.parcoords";
		info.ignored = false;
		toolBarInfos.put(info.viewType, info);

		info = new ToolBarInfo();
		info.viewType = "org.caleydo.view.bucket";
		info.ignored = false;
		toolBarInfos.put(info.viewType, info);

		info = new ToolBarInfo();
		info.viewType = "org.caleydo.view.radial";
		info.ignored = false;
		toolBarInfos.put(info.viewType, info);

		info = new ToolBarInfo();
		info.viewType = "org.caleydo.view.scatterplot";
		info.ignored = false;
		toolBarInfos.put(info.viewType, info);
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
	public List<AToolBarContent> getToolBarContent(List<IView> views) {
		List<AToolBarContent> contents = new ArrayList<AToolBarContent>();

		boolean isViewAttached = true;
		if (views.size() > 0) {
			isViewAttached = isViewAttached(views.get(0));
		}

		for (IView view : views) {
			if (view != null) {
				AToolBarContent content = getContent(view);
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
		if (view instanceof AGLView) {
			AGLView glView = (AGLView) view;
			if (glView.rendersContextOnly()) {
				type = AToolBarContent.CONTEXT_ONLY_RENDERING;
			}
		}
		return type;
	}

	/**
	 * determines if a view is attached to the caleydo's main window or not
	 * 
	 * @param viewType
	 *            view as used by {@link IViewManager}
	 * @return true if the view is attached, false otherwise
	 */
	private boolean isViewAttached(IView view) {

		if (view instanceof CaleydoRCPViewPart) {
			return ((CaleydoRCPViewPart) view).isAttached();
		}
		else if (view instanceof AView) {
			CaleydoRCPViewPart relatedView = getRelatedViewPart(view);
			if (relatedView != null) {
				return relatedView.isAttached();
			}
		}

		// GeneralManager.get().getLogger().log(
		// new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Could not find view with view-id=" + view
		// + " in the workbench"));
		return false;
	}

	private CaleydoRCPViewPart getRelatedViewPart(IView view) {

		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		ToolBarInfo info = toolBarInfos.get(view.getViewType());
		if (info != null) {
			String rcpViewPartID = info.viewType;
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
		ToolBarInfo info = toolBarInfos.get(view.getViewType());
		if (info != null) {
			IViewCreator viewCreator =
				GeneralManager.get().getViewGLCanvasManager().getViewCreator(info.viewType);
			if (viewCreator == null)
				return null;

			content = (AToolBarContent) viewCreator.createToolBarContent();
			content.setTargetViewData(view.getSerializableRepresentation());
			if (view instanceof AGLView) {
				if (((AGLView) view).rendersContextOnly()) {
					content.setRenderType(AToolBarContent.CONTEXT_ONLY_RENDERING);
				}
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
	public boolean isIgnored(List<IView> views) {
		boolean ignored = false;
		for (IView view : views) {
			ToolBarInfo info;
			try {
				info = toolBarInfos.get(view.getViewType());
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
