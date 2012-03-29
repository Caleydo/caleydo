package org.caleydo.core.gui.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.caleydo.core.view.IView;

/**
 * Factory to create toolbar contents in dependency of views.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ToolBarContentFactory {

	/** reference to singleton instance */
	private static ToolBarContentFactory toolBarContentFactory = null;

	/** Maps view type to its {@link ToolBarInfo} */
	private HashMap<String, ToolBarInfo> toolBarInfos;

	/** Maps view type to its {@link ToolBarInfo} */
	private HashMap<String, AToolBarContent> toolBarContents;

	/** Hidden default constructor. */
	public ToolBarContentFactory() {
		toolBarContents = new HashMap<String, AToolBarContent>();
		toolBarInfos = new HashMap<String, ToolBarInfo>();
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
		}
		return toolBarContentFactory;
	}

	public void addToolBarContent(String viewType, boolean isIgnored, AToolBarContent toolBarContent) {
		toolBarContents.put(viewType, toolBarContent);

		ToolBarInfo info = new ToolBarInfo();
		info.viewType = viewType;
		info.ignored = isIgnored;
		toolBarInfos.put(info.viewType, info);
	}

	public AToolBarContent getToolBarContent(String viewType) {
		return toolBarContents.get(viewType);
	}

	// /**
	// * Initializes an instance of this class. Must be called before the first usage.
	// */
	// public void init() {

	// ToolBarInfo info;

	// info = new ToolBarInfo();
	// info.viewType = "org.caleydo.view.histogram";
	// info.ignored = true;
	// toolBarInfos.put(info.viewType, info);

	// info = new ToolBarInfo();
	// info.viewType = "org.caleydo.view.parcoords";
	// info.ignored = false;
	// toolBarInfos.put(info.viewType, info);

	// info = new ToolBarInfo();
	// info.viewType = "org.caleydo.view.bucket";
	// info.ignored = false;
	// toolBarInfos.put(info.viewType, info);

	// info = new ToolBarInfo();
	// info.viewType = "org.caleydo.view.radial";
	// info.ignored = false;
	// toolBarInfos.put(info.viewType, info);

	// info = new ToolBarInfo();
	// info.viewType = "org.caleydo.view.scatterplot";
	// info.ignored = false;
	// toolBarInfos.put(info.viewType, info);

	// info = new ToolBarInfo();
	// info.viewType = "org.caleydo.view.matchmaker";
	// info.ignored = false;
	// toolBarInfos.put(info.viewType, info);

	// info = new ToolBarInfo();
	// info.viewType = "org.caleydo.view.dataflipper";
	// info.ignored = false;
	// toolBarInfos.put(info.viewType, info);

	// info = new ToolBarInfo();
	// info.viewType = "org.caleydo.view.treemap";
	// info.ignored = false;
	// toolBarInfos.put(info.viewType, info);

	// info = new ToolBarInfo();
	// info.viewType = "org.caleydo.view.filter";
	// info.ignored = false;
	// toolBarInfos.put(info.viewType, info);
	// }

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

		for (IView view : views) {
			if (view != null) {
				AToolBarContent content = getContent(view);
				if (content != null) {
					int renderType = retrieveRenderType(view);
					content.setRenderType(renderType);
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
		return type;
	}

	private AToolBarContent getContent(IView view) {

		AToolBarContent content = null;
		ToolBarInfo info = toolBarInfos.get(view.getViewType());
		if (info != null) {

			Object toolBarContent = toolBarContentFactory.getToolBarContent(view.getViewType());
			if (toolBarContent == null)
				return null;

			content = (AToolBarContent) toolBarContent;
			content.setTargetViewData(view.getSerializableRepresentation());
		}

		return content;
	}

	/**
	 * checks if the given views should be ignored by the toolbar. this means that the toolbar should stay as
	 * it is, e.g. when activating a help view like histogramm
	 * 
	 * @param viewIDs
	 *            list of view-ids as used by ViewManager
	 * @return true if the views should be ignored by the toolbar, false otherwise
	 */
	public boolean isIgnored(List<IView> views) {
		boolean ignored = false;
		for (IView view : views) {
			if (view == null)
				return ignored;
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
			catch (NullPointerException ex) {
				ex.printStackTrace();
			}
		}
		return ignored;
	}
}
