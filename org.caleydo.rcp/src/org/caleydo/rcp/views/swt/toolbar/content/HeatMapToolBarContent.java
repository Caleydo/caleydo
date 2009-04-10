package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.storagebased.GLHeatMap;
import org.caleydo.rcp.action.toolbar.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ResetViewAction;
import org.eclipse.jface.action.IAction;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.  
 * @author Werner Puff
 */
public class HeatMapToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/heatmap/heatmap.png";

	public static final String VIEW_TITLE = "Heat Map";

	@Override
	public Class<?> getViewClass() {
		return GLHeatMap.class;
	}
	
	@Override
	public List<ToolBarContainer> getDefaultToolBar() {
		ToolBarContainer container = new ToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);

		IAction switchAxesToPolylinesAction = new ChangeOrientationAction(targetViewID);
		container.add(switchAxesToPolylinesAction);
		if (contentType == STANDARD_CONTENT) {
			IAction clearSelectionsAction = new ClearSelectionsAction(targetViewID);
			container.add(clearSelectionsAction);
			IAction resetViewAction = new ResetViewAction(targetViewID);
			container.add(resetViewAction);
			IAction propagateSelectionAction = new PropagateSelectionsAction(targetViewID);
			container.add(propagateSelectionAction);
		}
		
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
