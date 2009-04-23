package org.caleydo.rcp.views.swt.toolbar.content.browser;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.serialize.SerializedHTMLBrowserView;
import org.caleydo.core.view.swt.browser.GenomeHTMLBrowserViewRep;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.views.swt.toolbar.content.ToolBarContainer;

/**
 * ToolBarContent implementation for browser specific toolbar items.  
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
	protected List<ToolBarContainer> getToolBarContent() {
		BrowserToolBarContainer container = new BrowserToolBarContainer();
		BrowserToolBarMediator browserToolBarMediator = new BrowserToolBarMediator();

		container.setBrowserToolBarMediator(browserToolBarMediator);
		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		SerializedHTMLBrowserView serializedForm = (SerializedHTMLBrowserView) getTargetViewData(); 
		container.setSelectedQueryType(serializedForm.getQueryType());

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
