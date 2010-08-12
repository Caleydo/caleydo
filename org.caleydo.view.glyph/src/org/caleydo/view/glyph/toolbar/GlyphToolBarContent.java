package org.caleydo.rcp.view.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.action.glyph.ChangeSelectionBrushAction;
import org.caleydo.rcp.view.toolbar.action.glyph.ChangeViewModeAction;
import org.caleydo.rcp.view.toolbar.action.glyph.ChangeViewModeSecondaryAction;
import org.caleydo.rcp.view.toolbar.action.glyph.EnterViewNameAction;
import org.caleydo.rcp.view.toolbar.action.glyph.OpenDataExportAction;
import org.caleydo.rcp.view.toolbar.action.glyph.OpenNewWindowAction;
import org.caleydo.rcp.view.toolbar.action.glyph.RemoveUnselectedFromViewAction;

/**
 * ToolBarContent implementation for glyph specific toolbar items.
 * 
 * @author Werner Puff
 */
public class GlyphToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/glyph/glyph.png";

	public static final String VIEW_TITLE = "Glyph";

	@Override
	public Class<?> getViewClass() {
		return GlyphToolBarContent.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		int targetViewID = getTargetViewData().getViewID();
		actionList.add(new OpenNewWindowAction(targetViewID));
		actionList.add(new ChangeSelectionBrushAction(targetViewID));
		actionList.add(new RemoveUnselectedFromViewAction(targetViewID));
		actionList.add(new EnterViewNameAction(targetViewID));
		actionList.add(new OpenDataExportAction(targetViewID));

		ChangeViewModeSecondaryAction cvm2a = new ChangeViewModeSecondaryAction(targetViewID);
		cvm2a.setAction(null);
		actionList.add(new ChangeViewModeAction(targetViewID, cvm2a));
		actionList.add(cvm2a);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
