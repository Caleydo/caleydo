package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.storagebased.GLParallelCoordinates;
import org.caleydo.rcp.action.toolbar.view.storagebased.ChangeOrientationAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.ResetViewAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.AngularBrushingAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.ResetAxisSpacingAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.parcoords.SaveSelectionsAction;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.  
 * @author Werner Puff
 */
public class ParCoordsToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Parallel Coordinates";

	@Override
	public Class<?> getViewClass() {
		return GLParallelCoordinates.class;
	}
	
	@Override
	public List<ToolBarContainer> getDefaultToolBar() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		// all pc views
		IToolBarItem angularBrushingAction = new AngularBrushingAction(targetViewID);
		actionList.add(angularBrushingAction);
		// IAction occlusionPreventionAction = new OcclusionPreventionAction(iViewID);
		// alToolbar.add(occlusionPreventionAction);
		IToolBarItem switchAxesToPolylinesAction = new ChangeOrientationAction(targetViewID);
		actionList.add(switchAxesToPolylinesAction);

		IToolBarItem resetAxisSpacing = new ResetAxisSpacingAction(targetViewID);
		actionList.add(resetAxisSpacing);

		if (contentType == STANDARD_CONTENT) {
			IToolBarItem clearSelectionsAction = new ClearSelectionsAction(targetViewID);
			actionList.add(clearSelectionsAction);
			IToolBarItem saveSelectionsAction = new SaveSelectionsAction(targetViewID);
			actionList.add(saveSelectionsAction);
			IToolBarItem resetViewAction = new ResetViewAction(targetViewID);
			actionList.add(resetViewAction);
			IToolBarItem propagateSelectionAction = new PropagateSelectionsAction(targetViewID);
			actionList.add(propagateSelectionAction);
		}

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
