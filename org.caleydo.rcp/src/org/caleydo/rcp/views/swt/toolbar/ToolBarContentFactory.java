package org.caleydo.rcp.views.swt.toolbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.ClinicalParCoordsToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.GlyphToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.HeatMapToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.HierarchicalHeatMapToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.ParCoordsToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.RemoteRenderingToolBarContent;

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
	public List<AToolBarContent> getToolBarContent(Collection<Integer> viewIDs) {
		IViewManager canvasManager = GeneralManager.get().getViewGLCanvasManager();
		List<AToolBarContent> contents = new ArrayList<AToolBarContent>();

		for (int viewID : viewIDs) {
			AGLEventListener glView = canvasManager.getGLEventListener(viewID);
			String type = glView.getClass().getName();
			Class<?> contentClass = contentMap.get(type);
			if (contentClass != null) {
				try {
					AToolBarContent content = (AToolBarContent) contentClass.newInstance();
					content.setTargetViewID(viewID);
					if (glView.isRenderedRemote()) {
						content.setContentType(AToolBarContent.REMOTE_RENDERED_CONTENT);
					}
					contents.add(content);
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
			} else {
				log.warning("not toolbar content providing class known for " + type);
			}
		}
		return contents;
	}
}
