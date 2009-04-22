package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.action.toolbar.view.glyph.ChangeSelectionBrushAction;
import org.caleydo.rcp.action.toolbar.view.glyph.ChangeViewModeAction;
import org.caleydo.rcp.action.toolbar.view.glyph.ChangeViewModeSecondaryAction;
import org.caleydo.rcp.action.toolbar.view.glyph.EnterViewNameAction;
import org.caleydo.rcp.action.toolbar.view.glyph.OpenDataExportAction;
import org.caleydo.rcp.action.toolbar.view.glyph.OpenNewWindowAction;
import org.caleydo.rcp.action.toolbar.view.glyph.RemoveUnselectedFromViewAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;

/**
 * ToolBarContent implementation for glyph specific toolbar items.  
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

		actionList.add(new OpenNewWindowAction(targetViewID));
		actionList.add(new ChangeSelectionBrushAction(targetViewID));
		actionList.add(new RemoveUnselectedFromViewAction(targetViewID));
		actionList.add(new ClearSelectionsAction(targetViewID));
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
