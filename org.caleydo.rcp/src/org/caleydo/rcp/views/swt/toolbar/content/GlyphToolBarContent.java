package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
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
		return GLHeatMap.class;
	}
	
	@Override
	public List<ToolBarContainer> getDefaultToolBar() {
		ToolBarContainer container = new ToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);

		container.add(new OpenNewWindowAction(targetViewID));
		container.add(new ChangeSelectionBrushAction(targetViewID));
		container.add(new RemoveUnselectedFromViewAction(targetViewID));
		container.add(new ClearSelectionsAction(targetViewID));
		container.add(new EnterViewNameAction(targetViewID));
		container.add(new OpenDataExportAction(targetViewID));

		ChangeViewModeSecondaryAction cvm2a = new ChangeViewModeSecondaryAction(targetViewID);
		cvm2a.setAction(null);
		container.add(new ChangeViewModeAction(targetViewID, cvm2a));
		container.add(cvm2a);
		
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
