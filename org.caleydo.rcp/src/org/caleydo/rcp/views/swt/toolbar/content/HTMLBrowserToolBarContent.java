package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.swt.browser.GenomeHTMLBrowserViewRep;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.  
 * @author Werner Puff
 */
public class HTMLBrowserToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/browser/browser.png";

	public static final String VIEW_TITLE = "Browser";

	@Override
	public Class<?> getViewClass() {
		return GenomeHTMLBrowserViewRep.class;
	}
	
	@Override
	public List<ToolBarContainer> getDefaultToolBar() {
		BrowserToolBarContainer container = new BrowserToolBarContainer();
		container.setTargetViewID(getTargetViewID());

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
