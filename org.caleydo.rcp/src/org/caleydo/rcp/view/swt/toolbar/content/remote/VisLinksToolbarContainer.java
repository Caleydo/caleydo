package org.caleydo.rcp.view.swt.toolbar.content.remote;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.action.toolbar.view.remote.vislinks.ToggleVisLinkAnimatedHighlighting;
import org.caleydo.rcp.action.toolbar.view.remote.vislinks.ToggleVisLinkAnimationAction;
import org.caleydo.rcp.action.toolbar.view.remote.vislinks.ToggleVisLinkColor;
import org.caleydo.rcp.action.toolbar.view.remote.vislinks.ToggleVisLinkStyle;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.view.swt.toolbar.content.ToolBarContainer;

public class VisLinksToolbarContainer
	extends ToolBarContainer {
	
	public static final String IMAGE_PATH = "resources/icons/view/storagebased/heatmap/heatmap.png";

	public static final String VIEW_TITLE = "Visual Links";

	@Override
	public List<IToolBarItem> getToolBarItems() {
		// TODO Auto-generated method stub
		
		List<IToolBarItem> elements = new ArrayList<IToolBarItem>();
		
		ToggleVisLinkAnimationAction toggleAnimation = new ToggleVisLinkAnimationAction();
		elements.add(toggleAnimation);
		
		ToggleVisLinkColor toggleColor = new ToggleVisLinkColor();
		elements.add(toggleColor);
		
		ToggleVisLinkStyle toggleStyle = new ToggleVisLinkStyle();
		elements.add(toggleStyle);
		
		ToggleVisLinkAnimatedHighlighting toggleAnimatedHighlighting = new ToggleVisLinkAnimatedHighlighting();
		elements.add(toggleAnimatedHighlighting);

		return elements;
	}

}
