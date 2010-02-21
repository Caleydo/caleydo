package org.caleydo.view.parcoords.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.action.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.ResetViewAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.parcoords.AngularBrushingAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.parcoords.ResetAxisSpacingAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.parcoords.SaveSelectionsAction;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.caleydo.view.parcoords.GLParallelCoordinates;

/**
 * ToolBarContent implementation for parcoords specific toolbar items.
 * 
 * @author Werner Puff
 */
public class ParCoordsToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Parallel Coordinates";

	@Override
	public Class<?> getViewClass() {
		return GLParallelCoordinates.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		int targetViewID = getTargetViewData().getViewID();

		// all pc views
		IToolBarItem angularBrushingAction = new AngularBrushingAction(
				targetViewID);
		actionList.add(angularBrushingAction);
		// IAction occlusionPreventionAction = new
		// OcclusionPreventionAction(iViewID);
		// alToolbar.add(occlusionPreventionAction);

		IToolBarItem resetAxisSpacing = new ResetAxisSpacingAction(targetViewID);
		actionList.add(resetAxisSpacing);

		if (renderType == STANDARD_RENDERING) {
			IToolBarItem saveSelectionsAction = new SaveSelectionsAction(
					targetViewID);
			actionList.add(saveSelectionsAction);
			IToolBarItem resetViewAction = new ResetViewAction(targetViewID);
			actionList.add(resetViewAction);
			IToolBarItem propagateSelectionAction = new PropagateSelectionsAction(
					targetViewID);
			actionList.add(propagateSelectionAction);
		}

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
